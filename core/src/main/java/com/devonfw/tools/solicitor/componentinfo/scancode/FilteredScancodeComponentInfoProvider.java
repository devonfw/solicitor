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
      for (JsonNode cr : file.get("copyrights")) {
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

      // special handling for Classpath-exception-2.0
      Map<String, String> spdxIdMap = new HashMap<>();
      boolean classPathExceptionExists = false, isNewVersion = false;
      int numberOfGplLicenses = 0;
      JsonNode licenses = file.get("licenses");
      String licenseName = "", spdxId = "", ruleIdentifier = "", matchedText = "";
      if (licenses.isEmpty()) {
        isNewVersion = true;
        licenses = file.get("license_detections");
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
      } else {

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

      for (JsonNode li : licenses) {
        LicenseCuration.NewLicenseData effective = null;
        if (isNewVersion) {
          for (JsonNode matche : li.get("matches")) {
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

        } else {
          ruleIdentifier = li.get("matched_rule").get("identifier").asText();
          matchedText = li.get("matched_text").asText();
          spdxId = li.get("spdx_license_key").asText();

          effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier, matchedText, spdxId, licenseCurations);
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
          licenseName = effective.license != null ? effective.license : li.get("spdx_license_key").asText();

        }

        String effectiveLicenseName = spdxIdMap.get(licenseName);
        if (effectiveLicenseName == null) {
          // not contained in map --> this must be the Classpath-exception-2.0
          continue;
        } else {
          licenseName = effectiveLicenseName;
        }
        String licenseDefaultUrl = li.get("scancode_text_url").asText();
        if (effective.url != null) {
          licenseDefaultUrl = effective.url;
        }
        licenseDefaultUrl = normalizeLicenseUrl(packageUrl, licenseDefaultUrl);
        double score = li.get("score").asDouble();
        String licenseUrl = path;
        int startLine = li.get("start_line").asInt();
        int endLine = li.get("end_line").asInt();
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
      // do any per scanned file postprocessing
      addCopyrightsByCuration(path, copyrightCurations, componentScancodeInfos);
      addLicensesByCuration(packageUrl, path, licenseCurations, componentScancodeInfos);

    }
    // add copyrights / licenses due to curations on package level
    addCopyrightsByCuration(null, copyrightCurations, componentScancodeInfos);
    addLicensesByCuration(packageUrl, null, licenseCurations, componentScancodeInfos);

    if (scancodeComponentInfoData.getNoticeFileUrl() != null) {
      scancodeComponentInfoData.setNoticeFileContent(this.fileScancodeRawComponentInfoProvider
          .retrieveContent(packageUrl, scancodeComponentInfoData.getNoticeFileUrl()));
    }
    return componentScancodeInfos;
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