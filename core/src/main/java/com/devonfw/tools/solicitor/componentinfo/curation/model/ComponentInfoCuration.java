package com.devonfw.tools.solicitor.componentinfo.curation.model;

import java.util.List;

import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Component curation info.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComponentInfoCuration {

  private String name;

  private String note;

  private String url;

  private List<String> copyrights;

  private List<LicenseInfoCuration> licenses;

  private List<String> excludedPaths;

  private List<LicenseCuration> licenseCurations;

  private List<CopyrightCuration> copyrightCurations;

  /**
   * The constructor.
   */
  public ComponentInfoCuration() {

  }

  /**
   * @return name
   */
  public String getName() {

    return this.name;
  }

  /**
   * @param name new value of {@link #getName}.
   */
  public void setName(String name) {

    this.name = name;
  }

  /**
   * @return note
   */
  public String getNote() {

    return this.note;
  }

  /**
   * @param note new value of {@link #getNote}.
   */
  public void setNote(String note) {

    this.note = note;
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

  /**
   * @return copyrights
   */
  public List<String> getCopyrights() {

    return this.copyrights;
  }

  /**
   * @param copyrights new value of {@link #getCopyrights}.
   */
  public void setCopyrights(List<String> copyrights) {

    this.copyrights = copyrights;
  }

  /**
   * @return licenses
   */
  public List<LicenseInfoCuration> getLicenses() {

    return this.licenses;
  }

  /**
   * @param licenses new value of {@link #getLicenses}.
   */
  public void setLicenses(List<LicenseInfoCuration> licenses) {

    this.licenses = licenses;
  }

  /**
   * @return excluded paths
   */
  public List<String> getExcludedPaths() {

    return this.excludedPaths;
  }

  /**
   * @param excludedPaths new value of {@link #getExcludedPaths}.
   */
  public void setExcludedPaths(List<String> excludedPaths) {

    this.excludedPaths = excludedPaths;
  }

  /**
   * @return licenseCurations
   */
  public List<LicenseCuration> getLicenseCurations() {

    return this.licenseCurations;
  }

  /**
   * @param licenseCurations new value of {@link #getLicenseCurations}.
   */
  public void setLicenseCurations(List<LicenseCuration> licenseCurations) {

    this.licenseCurations = licenseCurations;
  }

  /**
   * @return copyrightCurations
   */
  public List<CopyrightCuration> getCopyrightCurations() {

    return this.copyrightCurations;
  }

  /**
   * @param copyrightCurations new value of {@link #getCopyrightCurations}.
   */
  public void setCopyrightCurations(List<CopyrightCuration> copyrightCurations) {

    this.copyrightCurations = copyrightCurations;
  }

  /**
   * Validates the curation data.
   *
   * @throws CurationInvalidException indicates that the curation data is not valid.
   */
  public void validate() throws CurationInvalidException {

    if (this.licenseCurations != null) {
      for (LicenseCuration lc : this.licenseCurations) {
        lc.validate();
      }
    }
    if (this.copyrightCurations != null) {
      for (CopyrightCuration cc : this.copyrightCurations) {
        cc.validate();
      }
    }

  }

}
