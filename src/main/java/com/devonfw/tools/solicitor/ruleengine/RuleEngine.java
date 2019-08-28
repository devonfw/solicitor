/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.ruleengine;

import com.devonfw.tools.solicitor.model.masterdata.Engagement;

public interface RuleEngine {

    public void executeRules(Engagement engagement);

}
