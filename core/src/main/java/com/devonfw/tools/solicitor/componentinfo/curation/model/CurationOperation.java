package com.devonfw.tools.solicitor.componentinfo.curation.model;

/**
 * The curation operation to be performed.
 */
public enum CurationOperation {
  /**
   * Remove / Ignore a license or copyright finding.
   */
  REMOVE,
  /**
   * Replace a license or copyright finding with a different one.
   */
  REPLACE,
  /**
   * Add a license or copyright.
   */
  ADD

}
