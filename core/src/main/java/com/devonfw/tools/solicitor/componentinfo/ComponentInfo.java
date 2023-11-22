package com.devonfw.tools.solicitor.componentinfo;

import java.util.List;

/**
 * Data structure which encapsulates the information about a component which comes from an external data source, like the results
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
   * Indicates if detail data is available.
   *
   * @return <code>true</code> if data is available, <code>false</code> otherwise.
   */
  boolean isDataAvailable();

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
   * Gets the data on the component. Only available if {@link #isDataAvailable()} returns <code>true</code>.
   *
   * @return the detailed info on the component.
   */
  ComponentInfoData getComponentInfoData();

}