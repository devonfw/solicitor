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
 * This class is responsible for parsing Scancode JSON data and extracting relevant component information.
 * It applies curations to the parsed data and manages the state of the component information.
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
  private  List<LicenseCuration> licenseCurations = null;
  private  List<CopyrightCuration> copyrightCurations = null;
  private Map<String, String> spdxIdMap = new HashMap<>();


  private boolean classPathExceptionExists = false;
  private int numberOfGplLicenses = 0;
  private String licenseName = "", spdxId = "", ruleIdentifier = "", matchedText = "";


  /**
   * Constructor for ScancodeJsonParser.
   *
   * @param fileScancodeRawComponentInfoProvider provider for raw Scancode component information.
   * @param packageUrl URL of the package being processed.
   * @param rawScancodeData raw Scancode data.
   * @param componentScancodeInfos component information for Scancode.
   * @param scancodeComponentInfoData data for Scancode component information.
   * @param componentInfoCuration curation data for component information.
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
 * @param componentInfoCuration curation data for component information.
 */
  private void setComponentInfoCuration(ComponentInfoCuration componentInfoCuration){
    
      // Get all excludedPaths in this curation
      if (componentInfoCuration != null) {
        this.excludedPaths = componentInfoCuration.getExcludedPaths();
        this.licenseCurations = componentInfoCuration.getLicenseCurations();
        this.copyrightCurations = componentInfoCuration.getCopyrightCurations();
      }
  }

  /**
   * Check if the given path prefix is excluded in the curation.
   *
   * @param path          in the scancode data
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

  /**
   * Parses the Scancode JSON data and extracts relevant component information.
   *
   * @param licenseToTextRatioToTakeCompleteFile Ratio to determine if the complete file should be taken.
   * @return ScancodeComponentInfo containing the parsed component information.
   * @throws ComponentInfoAdapterException If there is an error in parsing the JSON data.
   */
  public  ScancodeComponentInfo parse(double licenseToTextRatioToTakeCompleteFile) throws ComponentInfoAdapterException {
    JsonNode scancodeJson;
      try {
        scancodeJson = mapper.readTree(rawScancodeData.rawScancodeResult);
      } catch (JsonProcessingException e) {
        throw new ComponentInfoAdapterException("Could not parse Scancode JSON", e);
      }

      // Skip all files, whose path have a prefix which is in the excluded path list
      for (JsonNode file : scancodeJson.get("files")) {
        String path = file.get("path").asText();
        if (isExcluded(path, this.excludedPaths)) {
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
        boolean takeCompleteFile = licenseTextRatio >= licenseToTextRatioToTakeCompleteFile;
        
        processCopyrights(file.get("copyrights"), path);

        // special handling for Classpath-exception-2.0
        boolean isNewVersion = false;
        JsonNode licenses = file.get("licenses");
        
        if (licenses.isEmpty()){
          isNewVersion = true;
          licenses = file.get("license_detections");
          setlicensesV32(licenses, file, path);
        }
        else
          setlicensesV31(licenses, path);

        setClassPathExceptionExists();

        for (JsonNode license : licenses) {
          LicenseCuration.NewLicenseData effective = null;
          if(isNewVersion)
            effective = setStatusV32(license, path);
          else
            effective = setStatusV31(license, path);
          
          if(effective != null)
            reformatData(license, effective, path, takeCompleteFile);

        }
        addCopyrightsByCuration(path);
        addLicensesByCuration(path);
      }

      // add copyrights / licenses due to curations on package level
    addCopyrightsByCuration(null);
    addLicensesByCuration(null);

    if (scancodeComponentInfoData.getNoticeFileUrl() != null) {
      scancodeComponentInfoData.setNoticeFileContent(this.fileScancodeRawComponentInfoProvider
          .retrieveContent(packageUrl, scancodeComponentInfoData.getNoticeFileUrl()));
    }
    return componentScancodeInfos;
  }

  /**
   * Processes the copyright information from the given JSON node.
   *
   * @param copyrights JSON node containing the copyright information.
   * @param path       path of the file in the Scancode data.
   */
  private void processCopyrights(JsonNode copyrights, String path){
    for (JsonNode cr : copyrights) {
      String copyright;
      if (cr.has("copyright")) {
        copyright = cr.get("copyright").asText();
      } else {
        copyright = cr.get("value").asText();
      }
      String copyrightAfterCuration = getEffectiveCopyrightWithCuration(path, copyright, copyrightCurations);
      if (copyrightAfterCuration != null) {
        if (!copyrightAfterCuration.equals(copyright)) {
          // the copyright info changed due to applying a curation, so set the status
          componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
        }
        scancodeComponentInfoData.addCopyright(copyrightAfterCuration);
      } else {
        if (copyright != null) {
          // the copyright info was removed due to applying a curation, so set the status
          componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);

        }
      }
    }
  }

  /**
   * Gets the effective copyright after possibly applying curations for a single copyright finding.
   *
   * @param path       The path of the file.
   * @param copyright The original copyright text.
   * @param copyrightCurations The list of copyright curations to apply.
   * @return Effective copyright text after applying curations, or null if removed.
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
   * Sets the license information for Scancode version 3.1 or older.
   *
   * @param licenses JSON node containing the license information.
   * @param path     Path of the file in the Scancode data.
   */
  private void setlicensesV31(JsonNode licenses, String path){
    for (JsonNode li : licenses) {
      ruleIdentifier = li.get("matched_rule").get("identifier").asText();
      matchedText = li.get("matched_text").asText();
      spdxId = li.get("spdx_license_key").asText();

      LicenseCuration.NewLicenseData effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier,
          matchedText, spdxId, licenseCurations);
      if (effective == null) {
        // license finding to be REMOVED via finding
        continue;
      }
      licenseName = effective.license != null ? effective.license : spdxId;

      if ("Classpath-exception-2.0".equals(licenseName)) {
        classPathExceptionExists = true;
      }
      if (!spdxIdMap.containsKey(licenseName)) {
        spdxIdMap.put(licenseName, licenseName);
        if (licenseName.startsWith("GPL")) {
          numberOfGplLicenses++;
        }
      }
    }

  }

  /**
   * Sets the license information for Scancode version 3.2 or newer.
   *
   * @param licenses JSON node containing the license information.
   * @param file     File node containing the license detections.
   * @param path     Path of the file in the Scancode data.
   */
  private void setlicensesV32(JsonNode licenses, JsonNode file, String path){
    spdxId = file.get("detected_license_expression_spdx").asText();
        for (JsonNode li : licenses) {
          for (JsonNode matche : li.get("matches")) {
            ruleIdentifier = matche.get("rule_identifier").asText();
            matchedText = matche.get("matched_text").asText();
            LicenseCuration.NewLicenseData effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier,
                matchedText, spdxId, licenseCurations);
            if (effective == null) {
              // license finding to be REMOVED via finding
              continue;
            }
            licenseName = effective.license != null ? effective.license : spdxId;

            if ("Classpath-exception-2.0".equals(licenseName)) {
              classPathExceptionExists = true;
            }
            if (!spdxIdMap.containsKey(licenseName)) {
              spdxIdMap.put(licenseName, licenseName);
              if (licenseName.startsWith("GPL")) {
                numberOfGplLicenses++;
              }
            }
          }
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


  private void setClassPathExceptionExists(){
    if (classPathExceptionExists) {
        if (numberOfGplLicenses == 0) {
          LOG.warn(LogMessages.CLASSPATHEXCEPTION_WITHOUT_GPL.msg(), packageUrl);
        } else if (numberOfGplLicenses > 1) {
          LOG.warn(LogMessages.CLASSPATHEXCEPTION_MULTIPLE_GPL.msg(), packageUrl);
        } else {
          LOG.debug("Adjusting GPL license to contain WITH Classpath-execption-2.0 for " + packageUrl);
          for (String licenseNameSpdx : spdxIdMap.keySet()) {
            if (licenseNameSpdx.startsWith("GPL")) {
              spdxIdMap.put(licenseNameSpdx, licenseNameSpdx + " WITH Classpath-exception-2.0");
            }
          }
          // do not output the Classpath-exception-2.0 as separate License
          spdxIdMap.remove("Classpath-exception-2.0");
        }
      }
  }

  /**
   * Sets the status of a license finding for Scancode version 3.1 or older.
   *
   * @param license license node containing the license information.
   * @param path    path of the file in the Scancode data.
   * @return license information.
   */
  private LicenseCuration.NewLicenseData setStatusV31(JsonNode license, String path){
    LicenseCuration.NewLicenseData effective =null;
    ruleIdentifier = license.get("matched_rule").get("identifier").asText();
          matchedText = license.get("matched_text").asText();
          spdxId = license.get("spdx_license_key").asText();

          effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier, matchedText, spdxId, licenseCurations);
          if (effective == null) {
            // license finding to be REMOVED via finding
            // this is a curation operation, so set the status
            componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
            return null;
          }
          if (effective.license != null || effective.url != null) {
            // license or url are altered due to curation, so set the status
            componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
          }
          licenseName = effective.license != null ? effective.license : license.get("spdx_license_key").asText();
          return effective;
  }

  /**
   * Sets the status of a license finding for Scancode version 3.2 or newer.
   *
   * @param license license node containing the license information.
   * @param path    path of the file in the Scancode data.
   * @return license information.
   */
  private LicenseCuration.NewLicenseData setStatusV32(JsonNode license, String path){
    LicenseCuration.NewLicenseData effective = null;
    for (JsonNode matche : license.get("matches")) {
      ruleIdentifier = matche.get("rule_identifier").asText();
      matchedText = matche.get("matched_text").asText();
      effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier, matchedText, spdxId,
          licenseCurations);
      if (effective == null) {
        // license finding to be REMOVED via finding
        // this is a curation operation, so set the status
        componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
        continue;
      }
      if (effective.license != null || effective.url != null) {
        // license or url are altered due to curation, so set the status
        componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
      }
      licenseName = effective.license != null ? effective.license : spdxId;
    }
    return effective;
  }

  /**
   * Reformats the license data and adds it to the Scancode component information.
   *
   * @param license JSON node containing the license information.
   * @param effective Effective license data.
   * @param path Path of the file in the Scancode data.
   * @param takeCompleteFile Flag indicating if the complete file should be taken.
   */
  private void reformatData(JsonNode license, LicenseCuration.NewLicenseData effective, String path, boolean takeCompleteFile){
    String effectiveLicenseName = spdxIdMap.get(licenseName);
        if (effectiveLicenseName == null) {
          // not contained in map --> this must be the Classpath-exception-2.0
          return;
        } else {
          licenseName = effectiveLicenseName;
        }
        String licenseDefaultUrl = license.get("scancode_text_url").asText();
        if (effective.url != null) {
          licenseDefaultUrl = effective.url;
        }
        licenseDefaultUrl = normalizeLicenseUrl(packageUrl, licenseDefaultUrl);
        double score = license.get("score").asDouble();
        String licenseUrl = path;
        int startLine = license.get("start_line").asInt();
        int endLine = license.get("end_line").asInt();
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

        licenseUrl = normalizeLicenseUrl(packageUrl, licenseUrl);
        String givenLicenseText = null;
        if (licenseUrl != null) {
          givenLicenseText = this.fileScancodeRawComponentInfoProvider.retrieveContent(packageUrl, licenseUrl);
        }

        scancodeComponentInfoData.addLicense(licenseName, licenseName, licenseDefaultUrl, score, licenseUrl,
            givenLicenseText, endLine - startLine);
    }
      
  /**
   * Adjustment of license paths/urls so that they might retrieved
   *
   * @param packageUrl package url of the package
   * @param licenseUrl the original path or URL
   * @return adjustedLicenseUrl the adjusted path or url as a url
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
   * Adds copyrights entries due to curations.
   *
   * @param effective effective license data
   */
  private void addCopyrightsByCuration(String path) {

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
   * Adds license entries due to curations.
   * 
   * @param path
   */
  private void addLicensesByCuration(String path) {

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

}