/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.inventory;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.devonfw.tools.solicitor.common.webcontent.WebContentProvider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class NormalizedLicense extends AbstractDataRowSource
        implements DataRowSource {

    private String declaredLicense;

    private String licenseUrl;

    private String normalizedLicenseType;

    private String normalizedLicense;

    private String normalizedLicenseUrl;

    private String effectiveNormalizedLicenseType;

    private String effectiveNormalizedLicense;

    private String effectiveNormalizedLicenseUrl; // really needed?

    private String legalPreApproved;

    private String copyLeft;

    private String licenseCompliance;

    private String licenseRefUrl;

    private String includeLicense;

    private String includeSource;

    private String reviewedForRelease;

    private String comments;

    private String legalApproved;

    private String legalComments;

    private ApplicationComponent applicationComponent;

    private WebContentProvider licenseContentProvider;

    // TODO: probably we don't need that constructer (user setters instead)
    public NormalizedLicense(ApplicationComponent applicationComponent,
            String declaredLicense, String licenseUrl,
            String normalizedLicenseType) {

        setApplicationComponent(applicationComponent);
        this.declaredLicense = declaredLicense;
        this.licenseUrl = licenseUrl;
        this.normalizedLicenseType = normalizedLicenseType;
    }

    public String getDeclaredLicenseContent() {

        return licenseContentProvider.getWebContentForUrl(this.licenseUrl);
    }

    public String getEffectiveNormalizedLicenseContent() {

        return licenseContentProvider
                .getWebContentForUrl(this.effectiveNormalizedLicenseUrl);
    }

    public String getLicenseRefContent() {

        return licenseContentProvider.getWebContentForUrl(this.licenseRefUrl);
    }

    public void setApplicationComponent(
            ApplicationComponent applicationComponent) {

        if (this.applicationComponent != null) {
            throw new IllegalStateException(
                    "Once the ApplicationComponent is set it can not be changed");
        }
        this.applicationComponent = applicationComponent;
        applicationComponent.getNormalizedLicenses().add(this);
    }

    @Override
    public String[] getHeadElements() {

        return new String[] { "declaredLicense", "licenseUrl",
        "declaredLicenseContent", "normalizedLicenseType", "normalizedLicense",
        "normalizedLicenseUrl", "effectiveNormalizedLicenseType",
        "effectiveNormalizedLicense", "effectiveNormalizedLicenseUrl",
        "effectiveNormalizedLicenseContent", "legalPreApproved", "copyLeft",
        "licenseCompliance", "licenseRefUrl", "licenseRefContent",
        "includeLicense", "includeSource", "reviewedForRelease", "comments",
        "legalApproved", "legalComments" };
    }

    @Override
    public String[] getDataElements() {

        return new String[] { declaredLicense, licenseUrl,
        getDeclaredLicenseContent(), normalizedLicenseType, normalizedLicense,
        normalizedLicenseUrl, effectiveNormalizedLicenseType,
        effectiveNormalizedLicense, effectiveNormalizedLicenseUrl,
        getEffectiveNormalizedLicenseContent(), legalPreApproved, copyLeft,
        licenseCompliance, licenseRefUrl, getLicenseRefContent(),
        includeLicense, includeSource, reviewedForRelease, comments,
        legalApproved, legalComments };
    }

    @Override
    public AbstractDataRowSource getParent() {

        return applicationComponent;
    }

    public void addComment(String comment) {

        if (this.comments == null) {
            this.comments = comment;
        } else {
            this.comments = this.comments + "; " + comment;
        }
    }

}
