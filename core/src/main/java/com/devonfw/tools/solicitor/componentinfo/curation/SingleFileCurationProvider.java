package com.devonfw.tools.solicitor.componentinfo.curation;

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
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.CurationList;
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

  private static final ObjectMapper yamlMapper;

  static {
    yamlMapper = new ObjectMapper(new YAMLFactory());
    yamlMapper.findAndRegisterModules();
  }

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
   * Return the curation data for a given package.
   *
   * @param packageUrl identifies the package
   * @param gitBranch identifies the git branch (optional, default is "main")
   * @return the curation data if it exists or <code>null</code> if no curations exist for the package.
   * @throws ComponentInfoAdapterException if something unexpected happens
   */
  @Override
  public ComponentInfoCuration findCurations(String packageUrl, String gitBranch) throws ComponentInfoAdapterException {

    ComponentInfoCuration foundCuration = null;

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

        CurationList curationList = yamlMapper.readValue(isc, CurationList.class);

        for (ComponentInfoCuration curation : curationList.getArtifacts()) {
          String component = curation.getName();
          if (component.equals(packagePathPart)) {
            foundCuration = curation;
            break;
          }
        }

      } catch (IOException e) {
        throw new ComponentInfoAdapterException("Could not read Curations JSON", e);
      }
    }
    return foundCuration;
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
