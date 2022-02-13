/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle.model;

import java.util.List;

/**
 * Java representation of Dependency in the Gradle JSON data file.
 */
public class Dependency {
  private String name;

  private String project;

  private String version;

  private String year;

  private String url;

  private String dependency;

  private List<License> licenses;

  /**
   * This method gets the field <code>dependency</code>.
   *
   * @return the field dependency
   */
  public String getDependency() {

    return this.dependency;
  }

  /**
   * This method gets the field <code>licenses</code>.
   *
   * @return the field licenses
   */
  public List<License> getLicenses() {

    return this.licenses;
  }

  /**
   * This method gets the field <code>name</code>.
   *
   * @return the field name
   */
  public String getName() {

    return this.name;
  }

  /**
   * This method gets the field <code>project</code>.
   *
   * @return the field project
   */
  public String getProject() {

    return this.project;
  }

  /**
   * This method gets the field <code>url</code>.
   *
   * @return the field url
   */
  public String getUrl() {

    return this.url;
  }

  /**
   * This method gets the field <code>version</code>.
   *
   * @return the field version
   */
  public String getVersion() {

    return this.version;
  }

  /**
   * This method gets the field <code>year</code>.
   *
   * @return the field year
   */
  public String getYear() {

    return this.year;
  }

  /**
   * This method sets the field <code>dependency</code>.
   *
   * @param dependency the new value of the field dependency
   */
  public void setDependency(String dependency) {

    this.dependency = dependency;
  }

  /**
   * This method sets the field <code>licenses</code>.
   *
   * @param licenses the new value of the field licenses
   */
  public void setLicenses(List<License> licenses) {

    this.licenses = licenses;
  }

  /**
   * This method sets the field <code>name</code>.
   *
   * @param name the new value of the field name
   */
  public void setName(String name) {

    this.name = name;
  }

  /**
   * This method sets the field <code>project</code>.
   *
   * @param project the new value of the field project
   */
  public void setProject(String project) {

    this.project = project;
  }

  /**
   * This method sets the field <code>url</code>.
   *
   * @param url the new value of the field url
   */
  public void setUrl(String url) {

    this.url = url;
  }

  /**
   * This method sets the field <code>version</code>.
   *
   * @param version the new value of the field version
   */
  public void setVersion(String version) {

    this.version = version;
  }

  /**
   * This method sets the field <code>year</code>.
   *
   * @param year the new value of the field year
   */
  public void setYear(String year) {

    this.year = year;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {

    String licensestring = "";
    for (License l : this.licenses) {
      licensestring += l + "\n";
    }
    return "";
  }
}
