package com.devonfw.tools.solicitor.common.packageurl;

/**
 * Specific kind of {@link SolicitorPackageURLException} thrown from
 * {@link com.devonfw.tools.solicitor.common.packageurl} and sub packages.
 */
public class SolicitorPackageURLTypException extends SolicitorPackageURLException {

  /**
   * The constructor.
   *
   * @param message the message
   */
  public SolicitorPackageURLTypException(String message) {

    super(message);
  }

  /**
   * The constructor.
   *
   * @param message the message
   * @param cause the cause
   */
  public SolicitorPackageURLTypException(String message, Throwable cause) {

    super(message, cause);
  }
}
