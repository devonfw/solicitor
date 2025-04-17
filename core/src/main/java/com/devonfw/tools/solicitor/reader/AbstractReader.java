/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.reader;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.github.packageurl.PackageURL;

/**
 * Abstract base functionality of a {@link com.devonfw.tools.solicitor.reader.Reader}.
 */
public abstract class AbstractReader implements Reader {

  /**
   * A simple data structure for holding statistical runtime information.
   */
  public static class ReaderStatistics {
    /**
     * number of total read components
     */
    public int readComponentCount = 0;

    /**
     * number of components which were filtered out (not included in the data model)
     */
    public int filteredComponentCount = 0;

    /**
     * number of read licenses
     */
    public int licenseCount = 0;
  }

  private static final Logger LOG = LoggerFactory.getLogger(AbstractReader.class);

  /**
   * The name of the configuration parameter which defines the (optional) include filter.
   */
  private static final String INCLUDE_FILTER_PARAMETER_NAME = "includeFilter";

  /**
   * The name of the configuration parameter which defines the (optional) exclude filter.
   */
  private static final String EXCLUDE_FILTER_PARAMETER_NAME = "excludeFilter";

  private ModelFactory modelFactory;

  @Autowired
  protected InputStreamFactory inputStreamFactory;

  /** {@inheritDoc} */
  @Override
  public boolean accept(String type) {

    return getSupportedTypes().contains(type);
  }

  /**
   * Performs logging. Log message differs depending on whether filters are defined or not.
   *
   * @param configuration the map with reader configuration parameters, might be <code>null</code>.
   * @param sourceUrl the URL from where the inventory data was read
   * @param application the application
   * @param statistics object with statistical information on the read data
   */
  protected void doLogging(Map<String, String> configuration, String sourceUrl, Application application,
      ReaderStatistics statistics) {

    boolean filterIsDefined = (configuration != null) && ( //
    configuration.get(INCLUDE_FILTER_PARAMETER_NAME) != null || //
        configuration.get(EXCLUDE_FILTER_PARAMETER_NAME) != null //
    );
    if (filterIsDefined) {
      LOG.info(LogMessages.READING_INVENTORY_WITH_FILTER.msg(),
          statistics.readComponentCount - statistics.filteredComponentCount, statistics.licenseCount,
          application.getName(), sourceUrl, statistics.readComponentCount, statistics.filteredComponentCount);
    } else {
      LOG.info(LogMessages.READING_INVENTORY.msg(), statistics.readComponentCount, statistics.licenseCount,
          application.getName(), sourceUrl);
    }
  }

  /**
   * Checks if the package url is filtered via include or exclude filters.
   *
   * @param purl the package url to filter
   * @param configuration the map of configuration parameters of the reader
   * @return <code>true</code> if the package url is filtered (i.e. should be suppressed), <code>false</code> otherwise
   */
  protected boolean isPackageFiltered(PackageURL purl, Map<String, String> configuration) {

    if (configuration == null) {
      return false;
    }

    String includePattern = configuration.get(INCLUDE_FILTER_PARAMETER_NAME);
    String excludePattern = configuration.get(EXCLUDE_FILTER_PARAMETER_NAME);

    if (includePattern != null && !isPurlMatchingPattern(purl, includePattern)) {
      // if includeFilter is defined then filter out if purl does not match the filter pattern
      LOG.debug("PackageURL '{}' is not matching includeFilter pattern '{}' and will be filtered out", purl,
          includePattern);
      return true;
    }
    if (excludePattern != null && isPurlMatchingPattern(purl, excludePattern)) {
      // if excludeFilter is defined then filter out if purl does match the filter pattern
      LOG.debug("PackageURL '{}' is matching excludeFilter pattern '{}' and will be filtered out", purl,
          excludePattern);
      return true;
    }
    return false;
  }

  /**
   * Checks if the given Package URL matches the given pattern.
   *
   * @param purl the Package URL to check
   * @param patternString the regular expression to be checked
   * @return
   */
  private boolean isPurlMatchingPattern(PackageURL purl, String patternString) {

    if (patternString == null) {
      throw new NullPointerException("Filter Pattern is undefined");
    }
    return Pattern.matches(patternString, purl.toString());
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
  protected void addRawLicense(ApplicationComponent appComponent, String name, String url, String path) {

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
  protected ModelFactory getModelFactory() {

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

  /**
   * Checks the include/exclude filter and potentially adds the appComponent to the application.
   *
   * @param application the application
   * @param appComponent the appcomponent
   * @param configuration the configuration (containing the optional filters)
   * @param statistics the statistics data structure
   * @return <code>true</code> if the appComponent was added to the application, <code>false</code> if it was not added
   *         due to filtering.
   */
  public boolean addComponentToApplicationIfNotFiltered(Application application, ApplicationComponent appComponent,
      Map<String, String> configuration, ReaderStatistics statistics) {

    if (appComponent.getPackageUrl() != null && isPackageFiltered(appComponent.getPackageUrl(), configuration)) {
      // skip this component as it is filtered out
      statistics.filteredComponentCount++;
      return false;
    }
    appComponent.setApplication(application);
    return true;
  }

}
