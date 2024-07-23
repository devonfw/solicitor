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
   * Parses and maps the Scancode JSON data to a {@link ScancodeComponentInfo} object.
   *
   * @param packageUrl the URL of the package.
   * @param rawScancodeData the raw Scancode data to parse.
   * @param curationDataHandle identifies the source of curation data.
   * @return the {@link ScancodeComponentInfo} populated with data from the Scancode JSON.
   * @throws ComponentInfoAdapterException if an error occurs while parsing the data.
   * @throws CurationInvalidException if the curation data is invalid.
   */
  private ScancodeComponentInfo parseAndMapScancodeJson(String packageUrl, ScancodeRawComponentInfo rawScancodeData,
      CurationDataHandle curationDataHandle) throws ComponentInfoAdapterException, CurationInvalidException {

    ScancodeComponentInfo componentScancodeInfos = new ScancodeComponentInfo(this.minLicenseScore,
        this.minLicensefileNumberOfLines);
    componentScancodeInfos.setPackageUrl(packageUrl);
    componentScancodeInfos.setDataStatus(DataStatusValue.NO_ISSUES);

    ScancodeComponentInfoData scancodeComponentInfoData = componentScancodeInfos.getComponentInfoData();
    ComponentInfoCuration componentInfoCuration = this.curationProvider.findCurations(packageUrl, curationDataHandle);

    JsonNode scancodeJson = parseScancodeJson(rawScancodeData.rawScancodeResult);
    String toolVersion = extractToolVersion(scancodeJson);

    ScancodeJsonParser scancodeJsonParser = createScancodeJsonParser(toolVersion, packageUrl, componentScancodeInfos,
        scancodeComponentInfoData, componentInfoCuration);

    return scancodeJsonParser.parse(scancodeJson, this.licenseToTextRatioToTakeCompleteFile);
  }

  /**
   * Parses the raw Scancode JSON result into a {@link JsonNode}.
   *
   * @param rawScancodeResult the raw Scancode JSON result as a string.
   * @return the parsed {@link JsonNode}.
   * @throws ComponentInfoAdapterException if an error occurs while parsing the JSON.
   */
  private JsonNode parseScancodeJson(String rawScancodeResult) throws ComponentInfoAdapterException {

    try {
      return new ObjectMapper().readTree(rawScancodeResult);
    } catch (Exception e) {
      LOG.error("Error parsing Scancode JSON data", e);
      throw new ComponentInfoAdapterException("Error parsing Scancode JSON data", e);
    }
  }

  /**
   * Extracts the tool version from the Scancode JSON data.
   *
   * @param scancodeJson the parsed Scancode JSON data.
   * @return the tool version as a {@link String}.
   * @throws ComponentInfoAdapterException if an error occurs while extracting the tool version.
   */
  private String extractToolVersion(JsonNode scancodeJson) throws ComponentInfoAdapterException {

    try {
      return scancodeJson.get("headers").get(0).get("tool_version").asText();
    } catch (Exception e) {
      LOG.error("Error extracting tool version from Scancode JSON", e);
      throw new ComponentInfoAdapterException("Error extracting tool version from Scancode JSON", e);
    }
  }

  /**
   * Creates an appropriate {@link ScancodeJsonParser} based on the Scancode tool version.
   *
   * @param toolVersion the version of the Scancode tool.
   * @param packageUrl the URL of the package.
   * @param componentScancodeInfos the Scancode component information to populate.
   * @param scancodeComponentInfoData the Scancode component information data.
   * @param componentInfoCuration the curation data for the component.
   * @return the {@link ScancodeJsonParser} instance for the specified tool version.
   */
  private ScancodeJsonParser createScancodeJsonParser(String toolVersion, String packageUrl,
      ScancodeComponentInfo componentScancodeInfos, ScancodeComponentInfoData scancodeComponentInfoData,
      ComponentInfoCuration componentInfoCuration) {

    if (toolVersion.startsWith("32.")) {
      return new ScancodeJsonParserV32(this.fileScancodeRawComponentInfoProvider, packageUrl, componentScancodeInfos,
          scancodeComponentInfoData, componentInfoCuration);
    } else {
      return new ScancodeJsonParserV31(this.fileScancodeRawComponentInfoProvider, packageUrl, componentScancodeInfos,
          scancodeComponentInfoData, componentInfoCuration);
    }
  }
}