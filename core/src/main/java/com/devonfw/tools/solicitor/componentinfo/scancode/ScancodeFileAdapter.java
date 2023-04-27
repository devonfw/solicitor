package com.devonfw.tools.solicitor.componentinfo.scancode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.content.web.DirectUrlWebContentProvider;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapter;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Adapter for reading Scancode information for a package from a file, applying any given curations and returning the
 * information as a {@link ComponentInfo} object.
 */
@Component
@Order(ComponentInfoAdapter.DEFAULT_PRIO)
public class ScancodeFileAdapter implements ComponentInfoAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ScancodeFileAdapter.class);

  private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  private String repoBasePath;

  private String curationsFileName;

  private double minLicenseScore;

  private int minLicensefileNumberOfLines;

  private boolean curationsExistenceLogged;

  private boolean featureFlag = false;

  private boolean featureLogged = false;

  private double licenseToTextRatioToTakeCompleteFile = 90;

  @Autowired
  private AllKindsPackageURLHandler packageURLHandler;

  @Autowired
  private DirectUrlWebContentProvider contentProvider;

  /**
   * The constructor.
   */

  public ScancodeFileAdapter() {

  }

  /**
   * Sets the feature flag for activating/deactivating this feature.
   *
   * @param featureFlag the flag
   */
  @Value("${solicitor.feature-flag.scancode}")
  public void setFeatureFlag(boolean featureFlag) {

    this.featureFlag = featureFlag;
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
   * Sets curationsFileName.
   *
   * @param curationsFileName new value of curationsFileName.
   */
  @Value("${solicitor.scancode.curations-filename}")
  public void setCurationsFileName(String curationsFileName) {

    this.curationsFileName = curationsFileName;
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
   * Retrieves the Scancode information and curations for a package identified by the given package URL. Returns the
   * data as a {@link ScancodeComponentInfo} object.
   *
   * @param packageUrl The identifier of the package for which information is requested
   * @return the data derived from the scancode results after applying any defined curations. <code>null</code> is
   *         returned if no data is available,
   * @throws ComponentInfoAdapterException if there was an exception when reading the data. In case that there is no
   *         data available no exception will be thrown. Instead <code>null</code> will be return in such a case.
   */
  @Override
  public ComponentInfo getComponentInfo(String packageUrl) throws ComponentInfoAdapterException {

    if (isFeatureActive()) {

      ScancodeComponentInfo componentScancodeInfos = determineScancodeInformation(packageUrl);
      if (componentScancodeInfos == null) {
        return null;
      }
      applyCurations(packageUrl, componentScancodeInfos);

      return componentScancodeInfos;

    } else {
      return null;
    }

  }

  private boolean isFeatureActive() {

    if (!this.featureLogged) {
      if (this.featureFlag) {
        LOG.warn(LogMessages.SCANCODE_PROCESSOR_STARTING.msg());
      } else {
        LOG.info(LogMessages.SCANCODE_FEATURE_DEACTIVATED.msg());
      }
      this.featureLogged = true;
    }
    return this.featureFlag;
  }

  /**
   * Read scancode information for the given package from local file storage.
   *
   * @param packageUrl The package url of the package
   * @return the read scancode information, <code>null</code> if no information was found
   * @throws ComponentInfoAdapterException if there was an exception when reading the data. In case that there is no
   *         data available no exception will be thrown. Instead <code>null</code> will be return in such a case.
   */
  private ScancodeComponentInfo determineScancodeInformation(String packageUrl) throws ComponentInfoAdapterException {

    ScancodeComponentInfo componentScancodeInfos = new ScancodeComponentInfo(this.minLicenseScore,
        this.minLicensefileNumberOfLines);
    String packagePathPart = this.packageURLHandler.pathFor(packageUrl);
    String path = this.repoBasePath + "/" + packagePathPart + "/scancode.json";

    File scanCodeFile = new File(path);
    if (!scanCodeFile.exists()) {
      LOG.debug("No Scancode info available for PackageURL '{}'", packageUrl);
      return null;
    }
    LOG.debug("Found Scancode info for PackageURL '{}'", packageUrl);
    try (InputStream is = new FileInputStream(scanCodeFile)) {

      JsonNode scancodeJson = mapper.readTree(is);

      for (JsonNode file : scancodeJson.get("files")) {
        if ("directory".equals(file.get("type").asText())) {
          continue;
        }
        if (file.get("path").asText().contains("/NOTICE")) {
          componentScancodeInfos.addNoticeFilePath("file:" + this.repoBasePath + "/"
              + this.packageURLHandler.pathFor(packageUrl) + "/" + file.get("path").asText(), 100.0);
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
          if (licenseFilePath != null && licenseFilePath.startsWith("file:")) {
            givenLicenseText = this.contentProvider.getContentForUri(licenseFilePath).getContent();
          }

          componentScancodeInfos.addLicense(licenseid, licenseName, licenseDefaultUrl, score, licenseFilePath,
              givenLicenseText, endLine - startLine);
        }
      }
      // if (componentScancodeInfos.getLicenses().size() == 0) {
      // componentScancodeInfos.addLicense("unknown", "unknown", "", 100.0, "", "", 0);
      // }
      if (componentScancodeInfos.getNoticeFilePath() != null
          && componentScancodeInfos.getNoticeFilePath().startsWith("file:")) {
        componentScancodeInfos.setNoticeFileContent(
            this.contentProvider.getContentForUri(componentScancodeInfos.getNoticeFilePath()).getContent());
      }
      LOG.debug("Scancode info for package {}: {} license, {} copyrights, {} NOTICE files", packageUrl,
          componentScancodeInfos.getLicenses().size(), componentScancodeInfos.getCopyrights().size(),
          componentScancodeInfos.getNoticeFilePath() != null ? 1 : 0);

    } catch (IOException e) {
      throw new ComponentInfoAdapterException("Could not read Scancode JSON", e);
    }
    return componentScancodeInfos;
  }

  /**
   * Checks for the existence of curations for the given package in the local file system and applies them to the
   * component scancode infos.
   *
   * @param packageUrl the identifier of the package
   * @param componentScancodeInfos the componentScancodeInfos to curate
   * @throws ComponentInfoAdapterException if the curations could not be read
   */
  private void applyCurations(String packageUrl, ScancodeComponentInfo componentScancodeInfos)
      throws ComponentInfoAdapterException {

    String packagePathPart = this.packageURLHandler.pathFor(packageUrl);

    File curationsFile = new File(this.curationsFileName);
    if (!curationsFile.exists()) {
      if (!this.curationsExistenceLogged) {
        // log only once
        this.curationsExistenceLogged = true;
        LOG.info(LogMessages.CURATIONS_NOT_EXISTING.msg(), this.curationsFileName);
      }
    } else {
      if (!this.curationsExistenceLogged) {
        // log only once
        this.curationsExistenceLogged = true;
        LOG.info(LogMessages.CURATIONS_PROCESSING.msg(), this.curationsFileName);
      }
      try (InputStream isc = new FileInputStream(this.curationsFileName)) {

        JsonNode curationsObj = yamlMapper.readTree(isc);

        for (JsonNode curations : curationsObj.get("artifacts")) {
          String component = curations.get("name").asText();
          if (component.equals(packagePathPart)) {
            ScancodeComponentInfo oneComponent = componentScancodeInfos;
            if (curations.get("copyrights") != null) {
              oneComponent.clearCopyrights();
              int authorCount = curations.get("copyrights").size();
              for (int i = 0; i < authorCount; i++) {
                oneComponent.addCopyright(curations.get("copyrights").get(i).asText());
              }
            }
            if (curations.get("url") != null) {
              String url = curations.get("url").asText();
              oneComponent.setSourceRepoUrl(url);
            }
            if (curations.get("licenses") != null) {
              oneComponent.clearLicenses();
              for (JsonNode licenseNode : curations.get("licenses")) {
                String license = licenseNode.get("license").asText();
                String url = licenseNode.get("url").asText();
                String givenLicenseText = null;
                if (url != null && url.startsWith("file:")) {
                  givenLicenseText = this.contentProvider.getContentForUri(url).getContent();
                }

                oneComponent.addLicense(license, license, "", 110, url, givenLicenseText, Integer.MAX_VALUE);
              }
            }
          }
        }

      } catch (IOException e) {
        throw new ComponentInfoAdapterException("Could not read Curations JSON", e);
      }

    }

  }

  /**
   * Adjustment of license paths/urls so that they might retrieved
   *
   * @param packageUrl package url of the package
   * @param licenseFilePath the original path or URL
   * @return the adjusted path or url as a url
   */
  private String normalizeLicenseUrl(String packageUrl, String licenseFilePath) {

    String adjustedLicenseFilePath;
    if (licenseFilePath != null) {
      if (licenseFilePath.startsWith("http")) {
        // TODO
        adjustedLicenseFilePath = licenseFilePath.replace(
            "https://github.com/nexB/scancode-toolkit/tree/develop/src/licensedcode/data/licenses",
            "https://scancode-licensedb.aboutcode.org");
        adjustedLicenseFilePath = adjustedLicenseFilePath.replace("github.com", "raw.github.com").replace("/tree", "");
      } else {
        adjustedLicenseFilePath = "file:" + this.repoBasePath + "/" + this.packageURLHandler.pathFor(packageUrl) + "/"
            + licenseFilePath;
        LOG.debug("LOCAL LICENSE: " + licenseFilePath);
      }
    } else {
      adjustedLicenseFilePath = null;
      // licenseFilePath = null;// "??????";// defaultGithubLicenseURL(repo);
      LOG.debug("NONLOCAL LICENSE: {} (was: {})" + adjustedLicenseFilePath, licenseFilePath);
    }
    return adjustedLicenseFilePath;
  }

}
