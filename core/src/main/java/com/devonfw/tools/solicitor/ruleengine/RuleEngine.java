/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.ruleengine;

import com.devonfw.tools.solicitor.model.ModelRoot;

/**
 * Interface of a rule engine which works on the data of the Solicitor data model.
 */
public interface RuleEngine {

  /**
   * Executes all rules.
   *
   * @param modelRoot the {@link ModelRoot} which is the root objects of the data model to be processed.
   */
  public void executeRules(ModelRoot modelRoot);

}
