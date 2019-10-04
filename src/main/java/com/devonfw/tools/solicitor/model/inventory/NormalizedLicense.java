package com.devonfw.tools.solicitor.model.inventory;

/**
 * Represents a NormalizedLicense in the Solicitor data model.
 */

public interface NormalizedLicense {

    /**
     * This method gets the field <tt>applicationComponent</tt>.
     *
     * @return the field applicationComponent
     */
    ApplicationComponent getApplicationComponent();

    /**
     * This method gets the field <tt>comments</tt>.
     *
     * @return the field comments
     */
    String getComments();

    /**
     * This method gets the field <tt>copyLeft</tt>.
     *
     * @return the field copyLeft
     */
    String getCopyLeft();

    /**
     * This method gets the field <tt>declaredLicense</tt>.
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
     * This method gets the field <tt>effectiveNormalizedLicense</tt>.
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
     * This method gets the field <tt>effectiveNormalizedLicenseType</tt>.
     *
     * @return the field effectiveNormalizedLicenseType
     */
    String getEffectiveNormalizedLicenseType();

    /**
     * This method gets the field <tt>effectiveNormalizedLicenseUrl</tt>.
     *
     * @return the field effectiveNormalizedLicenseUrl
     */
    String getEffectiveNormalizedLicenseUrl();

    /**
     * This method gets the field <tt>includeLicense</tt>.
     *
     * @return the field includeLicense
     */
    String getIncludeLicense();

    /**
     * This method gets the field <tt>includeSource</tt>.
     *
     * @return the field includeSource
     */
    String getIncludeSource();

    /**
     * This method gets the field <tt>legalApproved</tt>.
     *
     * @return the field legalApproved
     */
    String getLegalApproved();

    /**
     * This method gets the field <tt>legalComments</tt>.
     *
     * @return the field legalComments
     */
    String getLegalComments();

    /**
     * This method gets the field <tt>legalPreApproved</tt>.
     *
     * @return the field legalPreApproved
     */
    String getLegalPreApproved();

    /**
     * This method gets the field <tt>licenseCompliance</tt>.
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
     * This method gets the field <tt>licenseRefUrl</tt>.
     *
     * @return the field licenseRefUrl
     */
    String getLicenseRefUrl();

    /**
     * This method gets the field <tt>licenseUrl</tt>.
     *
     * @return the field licenseUrl
     */
    String getLicenseUrl();

    /**
     * This method gets the field <tt>normalizedLicense</tt>.
     *
     * @return the field normalizedLicense
     */
    String getNormalizedLicense();

    /**
     * This method gets the field <tt>normalizedLicenseType</tt>.
     *
     * @return the field normalizedLicenseType
     */
    String getNormalizedLicenseType();

    /**
     * This method gets the field <tt>normalizedLicenseUrl</tt>.
     *
     * @return the field normalizedLicenseUrl
     */
    String getNormalizedLicenseUrl();

    /**
     * This method gets the field <tt>reviewedForRelease</tt>.
     *
     * @return the field reviewedForRelease
     */
    String getReviewedForRelease();

    /**
     * This method gets the field <tt>trace</tt>.
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
     * This method sets the field <tt>comments</tt>.
     *
     * @param comments the new value of the field comments
     */
    void setComments(String comments);

    /**
     * This method sets the field <tt>copyLeft</tt>.
     *
     * @param copyLeft the new value of the field copyLeft
     */
    void setCopyLeft(String copyLeft);

    /**
     * This method sets the field <tt>declaredLicense</tt>.
     *
     * @param declaredLicense the new value of the field declaredLicense
     */
    void setDeclaredLicense(String declaredLicense);

    /**
     * This method sets the field <tt>effectiveNormalizedLicense</tt>.
     *
     * @param effectiveNormalizedLicense the new value of the field
     *        effectiveNormalizedLicense
     */
    void setEffectiveNormalizedLicense(String effectiveNormalizedLicense);

    /**
     * This method sets the field <tt>effectiveNormalizedLicenseType</tt>.
     *
     * @param effectiveNormalizedLicenseType the new value of the field
     *        effectiveNormalizedLicenseType
     */
    void setEffectiveNormalizedLicenseType(String effectiveNormalizedLicenseType);

    /**
     * This method sets the field <tt>effectiveNormalizedLicenseUrl</tt>.
     *
     * @param effectiveNormalizedLicenseUrl the new value of the field
     *        effectiveNormalizedLicenseUrl
     */
    void setEffectiveNormalizedLicenseUrl(String effectiveNormalizedLicenseUrl);

    /**
     * This method sets the field <tt>includeLicense</tt>.
     *
     * @param includeLicense the new value of the field includeLicense
     */
    void setIncludeLicense(String includeLicense);

    /**
     * This method sets the field <tt>includeSource</tt>.
     *
     * @param includeSource the new value of the field includeSource
     */
    void setIncludeSource(String includeSource);

    /**
     * This method sets the field <tt>legalApproved</tt>.
     *
     * @param legalApproved the new value of the field legalApproved
     */
    void setLegalApproved(String legalApproved);

    /**
     * This method sets the field <tt>legalComments</tt>.
     *
     * @param legalComments the new value of the field legalComments
     */
    void setLegalComments(String legalComments);

    /**
     * This method sets the field <tt>legalPreApproved</tt>.
     *
     * @param legalPreApproved the new value of the field legalPreApproved
     */
    void setLegalPreApproved(String legalPreApproved);

    /**
     * This method sets the field <tt>licenseCompliance</tt>.
     *
     * @param licenseCompliance the new value of the field licenseCompliance
     */
    void setLicenseCompliance(String licenseCompliance);

    /**
     * This method sets the field <tt>licenseRefUrl</tt>.
     *
     * @param licenseRefUrl the new value of the field licenseRefUrl
     */
    void setLicenseRefUrl(String licenseRefUrl);

    /**
     * This method sets the field <tt>licenseUrl</tt>.
     *
     * @param licenseUrl the new value of the field licenseUrl
     */
    void setLicenseUrl(String licenseUrl);

    /**
     * This method sets the field <tt>normalizedLicense</tt>.
     *
     * @param normalizedLicense the new value of the field normalizedLicense
     */
    void setNormalizedLicense(String normalizedLicense);

    /**
     * This method sets the field <tt>normalizedLicenseType</tt>.
     *
     * @param normalizedLicenseType the new value of the field
     *        normalizedLicenseType
     */
    void setNormalizedLicenseType(String normalizedLicenseType);

    /**
     * This method sets the field <tt>normalizedLicenseUrl</tt>.
     *
     * @param normalizedLicenseUrl the new value of the field
     *        normalizedLicenseUrl
     */
    void setNormalizedLicenseUrl(String normalizedLicenseUrl);

    /**
     * This method sets the field <tt>reviewedForRelease</tt>.
     *
     * @param reviewedForRelease the new value of the field reviewedForRelease
     */
    void setReviewedForRelease(String reviewedForRelease);

    /**
     * This method sets the field <tt>trace</tt>.
     *
     * @param trace the new value of the field trace
     */
    void setTrace(String trace);

}
