/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.writer;

import com.devonfw.tools.solicitor.model.ModelRoot;

/**
 * A facade for encapsulating the report processing with the {@link Writer}s.
 */
public interface WriterFacade {

  /**
   * Write the result of processing to the configured reports.
   * 
   * @param modelRoot the model representing the result of processing
   * @param oldModelRoot an optional old model loaded from the filesystem to which the current result will be compared
   *        to; might be <code>null</code>
   */
  void writeResult(ModelRoot modelRoot, ModelRoot oldModelRoot);

}
