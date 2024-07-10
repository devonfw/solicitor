package com.devonfw.tools.solicitor.componentinfo.scancode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.LogMessages;
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
import com.devonfw.tools.solicitor.componentinfo.curation.model.CopyrightCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.CurationOperation;
import com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration;
import com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeComponentInfo.ScancodeComponentInfoData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.packageurl.PackageURL;

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

    ScancodeComponentInfo componentScancodeInfos;
    JsonNode scancodeJson;
    try {
      scancodeJson = mapper.readTree(rawScancodeData.rawScancodeResult);
    } catch (JsonProcessingException e) {
      throw new ComponentInfoAdapterException("Could not parse Scancode JSON", e);
    }

    // Determine the version of the Scancode data
    String scancodeVersion = determineScancodeVersion(scancodeJson);

    // Process the Scancode data according to the version
    if ("32.x".equals(scancodeVersion)) {
      componentScancodeInfos = parseAndMapScancodeJsonV32(packageUrl, scancodeJson, curationDataHandle);
    } else if ("31.x".equals(scancodeVersion)) {
      componentScancodeInfos = parseAndMapScancodeJsonV31(packageUrl, scancodeJson, curationDataHandle);
    } else {
      throw new ComponentInfoAdapterException("Unsupported Scancode version: " + scancodeVersion);
    }

    addSupplementedData(rawScancodeData, componentScancodeInfos);
    LOG.debug("Scancode info for package {}: {} license, {} copyrights, {} NOTICE files", packageUrl,
        componentScancodeInfos.getComponentInfoData().getLicenses().size(),
        componentScancodeInfos.getComponentInfoData().getCopyrights().size(),
        componentScancodeInfos.getComponentInfoData().getNoticeFileUrl() != null ? 1 : 0);

    return componentScancodeInfos;
  }

  /**
   * @param rawScancodeData
   * @param componentScancodeInfos
   */
  private void addSupplementedData(ScancodeRawComponentInfo rawScancodeData,
      ScancodeComponentInfo componentScancodeInfos) {

    componentScancodeInfos.getComponentInfoData().setSourceDownloadUrl(rawScancodeData.sourceDownloadUrl);
    componentScancodeInfos.getComponentInfoData().setPackageDownloadUrl(rawScancodeData.packageDownloadUrl);
    // Add new attributes from Scancode version 32.x if applicable
    componentScancodeInfos.getComponentInfoData().setDetectedLicenseExpressionSpdx(rawScancodeData.detectedLicenseExpressionSpdx);
    componentScancodeInfos.getComponentInfoData().setLicenseDetections(rawScancodeData.licenseDetections);
    componentScancodeInfos.getComponentInfoData().setDetectedLicenseExpression(rawScancodeData.detectedLicenseExpression);
  }

    /**
   * Determines the Scancode version from the JSON data.
   *
   * @param scancodeJson the Scancode JSON data
   * @return the version of the Scancode data
   */
  private String determineScancodeVersion(JsonNode scancodeJson) {
    // Logic to determine version based on the JSON structure
    if (scancodeJson.has("files")) {
      JsonNode firstFile = scancodeJson.get("files").get(0);
      if (firstFile.has("license_detections")) {
        return "32.x";
      } else if (firstFile.has("licenses")) {
        return "31.x";
      }
    }
    return "unknown";
  }

    /**
   * Parses and maps Scancode JSON (version 32.x) to create ScancodeComponentInfo.
   *
   * @param packageUrl package URL of the package
   * @param scancodeJson the Scancode JSON data
   * @param curationDataHandle identifies which source should be used for the curation data.
   * @return the ScancodeComponentInfo
   * @throws ComponentInfoAdapterException if there was an issue during parsing
   * @throws CurationInvalidException if the curation data is not valid
   */
  private ScancodeComponentInfo parseAndMapScancodeJsonV32(String packageUrl, JsonNode scancodeJson,
      CurationDataHandle curationDataHandle) throws ComponentInfoAdapterException, CurationInvalidException {

    ScancodeComponentInfo componentScancodeInfos = new ScancodeComponentInfo(this.minLicenseScore,
        this.minLicensefileNumberOfLines);
    componentScancodeInfos.setPackageUrl(packageUrl);
    componentScancodeInfos.setDataStatus(DataStatusValue.NO_ISSUES);
    ScancodeComponentInfoData scancodeComponentInfoData = componentScancodeInfos.getComponentInfoData();
    ComponentInfoCuration componentInfoCuration = this.curationProvider.findCurations(packageUrl, curationDataHandle);

    List<String> excludedPaths = null;
    List<LicenseCuration> licenseCurations = null;
    List<CopyrightCuration> copyrightCurations = null;
    if (componentInfoCuration != null) {
      excludedPaths = componentInfoCuration.getExcludedPaths();
      licenseCurations = componentInfoCuration.getLicenseCurations();
      copyrightCurations = componentInfoCuration.getCopyrightCurations();
    }

    for (JsonNode fileNode : scancodeJson.get("files")) {
      String path = fileNode.get("path").asText();
      if (excludedPaths != null && excludedPaths.contains(path)) {
        continue;
      }

      String detectedLicenseExpression = fileNode.get("detected_license_expression").asText();
      String detectedLicenseExpressionSpdx = fileNode.get("detected_license_expression_spdx").asText();

      for (JsonNode licenseDetectionNode : fileNode.get("license_detections")) {
        String licenseExpression = licenseDetectionNode.get("license_expression").asText();
        for (JsonNode match : licenseDetectionNode.get("matches")) {
          String licenseId = match.get("identifier").asText();
          double score = match.get("score").asDouble();
          String filePath = path;
          String givenLicenseText = null;
          int fileScore = match.get("matched_length").asInt();

          scancodeComponentInfoData.addLicense(licenseId, detectedLicenseExpressionSpdx, licenseExpression, score, filePath, givenLicenseText, fileScore);
        }
      }

      for (JsonNode licenseClueNode : fileNode.get("license_clues")) {
        for (JsonNode match : licenseClueNode.get("matches")) {
          String licenseId = match.get("identifier").asText();
          double score = match.get("score").asDouble();
          String filePath = path;
          String givenLicenseText = null;
          int fileScore = match.get("matched_length").asInt();

          scancodeComponentInfoData.addLicense(licenseId, detectedLicenseExpressionSpdx, "", score, filePath, givenLicenseText, fileScore);
        }
      }

      for (JsonNode copyrightNode : fileNode.get("copyrights")) {
        String statement = copyrightNode.get("value").asText();
        scancodeComponentInfoData.addCopyright(statement);
      }

      if (fileNode.has("notices")) {
        for (JsonNode noticeNode : fileNode.get("notices")) {
          String notice = noticeNode.get("value").asText();
          scancodeComponentInfoData.setNoticeFileContent(notice);
        }
      }
    }

    for (JsonNode packageNode : scancodeJson.get("packages")) {
      for (JsonNode licenseDetectionNode : packageNode.get("license_detections")) {
        String licenseExpression = licenseDetectionNode.get("license_expression").asText();
        for (JsonNode match : licenseDetectionNode.get("matches")) {
          String licenseId = match.get("identifier").asText();
          double score = match.get("score").asDouble();
          String filePath = packageNode.get("path").asText();
          String givenLicenseText = null;
          int fileScore = match.get("matched_length").asInt();

          scancodeComponentInfoData.addLicense(licenseId, licenseExpression, licenseExpression, score, filePath, givenLicenseText, fileScore);
        }
      }

      for (JsonNode copyrightNode : packageNode.get("copyrights")) {
        String statement = copyrightNode.get("value").asText();
        scancodeComponentInfoData.addCopyright(statement);
      }
    }

    return componentScancodeInfos;
  }
  /**
   * Parses and maps Scancode JSON (version 31.x) to create ScancodeComponentInfo.
   *
   * @param packageUrl package URL of the package
   * @param scancodeJson the Scancode JSON data
   * @param curationDataHandle identifies which source should be used for the curation data.
   * @return the ScancodeComponentInfo
   * @throws ComponentInfoAdapterException if there was an issue during parsing
   * @throws CurationInvalidException if the curation data is not valid
   */
private ScancodeComponentInfo parseAndMapScancodeJsonV31(String packageUrl, JsonNode scancodeJson,
                                                             CurationDataHandle curationDataHandle)
            throws ComponentInfoAdapterException, CurationInvalidException {

        ScancodeComponentInfo componentScancodeInfos = new ScancodeComponentInfo(this.minLicenseScore,
                this.minLicensefileNumberOfLines);
        componentScancodeInfos.setPackageUrl(packageUrl);
        componentScancodeInfos.setDataStatus(DataStatusValue.NO_ISSUES);
        ScancodeComponentInfoData scancodeComponentInfoData = componentScancodeInfos.getComponentInfoData();
        ComponentInfoCuration componentInfoCuration = this.curationProvider.findCurations(packageUrl, curationDataHandle);

        List<String> excludedPaths = null;
        List<LicenseCuration> licenseCurations = null;
        List<CopyrightCuration> copyrightCurations = null;
        if (componentInfoCuration != null) {
            excludedPaths = componentInfoCuration.getExcludedPaths();
            licenseCurations = componentInfoCuration.getLicenseCurations();
            copyrightCurations = componentInfoCuration.getCopyrightCurations();
        }

        for (JsonNode fileNode : scancodeJson.get("files")) {
            String path = fileNode.get("path").asText();
            if (excludedPaths != null && excludedPaths.contains(path)) {
                continue;
            }

            if (fileNode.has("licenses")) {
                for (JsonNode licenseNode : fileNode.get("licenses")) {
                    String licenseId = licenseNode.get("key").asText();
                    String licenseSpdxId = licenseNode.get("spdx_license_key").asText();
                    double score = licenseNode.get("score").asDouble();
                    String filePath = path;
                    String givenLicenseText = null; // Not available in sample data
                    int fileScore = licenseNode.get("matched_length").asInt(); // Assuming number of matches

                    scancodeComponentInfoData.addLicense(licenseId, licenseSpdxId, "", score, filePath, givenLicenseText, fileScore);
                }
            }

            if (fileNode.has("copyrights")) {
                for (JsonNode copyrightNode : fileNode.get("copyrights")) {
                    String statement = copyrightNode.get("value").asText();
                    scancodeComponentInfoData.addCopyright(statement);
                }
            }

            if (fileNode.has("notices")) {
                for (JsonNode noticeNode : fileNode.get("notices")) {
                    String notice = noticeNode.get("value").asText();
                    scancodeComponentInfoData.setNoticeFileContent(notice);
                }
            }
        }

        return componentScancodeInfos;
    }

  /**
   * Gets the effective license info after possibly applying curations for a single license finding.
   *
   * @param path
   * @param li
   * @param licenseCurations
   * @return
   */
  private LicenseCuration.NewLicenseData getEffectiveLicenseInfoWithCuration(String path, JsonNode li,
      List<LicenseCuration> licenseCurations) {

    if (licenseCurations == null) {
      // NewLicenseData with all members being null indicates: no change
      return new LicenseCuration.NewLicenseData();
    }

    String ruleIdentifier = li.get("matched_rule").get("identifier").asText();
    String matchedText = li.get("matched_text").asText();
    String spdxId = li.get("spdx_license_key").asText();

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
