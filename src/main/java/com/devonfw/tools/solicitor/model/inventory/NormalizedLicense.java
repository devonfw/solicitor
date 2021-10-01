package com.devonfw.tools.solicitor.model.inventory;

/**
 * Represents a NormalizedLicense in the Solicitor data model.
 */

public interface NormalizedLicense {

    /**
     * This method gets the field <code>applicationComponent</code>.
     *
     * @return the field applicationComponent
     */
    ApplicationComponent getApplicationComponent();

    /**
     * This method gets the field <code>comments</code>.
     *
     * @return the field comments
     */
    String getComments();

    /**
     * This method gets the field <code>copyLeft</code>.
     *
     * @return the field copyLeft
     */
    String getCopyLeft();

    /**
     * This method gets the field <code>declaredLicense</code>.
     *
     * @return the field declaredLicense
     */
    String getDeclaredLicense();

    /**
     * Gets the content referenced by {@link #getLicenseUrl()}
     *
     * @return the referenced content.
     */
    String getDeclaredLicenseContent();

    /**
     * This method gets the field <code>effectiveNormalizedLicense</code>.
     *
     * @return the field effectiveNormalizedLicense
     */
    String getEffectiveNormalizedLicense();

    /**
     * Gets the content referenced by
     * {@link #getEffectiveNormalizedLicenseUrl()}
     *
     * @return the referenced content.
     */
    String getEffectiveNormalizedLicenseContent();

    /**
     * This method gets the field <code>effectiveNormalizedLicenseType</code>.
     *
     * @return the field effectiveNormalizedLicenseType
     */
    String getEffectiveNormalizedLicenseType();

    /**
     * This method gets the field <code>effectiveNormalizedLicenseUrl</code>.
     *
     * @return the field effectiveNormalizedLicenseUrl
     */
    String getEffectiveNormalizedLicenseUrl();

    /**
     * This method gets the field <code>includeLicense</code>.
     *
     * @return the field includeLicense
     */
    String getIncludeLicense();

    /**
     * This method gets the field <code>includeSource</code>.
     *
     * @return the field includeSource
     */
    String getIncludeSource();

    /**
     * This method gets the field <code>legalApproved</code>.
     *
     * @return the field legalApproved
     */
    String getLegalApproved();

    /**
     * This method gets the field <code>legalComments</code>.
     *
     * @return the field legalComments
     */
    String getLegalComments();

    /**
     * This method gets the field <code>legalPreApproved</code>.
     *
     * @return the field legalPreApproved
     */
    String getLegalPreApproved();

    /**
     * This method gets the field <code>licenseCompliance</code>.
     *
     * @return the field licenseCompliance
     */
    String getLicenseCompliance();

    /**
     * Gets the content referenced by {@link #getLicenseRefUrl()}
     *
     * @return the referenced content.
     */
    String getLicenseRefContent();

    /**
     * This method gets the field <code>licenseRefUrl</code>.
     *
     * @return the field licenseRefUrl
     */
    String getLicenseRefUrl();

    /**
     * This method gets the field <code>licenseUrl</code>.
     *
     * @return the field licenseUrl
     */
    String getLicenseUrl();

    /**
     * This method gets the field <code>normalizedLicense</code>.
     *
     * @return the field normalizedLicense
     */
    String getNormalizedLicense();

    /**
     * This method gets the field <code>normalizedLicenseType</code>.
     *
     * @return the field normalizedLicenseType
     */
    String getNormalizedLicenseType();

    /**
     * This method gets the field <code>normalizedLicenseUrl</code>.
     *
     * @return the field normalizedLicenseUrl
     */
    String getNormalizedLicenseUrl();

    /**
     * This method gets the field <code>reviewedForRelease</code>.
     *
     * @return the field reviewedForRelease
     */
    String getReviewedForRelease();

    /**
     * This method gets the field <code>guessedLicenseUrl</code>.
     *
     * @return the field guessedLicenseUrl
     */
    String getGuessedLicenseUrl();

    /**
     * This method gets the field <code>guessedLicenseUrlAuditInfos</code>.
     *
     * @return the field guessedLicenseUrlAuditInfo
     */
    String getGuessedLicenseUrlAuditInfo();

    /**
     * This method gets the field <code>guessedLicenseContent</code>.
     *
     * @return the field guessedLicenseContent
     */
    String getGuessedLicenseContent();

    /**
     * This method gets the field <code>trace</code>.
     *
     * @return the field trace
     */
    String getTrace();

    /**
     * Set the {@link ApplicationComponent} to which the
     * {@link NormalizedLicense} belongs.
     *
     * @param applicationComponent the parent of this object
     */
    void setApplicationComponent(ApplicationComponent applicationComponent);

    /**
     * This method sets the field <code>comments</code>.
     *
     * @param comments the new value of the field comments
     */
    void setComments(String comments);

    /**
     * This method sets the field <code>copyLeft</code>.
     *
     * @param copyLeft the new value of the field copyLeft
     */
    void setCopyLeft(String copyLeft);

    /**
     * This method sets the field <code>declaredLicense</code>.
     *
     * @param declaredLicense the new value of the field declaredLicense
     */
    void setDeclaredLicense(String declaredLicense);

    /**
     * This method sets the field <code>effectiveNormalizedLicense</code>.
     *
     * @param effectiveNormalizedLicense the new value of the field
     *        effectiveNormalizedLicense
     */
    void setEffectiveNormalizedLicense(String effectiveNormalizedLicense);

    /**
     * This method sets the field <code>effectiveNormalizedLicenseType</code>.
     *
     * @param effectiveNormalizedLicenseType the new value of the field
     *        effectiveNormalizedLicenseType
     */
    void setEffectiveNormalizedLicenseType(String effectiveNormalizedLicenseType);

    /**
     * This method sets the field <code>effectiveNormalizedLicenseUrl</code>.
     *
     * @param effectiveNormalizedLicenseUrl the new value of the field
     *        effectiveNormalizedLicenseUrl
     */
    void setEffectiveNormalizedLicenseUrl(String effectiveNormalizedLicenseUrl);

    /**
     * This method sets the field <code>includeLicense</code>.
     *
     * @param includeLicense the new value of the field includeLicense
     */
    void setIncludeLicense(String includeLicense);

    /**
     * This method sets the field <code>includeSource</code>.
     *
     * @param includeSource the new value of the field includeSource
     */
    void setIncludeSource(String includeSource);

    /**
     * This method sets the field <code>legalApproved</code>.
     *
     * @param legalApproved the new value of the field legalApproved
     */
    void setLegalApproved(String legalApproved);

    /**
     * This method sets the field <code>legalComments</code>.
     *
     * @param legalComments the new value of the field legalComments
     */
    void setLegalComments(String legalComments);

    /**
     * This method sets the field <code>legalPreApproved</code>.
     *
     * @param legalPreApproved the new value of the field legalPreApproved
     */
    void setLegalPreApproved(String legalPreApproved);

    /**
     * This method sets the field <code>licenseCompliance</code>.
     *
     * @param licenseCompliance the new value of the field licenseCompliance
     */
    void setLicenseCompliance(String licenseCompliance);

    /**
     * This method sets the field <code>licenseRefUrl</code>.
     *
     * @param licenseRefUrl the new value of the field licenseRefUrl
     */
    void setLicenseRefUrl(String licenseRefUrl);

    /**
     * This method sets the field <code>licenseUrl</code>.
     *
     * @param licenseUrl the new value of the field licenseUrl
     */
    void setLicenseUrl(String licenseUrl);

    /**
     * This method sets the field <code>normalizedLicense</code>.
     *
     * @param normalizedLicense the new value of the field normalizedLicense
     */
    void setNormalizedLicense(String normalizedLicense);

    /**
     * This method sets the field <code>normalizedLicenseType</code>.
     *
     * @param normalizedLicenseType the new value of the field
     *        normalizedLicenseType
     */
    void setNormalizedLicenseType(String normalizedLicenseType);

    /**
     * This method sets the field <code>normalizedLicenseUrl</code>.
     *
     * @param normalizedLicenseUrl the new value of the field
     *        normalizedLicenseUrl
     */
    void setNormalizedLicenseUrl(String normalizedLicenseUrl);

    /**
     * This method sets the field <code>reviewedForRelease</code>.
     *
     * @param reviewedForRelease the new value of the field reviewedForRelease
     */
    void setReviewedForRelease(String reviewedForRelease);

    /**
     * This method sets the field <code>trace</code>.
     *
     * @param trace the new value of the field trace
     */
    void setTrace(String trace);

    /**
     * Complete the data of this object by setting members which are derived
     * from other members.
     */
    public void completeData();
}
