package com.devonfw.tools.solicitor.componentinfo.scancode;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.curation.UncuratedComponentInfoProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.packageurl.PackageURL;

/**
 * {@link UncuratedComponentInfoProvider} which delivers data based on scancode data.
 *
 */
@Component
public class UncuratedScancodeComponentInfoProvider implements UncuratedComponentInfoProvider {

  private static final Logger LOG = LoggerFactory.getLogger(UncuratedScancodeComponentInfoProvider.class);

  private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  private String repoBasePath;

  private double minLicenseScore;

  private int minLicensefileNumberOfLines;

  private double licenseToTextRatioToTakeCompleteFile = 90;

  private AllKindsPackageURLHandler packageURLHandler;

  private ScancodeRawComponentInfoPovider fileScancodeRawComponentInfoProvider;

  /**
   * The constructor.
   *
   * @param fileScancodeRawComponentInfoProvider the provide for the raw scancode data
   * @param packageURLHandler the handler for dealing with {@link PackageURL}s.
   */
  @Autowired
  public UncuratedScancodeComponentInfoProvider(ScancodeRawComponentInfoPovider fileScancodeRawComponentInfoProvider,
      AllKindsPackageURLHandler packageURLHandler) {

    this.fileScancodeRawComponentInfoProvider = fileScancodeRawComponentInfoProvider;
    this.packageURLHandler = packageURLHandler;

  }

  /**
   * Sets repoBasePath.
   *
   * @param repoBasePath new value of repoBasePath.
   */
  @Value("${solicitor.scancode.repo-base-path}")
  public void setRepoBasePath(String repoBasePath) {

    this.repoBasePath = repoBasePath;
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
   * @return the read scancode information, <code>null</code> if no information was found
   * @throws ComponentInfoAdapterException if there was an exception when reading the data. In case that there is no
   *         data available no exception will be thrown. Instead <code>null</code> will be return in such a case.
   */
  @Override
  public ScancodeComponentInfo getComponentInfo(String packageUrl) throws ComponentInfoAdapterException {

    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider.readScancodeData(packageUrl);
    if (rawScancodeData == null) {
      return null;
    }

    ScancodeComponentInfo componentScancodeInfos = parseAndMapScancodeJson(packageUrl, rawScancodeData);
    addSupplementedData(rawScancodeData, componentScancodeInfos);
    LOG.debug("Scancode info for package {}: {} license, {} copyrights, {} NOTICE files", packageUrl,
        componentScancodeInfos.getLicenses().size(), componentScancodeInfos.getCopyrights().size(),
        componentScancodeInfos.getNoticeFilePath() != null ? 1 : 0);

    return componentScancodeInfos;
  }

  /**
   * @param rawScancodeData
   * @param componentScancodeInfos
   */
  private void addSupplementedData(ScancodeRawComponentInfo rawScancodeData,
      ScancodeComponentInfo componentScancodeInfos) {

    componentScancodeInfos.setSourceDownloadUrl(rawScancodeData.sourceDownloadUrl);
    componentScancodeInfos.setPackageDownloadUrl(rawScancodeData.packageDownloadUrl);
  }

  /**
   * @param packageUrl
   * @param rawScancodeData
   * @return
   * @throws ComponentInfoAdapterException
   */
  private ScancodeComponentInfo parseAndMapScancodeJson(String packageUrl, ScancodeRawComponentInfo rawScancodeData)
      throws ComponentInfoAdapterException {

    ScancodeComponentInfo componentScancodeInfos = new ScancodeComponentInfo(this.minLicenseScore,
        this.minLicensefileNumberOfLines);
    componentScancodeInfos.setPackageUrl(packageUrl);

    JsonNode scancodeJson;
    try {
      scancodeJson = mapper.readTree(rawScancodeData.rawScancodeResult);
    } catch (JsonProcessingException e) {
      throw new ComponentInfoAdapterException("Could not parse Scancode JSON", e);
    }

    for (JsonNode file : scancodeJson.get("files")) {
      if ("directory".equals(file.get("type").asText())) {
        continue;
      }
      if (file.get("path").asText().contains("/NOTICE")) {
        componentScancodeInfos.addNoticeFilePath(
            this.fileScancodeRawComponentInfoProvider.pkgContentUriFromPath(file.get("path").asText()), 100.0);
      }
      double licenseTextRatio = file.get("percentage_of_license_text").asDouble();
      boolean takeCompleteFile = licenseTextRatio >= this.licenseToTextRatioToTakeCompleteFile;
      for (JsonNode cr : file.get("copyrights")) {
        if (cr.has("copyright")) {
          componentScancodeInfos.addCopyright(cr.get("copyright").asText());
        } else {
          componentScancodeInfos.addCopyright(cr.get("value").asText());
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
        String licenseFilePath = file.get("path").asText();
        int startLine = li.get("start_line").asInt();
        int endLine = li.get("end_line").asInt();
        if (!takeCompleteFile) {
          licenseFilePath += "#L" + startLine;
          if (endLine != startLine) {
            licenseFilePath += "-L" + endLine;
          }
        }

        licenseFilePath = normalizeLicenseUrl(packageUrl, licenseFilePath);
        String givenLicenseText = null;
        if (licenseFilePath != null) {
          givenLicenseText = this.fileScancodeRawComponentInfoProvider.retrieveContent(packageUrl, licenseFilePath);
        }

        componentScancodeInfos.addLicense(licenseid, licenseName, licenseDefaultUrl, score, licenseFilePath,
            givenLicenseText, endLine - startLine);
      }
    }
    if (componentScancodeInfos.getNoticeFilePath() != null) {
      componentScancodeInfos.setNoticeFileContent(this.fileScancodeRawComponentInfoProvider.retrieveContent(packageUrl,
          componentScancodeInfos.getNoticeFilePath()));
    }
    return componentScancodeInfos;
  }

  /**
   * Adjustment of license paths/urls so that they might retrieved
   *
   * @param packageUrl package url of the package
   * @param licenseFilePath the original path or URL
   * @return the adjusted path or url as a url
   */
  private String normalizeLicenseUrl(String packageUrl, String licenseFilePath) {

    String adjustedLicenseFilePath = licenseFilePath;
    if (licenseFilePath != null) {
      if (licenseFilePath.startsWith("http")) {
        adjustedLicenseFilePath = licenseFilePath.replace(
            "https://github.com/nexB/scancode-toolkit/tree/develop/src/licensedcode/data/licenses",
            "https://scancode-licensedb.aboutcode.org");
        adjustedLicenseFilePath = adjustedLicenseFilePath.replace("github.com", "raw.github.com").replace("/tree", "");
      } else if (this.fileScancodeRawComponentInfoProvider.isLocalContentPath(licenseFilePath)) {
        adjustedLicenseFilePath = this.fileScancodeRawComponentInfoProvider.pkgContentUriFromPath(licenseFilePath);
        LOG.debug("LOCAL LICENSE: " + licenseFilePath);
      }
    }
    return adjustedLicenseFilePath;
  }

}
