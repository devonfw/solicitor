package com.devonfw.tools.solicitor.componentinfo;

/**
 * License information within the {@link ComponentInfo} data structure.
 *
 */
public interface LicenseInfo {

  /**
   * @return spdxid
   */
  String getSpdxid();

  /**
   * @return licenseFilePath
   */
  String getLicenseFilePath();

}