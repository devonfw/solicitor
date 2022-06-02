/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.reader;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;

/**
 * Abstract base functionality of a {@link com.devonfw.tools.solicitor.reader.Reader}.
 */
public abstract class AbstractReader implements Reader {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractReader.class);

  private ModelFactory modelFactory;

  @Autowired
  protected InputStreamFactory inputStreamFactory;

  /** {@inheritDoc} */
  @Override
  public boolean accept(String type) {

    return getSupportedTypes().contains(type);
  }

  /**
   * Performs logging.
   *
   * @param sourceUrl the URL from where the inventory data was read
   * @param application the application
   * @param readComponents number of read ApplicationComponents
   * @param readLicenses number of read RawLicenses
   */
  public void doLogging(String sourceUrl, Application application, int readComponents, int readLicenses) {

    LOG.info(LogMessages.READING_INVENTORY.msg(), readComponents, readLicenses, application.getName(), sourceUrl);
  }

  /**
   * Adds a {@link com.devonfw.tools.solicitor.model.inventory.RawLicense} to the given
   * {@link com.devonfw.tools.solicitor.model.inventory.ApplicationComponent}.
   *
   * @param appComponent a {@link com.devonfw.tools.solicitor.model.inventory.ApplicationComponent} object.
   * @param name a {@link java.lang.String} object.
   * @param url a {@link java.lang.String} object.
   * @param path a {@link java.lang.String} object.
   */
  public void addRawLicense(ApplicationComponent appComponent, String name, String url, String path) {

    RawLicense mlic = this.modelFactory.newRawLicense();
    mlic.setApplicationComponent(appComponent);
    mlic.setDeclaredLicense(name);
    mlic.setLicenseUrl(url);
    mlic.setOrigin(this.getClass().getSimpleName().toLowerCase());
    String trace;
    if (name == null && url == null) {
      trace = "+ Component info (without license) read in '" + getSupportedTypes() + "' format from '" + path + "'";
    } else {
      trace = "+ Component/License info read in '" + getSupportedTypes() + "' format from '" + path + "'";

    }
    mlic.setTrace(trace);
  }

  /**
   * This method gets the field <code>modelFactory</code>.
   *
   * @return the field modelFactory
   */
  public ModelFactory getModelFactory() {

    return this.modelFactory;
  }

  /**
   * Returns the supported types. Concrete {@link Reader}s need to override this to specify which types they are able to
   * handle.
   *
   * @return the supported type
   */
  public abstract Set<String> getSupportedTypes();

  /**
   * This method sets the field <code>inputStreamFactory</code>.
   *
   * @param inputStreamFactory the new value of the field inputStreamFactory
   */
  public void setInputStreamFactory(InputStreamFactory inputStreamFactory) {

    this.inputStreamFactory = inputStreamFactory;
  }

  /**
   * This method sets the field <code>modelFactory</code>.
   *
   * @param modelFactory the new value of the field modelFactory
   */
  @Autowired
  public void setModelFactory(ModelFactory modelFactory) {

    this.modelFactory = modelFactory;
  }

}
