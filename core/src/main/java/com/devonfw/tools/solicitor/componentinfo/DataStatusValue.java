package com.devonfw.tools.solicitor.componentinfo;

/**
 * Constants for data status values within Solicitor.
 */
public class DataStatusValue {

  /**
   * No data available. Functionality is disabled.
   */
  public static final String DISABLED = "DISABLED";

  /**
   * No data available. No scan results existing and no indication that attempting that download/scanning has failed.
   */
  public static final String NOT_AVAILABLE = "NOT_AVAILABLE";

  /**
   * No data available. No scan results existing. Processing (downloading or scanning) had failed.
   */
  public static final String PROCESSING_FAILED = "PROCESSING_FAILED";

  /**
   * Data available. Issues were detected in the data which prabably need to be curated.
   */
  public static final String WITH_ISSUES = "WITH_ISSUES";

  /**
   * Data available. No curations applied. No issues were detected.
   */
  public static final String NO_ISSUES = "NO_ISSUES";

  /**
   * Data available. Curations were applied. No issues were detected.
   */
  public static final String CURATED = "CURATED";

  /**
   * Private constructor to prevent instantiation.
   */
  private DataStatusValue() {

  }

}
