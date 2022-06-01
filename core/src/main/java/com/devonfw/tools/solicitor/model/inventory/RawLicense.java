package com.devonfw.tools.solicitor.model.inventory;

/**
 * Represents a RawLicense in the Solicitor data model.
 */
public interface RawLicense {

  /**
   * This method gets the field <code>applicationComponent</code>.
   *
   * @return the field applicationComponent
   */
  ApplicationComponent getApplicationComponent();

  /**
   * This method gets the field <code>declaredLicense</code>.
   *
   * @return the field declaredLicense
   */
  String getDeclaredLicense();

  /**
   * This method gets the field <code>licenseUrl</code>.
   *
   * @return the field licenseUrl
   */
  String getLicenseUrl();

  /**
   * This method gets the field <code>trace</code>.
   *
   * @return the field trace
   */
  String getTrace();

  /**
   * This method gets the field <code>origin</code>.
   *
   * @return the field origin
   */
  String getOrigin();

  /**
   * This method gets the field <code>specialHandling</code>.
   *
   * @return the field specialHandling
   */
  boolean isSpecialHandling();

  /**
   * Set the {@link ApplicationComponent} to which the {@link RawLicense} belongs.
   *
   * @param applicationComponent the parent of this object
   */
  void setApplicationComponent(ApplicationComponent applicationComponent);

  /**
   * This method sets the field <code>declaredLicense</code>.
   *
   * @param declaredLicense the new value of the field declaredLicense
   */
  void setDeclaredLicense(String declaredLicense);

  /**
   * This method sets the field <code>licenseUrl</code>.
   *
   * @param licenseUrl the new value of the field licenseUrl
   */
  void setLicenseUrl(String licenseUrl);

  /**
   * This method sets the field <code>specialHandling</code>.
   *
   * @param specialHandling the new value of the field specialHandling
   */
  void setSpecialHandling(boolean specialHandling);

  /**
   * This method sets the field <code>trace</code>.
   *
   * @param trace the new value of the field trace
   */
  void setTrace(String trace);

  /**
   * This method sets the field <code>origin</code>.
   *
   * @param origin the new value of the field origin
   */
  void setOrigin(String origin);

  /**
   * Complete the data of this object by setting members which are derived from other members.
   */
  public void completeData();
}
