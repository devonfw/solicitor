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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Abstract base class for parsing and processing Scancode JSON data.
 * <p>
 * This class provides methods to extract and curate component information from Scancode scan results, applying license
 * and copyright curation rules. It handles parsing, processing file attributes, and normalizing URLs.
 * </p>
 */
public abstract class ScancodeJsonParserEngine {

  protected static final Logger LOG = LoggerFactory.getLogger(ScancodeJsonParserEngine.class);

  protected static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  protected final ScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider;

  protected final String packageUrl;

  protected final ScancodeComponentInfo componentScancodeInfos;

  protected final ScancodeComponentInfoData scancodeComponentInfoData;

  protected List<String> excludedPaths;

  protected List<LicenseCuration> licenseCurations;

  protected List<CopyrightCuration> copyrightCurations;

  protected Map<String, String> spdxIdMap = new HashMap<>();

  protected boolean classPathExceptionExists = false;

  protected int numberOfGplLicenses = 0;

  /**
   * Constructor for ScancodeJsonParserEngine.
   *
   * @param fileScancodeRawComponentInfoProvider Provider for raw component information from Scancode.
   * @param packageUrl URL of the package being scanned.
   * @param componentScancodeInfos Container for component information data.
   * @param scancodeComponentInfoData Data structure holding Scancode component information.
   * @param componentInfoCuration Curation rules to be applied.
   */
  public ScancodeJsonParserEngine(ScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider,
      String packageUrl, ScancodeComponentInfo componentScancodeInfos,
      ScancodeComponentInfoData scancodeComponentInfoData, ComponentInfoCuration componentInfoCuration) {

    this.fileScancodeRawComponentInfoProvider = fileScancodeRawComponentInfoProvider;
    this.packageUrl = packageUrl;
    this.componentScancodeInfos = componentScancodeInfos;
    this.scancodeComponentInfoData = scancodeComponentInfoData;
    setComponentInfoCuration(componentInfoCuration);
  }

  /**
   * Sets the component information curation.
   *
   * @param componentInfoCuration The curation rules to apply.
   */
  protected void setComponentInfoCuration(ComponentInfoCuration componentInfoCuration) {

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
  protected boolean isExcluded(String path, List<String> excludedPaths) {

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
   * Processes file attributes based on the provided Scancode file JSON node.
   *
   * @param file JSON node representing the file.
   * @param licenseToTextRatioToTakeCompleteFile Ratio to determine if the complete file should be taken.
   * @param path Path of the file in the Scancode data.
   * @return true if the file should be skipped, false otherwise.
   */
  protected boolean processFileAttributes(JsonNode file, double licenseToTextRatioToTakeCompleteFile, String path) {

    if (isExcluded(path, this.excludedPaths)) {
      // this is a curation operation, so set the status
      this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
      return true;
    }
    if ("directory".equals(file.get("type").asText())) {
      return true;
    }
    if (path.contains("/NOTICE")) {
      this.scancodeComponentInfoData.addNoticeFileUrl(
          this.fileScancodeRawComponentInfoProvider.pkgContentUriFromPath(this.packageUrl, path), 100.0);
    }

    processCopyrights(file.get("copyrights"), path);

    return false;
  }

  /**
   * Parses the Scancode JSON data and updates component information.
   *
   * @param scancodeJson JSON node containing the Scancode data.
   * @param licenseToTextRatioToTakeCompleteFile Ratio to determine if the complete file should be taken.
   * @param liceenseKey Key to access license data in the JSON node.
   * @param jsonParser The JSON parser implementation.
   * @return ScancodeComponentInfo object with parsed data.
   * @throws ComponentInfoAdapterException If an error occurs during parsing.
   */
  protected ScancodeComponentInfo parse(JsonNode scancodeJson, double licenseToTextRatioToTakeCompleteFile,
      String liceenseKey, ScancodeJsonParser jsonParser) throws ComponentInfoAdapterException {

    // Skip all files whose path have a prefix which is in the excluded path list
    for (JsonNode file : scancodeJson.get("files")) {
      String path = file.get("path").asText();
      if (processFileAttributes(file, licenseToTextRatioToTakeCompleteFile, path))
        continue;
      double licenseTextRatio = file.get("percentage_of_license_text").asDouble();
      boolean takeCompleteFile = licenseTextRatio >= licenseToTextRatioToTakeCompleteFile;
      JsonNode licenses = file.get(liceenseKey);
      jsonParser.setLicenses(licenses, path);
      setClassPathExceptionExists();
      for (JsonNode license : licenses) {
        LicenseCuration.NewLicenseData effective = jsonParser.setStatus(license, path);
        if (effective != null) {
          jsonParser.reformatData(license, effective, path, takeCompleteFile);
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
  protected void processCopyrights(JsonNode copyrights, String path) {

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
   * Retrieves the effective license information after applying license curation rules.
   *
   * @param path The path of the file or component.
   * @param ruleIdentifier Identifier of the rule associated with license curation.
   * @param matchedText Text matched during license analysis.
   * @param spdxId SPDX license identifier.
   * @param licenseCurations List of LicenseCuration objects containing curation rules.
   * @return NewLicenseData representing the effective license data after curation.
   */
  protected LicenseCuration.NewLicenseData getEffectiveLicenseInfoWithCuration(String path, String ruleIdentifier,
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
   * Retrieves the effective copyright information after applying copyright curation rules.
   *
   * @param path The path of the file or component.
   * @param copyright The original copyright text.
   * @param copyrightCurations List of CopyrightCuration objects containing curation rules.
   * @return The effective copyright text after curation.
   */
  protected String getEffectiveCopyrightWithCuration(String path, String copyright,
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
   * Sets whether there is a class path exception for a file.
   */
  protected void setClassPathExceptionExists() {

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
   * Normalizes the license URL based on specific rules.
   *
   * @param packageUrl The URL of the package or component.
   * @param licenseUrl The original license URL to be normalized.
   * @return Adjusted license URL after applying normalization rules.
   */
  protected String normalizeLicenseUrl(String packageUrl, String licenseUrl) {

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
   * Reformats data with the effective license information.
   *
   * @param license JSON node containing the license information.
   * @param effective The effective license data.
   * @param path Path of the file in the Scancode data.
   * @param takeCompleteFile Flag to indicate if the complete file should be taken.
   */
  protected void reformatData(JsonNode license, LicenseCuration.NewLicenseData effective, String path,
      boolean takeCompleteFile, String spdxId, String licenseDefaultUrl, double score, int startLine, int endLine) {

    String licenseUrl = path;

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
   * Adds copyrights by applying curations.
   *
   * @param path The path of the file in the Scancode data.
   */
  protected void addCopyrightsByCuration(String path) {

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
  protected void addLicensesByCuration(String path) {

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
