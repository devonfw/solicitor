package com.devonfw.tools.solicitor.componentinfo.scancode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.CurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.DataStatusValue;
import com.devonfw.tools.solicitor.componentinfo.DefaultComponentInfoImpl;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationProvider;
import com.devonfw.tools.solicitor.componentinfo.curation.FilteredComponentInfoProvider;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
import com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeComponentInfo.ScancodeComponentInfoData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.packageurl.PackageURL;

/**
 * {@link FilteredComponentInfoProvider} which delivers data based on scancode data.
 *
 */
@Component
public class FilteredScancodeComponentInfoProvider implements FilteredComponentInfoProvider {

  private static final Logger LOG = LoggerFactory.getLogger(FilteredScancodeComponentInfoProvider.class);

  private double minLicenseScore;

  private int minLicensefileNumberOfLines;

  private double licenseToTextRatioToTakeCompleteFile = 90;

  private ScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider;

  private CurationProvider curationProvider;

  /**
   * The constructor.
   *
   * @param fileScancodeRawComponentInfoProvider the provide for the raw scancode data
   * @param packageURLHandler the handler for dealing with {@link PackageURL}s.
   * @param curationProvider for getting the filter information used for filtering findings based on the paths in the
   *        code
   */
  @Autowired
  public FilteredScancodeComponentInfoProvider(ScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider,
      AllKindsPackageURLHandler packageURLHandler, CurationProvider curationProvider) {

    this.fileScancodeRawComponentInfoProvider = fileScancodeRawComponentInfoProvider;
    this.curationProvider = curationProvider;

  }

  /**
   * Sets minLicenseScore.
   *
   * @param minLicenseScore new value of minLicenseScore.
   */
  @Value("${solicitor.scancode.min-license-score}")
  public void setMinLicenseScore(double minLicenseScore) {

    this.minLicenseScore = minLicenseScore;
  }

  /**
   * Sets minLicensefileNumberOfLines.
   *
   * @param minLicensefileNumberOfLines new value of minLicensefileNumberOfLines.
   */
  @Value("${solicitor.scancode.min-licensefile-number-of-lines}")
  public void setMinLicensefileNumberOfLines(int minLicensefileNumberOfLines) {

    this.minLicensefileNumberOfLines = minLicensefileNumberOfLines;
  }

  /**
   * Determines the tool version from the Scancode raw data.
   *
   * @param rawScancodeData The raw Scancode data.
   * @return The tool version.
   */
  public String determineToolVersion(ScancodeRawComponentInfo rawScancodeData) {

    try {
      JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);
      return scancodeJson.get("headers").get(0).get("tool_version").asText();
    } catch (Exception e) {
      LOG.error("Error determining tool version", e);
      return "Unknown";
    }
  }

  /**
   * Read scancode information for the given package from local file storage.
   *
   * @param packageUrl The package url of the package
   * @param curationDataHandle identifies which source should be used for the curation data.
   * @return the read scancode information
   * @throws ComponentInfoAdapterException if there was an exception when reading the data. In case that there is no
   *         data available no exception will be thrown. Instead <code>null</code> will be returned in such a case.
   * @throws CurationInvalidException if the curation data is not valid
   */
  @Override
  public ComponentInfo getComponentInfo(String packageUrl, CurationDataHandle curationDataHandle)
      throws ComponentInfoAdapterException, CurationInvalidException {

    ScancodeRawComponentInfo rawScancodeData;
    try {
      rawScancodeData = this.fileScancodeRawComponentInfoProvider.readScancodeData(packageUrl);
    } catch (ScancodeProcessingFailedException e) {
      return new DefaultComponentInfoImpl(packageUrl, DataStatusValue.PROCESSING_FAILED);
    }
    if (rawScancodeData == null) {
      return new DefaultComponentInfoImpl(packageUrl, DataStatusValue.NOT_AVAILABLE);
    }

    // Determine the tool version
    String toolVersion = determineToolVersion(rawScancodeData);
    LOG.debug("Scancode tool version: {}", toolVersion);
    ScancodeComponentInfo componentScancodeInfos = parseAndMapScancodeJson(packageUrl, rawScancodeData,
        curationDataHandle);
    addSupplementedData(rawScancodeData, componentScancodeInfos);
    LOG.debug("Scancode info for package {}: {} license, {} copyrights, {} NOTICE files", packageUrl,
        componentScancodeInfos.getComponentInfoData().getLicenses().size(),
        componentScancodeInfos.getComponentInfoData().getCopyrights().size(),
        componentScancodeInfos.getComponentInfoData().getNoticeFileUrl() != null ? 1 : 0);

    return componentScancodeInfos;
  }

  /**
   * Adds supplemented data to the Scancode component information.
   *
   * @param rawScancodeData The raw Scancode data.
   * @param componentScancodeInfos The Scancode component information.
   */
  private void addSupplementedData(ScancodeRawComponentInfo rawScancodeData,
      ScancodeComponentInfo componentScancodeInfos) {

    componentScancodeInfos.getComponentInfoData().setSourceDownloadUrl(rawScancodeData.sourceDownloadUrl);
    componentScancodeInfos.getComponentInfoData().setPackageDownloadUrl(rawScancodeData.packageDownloadUrl);
  }

  /**
   * Parses and maps Scancode JSON to create ScancodeComponentInfo.
   *
   * @param packageUrl The package URL.
   * @param rawScancodeData The raw Scancode data.
   * @param curationDataHandle Identifies the source for curation data.
   * @return The created ScancodeComponentInfo.
   * @throws ComponentInfoAdapterException If an issue occurs during parsing.
   * @throws CurationInvalidException If the curation data is invalid.
   */
  private ScancodeComponentInfo parseAndMapScancodeJson(String packageUrl, ScancodeRawComponentInfo rawScancodeData,
      CurationDataHandle curationDataHandle) throws ComponentInfoAdapterException, CurationInvalidException {

    ScancodeComponentInfo componentScancodeInfos = new ScancodeComponentInfo(this.minLicenseScore,
        this.minLicensefileNumberOfLines);
    componentScancodeInfos.setPackageUrl(packageUrl);
    // set status to NO_ISSUES. This might be overridden later if issues are detected or curations are applied
    componentScancodeInfos.setDataStatus(DataStatusValue.NO_ISSUES);

    // get the object which hold the actual data
    ScancodeComponentInfoData scancodeComponentInfoData = componentScancodeInfos.getComponentInfoData();

    // Get the curation for a given packageUrl
    ComponentInfoCuration componentInfoCuration = this.curationProvider.findCurations(packageUrl, curationDataHandle);
    ScancodeJsonParser scancodeJsonParser = new ScancodeJsonParser(this.fileScancodeRawComponentInfoProvider,
        packageUrl, rawScancodeData, componentScancodeInfos, scancodeComponentInfoData, componentInfoCuration);
    return scancodeJsonParser.parse(this.licenseToTextRatioToTakeCompleteFile);
  }
}