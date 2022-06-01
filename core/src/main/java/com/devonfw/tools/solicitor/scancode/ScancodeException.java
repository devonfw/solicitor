package com.devonfw.tools.solicitor.scancode;

/**
 * Exception indicating that the Scancode information could not be read.
 */
public class ScancodeException extends Exception {

  /**
   * A constructor.
   */
  public ScancodeException() {

  }

  /**
   * A constructor.
   *
   * @param message the message of the exception
   */
  public ScancodeException(String message) {

    super(message);
  }

  /**
   * A constructor.
   *
   * @param message the message of the exception
   * @param cause the underlying {@link Throwable}.
   */
  public ScancodeException(String message, Throwable cause) {

    super(message, cause);
  }

}
