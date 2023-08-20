package com.devonfw.tools.solicitor.componentinfo;

import java.util.Collection;
import java.util.List;

/**
 * Data structure which holds information about a component which comes from an external data source, like the results
 * of a scancode scan.
 *
 */
public interface ComponentInfo {

  /**
   * Get the packageUrl.
   *
   * @return the packageURL. This is an identifier for this object.
   */
  String getPackageUrl();

  /**
   * Gets all copyrights.
   *
   * @return the copyrights
   */
  Collection<String> getCopyrights();

  /**
   * Gets all licenses.
   *
   * @return all licenses
   */
  Collection<? extends LicenseInfo> getLicenses();

  /**
   * Gets the url to the notice file (if any)
   *
   * @return url of the notice file
   */
  String getNoticeFileUrl();

  /**
   * Gets the contents of the notice file (if any)
   *
   * @return contents of the notice file
   */
  String getNoticeFileContent();

  /**
   * Gets the url of the projects homepage,
   *
   * @return url to the projects homepage
   */
  String getHomepageUrl();

  /**
   * Gets the url of the source code repository.
   *
   * @return url to source code repository
   */
  String getSourceRepoUrl();

  /**
   * Gets the url for downloading the package/component.
   *
   * @return url to download the package
   */
  String getPackageDownloadUrl();

  /**
   * Gets the url for downloading the sources of the package/component.
   *
   * @return url to download the sources of the package/component
   */
  String getSourceDownloadUrl();

  /**
   * This method gets the field <code>dataStatus</code>.
   *
   * @return the field dataStatus
   */
  String getDataStatus();

  /**
   * Gets the traceability notes of the component.
   *
   * @return the traceability notes
   */
  List<String> getTraceabilityNotes();
}