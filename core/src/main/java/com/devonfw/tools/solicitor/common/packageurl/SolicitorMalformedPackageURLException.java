package com.devonfw.tools.solicitor.common.packageurl;

/**
 * Exception which is thrown if a PackageURL could not be parsed..
 */
public class SolicitorMalformedPackageURLException extends Exception {

  /**
   * The constructor.
   *
   * @param message the message
   */
  public SolicitorMalformedPackageURLException(String message) {

    super(message);
  }

  /**
   * The constructor.
   *
   * @param message the message
   * @param cause the cause
   */
  public SolicitorMalformedPackageURLException(String message, Throwable cause) {

    super(message, cause);
  }

}
