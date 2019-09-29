/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.impl.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.devonfw.tools.solicitor.common.webcontent.WebContentProvider;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class NormalizedLicenseImpl extends AbstractDataRowSource
        implements DataRowSource, NormalizedLicense {

    private static final Logger LOG =
            LoggerFactory.getLogger(NormalizedLicenseImpl.class);

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

    private String trace;

    private ApplicationComponent applicationComponent;

    private WebContentProvider licenseContentProvider;

    /**
     * Creates a new instance.
     */
    public NormalizedLicenseImpl() {

    }

    /**
     * Creates a anew instance.
     * 
     * @param rawLicense the raw license which this object should be based on;
     *        identical fields will be copied.
     */
    public NormalizedLicenseImpl(RawLicense rawLicense) {

        setApplicationComponent(rawLicense.getApplicationComponent());
        this.declaredLicense = rawLicense.getDeclaredLicense();
        this.licenseUrl = rawLicense.getLicenseUrl();
        this.trace = rawLicense.getTrace();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public String getDeclaredLicenseContent() {

        return licenseContentProvider.getWebContentForUrl(this.licenseUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public String getEffectiveNormalizedLicenseContent() {

        return licenseContentProvider
                .getWebContentForUrl(this.effectiveNormalizedLicenseUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public String getLicenseRefContent() {

        return licenseContentProvider.getWebContentForUrl(this.licenseRefUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationComponent(
            ApplicationComponent applicationComponent) {

        if (this.applicationComponent != null) {
            throw new IllegalStateException(
                    "Once the ApplicationComponentImpl is set it can not be changed");
        }
        this.applicationComponent = applicationComponent;
        applicationComponent.addNormalizedLicense(this);
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

        // TODO: How to avoid casting?
        return (AbstractDataRowSource) applicationComponent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeclaredLicense() {

        return declaredLicense;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeclaredLicense(String declaredLicense) {

        this.declaredLicense = declaredLicense;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLicenseUrl() {

        return licenseUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLicenseUrl(String licenseUrl) {

        this.licenseUrl = licenseUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNormalizedLicenseType() {

        return normalizedLicenseType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNormalizedLicenseType(String normalizedLicenseType) {

        this.normalizedLicenseType = normalizedLicenseType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNormalizedLicense() {

        return normalizedLicense;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNormalizedLicense(String normalizedLicense) {

        this.normalizedLicense = normalizedLicense;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNormalizedLicenseUrl() {

        return normalizedLicenseUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNormalizedLicenseUrl(String normalizedLicenseUrl) {

        this.normalizedLicenseUrl = normalizedLicenseUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEffectiveNormalizedLicenseType() {

        return effectiveNormalizedLicenseType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEffectiveNormalizedLicenseType(
            String effectiveNormalizedLicenseType) {

        this.effectiveNormalizedLicenseType = effectiveNormalizedLicenseType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEffectiveNormalizedLicense() {

        return effectiveNormalizedLicense;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEffectiveNormalizedLicense(
            String effectiveNormalizedLicense) {

        this.effectiveNormalizedLicense = effectiveNormalizedLicense;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEffectiveNormalizedLicenseUrl() {

        return effectiveNormalizedLicenseUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEffectiveNormalizedLicenseUrl(
            String effectiveNormalizedLicenseUrl) {

        this.effectiveNormalizedLicenseUrl = effectiveNormalizedLicenseUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLegalPreApproved() {

        return legalPreApproved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLegalPreApproved(String legalPreApproved) {

        this.legalPreApproved = legalPreApproved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCopyLeft() {

        return copyLeft;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCopyLeft(String copyLeft) {

        this.copyLeft = copyLeft;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLicenseCompliance() {

        return licenseCompliance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLicenseCompliance(String licenseCompliance) {

        this.licenseCompliance = licenseCompliance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLicenseRefUrl() {

        return licenseRefUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLicenseRefUrl(String licenseRefUrl) {

        this.licenseRefUrl = licenseRefUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIncludeLicense() {

        return includeLicense;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIncludeLicense(String includeLicense) {

        this.includeLicense = includeLicense;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIncludeSource() {

        return includeSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIncludeSource(String includeSource) {

        this.includeSource = includeSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReviewedForRelease() {

        return reviewedForRelease;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReviewedForRelease(String reviewedForRelease) {

        this.reviewedForRelease = reviewedForRelease;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getComments() {

        return comments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setComments(String comments) {

        this.comments = comments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLegalApproved() {

        return legalApproved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLegalApproved(String legalApproved) {

        this.legalApproved = legalApproved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLegalComments() {

        return legalComments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLegalComments(String legalComments) {

        this.legalComments = legalComments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTrace() {

        return trace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTrace(String trace) {

        this.trace = trace;
    }

    /**
     * This method gets the field <tt>licenseContentProvider</tt>.
     *
     * @return the field licenseContentProvider
     */
    @JsonIgnore
    public WebContentProvider getLicenseContentProvider() {

        return licenseContentProvider;
    }

    /**
     * This method sets the field <tt>licenseContentProvider</tt>.
     *
     * @param licenseContentProvider the new value of the field
     *        licenseContentProvider
     */
    public void setLicenseContentProvider(
            WebContentProvider licenseContentProvider) {

        this.licenseContentProvider = licenseContentProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public ApplicationComponent getApplicationComponent() {

        return applicationComponent;
    }

}
