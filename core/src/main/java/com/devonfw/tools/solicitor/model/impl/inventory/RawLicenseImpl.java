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

  private String declaredLicenseContentKey;

  private String trace;

  private String origin;

  private boolean specialHandling;

  private ApplicationComponent applicationComponent;

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
  public String[] getDataElements() {

    return new String[] { this.declaredLicense, this.licenseUrl, this.trace, this.origin };
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

    return retrieveTextFromPool(this.declaredLicenseContentKey);
  }

  /**
   * Gets the text pool key of the {@link #getDeclaredLicenseContent()}.
   *
   * @return the key
   */
  public String getDeclaredLicenseContentKey() {

    return this.declaredLicenseContentKey;
  }

  /** {@inheritDoc} */
  @Override
  public String[] getHeadElements() {

    return new String[] { "declaredLicense", "licenseUrl", "trace", "origin" };
  }

  /** {@inheritDoc} */
  @Override
  public String getLicenseUrl() {

    return this.licenseUrl;
  }

  /** {@inheritDoc} */
  @Override
  public String getTrace() {

    return this.trace;
  }

  /** {@inheritDoc} */
  @Override
  public String getOrigin() {

    return this.origin;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSpecialHandling() {

    return this.specialHandling;
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
  public void setDeclaredLicenseContent(String declaredLicenseContent) {

    this.declaredLicenseContentKey = storeTextInPool(declaredLicenseContent);
  }

  /**
   * Sets the text pool key of the {@link #getDeclaredLicenseContent()}.
   *
   * @param declaredLicenseContentKey the key
   */
  public void setDeclaredLicenseContentKey(String declaredLicenseContentKey) {

    this.declaredLicenseContentKey = declaredLicenseContentKey;
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

  /** {@inheritDoc} */
  @Override
  public void setOrigin(String origin) {

    this.origin = origin;
  }

  /** {@inheritDoc} */
  @Override
  public void completeData() {

  }

}
