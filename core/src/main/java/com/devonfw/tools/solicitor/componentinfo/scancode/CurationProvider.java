package com.devonfw.tools.solicitor.componentinfo.scancode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * TODO ohecker This type ...
 *
 */
@Component
public class CurationProvider {
  private static final Logger LOG = LoggerFactory.getLogger(CurationProvider.class);

  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  private String curationsFileName;

  private boolean curationsExistenceLogged;

  private AllKindsPackageURLHandler packageURLHandler;

  /**
   * The constructor.
   */
  @Autowired
  public CurationProvider(AllKindsPackageURLHandler packageURLHandler) {

    this.packageURLHandler = packageURLHandler;
  }

  /**
   * @param packageUrl
   * @return
   * @throws ComponentInfoAdapterException
   */
  public JsonNode findCurations(String packageUrl) throws ComponentInfoAdapterException {

    JsonNode foundCurations = null;

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
            foundCurations = curations;
            break;
          }
        }

      } catch (IOException e) {
        throw new ComponentInfoAdapterException("Could not read Curations JSON", e);
      }

    }
    return foundCurations;
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

}
