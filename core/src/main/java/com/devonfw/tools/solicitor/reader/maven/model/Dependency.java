/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.maven.model;

import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

/**
 * Java representation of Dependency in the Maven XML data file.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Dependency {

  @XmlElement(name = "groupId")
  private String groupId;

  @XmlElement(name = "artifactId")
  private String artifactId;

  @XmlElement(name = "version")
  private String version;

  @XmlElementWrapper(name = "licenses")
  @XmlElement(name = "license")
  private ArrayList<License> licenses;

  /**
   * This method gets the field <code>artifactId</code>.
   *
   * @return the field artifactId
   */
  public String getArtifactId() {

    return this.artifactId;
  }

  /**
   * This method gets the field <code>groupId</code>.
   *
   * @return the field groupId
   */
  public String getGroupId() {

    return this.groupId;
  }

  /**
   * This method gets the field <code>licenses</code>.
   *
   * @return the field licenses
   */
  public ArrayList<License> getLicenses() {

    return this.licenses;
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
   * This method sets the field <code>artifactId</code>.
   *
   * @param artifactId the new value of the field artifactId
   */
  public void setArtifactId(String artifactId) {

    this.artifactId = artifactId;
  }

  /**
   * This method sets the field <code>groupId</code>.
   *
   * @param groupId the new value of the field groupId
   */
  public void setGroupId(String groupId) {

    this.groupId = groupId;
  }

  /**
   * This method sets the field <code>licenses</code>.
   *
   * @param licenses the new value of the field licenses
   */
  public void setLicenses(ArrayList<License> licenses) {

    this.licenses = licenses;
  }

  /**
   * This method sets the field <code>version</code>.
   *
   * @param version the new value of the field version
   */
  public void setVersion(String version) {

    this.version = version;
  }
}
