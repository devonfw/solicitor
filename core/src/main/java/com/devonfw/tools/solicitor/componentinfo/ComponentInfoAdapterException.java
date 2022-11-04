package com.devonfw.tools.solicitor.componentinfo;

/**
 * Exception indicating that the {@link ComponentInfoAdapter} had a problem reading data. This should not be used if
 * there is simply no data available for the given component but only if there was either a general problem reading data
 * or with reading the (existing) specific data for the component.
 */
public class ComponentInfoAdapterException extends Exception {

  /**
   * A constructor.
   */
  public ComponentInfoAdapterException() {

  }

  /**
   * A constructor.
   *
   * @param message the message of the exception
   */
  public ComponentInfoAdapterException(String message) {

    super(message);
  }

  /**
   * A constructor.
   *
   * @param message the message of the exception
   * @param cause the underlying {@link Throwable}.
   */
  public ComponentInfoAdapterException(String message, Throwable cause) {

    super(message, cause);
  }

}
