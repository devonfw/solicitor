/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.model;

import com.devonfw.tools.solicitor.model.masterdata.Engagement;

/**
 * Represents the root of in the Solicitor data model. Holds some information
 * about the processing within Solicitor.
 */
public interface ModelRoot {

    /**
     * This method gets the field <tt>engagement</tt>.
     *
     * @return the field engagement
     */
    Engagement getEngagement();

    /**
     * This method gets the field <tt>executionTime</tt>.
     *
     * @return the field executionTime
     */
    String getExecutionTime();

    /**
     * This method gets the field <tt>modelVersion</tt>.
     *
     * @return the field modelVersion
     */
    int getModelVersion();

    /**
     * This method gets the field <tt>solicitorBuilddate</tt>.
     *
     * @return the field solicitorBuilddate
     */
    String getSolicitorBuilddate();

    /**
     * This method gets the field <tt>solicitorGitHash</tt>.
     *
     * @return the field solicitorGitHash
     */
    String getSolicitorGitHash();

    /**
     * This method gets the field <tt>solicitorVersion</tt>.
     *
     * @return the field solicitorVersion
     */
    String getSolicitorVersion();

    /**
     * This method gets the field <tt>extensionArtifactId</tt>.
     *
     * @return the field extensionArtifactId
     */
    String getExtensionArtifactId();

    /**
     * This method gets the field <tt>extensionVersion</tt>.
     *
     * @return the field extensionVersion
     */
    String getExtensionVersion();

    /**
     * This method gets the field <tt>extensionGitHash</tt>.
     *
     * @return the field extensionGitHash
     */
    public String getExtensionGitHash();

    /**
     * This method gets the field <tt>extensionBuilddate</tt>.
     *
     * @return the field extensionBuilddate
     */
    public String getExtensionBuilddate();

    /**
     * This method sets the field <tt>engagement</tt>.
     *
     * @param engagement the new value of the field engagement
     */
    void setEngagement(Engagement engagement);

    /**
     * This method sets the field <tt>executionTime</tt>.
     *
     * @param executionTime the new value of the field executionTime
     */
    void setExecutionTime(String executionTime);

    /**
     * This method sets the field <tt>modelVersion</tt>.
     *
     * @param modelVersion the new value of the field modelVersion
     */
    void setModelVersion(int modelVersion);

    /**
     * This method sets the field <tt>solicitorBuilddate</tt>.
     *
     * @param solicitorBuilddate the new value of the field solicitorBuilddate
     */
    void setSolicitorBuilddate(String solicitorBuilddate);

    /**
     * This method sets the field <tt>solicitorGitHash</tt>.
     *
     * @param solicitorGitHash the new value of the field solicitorGitHash
     */
    void setSolicitorGitHash(String solicitorGitHash);

    /**
     * This method sets the field <tt>solicitorVersion</tt>.
     *
     * @param solicitorVersion the new value of the field solicitorVersion
     */
    void setSolicitorVersion(String solicitorVersion);

    /**
     * This method sets the field <tt>extensionArtifactId</tt>.
     *
     * @param extensionArtifactId the new value of the field extensionArtifactId
     */
    void setExtensionArtifactId(String extensionArtifactId);

    /**
     * This method sets the field <tt>extensionVersion</tt>.
     *
     * @param extensionVersion the new value of the field extensionVersion
     */
    public void setExtensionVersion(String extensionVersion);

    /**
     * This method sets the field <tt>extensionGitHash</tt>.
     *
     * @param extensionGitHash the new value of the field extensionGitHash
     */
    public void setExtensionGitHash(String extensionGitHash);

    /**
     * This method sets the field <tt>extensionBuilddate</tt>.
     *
     * @param extensionBuilddate the new value of the field extensionBuilddate
     */
    public void setExtensionBuilddate(String extensionBuilddate);

}
