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
import com.devonfw.tools.solicitor.componentinfo.DataStatusValue;
import com.devonfw.tools.solicitor.componentinfo.DefaultComponentInfoImpl;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationProvider;
import com.devonfw.tools.solicitor.componentinfo.curation.FilteredComponentInfoProvider;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
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

  private AllKindsPackageURLHandler packageURLHandler;

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
    this.packageURLHandler = packageURLHandler;
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
   * @param curationDataSelector identifies which source should be used for the curation data. <code>null</code>
   *        indicates that the default should be used. If the value of curationDataSelector equals "none," no curations
   *        will be applied.
   * @return the read scancode information
   * @throws ComponentInfoAdapterException if there was an exception when reading the data. In case that there is no
   *         data available no exception will be thrown. Instead <code>null</code> will be return in such a case.
   */
  @Override
  public ComponentInfo getComponentInfo(String packageUrl, String curationDataSelector)
      throws ComponentInfoAdapterException {

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
        curationDataSelector);
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
  }

  /**
   * Parses and maps scancode JSON to create ScancodeComponentInfo.
   *
   * @param packageUrl package URL of the package
   * @param rawScancodeData raw scancode data
   * @param curationDataSelector identifies which source should be used for the curation data. If the value of
   *        curationdataselector equals "none," no curations will be applied.
   * @return the ScancodeComponentInfo
   * @throws ComponentInfoAdapterException if there was an issue during parsing
   */
  private ScancodeComponentInfo parseAndMapScancodeJson(String packageUrl, ScancodeRawComponentInfo rawScancodeData,
      String curationDataSelector) throws ComponentInfoAdapterException {

    ScancodeComponentInfo componentScancodeInfos = new ScancodeComponentInfo(this.minLicenseScore,
        this.minLicensefileNumberOfLines);
    componentScancodeInfos.setPackageUrl(packageUrl);
    // set status to NO_ISSUES. This might be overridden later if issues are detected
    componentScancodeInfos.setDataStatus(DataStatusValue.NO_ISSUES);

    // get the object which hold the actual data
    ScancodeComponentInfoData scancodeComponentInfoData = componentScancodeInfos.getComponentInfoData();

    // Get the curation for a given packageUrl
    ComponentInfoCuration componentInfoCuration = this.curationProvider.findCurations(packageUrl, curationDataSelector);

    // Get all excludedPaths in this curation
    List<String> excludedPaths = null;
    if (componentInfoCuration != null) {
      excludedPaths = componentInfoCuration.getExcludedPaths();
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
        if (cr.has("copyright")) {
          scancodeComponentInfoData.addCopyright(cr.get("copyright").asText());
        } else {
          scancodeComponentInfoData.addCopyright(cr.get("value").asText());
        }
      }

      // special handling for Classpath-exception-2.0
      Map<String, String> spdxIdMap = new HashMap<>();
      boolean classPathExceptionExists = false;
      int numberOfGplLicenses = 0;
      for (JsonNode li : file.get("licenses")) {
        String licenseName = li.get("spdx_license_key").asText();
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
        String licenseid = li.get("key").asText();
        String licenseName = li.get("spdx_license_key").asText();
        String effectiveLicenseName = spdxIdMap.get(licenseName);
        if (effectiveLicenseName == null) {
          // not contained in map --> this must be the Classpath-exception-2.0
          continue;
        } else {
          licenseName = effectiveLicenseName;
          if (licenseName.endsWith("WITH Classpath-exception-2.0")) {
            licenseid = licenseid + "WITH Classpath-exception-2.0";
          }
        }
        String licenseDefaultUrl = li.get("scancode_text_url").asText();
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

        licenseUrl = normalizeLicenseUrl(packageUrl, licenseUrl);
        String givenLicenseText = null;
        if (licenseUrl != null) {
          givenLicenseText = this.fileScancodeRawComponentInfoProvider.retrieveContent(packageUrl, licenseUrl);
        }

        scancodeComponentInfoData.addLicense(licenseid, licenseName, licenseDefaultUrl, score, licenseUrl,
            givenLicenseText, endLine - startLine);
      }
    }
    if (scancodeComponentInfoData.getNoticeFileUrl() != null) {
      scancodeComponentInfoData.setNoticeFileContent(this.fileScancodeRawComponentInfoProvider
          .retrieveContent(packageUrl, scancodeComponentInfoData.getNoticeFileUrl()));
    }
    return componentScancodeInfos;
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
