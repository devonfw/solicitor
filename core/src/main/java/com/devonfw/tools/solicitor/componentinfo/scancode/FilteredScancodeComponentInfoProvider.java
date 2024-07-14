package com.devonfw.tools.solicitor.componentinfo.scancode;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.*;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationProvider;
import com.devonfw.tools.solicitor.componentinfo.curation.FilteredComponentInfoProvider;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.CopyrightCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.CurationOperation;
import com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration;
import com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeComponentInfo.ScancodeComponentInfoData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * {@link FilteredComponentInfoProvider} which delivers data based on scancode data.
 *
 */
@Component
public class FilteredScancodeComponentInfoProvider implements FilteredComponentInfoProvider {

  private static final Logger LOG = LoggerFactory.getLogger(FilteredScancodeComponentInfoProvider.class);

  private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  private double minLicenseScore;

  private int minLicensefileNumberOfLines;

  private double licenseToTextRatioToTakeCompleteFile = 90;

  private ScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider;

  private CurationProvider curationProvider;

  private ScancodeJsonParserV32 scancodeJsonParserV32;

  /**
   * Constructor for {@link FilteredScancodeComponentInfoProvider}.
   *
   * @param fileScancodeRawComponentInfoProvider the provider for raw Scancode data
   * @param packageURLHandler the handler for package URLs
   * @param curationProvider the provider for curation data
   */
  @Autowired
  public FilteredScancodeComponentInfoProvider(ScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider,
      AllKindsPackageURLHandler packageURLHandler, CurationProvider curationProvider) {

    this.fileScancodeRawComponentInfoProvider = fileScancodeRawComponentInfoProvider;
    this.curationProvider = curationProvider;
    this.scancodeJsonParserV32 = new ScancodeJsonParserV32(minLicenseScore, fileScancodeRawComponentInfoProvider);
  }

  /**
   * Sets the minimum license score.
   *
   * @param minLicenseScore the minimum score for a license to be considered
   */
  @Value("${solicitor.scancode.min-license-score}")
  public void setMinLicenseScore(double minLicenseScore) {

    this.minLicenseScore = minLicenseScore;
    this.scancodeJsonParserV32 = new ScancodeJsonParserV32(minLicenseScore, fileScancodeRawComponentInfoProvider);
  }

  /**
   * Sets the minimum number of lines for a license file.
   *
   * @param minLicensefileNumberOfLines the minimum number of lines in a license file
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
   * Parses and maps scancode JSON to create ScancodeComponentInfo.
   *
   * @param packageUrl package URL of the package
   * @param rawScancodeData raw scancode data
   * @param curationDataHandle identifies which source should be used for the curation data.
   * @return the ScancodeComponentInfo
   * @throws ComponentInfoAdapterException if there was an issue during parsing
   * @throws CurationInvalidException if the curation data is not valid
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

    // Get all excludedPaths in this curation
    List<String> excludedPaths = null;
    List<LicenseCuration> licenseCurations = null;
    List<CopyrightCuration> copyrightCurations = null;
    if (componentInfoCuration != null) {
      excludedPaths = componentInfoCuration.getExcludedPaths();
      licenseCurations = componentInfoCuration.getLicenseCurations();
      copyrightCurations = componentInfoCuration.getCopyrightCurations();
    }

    JsonNode scancodeJson;
    try {
      scancodeJson = mapper.readTree(rawScancodeData.rawScancodeResult);
    } catch (JsonProcessingException e) {
      throw new ComponentInfoAdapterException("Could not parse Scancode JSON", e);
    }

    // Skip all files, whose path have a prefix which is in the excluded path list
    for (JsonNode file : scancodeJson.get("files")) {
      String path = file.get("path").asText();
      if (isExcluded(path, excludedPaths)) {
        // this is a curation operation, so set the status
        componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
        continue;
      }
      if ("directory".equals(file.get("type").asText())) {
        continue;
      }
      if (path.contains("/NOTICE")) {
        scancodeComponentInfoData
            .addNoticeFileUrl(this.fileScancodeRawComponentInfoProvider.pkgContentUriFromPath(packageUrl, path), 100.0);
      }
      double licenseTextRatio = file.get("percentage_of_license_text").asDouble();
      boolean takeCompleteFile = licenseTextRatio >= this.licenseToTextRatioToTakeCompleteFile;

      // Determine Scancode version
      int version = determineScancodeVersion(file);
      switch (version) {
        case 32:
          scancodeJsonParserV32.parseAndMapScancodeJsonV32(file, scancodeComponentInfoData, packageUrl,
              licenseCurations, takeCompleteFile);
          break;
        case 31:
          parseAndMapScancodeJsonV31(file, scancodeComponentInfoData, packageUrl, licenseCurations, takeCompleteFile);
          break;
        default:
          throw new ComponentInfoAdapterException("Unsupported Scancode JSON version");
      }
    }

    if (scancodeComponentInfoData.getNoticeFileUrl() != null) {
      scancodeComponentInfoData.setNoticeFileContent(this.fileScancodeRawComponentInfoProvider
          .retrieveContent(packageUrl, scancodeComponentInfoData.getNoticeFileUrl()));
    }

    return componentScancodeInfos;
  }

  /**
   * Determines the Scancode version from the JSON file node.
   *
   * @param file the JSON file node
   * @return the Scancode version
   */
  private int determineScancodeVersion(JsonNode file) {

    if (file.has("license_detections")) {
      return 32;
    } else if (file.has("licenses")) {
      return 31;
    }
    throw new IllegalArgumentException("Unsupported Scancode JSON format");
  }

  /**
   * Parses and maps Scancode JSON version 31.x to component information.
   *
   * @param file the JSON file node
   * @param scancodeComponentInfoData the component information data
   * @param packageUrl the package URL
   * @param licenseCurations the list of license curations
   * @param takeCompleteFile whether to take the complete file content
   */
  private void parseAndMapScancodeJsonV31(JsonNode file, ScancodeComponentInfoData scancodeComponentInfoData,
      String packageUrl, List<LicenseCuration> licenseCurations, boolean takeCompleteFile) {

    for (JsonNode license : file.get("licenses")) {
      String licenseName = license.get("key").asText();
      String ruleIdentifier = license.get("matched_rule").get("identifier").asText();
      String matchedText = license.get("matched_text").asText();
      String spdxId = license.get("spdx_license_key").asText();
      int startLine = license.get("start_line").asInt();
      int endLine = license.get("end_line").asInt();
      double score = license.get("score").asDouble();
      if (score < this.minLicenseScore) {
        continue;
      }
      LicenseCuration.NewLicenseData curatedLicenseData = getEffectiveLicenseInfoWithCuration(packageUrl,
          ruleIdentifier, matchedText, spdxId, licenseCurations);
      if (curatedLicenseData != null) {
        licenseName = curatedLicenseData.getName();
        spdxId = curatedLicenseData.getSpdxId();
        score = curatedLicenseData.getScore();
        startLine = 0;
        endLine = 0;
      }
      String licenseUrl = this.fileScancodeRawComponentInfoProvider.pkgContentUriFromPath(packageUrl,
          file.get("path").asText());
      if (!takeCompleteFile) {
        licenseUrl += "#L" + startLine;
        if (endLine != startLine) {
          licenseUrl += "-L" + endLine;
        }
      }
      String givenLicenseText = this.fileScancodeRawComponentInfoProvider.retrieveContent(packageUrl, licenseUrl);
      scancodeComponentInfoData.addLicense(licenseName, ruleIdentifier, spdxId, score, licenseUrl, givenLicenseText,
          endLine - startLine);
    }
  }

  /**
   * Gets the effective license information after possibly applying curations for a single license finding.
   *
   * @param path The path to the file.
   * @param ruleIdentifier The identifier of the rule.
   * @param matchedText The matched text for the license finding.
   * @param spdxId The SPDX identifier of the license.
   * @param licenseCurations The list of license curations to apply.
   * @return The new license data after applying curations.
   */
  private LicenseCuration.NewLicenseData getEffectiveLicenseInfoWithCuration(String path, String ruleIdentifier,
      String matchedText, String spdxId, List<LicenseCuration> licenseCurations) {

    if (licenseCurations == null) {
      // NewLicenseData with all members being null indicates: no change
      return new LicenseCuration.NewLicenseData();
    }

    /*
     * String ruleIdentifier = li.get("matched_rule").get("identifier").asText(); String matchedText =
     * li.get("matched_text").asText(); String spdxId = li.get("spdx_license_key").asText();
     */
    for (LicenseCuration rule : licenseCurations) {
      if (rule.matches(path, ruleIdentifier, matchedText, spdxId)) {
        LicenseCuration.NewLicenseData result = rule.newLicenseData();
        if (LOG.isDebugEnabled()) {
          if (result == null) {
            LOG.debug("License finding of rule '{}' in '{}' will be ignored due to remove license curation",
                ruleIdentifier, path);
          } else {
            LOG.debug("License finding of rule '{}' in '{}' will be replaced by SPDX-ID '{}' and URL '{}'",
                ruleIdentifier, path, result.license, result.url);

          }
        }
        return result;
      }
    }
    // NewLicenseData with all members being null indicates: no change
    return new LicenseCuration.NewLicenseData();
  }

  /**
   * Adds license entries due to curations.
   *
   * @param packageUrl
   * @param path
   * @param licenseCurations
   * @param componentScancodeInfos
   */
  private void addLicensesByCuration(String packageUrl, String path, List<LicenseCuration> licenseCurations,
      ScancodeComponentInfo componentScancodeInfos) {

    if (licenseCurations == null) {
      // no curations available: return empty collection
      return;
    }

    for (LicenseCuration rule : licenseCurations) {
      if (rule.matches(path)) {
        if (rule.getOperation() == CurationOperation.ADD) {
          LicenseCuration.NewLicenseData license = rule.newLicenseData();
          if (LOG.isDebugEnabled()) {
            LOG.debug(
                "License finding with SPDX-ID '{}' and url '{}' in '{}' will be added due to ADD copyright curation",
                license.license, license.url, path);
          }
          String licenseUrl = normalizeLicenseUrl(packageUrl, license.url);
          String givenLicenseText = this.fileScancodeRawComponentInfoProvider.retrieveContent(packageUrl, licenseUrl);

          componentScancodeInfos.getComponentInfoData().addLicense(license.license, license.license, license.url, 100,
              licenseUrl, givenLicenseText, Integer.MAX_VALUE);
          componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
        } else {
          throw new IllegalStateException("This seems to be a bug");
        }
      }
    }
  }

  /**
   * Gets the effective copyright after possibly applying curations for a single copyright finding.
   *
   * @param path
   * @param copyright
   * @param copyrightCurations
   * @return
   */
  private String getEffectiveCopyrightWithCuration(String path, String copyright,
      List<CopyrightCuration> copyrightCurations) {

    if (copyrightCurations == null) {
      // no curations available: return the original copyright
      return copyright;
    }

    for (CopyrightCuration rule : copyrightCurations) {
      if (rule.matches(path, copyright)) {
        if (rule.getOperation() == CurationOperation.REMOVE) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Copyright finding '{}' in '{}' will be ignored due to remove copyright curation", copyright,
                path);
          }
          return null;
        }
        if (rule.getOperation() == CurationOperation.REPLACE) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Copyright finding '{}' in '{}' will be ignored due to remove copyright curation", copyright,
                path);
          }
          return rule.getNewCopyright();
        }
        throw new IllegalStateException("This seems to be a bug");
      }
    }
    // no curations applied: return the original copyright
    return copyright;
  }

  /**
   * Adds copyrights entries due to curations.
   *
   * @param path
   * @param copyrightCurations
   * @param componentScancodeInfos
   */
  private void addCopyrightsByCuration(String path, List<CopyrightCuration> copyrightCurations,
      ScancodeComponentInfo componentScancodeInfos) {

    if (copyrightCurations == null) {
      // no curations available: return empty collection
      return;
    }

    for (CopyrightCuration rule : copyrightCurations) {
      if (rule.matches(path)) {
        if (rule.getOperation() == CurationOperation.ADD) {
          String copyrightToBeAdded = rule.getNewCopyright();
          if (LOG.isDebugEnabled()) {
            LOG.debug("Copyright finding '{}' in '{}' will be added due to ADD copyright curation", copyrightToBeAdded,
                path);
          }
          componentScancodeInfos.getComponentInfoData().addCopyright(copyrightToBeAdded);
          componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
        } else {
          throw new IllegalStateException("This seems to be a bug");
        }
      }
    }
  }

  /**
   * Adjustment of license paths/urls so that they might retrieved
   *
   * @param packageUrl package url of the package
   * @param licenseUrl the original path or URL
   * @return the adjusted path or url as a url
   */
  private String normalizeLicenseUrl(String packageUrl, String licenseUrl) {

    String adjustedLicenseUrl = licenseUrl;
    if (licenseUrl != null) {
      if (licenseUrl.startsWith("http")) {
        adjustedLicenseUrl = licenseUrl.replace(
            "https://github.com/nexB/scancode-toolkit/tree/develop/src/licensedcode/data/licenses",
            "https://scancode-licensedb.aboutcode.org");
        adjustedLicenseUrl = adjustedLicenseUrl.replace("github.com", "raw.github.com").replace("/tree", "");
      } else if (this.fileScancodeRawComponentInfoProvider.isLocalContentPath(packageUrl, licenseUrl)) {
        adjustedLicenseUrl = this.fileScancodeRawComponentInfoProvider.pkgContentUriFromPath(packageUrl, licenseUrl);
        LOG.debug("LOCAL LICENSE: " + licenseUrl);
      }
    }
    return adjustedLicenseUrl;
  }

  /**
   * Check if the given path prefix is excluded in the curation.
   *
   * @param path in the scancode data
   * @param excludedPaths all excluded paths defined in the curation
   * @return true if path prefix is excluded in curation
   */
  private boolean isExcluded(String path, List<String> excludedPaths) {

    if (excludedPaths != null) {
      for (String excludedPath : excludedPaths) {
        if (path.startsWith(excludedPath)) {
          return true;
        }
      }
    }
    return false;
  }
}