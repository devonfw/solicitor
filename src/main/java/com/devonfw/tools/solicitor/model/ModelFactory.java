/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.webcontent.InMemoryMapWebContentProvider;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;

@Component
public class ModelFactory {

    private static ModelFactory instance;

    @Autowired
    private InMemoryMapWebContentProvider licenseContentProvider;

    public static NormalizedLicense newNormalizedLicense(
            ApplicationComponent applicationComponent, String declaredLicense,
            String licenseUrl, String normalizedLicense) {

        NormalizedLicense result = new NormalizedLicense(applicationComponent,
                declaredLicense, licenseUrl, normalizedLicense);
        result.setLicenseContentProvider(instance.licenseContentProvider);
        return result;
    }

    public ModelFactory() {

        instance = this;
    }

}
