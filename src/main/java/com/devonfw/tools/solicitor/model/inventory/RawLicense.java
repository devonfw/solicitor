package com.devonfw.tools.solicitor.model.inventory;

public interface RawLicense {

    void setApplicationComponent(ApplicationComponent applicationComponent);

    /**
     * This method gets the field <tt>declaredLicense</tt>.
     *
     * @return the field declaredLicense
     */
    String getDeclaredLicense();

    /**
     * This method sets the field <tt>declaredLicense</tt>.
     *
     * @param declaredLicense the new value of the field declaredLicense
     */
    void setDeclaredLicense(String declaredLicense);

    /**
     * This method gets the field <tt>licenseUrl</tt>.
     *
     * @return the field licenseUrl
     */
    String getLicenseUrl();

    /**
     * This method sets the field <tt>licenseUrl</tt>.
     *
     * @param licenseUrl the new value of the field licenseUrl
     */
    void setLicenseUrl(String licenseUrl);

    /**
     * This method gets the field <tt>trace</tt>.
     *
     * @return the field trace
     */
    String getTrace();

    /**
     * This method sets the field <tt>trace</tt>.
     *
     * @param trace the new value of the field trace
     */
    void setTrace(String trace);

    /**
     * This method gets the field <tt>specialHandling</tt>.
     *
     * @return the field specialHandling
     */
    boolean isSpecialHandling();

    /**
     * This method sets the field <tt>specialHandling</tt>.
     *
     * @param specialHandling the new value of the field specialHandling
     */
    void setSpecialHandling(boolean specialHandling);

    /**
     * This method gets the field <tt>applicationComponent</tt>.
     *
     * @return the field applicationComponent
     */
    ApplicationComponent getApplicationComponent();

}