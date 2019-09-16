/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.inventory;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RawLicense extends AbstractDataRowSource implements DataRowSource {

    @JsonProperty
    private String declaredLicense;

    @JsonProperty
    private String licenseUrl;

    @JsonProperty
    private String trace;

    @JsonProperty
    private boolean specialHandling;

    @JsonIgnore
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

        return new String[] { "declaredLicense", "licenseUrl", "trace" };
    }

    @Override
    public String[] getDataElements() {

        return new String[] { declaredLicense, licenseUrl, trace };
    }

    @Override
    public AbstractDataRowSource getParent() {

        return applicationComponent;
    }

}
