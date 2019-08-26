/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.ruleengine;

import com.capgemini.solicitor.model.masterdata.Engagement;

public interface RuleEngine {

    public void executeRules(Engagement engagement);

}
