/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.ruleengine.drools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;

/**
 * A factory for getting the appropriate {@link DroolsRulesReader} for a given rule type.
 */
@Component
public class DroolsRulesReaderFactory {

  @Autowired
  private DroolsRulesReader[] ruleReaders;

  /**
   * Returns the appropriate {@link DroolsRulesReader} for the given rule type.
   *
   * @param type the name of the type
   * @return the right {@link DroolsRulesReader}
   * @throws SolicitorRuntimeException if no appropriate {@link DroolsRulesReader} is defined
   * 
   */
  public DroolsRulesReader readerFor(String type) {

    for (DroolsRulesReader reader : ruleReaders) {
      if (reader.accept(type)) {
        return reader;
      }
    }
    throw new SolicitorRuntimeException("No Reader defined for type '" + type + "'");
  }

}
