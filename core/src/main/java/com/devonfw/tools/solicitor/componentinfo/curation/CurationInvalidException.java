package com.devonfw.tools.solicitor.componentinfo.curation;

/**
 * Exception which indicates that curation data is invalid.
 */
public class CurationInvalidException extends Exception {

  /**
   * The constructor.
   *
   * @param message the message
   */
  public CurationInvalidException(String message) {

    super(message);
  }

  /**
   * The constructor.
   *
   * @param cause the underlying cause
   */
  public CurationInvalidException(Throwable cause) {

    super("Curation data is invalid", cause);
  }

}