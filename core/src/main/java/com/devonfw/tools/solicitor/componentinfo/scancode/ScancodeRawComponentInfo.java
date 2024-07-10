package com.devonfw.tools.solicitor.componentinfo.scancode;

/**
 * Contains raw scancode data and any additional data needed to create a ComponentInfo structure.
 *
 */
public class ScancodeRawComponentInfo {

  public String rawScancodeResult;

  public String sourceDownloadUrl;

  public String packageDownloadUrl;

 // New attributes for Scancode version 32.x
  public String detectedLicenseExpressionSpdx;

  public String licenseDetections;

  public String licenses; 

  public String licenseExpressions;  

  public String detectedLicenseExpression;
  
  /**
   * The constructor.
   */
  public ScancodeRawComponentInfo() {

  }

}
