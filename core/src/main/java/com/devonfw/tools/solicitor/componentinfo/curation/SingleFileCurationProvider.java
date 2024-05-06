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
public class SingleFileCurationProvider extends AbstractHierarchicalCurationProvider {
  private static final Logger LOG = LoggerFactory.getLogger(SingleFileCurationProvider.class);

  private static final ObjectMapper yamlMapper;

  static {
    yamlMapper = new ObjectMapper(new YAMLFactory());
    yamlMapper.findAndRegisterModules();
  }

  private String curationsFileName;

  private boolean curationsExistenceLogged;

  /**
   * The constructor.
   *
   * @param packageURLHandler the packageURLHandler for handling packageURLs.
   */
  @Autowired
  public SingleFileCurationProvider(AllKindsPackageURLHandler packageURLHandler) {

    super(packageURLHandler);
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

  @Override
  protected ComponentInfoCuration fetchCurationFromRepository(String effectiveCurationDataSelector,
      String pathFragmentWithinRepo) throws ComponentInfoAdapterException, CurationInvalidException {

    ComponentInfoCuration foundCuration = null;

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
          if (component.equals(pathFragmentWithinRepo)) {
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

  @Override
  protected void validateEffectiveCurationDataSelector(String effectiveCurationDataSelector)
      throws ComponentInfoAdapterNonExistingCurationDataSelectorException {

    // as the curationDataSelector is not supported/used there is nothing to do here
  }

  @Override
  protected String determineEffectiveCurationDataSelector(String curationDataSelector) {

    // actually this value is unused in the class
    return "-";
  }

  @Override
  protected void assureCurationDataSelectorAvailable(String effectiveCurationDataSelector)
      throws ComponentInfoAdapterException, ComponentInfoAdapterNonExistingCurationDataSelectorException {

    // as the curationDataSelector is not supported/used there is nothing to do here

  }

}
