package com.devonfw.tools.solicitor.componentinfo.scancode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.DataStatusValue;
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
 * This class is responsible for parsing Scancode JSON data and extracting relevant component information. It applies
 * curations to the parsed data and manages the state of the component information.
 */
public class ScancodeJsonParser {

  private static final Logger LOG = LoggerFactory.getLogger(FilteredScancodeComponentInfoProvider.class);

  private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  private ScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider;

  private String packageUrl = null;

  private ScancodeRawComponentInfo rawScancodeData = null;

  private ScancodeComponentInfo componentScancodeInfos = null;

  private ScancodeComponentInfoData scancodeComponentInfoData = null;

  private List<String> excludedPaths = null;

  private List<LicenseCuration> licenseCurations = null;

  private List<CopyrightCuration> copyrightCurations = null;

  private Map<String, String> spdxIdMap = new HashMap<>();

  private boolean classPathExceptionExists = false;

  private int numberOfGplLicenses = 0;

  /**
   * Constructs a new ScancodeJsonParser.
   *
   * @param fileScancodeRawComponentInfoProvider The provider for raw Scancode component information.
   * @param packageUrl The URL of the package being parsed.
   * @param rawScancodeData The raw Scancode data to parse.
   * @param componentScancodeInfos The object to store parsed component information.
   * @param scancodeComponentInfoData The object to store parsed Scancode component information data.
   * @param componentInfoCuration The curation rules to apply to the parsed data.
   */
  public ScancodeJsonParser(ScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider, String packageUrl,
      ScancodeRawComponentInfo rawScancodeData, ScancodeComponentInfo componentScancodeInfos,
      ScancodeComponentInfoData scancodeComponentInfoData, ComponentInfoCuration componentInfoCuration) {

    this.fileScancodeRawComponentInfoProvider = fileScancodeRawComponentInfoProvider;
    this.packageUrl = packageUrl;
    this.rawScancodeData = rawScancodeData;
    this.componentScancodeInfos = componentScancodeInfos;
    this.scancodeComponentInfoData = scancodeComponentInfoData;
    setComponentInfoCuration(componentInfoCuration);
  }

  /**
   * Sets the component information curation.
   *
   * @param componentInfoCuration The curation rules to apply.
   */
  private void setComponentInfoCuration(ComponentInfoCuration componentInfoCuration) {

    // Get all excludedPaths in this curation
    if (componentInfoCuration != null) {
      this.excludedPaths = componentInfoCuration.getExcludedPaths();
      this.licenseCurations = componentInfoCuration.getLicenseCurations();
      this.copyrightCurations = componentInfoCuration.getCopyrightCurations();
    }
  }

  /**
   * Checks if a given path is excluded based on the excluded paths list.
   *
   * @param path The path to check.
   * @param excludedPaths The list of excluded paths.
   * @return true if the path is excluded, false otherwise.
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

  /**
   * Parses the Scancode JSON data.
   *
   * @param licenseToTextRatioToTakeCompleteFile The ratio of license text to take the complete file.
   * @return The parsed Scancode component information.
   * @throws ComponentInfoAdapterException if there is an error during parsing.
   */
  public ScancodeComponentInfo parse(double licenseToTextRatioToTakeCompleteFile) throws ComponentInfoAdapterException {

    JsonNode scancodeJson;
    try {
      scancodeJson = mapper.readTree(this.rawScancodeData.rawScancodeResult);
    } catch (JsonProcessingException e) {
      throw new ComponentInfoAdapterException("Could not parse Scancode JSON", e);
    }

    String toolVersion = scancodeJson.get("headers").get(0).get("tool_version").asText();
    boolean isNewVersion = toolVersion.startsWith("32.");

    // Skip all files whose path have a prefix which is in the excluded path list
    for (JsonNode file : scancodeJson.get("files")) {
      String path = file.get("path").asText();
      if (isExcluded(path, this.excludedPaths)) {
        // this is a curation operation, so set the status
        this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
        continue;
      }
      if ("directory".equals(file.get("type").asText())) {
        continue;
      }
      if (path.contains("/NOTICE")) {
        this.scancodeComponentInfoData.addNoticeFileUrl(
            this.fileScancodeRawComponentInfoProvider.pkgContentUriFromPath(this.packageUrl, path), 100.0);
      }
      double licenseTextRatio = file.get("percentage_of_license_text").asDouble();
      boolean takeCompleteFile = licenseTextRatio >= licenseToTextRatioToTakeCompleteFile;

      processCopyrights(file.get("copyrights"), path);

      JsonNode licenses = isNewVersion ? file.get("license_detections") : file.get("licenses");

      if (isNewVersion) {
        setlicensesV32(licenses, file, path, toolVersion);
      } else {
        setlicensesV31(licenses, path);
      }

      setClassPathExceptionExists();

      for (JsonNode license : licenses) {
        LicenseCuration.NewLicenseData effective = isNewVersion ? setStatusV32(license, path)
            : setStatusV31(license, path);
        if (effective != null) {
          reformatData(license, effective, path, takeCompleteFile, isNewVersion);
        }
      }
      addCopyrightsByCuration(path);
      addLicensesByCuration(path);
    }

    // add copyrights / licenses due to curations on package level
    addCopyrightsByCuration(null);
    addLicensesByCuration(null);

    if (this.scancodeComponentInfoData.getNoticeFileUrl() != null) {
      this.scancodeComponentInfoData.setNoticeFileContent(this.fileScancodeRawComponentInfoProvider
          .retrieveContent(this.packageUrl, this.scancodeComponentInfoData.getNoticeFileUrl()));
    }
    return this.componentScancodeInfos;
  }

  /**
   * Processes the copyright information from the given JSON node.
   *
   * @param copyrights JSON node containing the copyright information.
   * @param path Path of the file in the Scancode data.
   */
  private void processCopyrights(JsonNode copyrights, String path) {

    for (JsonNode cr : copyrights) {
      String copyright;
      if (cr.has("copyright")) {
        copyright = cr.get("copyright").asText();
      } else {
        copyright = cr.get("value").asText();
      }
      String copyrightAfterCuration = getEffectiveCopyrightWithCuration(path, copyright, this.copyrightCurations);
      if (copyrightAfterCuration != null) {
        if (!copyrightAfterCuration.equals(copyright)) {
          // the copyright info changed due to applying a curation, so set the status
          this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
        }
        this.scancodeComponentInfoData.addCopyright(copyrightAfterCuration);
      } else {
        if (copyright != null) {
          // the copyright info was removed due to applying a curation, so set the status
          this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);

        }
      }
    }
  }

  /**
   * Gets the effective copyright with curation applied.
   *
   * @param path The path of the file.
   * @param copyright The original copyright.
   * @param copyrightCurations The list of copyright curations.
   * @return The effective copyright after applying curations.
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
   * Sets the licenses for Scancode 31 version.
   *
   * @param licenses The JSON node containing license information.
   * @param path The path of the file in the Scancode data.
   */
  private void setlicensesV31(JsonNode licenses, String path) {

    for (JsonNode li : licenses) {
      String ruleIdentifier = li.get("matched_rule").get("identifier").asText();
      String matchedText = li.get("matched_text").asText();
      String spdxId = li.get("spdx_license_key").asText();

      LicenseCuration.NewLicenseData effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier, matchedText,
          spdxId, this.licenseCurations);
      if (effective == null) {
        // license finding to be REMOVED via finding
        continue;
      }
      String licenseName = effective.license != null ? effective.license : spdxId;

      if ("Classpath-exception-2.0".equals(licenseName)) {
        this.classPathExceptionExists = true;
      }
      if (!this.spdxIdMap.containsKey(licenseName)) {
        this.spdxIdMap.put(licenseName, licenseName);
        if (licenseName.startsWith("GPL")) {
          this.numberOfGplLicenses++;
        }
      }
    }
  }

  /**
   * Sets the licenses for Scancode 32 version.
   *
   * @param licenses The JSON node containing license information.
   * @param file The JSON node containing file information.
   * @param path The path of the file in the Scancode data.
   * @param toolVersion The version of the Scancode tool.
   */
  private void setlicensesV32(JsonNode licenses, JsonNode file, String path, String toolVersion) {

    if (licenses == null) {
      LOG.error("Licenses node is null");
      return;
    }

    for (JsonNode li : licenses) {
      JsonNode matchesNode = li.get("matches");
      if (matchesNode == null) {
        LOG.error("Matches node is null for license: " + li.toString());
        continue;
      }

      for (JsonNode matche : matchesNode) {
        String ruleIdentifier = matche.has("rule_identifier") ? matche.get("rule_identifier").asText() : null;
        String matchedText = matche.has("matched_text") ? matche.get("matched_text").asText() : null;
        String spdxId = matche.has("spdx_license_expression") ? matche.get("spdx_license_expression").asText() : null;

        if (ruleIdentifier == null || matchedText == null || spdxId == null) {
          LOG.error("Required license fields are missing: ruleIdentifier=" + ruleIdentifier + ", matchedText="
              + matchedText + ", spdxId=" + spdxId);
          continue;
        }

        LicenseCuration.NewLicenseData effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier,
            matchedText, spdxId, this.licenseCurations);
        if (effective == null) {
          // license finding to be REMOVED via finding
          continue;
        }
        if (effective.license != null || effective.url != null) {
          // license or url are altered due to curation, so set the status
          this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
        }
        String licenseName = effective.license != null ? effective.license : spdxId;

        if ("Classpath-exception-2.0".equals(licenseName)) {
          this.classPathExceptionExists = true;
        }
        if (!this.spdxIdMap.containsKey(licenseName)) {
          this.spdxIdMap.put(licenseName, licenseName);
          if (licenseName.startsWith("GPL")) {
            this.numberOfGplLicenses++;
          }
        }
      }
    }
  }

  /**
   * Retrieves the effective license information after applying license curation rules.
   *
   * @param path The path of the file or component.
   * @param ruleIdentifier Identifier of the rule associated with license curation.
   * @param matchedText Text matched during license analysis.
   * @param spdxId SPDX license identifier.
   * @param licenseCurations List of LicenseCuration objects containing curation rules.
   * @return NewLicenseData representing the effective license data after curation.
   */
  private LicenseCuration.NewLicenseData getEffectiveLicenseInfoWithCuration(String path, String ruleIdentifier,
      String matchedText, String spdxId, List<LicenseCuration> licenseCurations) {

    if (licenseCurations == null) {
      // NewLicenseData with all members being null indicates: no change
      return new LicenseCuration.NewLicenseData();
    }

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
   * Sets whether there is a class path exception for a file.
   */
  private void setClassPathExceptionExists() {

    if (this.classPathExceptionExists) {
      if (this.numberOfGplLicenses == 0) {
        LOG.warn(LogMessages.CLASSPATHEXCEPTION_WITHOUT_GPL.msg(), this.packageUrl);
      } else if (this.numberOfGplLicenses > 1) {
        LOG.warn(LogMessages.CLASSPATHEXCEPTION_MULTIPLE_GPL.msg(), this.packageUrl);
      } else {
        LOG.debug("Adjusting GPL license to contain WITH Classpath-execption-2.0 for " + this.packageUrl);
        for (String licenseNameSpdx : this.spdxIdMap.keySet()) {
          if (licenseNameSpdx.startsWith("GPL")) {
            this.spdxIdMap.put(licenseNameSpdx, licenseNameSpdx + " WITH Classpath-exception-2.0");
          }
        }
        // do not output the Classpath-exception-2.0 as separate License
        this.spdxIdMap.remove("Classpath-exception-2.0");
      }
    }
  }

  /**
   * Sets the status for licenses in version 3.1.
   *
   * @param license JSON node containing the license information.
   * @param path Path of the file in the Scancode data.
   * @return The effective license data.
   */
  private LicenseCuration.NewLicenseData setStatusV31(JsonNode license, String path) {

    String ruleIdentifier = license.get("matched_rule").get("identifier").asText();
    String matchedText = license.get("matched_text").asText();
    String spdxId = license.get("spdx_license_key").asText();

    LicenseCuration.NewLicenseData effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier, matchedText,
        spdxId, this.licenseCurations);
    if (effective == null) {
      // license finding to be REMOVED via finding
      // this is a curation operation, so set the status
      this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
      return null;
    }
    if (effective.license != null || effective.url != null) {
      // license or url are altered due to curation, so set the status
      this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
    }
    String licenseName = effective.license != null ? effective.license : spdxId;
    return effective;
  }

  /**
   * Sets the status for licenses in version 3.2.
   *
   * @param license JSON node containing the license information.
   * @param path Path of the file in the Scancode data.
   * @return The effective license data.
   */
  private LicenseCuration.NewLicenseData setStatusV32(JsonNode license, String path) {

    LicenseCuration.NewLicenseData effective = null;
    JsonNode matchesNode = license.get("matches");
    if (matchesNode == null || !matchesNode.isArray()) {
      LOG.error("Matches node is null or not an array for license: " + license.toString());
      return null;
    }

    for (JsonNode matche : matchesNode) {
      JsonNode ruleIdentifierNode = matche.get("rule_identifier");
      JsonNode matchedTextNode = matche.get("matched_text");
      JsonNode spdxIdNode = matche.get("spdx_license_expression");

      if (ruleIdentifierNode == null || matchedTextNode == null || spdxIdNode == null) {
        LOG.error("Required fields are missing: ruleIdentifier=" + ruleIdentifierNode + ", matchedText="
            + matchedTextNode + ", spdxId=" + spdxIdNode);
        continue;
      }

      String ruleIdentifier = ruleIdentifierNode.asText();
      String matchedText = matchedTextNode.asText();
      String spdxId = spdxIdNode.asText();

      effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier, matchedText, spdxId, this.licenseCurations);
      if (effective == null) {
        // license finding to be REMOVED via finding
        // this is a curation operation, so set the status
        this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
        return null;
      }
      if (effective.license != null || effective.url != null) {
        // license or url are altered due to curation, so set the status
        this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
      }
    }
    return effective;
  }

  /**
   * Reformats data with the effective license information.
   *
   * @param license JSON node containing the license information.
   * @param effective The effective license data.
   * @param path Path of the file in the Scancode data.
   * @param takeCompleteFile Flag to indicate if the complete file should be taken.
   */
  private void reformatData(JsonNode license, LicenseCuration.NewLicenseData effective, String path,
      boolean takeCompleteFile, boolean isNewVersion) {

    if (effective == null || this.spdxIdMap == null) {
      // Log or handle the error appropriately
      System.err.println("Effective license or spdxIdMap is null");
      return;
    }

    String spdxId = null;
    String licenseDefaultUrl = null;
    double score = 0.0;
    String licenseUrl = path;
    int startLine = 0;
    int endLine = Integer.MAX_VALUE;
    if (isNewVersion) {
      for (JsonNode matche : license.get("matches")) {
        if (matche.get("rule_identifier").toString().contains("mit.LICENSE")) {
          spdxId = matche.get("spdx_license_expression").toString();
          score = matche.get("score").asDouble();
          startLine = matche.get("start_line").asInt();
          endLine = matche.get("end_line").asInt();
          licenseDefaultUrl = matche.get("rule_url").toString().replace("\"", "").trim();
          break;
        }
      }
    } else {
      spdxId = license.get("spdx_license_key").asText();
      licenseDefaultUrl = license.get("scancode_text_url").asText();
      score = license.get("score").asDouble();
      startLine = license.get("start_line").asInt();
      endLine = license.get("end_line").asInt();
    }
    if (spdxId == null)
      return;

    String licenseName = effective.license != null ? effective.license : spdxId.replace("\"", "").trim();
    String effectiveLicenseName = this.spdxIdMap.get(licenseName);

    if (effectiveLicenseName == null) {
      // not contained in map --> this must be the Classpath-exception-2.0
      return;
    } else {
      licenseName = effectiveLicenseName;
    }

    if (effective.url != null) {
      licenseDefaultUrl = effective.url;
    }
    licenseDefaultUrl = normalizeLicenseUrl(this.packageUrl, licenseDefaultUrl);

    if (!takeCompleteFile) {
      licenseUrl += "#L" + startLine;
      if (endLine != startLine) {
        licenseUrl += "-L" + endLine;
      }
    }

    if (effective.url != null) {
      // curation redefined the license URL
      licenseUrl = effective.url;
      // enforce that the filescore always exceeds the threshold
      startLine = 0;
      endLine = Integer.MAX_VALUE;
    }

    licenseUrl = normalizeLicenseUrl(this.packageUrl, licenseUrl);
    String givenLicenseText = null;
    if (licenseUrl != null) {
      givenLicenseText = this.fileScancodeRawComponentInfoProvider.retrieveContent(this.packageUrl, licenseUrl);
    }

    this.scancodeComponentInfoData.addLicense(licenseName, licenseName, licenseDefaultUrl, score, licenseUrl,
        givenLicenseText, endLine - startLine);
  }

  /**
   * Normalizes the license URL based on specific rules.
   *
   * @param packageUrl The URL of the package or component.
   * @param licenseUrl The original license URL to be normalized.
   * @return Adjusted license URL after applying normalization rules.
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
   * Adds copyrights by applying curations.
   *
   * @param path The path of the file in the Scancode data.
   */
  private void addCopyrightsByCuration(String path) {

    if (this.copyrightCurations == null) {
      // no curations available: return empty collection
      return;
    }

    for (CopyrightCuration rule : this.copyrightCurations) {
      if (rule.matches(path)) {
        if (rule.getOperation() == CurationOperation.ADD) {
          String copyrightToBeAdded = rule.getNewCopyright();
          if (LOG.isDebugEnabled()) {
            LOG.debug("Copyright finding '{}' in '{}' will be added due to ADD copyright curation", copyrightToBeAdded,
                path);
          }
          this.componentScancodeInfos.getComponentInfoData().addCopyright(copyrightToBeAdded);
          this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
        } else {
          throw new IllegalStateException("This seems to be a bug");
        }
      }
    }
  }

  /**
   * Adds licenses by applying curations.
   *
   * @param path The path of the file in the Scancode data.
   */
  private void addLicensesByCuration(String path) {

    if (this.licenseCurations == null) {
      // no curations available: return empty collection
      return;
    }

    for (LicenseCuration rule : this.licenseCurations) {
      if (rule.matches(path)) {
        if (rule.getOperation() == CurationOperation.ADD) {
          LicenseCuration.NewLicenseData license = rule.newLicenseData();
          if (LOG.isDebugEnabled()) {
            LOG.debug(
                "License finding with SPDX-ID '{}' and url '{}' in '{}' will be added due to ADD copyright curation",
                license.license, license.url, path);
          }
          String licenseUrl = normalizeLicenseUrl(this.packageUrl, license.url);
          String givenLicenseText = this.fileScancodeRawComponentInfoProvider.retrieveContent(this.packageUrl,
              licenseUrl);

          this.componentScancodeInfos.getComponentInfoData().addLicense(license.license, license.license, license.url,
              100, licenseUrl, givenLicenseText, Integer.MAX_VALUE);
          this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
        } else {
          throw new IllegalStateException("This seems to be a bug");
        }
      }
    }
  }

}