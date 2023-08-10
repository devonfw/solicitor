package com.devonfw.tools.solicitor.componentinfo.scancode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.content.web.DirectUrlWebContentProvider;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO ohecker This type ...
 *
 */
@Component
public class ComponentInfoCurator {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentInfoCurator.class);

  private DirectUrlWebContentProvider contentProvider;

  private CurationProvider curationProvider;

  /**
   * The constructor.
   */
  @Autowired
  public ComponentInfoCurator(CurationProvider curationProvider, DirectUrlWebContentProvider contentProvider) {

    this.curationProvider = curationProvider;
    this.contentProvider = contentProvider;
  }

  /**
   * Checks for the existence of curations for the given package in the local file system and applies them to the
   * component scancode infos.
   *
   * @param packageUrl the identifier of the package
   * @param componentScancodeInfos the componentScancodeInfos to curate
   * @throws ComponentInfoAdapterException if the curations could not be read
   */
  public void applyCurations(String packageUrl, ScancodeComponentInfo componentScancodeInfos)
      throws ComponentInfoAdapterException {

    JsonNode foundCurations = this.curationProvider.findCurations(packageUrl);
    if (foundCurations != null) {
      applyFoundCurations(componentScancodeInfos, foundCurations);
    }

  }

  /**
   * @param packageUrl
   * @return
   * @throws ComponentInfoAdapterException
   */
  // private JsonNode findCurations(String packageUrl) throws ComponentInfoAdapterException {
  //
  // JsonNode foundCurations = null;
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
  // foundCurations = curations;
  // break;
  // }
  // }
  //
  // } catch (IOException e) {
  // throw new ComponentInfoAdapterException("Could not read Curations JSON", e);
  // }
  //
  // }
  // return foundCurations;
  // }

  /**
   * @param componentScancodeInfos
   * @param curations
   */
  private void applyFoundCurations(ScancodeComponentInfo componentScancodeInfos, JsonNode curations) {

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
