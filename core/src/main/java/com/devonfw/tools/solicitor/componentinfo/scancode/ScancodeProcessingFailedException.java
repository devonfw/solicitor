package com.devonfw.tools.solicitor.componentinfo.scancode;

/**
 * Exception indicating that the {@link ScancodeRawComponentInfoProvider} could not provide any data due to previous
 * failures in (asynchronous) downloading or scanning of the package data.
 */
public class ScancodeProcessingFailedException extends Exception {

  /**
   * A constructor.
   */
  public ScancodeProcessingFailedException() {

  }

  /**
   * A constructor.
   *
   * @param message the message of the exception
   */
  public ScancodeProcessingFailedException(String message) {

    super(message);
  }

  /**
   * A constructor.
   *
   * @param message the message of the exception
   * @param cause the underlying {@link Throwable}.
   */
  public ScancodeProcessingFailedException(String message, Throwable cause) {

    super(message, cause);
  }

}
