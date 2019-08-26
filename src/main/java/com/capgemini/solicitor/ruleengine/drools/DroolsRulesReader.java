/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.ruleengine.drools;

import java.util.Collection;

import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.io.Resource;

public interface DroolsRulesReader {

    public boolean accept(String type);

    public void readRules(String ruleSource, String templateSource,
            String decription, KieBaseModel baseModel,
            Collection<Resource> resources);

}
