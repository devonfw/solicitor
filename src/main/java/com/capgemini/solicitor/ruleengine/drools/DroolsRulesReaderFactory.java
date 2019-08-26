/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.ruleengine.drools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capgemini.solicitor.SolicitorRuntimeException;

@Component
public class DroolsRulesReaderFactory {

    @Autowired
    private DroolsRulesReader[] ruleReaders;

    public DroolsRulesReader readerFor(String type) {

        for (DroolsRulesReader reader : ruleReaders) {
            if (reader.accept(type)) {
                return reader;
            }
        }
        throw new SolicitorRuntimeException(
                "No Reader defined for type '" + type + "'");
    }

}
