/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;

/**
 * A factory for determining the appropriate {@link Writer} for a template type.
 */
@Component
public class WriterFactory {

  @Autowired
  private Writer[] writers;

  /**
   * Find the right writer for the given type
   * 
   * @param type the type of template
   * @return the appropriate {@link Writer}
   * @throws SolicitorRuntimeException if no {@link Writer} could be found for the given type
   */
  public Writer writerFor(String type) {

    for (Writer writer : writers) {
      if (writer.accept(type)) {
        return writer;
      }
    }
    throw new SolicitorRuntimeException("No Writer defined for type '" + type + "'");
  }

}
