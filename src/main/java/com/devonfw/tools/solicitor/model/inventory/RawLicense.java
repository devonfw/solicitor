/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.inventory;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RawLicense extends AbstractDataRowSource implements DataRowSource {

    private String declaredLicense;

    private String licenseUrl;

    private boolean specialHandling;

    private ApplicationComponent applicationComponent;

    public void setApplicationComponent(
            ApplicationComponent applicationComponent) {

        if (this.applicationComponent != null) {
            throw new IllegalStateException(
                    "Once the ApplicationComponent is set it can not be changed");
        }
        this.applicationComponent = applicationComponent;
        applicationComponent.getRawLicenses().add(this);
    }

    @Override
    public String[] getHeadElements() {

        return new String[] { "declaredLicense", "licenseUrl" };
    }

    @Override
    public String[] getDataElements() {

        return new String[] { declaredLicense, licenseUrl };
    }

    @Override
    public AbstractDataRowSource getParent() {

        return applicationComponent;
    }

}
