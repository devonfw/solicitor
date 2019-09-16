/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.inventory;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.devonfw.tools.solicitor.common.webcontent.WebContentProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class NormalizedLicense extends AbstractDataRowSource
        implements DataRowSource {

    @JsonProperty
    private String declaredLicense;

    @JsonProperty
    private String licenseUrl;

    @JsonProperty
    private String normalizedLicenseType;

    @JsonProperty
    private String normalizedLicense;

    @JsonProperty
    private String normalizedLicenseUrl;

    @JsonProperty
    private String effectiveNormalizedLicenseType;

    @JsonProperty
    private String effectiveNormalizedLicense;

    @JsonProperty
    private String effectiveNormalizedLicenseUrl; // really needed?

    @JsonProperty
    private String legalPreApproved;

    @JsonProperty
    private String copyLeft;

    @JsonProperty
    private String licenseCompliance;

    @JsonProperty
    private String licenseRefUrl;

    @JsonProperty
    private String includeLicense;

    @JsonProperty
    private String includeSource;

    @JsonProperty
    private String reviewedForRelease;

    @JsonProperty
    private String comments;

    @JsonProperty
    private String legalApproved;

    @JsonProperty
    private String legalComments;

    @JsonProperty
    private String trace;

    @JsonIgnore
    private ApplicationComponent applicationComponent;

    @JsonIgnore
    private WebContentProvider licenseContentProvider;

    public NormalizedLicense(RawLicense rawLicense) {

        setApplicationComponent(rawLicense.getApplicationComponent());
        this.declaredLicense = rawLicense.getDeclaredLicense();
        this.licenseUrl = rawLicense.getLicenseUrl();
        this.trace = rawLicense.getTrace();
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
        "legalApproved", "legalComments", "trace" };
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
        legalApproved, legalComments, trace };
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

    public void appendTrace(String traceEntry) {

        LOG.debug(traceEntry);
        if (this.trace == null) {
            this.trace = traceEntry;
        } else {
            this.trace += System.lineSeparator() + traceEntry;
        }
    }

}
