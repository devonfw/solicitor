package com.devonfw.tools.solicitor.common.packageurl;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;

/**
 * Specific kind of {@link SolicitorRuntimeException} thrown from {@link com.devonfw.tools.solicitor.common.packageurl}
 * and sub packages.
 */
public class SolicitorPackageURLException extends SolicitorRuntimeException {

  /**
   * The constructor.
   *
   * @param message the message
   */
  public SolicitorPackageURLException(String message) {

    super(message);
  }

  /**
   * The constructor.
   *
   * @param message the message
   * @param cause the cause
   */
  public SolicitorPackageURLException(String message, Throwable cause) {

    super(message, cause);
  }
}
