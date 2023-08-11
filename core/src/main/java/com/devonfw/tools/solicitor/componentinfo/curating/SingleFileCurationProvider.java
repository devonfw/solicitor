package com.devonfw.tools.solicitor.componentinfo.curating;

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
 * Implementation of the {@link CurationProvider} interface which reads curation data for all packages from a single
 * file.
 *
 */
@Component
public class SingleFileCurationProvider implements CurationProvider {
  private static final Logger LOG = LoggerFactory.getLogger(SingleFileCurationProvider.class);

  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  private String curationsFileName;

  private boolean curationsExistenceLogged;

  private AllKindsPackageURLHandler packageURLHandler;

  /**
   * The constructor.
   *
   * @param packageURLHandler the packageURLHandler for handling packageURLs.
   */
  @Autowired
  public SingleFileCurationProvider(AllKindsPackageURLHandler packageURLHandler) {

    this.packageURLHandler = packageURLHandler;
  }

  /**
   * Return the curation data for a given packe
   *
   * @param packageUrl identifies the package
   * @return the curation data if it existes or <code>null</code> if no curations exist for the package.
   * @throws ComponentInfoAdapterException if something unexpected happens
   */
  @Override
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
