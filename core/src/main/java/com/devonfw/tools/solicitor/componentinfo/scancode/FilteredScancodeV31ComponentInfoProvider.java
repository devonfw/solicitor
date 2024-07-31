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
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.CurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.DataStatusValue;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationProvider;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.CopyrightCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.CurationOperation;
import com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration;
import com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeComponentInfo.ScancodeComponentInfoData;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * {@link FilteredScancodeVersionComponentInfoProvider} which delivers data based on scancode data.
 *
 */
@Component
public class FilteredScancodeV31ComponentInfoProvider implements FilteredScancodeVersionComponentInfoProvider {

  private static final Logger LOG = LoggerFactory.getLogger(FilteredScancodeV31ComponentInfoProvider.class);

  private double minLicenseScore;

  private int minLicensefileNumberOfLines;

  private double licenseToTextRatioToTakeCompleteFile = 90;

  private ScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider;

  private CurationProvider curationProvider;

  /**
   * The constructor.
   *
   * @param fileScancodeRawComponentInfoProvider the provide for the raw scancode data
   * @param curationProvider for getting the filter information used for filtering findings based on the paths in the
   *        code
   */
  @Autowired
  public FilteredScancodeV31ComponentInfoProvider(ScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider,
      CurationProvider curationProvider) {

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

  @Override
  public boolean accept(String toolVersion) {

    return toolVersion.startsWith("30.") || toolVersion.startsWith("31.");
  }

  /**
   * Reads Scancode information for the given package from local file storage.
   *
   * @param packageUrl the package URL of the package
   * @param curationDataHandle identifies the source for the curation data
   * @param rawScancodeData the raw Scancode data
   * @param scancodeJson the parsed Scancode JSON data
   * @return the component information based on the Scancode data
   * @throws ComponentInfoAdapterException if an error occurs while reading the data
   * @throws CurationInvalidException if the curation data is invalid
   */
  @Override
  public ComponentInfo getComponentInfo(String packageUrl, CurationDataHandle curationDataHandle,
      ScancodeRawComponentInfo rawScancodeData, JsonNode scancodeJson)
      throws ComponentInfoAdapterException, CurationInvalidException {

    ScancodeComponentInfo componentScancodeInfos = mapScancodeJson(packageUrl, scancodeJson, curationDataHandle);
    addSupplementedData(rawScancodeData, componentScancodeInfos);
    LOG.debug("Scancode info for package {}: {} license, {} copyrights, {} NOTICE files", packageUrl,
        componentScancodeInfos.getComponentInfoData().getLicenses().size(),
        componentScancodeInfos.getComponentInfoData().getCopyrights().size(),
        componentScancodeInfos.getComponentInfoData().getNoticeFileUrl() != null ? 1 : 0);

    return componentScancodeInfos;
  }

  /**
   * Adds supplemented data to the component Scancode information.
   *
   * @param rawScancodeData the raw Scancode data
   * @param componentScancodeInfos the component Scancode information to be updated
   */
  private void addSupplementedData(ScancodeRawComponentInfo rawScancodeData,
      ScancodeComponentInfo componentScancodeInfos) {

    componentScancodeInfos.getComponentInfoData().setSourceDownloadUrl(rawScancodeData.sourceDownloadUrl);
    componentScancodeInfos.getComponentInfoData().setPackageDownloadUrl(rawScancodeData.packageDownloadUrl);
  }

  /**
   * Maps scancode JSON to create ScancodeComponentInfo.
   *
   * @param packageUrl the URL of the package for which Scancode data is being processed
   * @param scancodeJson the parsed JSON data from Scancode results
   * @param curationDataHandle identifies which source should be used for the curation data
   * @return the {@link ScancodeComponentInfo} containing the processed data
   * @throws ComponentInfoAdapterException if there is an issue with parsing the Scancode JSON or if a suitable provider
   *         for the Scancode version is not found
   * @throws CurationInvalidException if the curation data is not valid or if there is an error applying curation data
   */
  private ScancodeComponentInfo mapScancodeJson(String packageUrl, JsonNode scancodeJson,
      CurationDataHandle curationDataHandle) throws ComponentInfoAdapterException, CurationInvalidException {

    String toolVersion = scancodeJson.get("headers").get(0).get("tool_version").asText();
    if (!accept(toolVersion)) {
      throw new ComponentInfoAdapterException(
          "Can only handle version 30 and 31 but found version was '" + toolVersion + "'");
    }

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
      boolean classPathExceptionExists = false;
      int numberOfGplLicenses = 0;
      for (JsonNode li : file.get("licenses")) {
        LicenseCuration.NewLicenseData effective = getEffectiveLicenseInfoWithCuration(path, li, licenseCurations);
        if (effective == null) {
          // license finding to be REMOVED via finding
          continue;
        }
        String licenseName = effective.license != null ? effective.license : li.get("spdx_license_key").asText();

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
      if (classPathExceptionExists) {
        if (numberOfGplLicenses == 0) {
          LOG.warn(LogMessages.CLASSPATHEXCEPTION_WITHOUT_GPL.msg(), packageUrl);
        } else if (numberOfGplLicenses > 1) {
          LOG.warn(LogMessages.CLASSPATHEXCEPTION_MULTIPLE_GPL.msg(), packageUrl);
        } else {
          LOG.debug("Adjusting GPL license to contain WITH Classpath-execption-2.0 for " + packageUrl);
          for (String licenseName : spdxIdMap.keySet()) {
            if (licenseName.startsWith("GPL")) {
              spdxIdMap.put(licenseName, licenseName + " WITH Classpath-exception-2.0");
            }
          }
          // do not output the Classpath-exception-2.0 as separate License
          spdxIdMap.remove("Classpath-exception-2.0");
        }
      }
      for (JsonNode li : file.get("licenses")) {
        LicenseCuration.NewLicenseData effective = getEffectiveLicenseInfoWithCuration(path, li, licenseCurations);
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
        String licenseName = effective.license != null ? effective.license : li.get("spdx_license_key").asText();
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
   * Gets the effective license info after possibly applying curations for a single license finding.
   *
   * @param path the path of the license finding which is used in the curation rules matching.
   * @param li a {@link JsonNode} representing the license finding data that includes information such as the matched
   *        rule identifier, matched text, and SPDX license key.
   * @param licenseCurations s a list of {@link LicenseCuration} objects that may contain rules for curating license
   *        data.
   * @return a {@link LicenseCuration.NewLicenseData} object containing the effective license information after applying
   *         the curation. If no applicable curation is found or if the curation list is {@code null}, a new
   *         {@link LicenseCuration.NewLicenseData} instance with all members being {@code null} is returned, indicating
   *         no change.
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
   * @param path the path of the file or component for which the copyright curation is applied
   * @param copyright the original copyright information
   * @param copyrightCurations a list of copyright curation rules to be applied
   * @return the effective copyright string after applying curations
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
