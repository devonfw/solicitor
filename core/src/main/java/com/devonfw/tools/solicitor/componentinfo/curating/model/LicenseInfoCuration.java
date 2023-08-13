package com.devonfw.tools.solicitor.componentinfo.curating.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * License curation info.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LicenseInfoCuration {

  private String license;

  private String url;

  /**
   * The constructor.
   */
  public LicenseInfoCuration() {

  }

  /**
   * @return license
   */
  public String getLicense() {

    return this.license;
  }

  /**
   * @param license new value of {@link #getLicense}.
   */
  public void setLicense(String license) {

    this.license = license;
  }

  /**
   * @return url
   */
  public String getUrl() {

    return this.url;
  }

  /**
   * @param url new value of {@link #getUrl}.
   */
  public void setUrl(String url) {

    this.url = url;
  }

}
