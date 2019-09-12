/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.webcontent.InMemoryMapWebContentProvider;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;

@Component
public class ModelFactory {

    private static ModelFactory instance;

    @Autowired
    private InMemoryMapWebContentProvider licenseContentProvider;

    public static NormalizedLicense newNormalizedLicense(
            RawLicense rawLicense) {

        NormalizedLicense result = new NormalizedLicense(rawLicense);
        result.setLicenseContentProvider(instance.licenseContentProvider);
        return result;
    }

    public ModelFactory() {

        instance = this;
    }

}
