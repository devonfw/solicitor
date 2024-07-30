package com.devonfw.tools.solicitor.componentinfo.scancode;

/**
 * Contains raw scancode data and any additional data needed to create a ComponentInfo structure.
 *
 */
public class ScancodeRawComponentInfo {

  public String rawScancodeResult;

  public String sourceDownloadUrl;

  public String packageDownloadUrl;

  /**
   * The constructor.
   */
  public ScancodeRawComponentInfo() {

  }

  /**
   * Constructor to initialize with raw JSON data.
   *
   * @param rawScancodeResult JSON data as a string.
   */
  public ScancodeRawComponentInfo(String rawScancodeResult) {

    this.rawScancodeResult = rawScancodeResult;
  }
}
