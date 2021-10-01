/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.impl.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.content.ContentProvider;
import com.devonfw.tools.solicitor.common.content.web.WebContent;
import com.devonfw.tools.solicitor.licensetexts.GuessedLicenseUrlContent;
import com.devonfw.tools.solicitor.model.impl.AbstractModelObject;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Implementation of the {@link NormalizedLicense} model object interface.
 */
public class NormalizedLicenseImpl extends AbstractModelObject implements NormalizedLicense {

    private static final Logger LOG = LoggerFactory.getLogger(NormalizedLicenseImpl.class);

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

    private ContentProvider<WebContent> licenseContentProvider;

    private ContentProvider<GuessedLicenseUrlContent> licenseUrlGuesser;

    /**
     * Creates a new instance.
     */
    public NormalizedLicenseImpl() {

    }

    /**
     * Creates a a new instance.
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

    /** {@inheritDoc} */
    @Override
    protected ApplicationComponent doGetParent() {

        return this.applicationComponent;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public ApplicationComponent getApplicationComponent() {

        return this.applicationComponent;
    }

    /** {@inheritDoc} */
    @Override
    public String getComments() {

        return this.comments;
    }

    /** {@inheritDoc} */
    @Override
    public String getCopyLeft() {

        return this.copyLeft;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getDataElements() {

        return new String[] { this.declaredLicense, this.licenseUrl, getDeclaredLicenseContent(),
        this.normalizedLicenseType, this.normalizedLicense, this.normalizedLicenseUrl,
        this.effectiveNormalizedLicenseType, this.effectiveNormalizedLicense, this.effectiveNormalizedLicenseUrl,
        getEffectiveNormalizedLicenseContent(), this.legalPreApproved, this.copyLeft, this.licenseCompliance,
        this.licenseRefUrl, getLicenseRefContent(), this.includeLicense, this.includeSource, this.reviewedForRelease,
        this.comments, this.legalApproved, this.legalComments, this.trace, getGuessedLicenseUrl(),
        getGuessedLicenseUrlAuditInfo(), getGuessedLicenseContent() };
    }

    /** {@inheritDoc} */
    @Override
    public String getDeclaredLicense() {

        return this.declaredLicense;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public String getDeclaredLicenseContent() {

        return this.licenseContentProvider.getContentForUri(this.licenseUrl).getContent();
    }

    /** {@inheritDoc} */
    @Override
    public String getEffectiveNormalizedLicense() {

        return this.effectiveNormalizedLicense;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public String getEffectiveNormalizedLicenseContent() {

        return this.licenseContentProvider.getContentForUri(this.effectiveNormalizedLicenseUrl).getContent();
    }

    /** {@inheritDoc} */
    @Override
    public String getEffectiveNormalizedLicenseType() {

        return this.effectiveNormalizedLicenseType;
    }

    /** {@inheritDoc} */
    @Override
    public String getEffectiveNormalizedLicenseUrl() {

        return this.effectiveNormalizedLicenseUrl;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getHeadElements() {

        return new String[] { "declaredLicense", "licenseUrl", "declaredLicenseContent", "normalizedLicenseType",
        "normalizedLicense", "normalizedLicenseUrl", "effectiveNormalizedLicenseType", "effectiveNormalizedLicense",
        "effectiveNormalizedLicenseUrl", "effectiveNormalizedLicenseContent", "legalPreApproved", "copyLeft",
        "licenseCompliance", "licenseRefUrl", "licenseRefContent", "includeLicense", "includeSource",
        "reviewedForRelease", "comments", "legalApproved", "legalComments", "trace", "guessedLicenseUrl",
        "guessedLicenseUrlAuditInfo", "guessedLicenseContent" };
    }

    /** {@inheritDoc} */
    @Override
    public String getIncludeLicense() {

        return this.includeLicense;
    }

    /** {@inheritDoc} */
    @Override
    public String getIncludeSource() {

        return this.includeSource;
    }

    /** {@inheritDoc} */
    @Override
    public String getLegalApproved() {

        return this.legalApproved;
    }

    /** {@inheritDoc} */
    @Override
    public String getLegalComments() {

        return this.legalComments;
    }

    /** {@inheritDoc} */
    @Override
    public String getLegalPreApproved() {

        return this.legalPreApproved;
    }

    /** {@inheritDoc} */
    @Override
    public String getLicenseCompliance() {

        return this.licenseCompliance;
    }

    /**
     * This method gets the field <code>licenseContentProvider</code>.
     *
     * @return the field licenseContentProvider
     */
    @JsonIgnore
    public ContentProvider<WebContent> getLicenseContentProvider() {

        return this.licenseContentProvider;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public String getLicenseRefContent() {

        return this.licenseContentProvider.getContentForUri(this.licenseRefUrl).getContent();
    }

    /** {@inheritDoc} */
    @Override
    public String getLicenseRefUrl() {

        return this.licenseRefUrl;
    }

    /** {@inheritDoc} */
    @Override
    public String getLicenseUrl() {

        return this.licenseUrl;
    }

    /** {@inheritDoc} */
    @Override
    public String getNormalizedLicense() {

        return this.normalizedLicense;
    }

    /** {@inheritDoc} */
    @Override
    public String getNormalizedLicenseType() {

        return this.normalizedLicenseType;
    }

    /** {@inheritDoc} */
    @Override
    public String getNormalizedLicenseUrl() {

        return this.normalizedLicenseUrl;
    }

    /** {@inheritDoc} */
    @Override
    public String getReviewedForRelease() {

        return this.reviewedForRelease;
    }

    /** {@inheritDoc} */
    @Override
    public String getTrace() {

        return this.trace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGuessedLicenseUrl() {

        return this.licenseUrlGuesser.getContentForUri(this.effectiveNormalizedLicenseUrl).getGuessedUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGuessedLicenseUrlAuditInfo() {

        return this.licenseUrlGuesser.getContentForUri(this.effectiveNormalizedLicenseUrl).getAuditInfo();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGuessedLicenseContent() {

        String guessedUrl = getGuessedLicenseUrl();
        return this.licenseContentProvider.getContentForUri(guessedUrl).getContent();
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationComponent(ApplicationComponent applicationComponent) {

        if (this.applicationComponent != null) {
            throw new IllegalStateException("Once the ApplicationComponentImpl is set it can not be changed");
        }
        this.applicationComponent = applicationComponent;
        applicationComponent.addNormalizedLicense(this);
    }

    /** {@inheritDoc} */
    @Override
    public void setComments(String comments) {

        this.comments = comments;
    }

    /** {@inheritDoc} */
    @Override
    public void setCopyLeft(String copyLeft) {

        this.copyLeft = copyLeft;
    }

    /** {@inheritDoc} */
    @Override
    public void setDeclaredLicense(String declaredLicense) {

        this.declaredLicense = declaredLicense;
    }

    /** {@inheritDoc} */
    @Override
    public void setEffectiveNormalizedLicense(String effectiveNormalizedLicense) {

        this.effectiveNormalizedLicense = effectiveNormalizedLicense;
    }

    /** {@inheritDoc} */
    @Override
    public void setEffectiveNormalizedLicenseType(String effectiveNormalizedLicenseType) {

        this.effectiveNormalizedLicenseType = effectiveNormalizedLicenseType;
    }

    /** {@inheritDoc} */
    @Override
    public void setEffectiveNormalizedLicenseUrl(String effectiveNormalizedLicenseUrl) {

        this.effectiveNormalizedLicenseUrl = effectiveNormalizedLicenseUrl;
    }

    /** {@inheritDoc} */
    @Override
    public void setIncludeLicense(String includeLicense) {

        this.includeLicense = includeLicense;
    }

    /** {@inheritDoc} */
    @Override
    public void setIncludeSource(String includeSource) {

        this.includeSource = includeSource;
    }

    /** {@inheritDoc} */
    @Override
    public void setLegalApproved(String legalApproved) {

        this.legalApproved = legalApproved;
    }

    /** {@inheritDoc} */
    @Override
    public void setLegalComments(String legalComments) {

        this.legalComments = legalComments;
    }

    /** {@inheritDoc} */
    @Override
    public void setLegalPreApproved(String legalPreApproved) {

        this.legalPreApproved = legalPreApproved;
    }

    /** {@inheritDoc} */
    @Override
    public void setLicenseCompliance(String licenseCompliance) {

        this.licenseCompliance = licenseCompliance;
    }

    /**
     * This method sets the field <code>licenseContentProvider</code>.
     *
     * @param licenseContentProvider the new value of the field
     *        licenseContentProvider
     */
    public void setLicenseContentProvider(ContentProvider<WebContent> licenseContentProvider) {

        this.licenseContentProvider = licenseContentProvider;
    }

    /**
     * This method sets the field <code>licenseUrlGuesser</code>.
     *
     * @param licenseUrlGuesser the new value of the field icenseUrlGuesser
     */
    public void setLicenseUrlGuesser(ContentProvider<GuessedLicenseUrlContent> licenseUrlGuesser) {

        this.licenseUrlGuesser = licenseUrlGuesser;
    }

    /** {@inheritDoc} */
    @Override
    public void setLicenseRefUrl(String licenseRefUrl) {

        this.licenseRefUrl = licenseRefUrl;
    }

    /** {@inheritDoc} */
    @Override
    public void setLicenseUrl(String licenseUrl) {

        this.licenseUrl = licenseUrl;
    }

    /** {@inheritDoc} */
    @Override
    public void setNormalizedLicense(String normalizedLicense) {

        this.normalizedLicense = normalizedLicense;
    }

    /** {@inheritDoc} */
    @Override
    public void setNormalizedLicenseType(String normalizedLicenseType) {

        this.normalizedLicenseType = normalizedLicenseType;
    }

    /** {@inheritDoc} */
    @Override
    public void setNormalizedLicenseUrl(String normalizedLicenseUrl) {

        this.normalizedLicenseUrl = normalizedLicenseUrl;
    }

    /** {@inheritDoc} */
    @Override
    public void setReviewedForRelease(String reviewedForRelease) {

        this.reviewedForRelease = reviewedForRelease;
    }

    /** {@inheritDoc} */
    @Override
    public void setTrace(String trace) {

        this.trace = trace;
    }

    /** {@inheritDoc} */
    @Override
    public void completeData() {

    }

}
