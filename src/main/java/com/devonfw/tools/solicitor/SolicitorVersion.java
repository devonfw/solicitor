/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Solicitor version information.
 */
@Component
@PropertySource({ "classpath:com/devonfw/tools/solicitor/version.properties" })
@ConfigurationProperties(prefix = "solicitor")
public class SolicitorVersion {

  private static final String EXTENSION_ARTIFACT_DEFAULT = "NONE";

  private String artifact;

  private String version;

  private String githash;

  private String builddate;

  private String extensionArtifact;

  private String extensionVersion;

  private String extensionGithash;

  private String extensionBuilddate;

  private String extensionExpectedSolicitorVersionRange;

  private String extensionMessage1;

  private String extensionMessage2;

  /**
   * Constructor.
   */
  public SolicitorVersion() {

  }

  /**
   * This method gets the field <code>artifact</code>.
   *
   * @return the field artifact
   */
  public String getArtifact() {

    return this.artifact;
  }

  /**
   * This method gets the field <code>builddate</code>.
   *
   * @return the field builddate
   */
  public String getBuilddate() {

    return this.builddate;
  }

  /**
   * This method gets the field <code>githash</code>.
   *
   * @return the field githash
   */
  public String getGithash() {

    return this.githash;
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
   * This method sets the field <code>artifact</code>.
   *
   * @param artifact the new value of the field artifact
   */
  public void setArtifact(String artifact) {

    this.artifact = artifact;
  }

  /**
   * This method sets the field <code>builddate</code>.
   *
   * @param builddate the new value of the field builddate
   */
  public void setBuilddate(String builddate) {

    this.builddate = builddate;
  }

  /**
   * This method sets the field <code>githash</code>.
   *
   * @param githash the new value of the field githash
   */
  public void setGithash(String githash) {

    this.githash = githash;
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
   * This method gets the field <code>extensionArtifact</code>.
   *
   * @return the field extensionArtifact
   */
  public String getExtensionArtifact() {

    return this.extensionArtifact;
  }

  /**
   * This method sets the field <code>extensionArtifact</code>.
   *
   * @param extensionArtifact the new value of the field extensionArtifact
   */
  public void setExtensionArtifact(String extensionArtifact) {

    this.extensionArtifact = extensionArtifact;
  }

  /**
   * This method gets the field <code>extensionVersion</code>.
   *
   * @return the field extensionVersion
   */
  public String getExtensionVersion() {

    return this.extensionVersion;
  }

  /**
   * This method sets the field <code>extensionVersion</code>.
   *
   * @param extensionVersion the new value of the field extensionVersion
   */
  public void setExtensionVersion(String extensionVersion) {

    this.extensionVersion = extensionVersion;
  }

  /**
   * This method gets the field <code>extensionGithash</code>.
   *
   * @return the field extensionGithash
   */
  public String getExtensionGithash() {

    return this.extensionGithash;
  }

  /**
   * This method sets the field <code>extensionGithash</code>.
   *
   * @param extensionGithash the new value of the field extensionGithash
   */
  public void setExtensionGithash(String extensionGithash) {

    this.extensionGithash = extensionGithash;
  }

  /**
   * This method gets the field <code>extensionBuilddate</code>.
   *
   * @return the field extensionBuilddate
   */
  public String getExtensionBuilddate() {

    return this.extensionBuilddate;
  }

  /**
   * This method sets the field <code>extensionBuilddate</code>.
   *
   * @param extensionBuilddate the new value of the field extensionBuilddate
   */
  public void setExtensionBuilddate(String extensionBuilddate) {

    this.extensionBuilddate = extensionBuilddate;
  }

  /**
   * This method gets the field <code>extensionExpectedSolicitorVersionRange</code>.
   *
   * @return the field extensionExpectedSolicitorVersionRange
   */
  public String getExtensionExpectedSolicitorVersionRange() {

    return this.extensionExpectedSolicitorVersionRange;
  }

  /**
   * This method sets the field <code>extensionExpectedSolicitorVersionRange</code>.
   *
   * @param extensionExpectedSolicitorVersionRange the new value of the field extensionExpectedSolicitorVersionRange
   */
  public void setExtensionExpectedSolicitorVersionRange(String extensionExpectedSolicitorVersionRange) {

    this.extensionExpectedSolicitorVersionRange = extensionExpectedSolicitorVersionRange;
  }

  /**
   * This method gets the field <code>extensionMessage1</code>.
   *
   * @return the field extensionMessage1
   */
  public String getExtensionMessage1() {

    return this.extensionMessage1;
  }

  /**
   * This method sets the field <code>extensionMessage1</code>.
   *
   * @param extensionMessage1 the new value of the field extensionMessage1
   */
  public void setExtensionMessage1(String extensionMessage1) {

    this.extensionMessage1 = extensionMessage1;
  }

  /**
   * This method gets the field <code>extensionMessage2</code>.
   *
   * @return the field extensionMessage2
   */
  public String getExtensionMessage2() {

    return this.extensionMessage2;
  }

  /**
   * This method sets the field <code>extensionMessage2</code>.
   *
   * @param extensionMessage2 the new value of the field extensionMessage2
   */
  public void setExtensionMessage2(String extensionMessage2) {

    this.extensionMessage2 = extensionMessage2;
  }

  public boolean isExtensionPresent() {

    return !EXTENSION_ARTIFACT_DEFAULT.equals(getExtensionArtifact());
  }
}
