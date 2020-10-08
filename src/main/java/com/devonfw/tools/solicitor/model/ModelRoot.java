/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.model;

import com.devonfw.tools.solicitor.model.masterdata.Engagement;

/**
 * Represents the root of in the Solicitor data model. Holds some information about the processing within Solicitor.
 */
public interface ModelRoot {

  /**
   * This method gets the field <code>engagement</code>.
   *
   * @return the field engagement
   */
  Engagement getEngagement();

  /**
   * This method gets the field <code>executionTime</code>.
   *
   * @return the field executionTime
   */
  String getExecutionTime();

  /**
   * This method gets the field <code>modelVersion</code>.
   *
   * @return the field modelVersion
   */
  int getModelVersion();

  /**
   * This method gets the field <code>solicitorBuilddate</code>.
   *
   * @return the field solicitorBuilddate
   */
  String getSolicitorBuilddate();

  /**
   * This method gets the field <code>solicitorGitHash</code>.
   *
   * @return the field solicitorGitHash
   */
  String getSolicitorGitHash();

  /**
   * This method gets the field <code>solicitorVersion</code>.
   *
   * @return the field solicitorVersion
   */
  String getSolicitorVersion();

  /**
   * This method gets the field <code>extensionArtifactId</code>.
   *
   * @return the field extensionArtifactId
   */
  String getExtensionArtifactId();

  /**
   * This method gets the field <code>extensionVersion</code>.
   *
   * @return the field extensionVersion
   */
  String getExtensionVersion();

  /**
   * This method gets the field <code>extensionGitHash</code>.
   *
   * @return the field extensionGitHash
   */
  public String getExtensionGitHash();

  /**
   * This method gets the field <code>extensionBuilddate</code>.
   *
   * @return the field extensionBuilddate
   */
  public String getExtensionBuilddate();

  /**
   * This method sets the field <code>engagement</code>.
   *
   * @param engagement the new value of the field engagement
   */
  void setEngagement(Engagement engagement);

  /**
   * This method sets the field <code>executionTime</code>.
   *
   * @param executionTime the new value of the field executionTime
   */
  void setExecutionTime(String executionTime);

  /**
   * This method sets the field <code>modelVersion</code>.
   *
   * @param modelVersion the new value of the field modelVersion
   */
  void setModelVersion(int modelVersion);

  /**
   * This method sets the field <code>solicitorBuilddate</code>.
   *
   * @param solicitorBuilddate the new value of the field solicitorBuilddate
   */
  void setSolicitorBuilddate(String solicitorBuilddate);

  /**
   * This method sets the field <code>solicitorGitHash</code>.
   *
   * @param solicitorGitHash the new value of the field solicitorGitHash
   */
  void setSolicitorGitHash(String solicitorGitHash);

  /**
   * This method sets the field <code>solicitorVersion</code>.
   *
   * @param solicitorVersion the new value of the field solicitorVersion
   */
  void setSolicitorVersion(String solicitorVersion);

  /**
   * This method sets the field <code>extensionArtifactId</code>.
   *
   * @param extensionArtifactId the new value of the field extensionArtifactId
   */
  void setExtensionArtifactId(String extensionArtifactId);

  /**
   * This method sets the field <code>extensionVersion</code>.
   *
   * @param extensionVersion the new value of the field extensionVersion
   */
  public void setExtensionVersion(String extensionVersion);

  /**
   * This method sets the field <code>extensionGitHash</code>.
   *
   * @param extensionGitHash the new value of the field extensionGitHash
   */
  public void setExtensionGitHash(String extensionGitHash);

  /**
   * This method sets the field <code>extensionBuilddate</code>.
   *
   * @param extensionBuilddate the new value of the field extensionBuilddate
   */
  public void setExtensionBuilddate(String extensionBuilddate);

}
