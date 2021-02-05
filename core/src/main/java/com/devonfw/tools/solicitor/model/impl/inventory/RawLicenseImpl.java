/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.impl.inventory;

import com.devonfw.tools.solicitor.model.impl.AbstractModelObject;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Implementation of the {@link RawLicense} model object interface.
 */
public class RawLicenseImpl extends AbstractModelObject implements RawLicense {

    private String declaredLicense;

    private String licenseUrl;

    private String trace;

    private boolean specialHandling;

    private ApplicationComponent applicationComponent;

    /** {@inheritDoc} */
    @Override
    protected ApplicationComponent doGetParent() {

        return applicationComponent;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public ApplicationComponent getApplicationComponent() {

        return applicationComponent;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getDataElements() {

        return new String[] { declaredLicense, licenseUrl, trace };
    }

    /** {@inheritDoc} */
    @Override
    public String getDeclaredLicense() {

        return declaredLicense;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getHeadElements() {

        return new String[] { "declaredLicense", "licenseUrl", "trace" };
    }

    /** {@inheritDoc} */
    @Override
    public String getLicenseUrl() {

        return licenseUrl;
    }

    /** {@inheritDoc} */
    @Override
    public String getTrace() {

        return trace;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSpecialHandling() {

        return specialHandling;
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationComponent(ApplicationComponent applicationComponent) {

        if (this.applicationComponent != null) {
            throw new IllegalStateException("Once the ApplicationComponentImpl is set it can not be changed");
        }
        this.applicationComponent = applicationComponent;
        applicationComponent.addRawLicense(this);
    }

    /** {@inheritDoc} */
    @Override
    public void setDeclaredLicense(String declaredLicense) {

        this.declaredLicense = declaredLicense;
    }

    /** {@inheritDoc} */
    @Override
    public void setLicenseUrl(String licenseUrl) {

        this.licenseUrl = licenseUrl;
    }

    /** {@inheritDoc} */
    @Override
    public void setSpecialHandling(boolean specialHandling) {

        this.specialHandling = specialHandling;
    }

    /** {@inheritDoc} */
    @Override
    public void setTrace(String trace) {

        this.trace = trace;
    }

}
