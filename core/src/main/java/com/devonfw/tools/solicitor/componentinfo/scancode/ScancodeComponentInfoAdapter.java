package com.devonfw.tools.solicitor.componentinfo.scancode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapter;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;

/**
 * Adapter for reading Scancode information for a package from a file, applying any given curations and returning the
 * information as a {@link ComponentInfo} object.
 */
@Component
@Order(ComponentInfoAdapter.DEFAULT_PRIO)
public class ScancodeComponentInfoAdapter implements ComponentInfoAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ScancodeComponentInfoAdapter.class);

  private boolean featureFlag = false;

  private boolean featureLogged = false;

  private ScancodeComponentInfoMapper scancodeComponentInfoMapper;

  private ComponentInfoCurator componentInfoCurator;

  /**
   * The constructor.
   */
  @Autowired
  public ScancodeComponentInfoAdapter(ScancodeComponentInfoMapper scancodeComponentInfoMapper,
      ComponentInfoCurator componentInfoCurator) {

    this.scancodeComponentInfoMapper = scancodeComponentInfoMapper;
    this.componentInfoCurator = componentInfoCurator;
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

      ScancodeComponentInfo componentScancodeInfos = this.scancodeComponentInfoMapper
          .determineScancodeInformation(packageUrl);
      if (componentScancodeInfos == null) {
        return null;
      }
      this.componentInfoCurator.applyCurations(packageUrl, componentScancodeInfos);

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
   * Checks for the existence of curations for the given package in the local file system and applies them to the
   * component scancode infos.
   *
   * @param packageUrl the identifier of the package
   * @param componentScancodeInfos the componentScancodeInfos to curate
   * @throws ComponentInfoAdapterException if the curations could not be read
   */
  // private void applyCurations(String packageUrl, ScancodeComponentInfo componentScancodeInfos)
  // throws ComponentInfoAdapterException {
  //
  // String packagePathPart = this.packageURLHandler.pathFor(packageUrl);
  //
  // File curationsFile = new File(this.curationsFileName);
  // if (!curationsFile.exists()) {
  // if (!this.curationsExistenceLogged) {
  // // log only once
  // this.curationsExistenceLogged = true;
  // LOG.info(LogMessages.CURATIONS_NOT_EXISTING.msg(), this.curationsFileName);
  // }
  // } else {
  // if (!this.curationsExistenceLogged) {
  // // log only once
  // this.curationsExistenceLogged = true;
  // LOG.info(LogMessages.CURATIONS_PROCESSING.msg(), this.curationsFileName);
  // }
  // try (InputStream isc = new FileInputStream(this.curationsFileName)) {
  //
  // JsonNode curationsObj = yamlMapper.readTree(isc);
  //
  // for (JsonNode curations : curationsObj.get("artifacts")) {
  // String component = curations.get("name").asText();
  // if (component.equals(packagePathPart)) {
  // ScancodeComponentInfo oneComponent = componentScancodeInfos;
  // if (curations.get("copyrights") != null) {
  // oneComponent.clearCopyrights();
  // int authorCount = curations.get("copyrights").size();
  // for (int i = 0; i < authorCount; i++) {
  // oneComponent.addCopyright(curations.get("copyrights").get(i).asText());
  // }
  // }
  // if (curations.get("url") != null) {
  // String url = curations.get("url").asText();
  // oneComponent.setSourceRepoUrl(url);
  // }
  // if (curations.get("licenses") != null) {
  // oneComponent.clearLicenses();
  // for (JsonNode licenseNode : curations.get("licenses")) {
  // String license = licenseNode.get("license").asText();
  // String url = licenseNode.get("url").asText();
  // String givenLicenseText = null;
  // if (url != null && url.startsWith("file:")) {
  // givenLicenseText = this.contentProvider.getContentForUri(url).getContent();
  // }
  //
  // oneComponent.addLicense(license, license, "", 110, url, givenLicenseText, Integer.MAX_VALUE);
  // }
  // }
  // }
  // }
  //
  // } catch (IOException e) {
  // throw new ComponentInfoAdapterException("Could not read Curations JSON", e);
  // }
  //
  // }
  //
  // }

  /**
   * Adjustment of license paths/urls so that they might retrieved
   *
   * @param packageUrl package url of the package
   * @param licenseFilePath the original path or URL
   * @return the adjusted path or url as a url
   */
  // private String normalizeLicenseUrl(String packageUrl, String licenseFilePath) {
  //
  // String adjustedLicenseFilePath;
  // if (licenseFilePath != null) {
  // if (licenseFilePath.startsWith("http")) {
  // // TODO
  // adjustedLicenseFilePath = licenseFilePath.replace(
  // "https://github.com/nexB/scancode-toolkit/tree/develop/src/licensedcode/data/licenses",
  // "https://scancode-licensedb.aboutcode.org");
  // adjustedLicenseFilePath = adjustedLicenseFilePath.replace("github.com", "raw.github.com").replace("/tree", "");
  // } else {
  // adjustedLicenseFilePath = "file:" + this.repoBasePath + "/" + this.packageURLHandler.pathFor(packageUrl) + "/"
  // + licenseFilePath;
  // LOG.debug("LOCAL LICENSE: " + licenseFilePath);
  // }
  // } else {
  // adjustedLicenseFilePath = null;
  // // licenseFilePath = null;// "??????";// defaultGithubLicenseURL(repo);
  // LOG.debug("NONLOCAL LICENSE: {} (was: {})" + adjustedLicenseFilePath, licenseFilePath);
  // }
  // return adjustedLicenseFilePath;
  // }

}
