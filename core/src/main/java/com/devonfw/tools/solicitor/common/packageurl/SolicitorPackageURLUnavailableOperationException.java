package com.devonfw.tools.solicitor.common.packageurl;

/**
 * Exception which might be thrown by methods of {@link PackageURLHandler}s if the called method is unavailable within
 * the given PackageURLHandler implementation / for the given PackageURL.
 */
public class SolicitorPackageURLUnavailableOperationException extends Exception {

  /**
   * The constructor.
   *
   * @param message the message
   */
  public SolicitorPackageURLUnavailableOperationException(String message) {

    super(message);
  }

}
