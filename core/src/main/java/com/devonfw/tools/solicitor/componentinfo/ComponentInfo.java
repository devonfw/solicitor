package com.devonfw.tools.solicitor.componentinfo;

import java.util.List;

import com.github.packageurl.PackageURL;

/**
 * Data structure which encapsulates the information about a component which comes from an external data source, like
 * the results of a scancode scan.
 *
 */
public interface ComponentInfo {

  /**
   * Get the packageUrl.
   *
   * @return the packageURL. This is an identifier for this object.
   */
  PackageURL getPackageUrl();

  /**
   * This method gets the field <code>dataStatus</code>. If no data is available this indicates why there is no data
   * available.
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

  /**
   * Gets the data on the component.
   *
   * @return the detailed info on the component. In case that no data is available (e.g. because there is no scancode
   *         result available) this method will return <code>null</code>.
   */
  ComponentInfoData getComponentInfoData();

}