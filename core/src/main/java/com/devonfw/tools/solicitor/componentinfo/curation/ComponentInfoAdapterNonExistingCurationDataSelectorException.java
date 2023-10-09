package com.devonfw.tools.solicitor.componentinfo.curation;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;

/**
 * This exception is thrown when an attempt is made to access a non-existing curation data selector when trying to
 * retrieve curations.
 *
 */
public class ComponentInfoAdapterNonExistingCurationDataSelectorException extends ComponentInfoAdapterException {

  /**
   * Constructs a new ComponentInfoAdapterNonExistingCurationDataSelectorException with the specified detail message.
   *
   * @param message the detail message (which is saved for later retrieval by the getMessage() method).
   */
  public ComponentInfoAdapterNonExistingCurationDataSelectorException(String message) {

    super(message);
  }

  /**
   * Constructs a new ComponentInfoAdapterNonExistingCurationDataSelectorException with the specified detail message and
   * a nested Throwable cause.
   *
   * @param message the detail message (which is saved for later retrieval by the getMessage() method).
   * @param cause the nested cause Throwable (which is saved for later retrieval by the getCause() method).
   */
  public ComponentInfoAdapterNonExistingCurationDataSelectorException(String message, Throwable cause) {

    super(message, cause);
  }
}