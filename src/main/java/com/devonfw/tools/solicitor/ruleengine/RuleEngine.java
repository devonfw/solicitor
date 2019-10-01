/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.ruleengine;

import com.devonfw.tools.solicitor.model.ModelRoot;

public interface RuleEngine {

    public void executeRules(ModelRoot modelRoot);

}
