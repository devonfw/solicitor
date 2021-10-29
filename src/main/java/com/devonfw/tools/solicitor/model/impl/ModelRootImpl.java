/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.model.impl;

import java.util.Date;

import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;

/**
 * Implementation class of the root of the Solicitor data model.
 */
public class ModelRootImpl extends AbstractModelObject implements ModelRoot {

    private static final int DEFAULT_MODEL_VERSION = 3;

    private String executionTime;

    private int modelVersion;

    private String solicitorVersion;

    private String solicitorGitHash;

    private String solicitorBuilddate;

    private String extensionArtifactId;

    private String extensionVersion;

    private String extensionGitHash;

    private String extensionBuilddate;

    private Engagement engagement;

    /**
     * Constructor.
     */
    public ModelRootImpl() {

        super();
        this.modelVersion = DEFAULT_MODEL_VERSION;
        this.executionTime = (new Date()).toString();
    }

    /** {@inheritDoc} */
    @Override
    public String[] getDataElements() {

        return new String[] { this.executionTime, Integer.toString(this.modelVersion), this.solicitorVersion,
        this.solicitorGitHash, this.solicitorBuilddate, this.extensionArtifactId, this.extensionVersion,
        this.extensionGitHash, this.extensionBuilddate };
    }

    /** {@inheritDoc} */
    @Override
    public Engagement getEngagement() {

        return this.engagement;
    }

    /** {@inheritDoc} */
    @Override
    public String getExecutionTime() {

        return this.executionTime;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getHeadElements() {

        return new String[] { "executionTime", "modelVersion", "solicitorVersion", "solicitorGitHash",
        "solicitorBuilddate", "extensionArtifactId", "extensionVersion", "extensionGitHash", "extensionBuilddate" };
    }

    /** {@inheritDoc} */
    @Override
    public int getModelVersion() {

        return this.modelVersion;
    }

    /** {@inheritDoc} */
    @Override
    public String getSolicitorBuilddate() {

        return this.solicitorBuilddate;
    }

    /** {@inheritDoc} */
    @Override
    public String getSolicitorGitHash() {

        return this.solicitorGitHash;
    }

    /** {@inheritDoc} */
    @Override
    public String getSolicitorVersion() {

        return this.solicitorVersion;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtensionArtifactId() {

        return this.extensionArtifactId;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtensionVersion() {

        return this.extensionVersion;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtensionGitHash() {

        return this.extensionGitHash;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtensionBuilddate() {

        return this.extensionBuilddate;
    }

    /** {@inheritDoc} */
    @Override
    public void setEngagement(Engagement engagement) {

        this.engagement = engagement;
    }

    /** {@inheritDoc} */
    @Override
    public void setExecutionTime(String executionTime) {

        this.executionTime = executionTime;
    }

    /** {@inheritDoc} */
    @Override
    public void setModelVersion(int modelVersion) {

        this.modelVersion = modelVersion;
    }

    /** {@inheritDoc} */
    @Override
    public void setSolicitorBuilddate(String solicitorBuilddate) {

        this.solicitorBuilddate = solicitorBuilddate;
    }

    /** {@inheritDoc} */
    @Override
    public void setSolicitorGitHash(String solicitorGitHash) {

        this.solicitorGitHash = solicitorGitHash;
    }

    /** {@inheritDoc} */
    @Override
    public void setSolicitorVersion(String solicitorVersion) {

        this.solicitorVersion = solicitorVersion;
    }

    /** {@inheritDoc} */
    @Override
    public void setExtensionArtifactId(String extensionArtifactId) {

        this.extensionArtifactId = extensionArtifactId;
    }

    /** {@inheritDoc} */
    @Override
    public void setExtensionVersion(String extensionVersion) {

        this.extensionVersion = extensionVersion;
    }

    /** {@inheritDoc} */
    @Override
    public void setExtensionGitHash(String extensionGitHash) {

        this.extensionGitHash = extensionGitHash;
    }

    /** {@inheritDoc} */
    @Override
    public void setExtensionBuilddate(String extensionBuilddate) {

        this.extensionBuilddate = extensionBuilddate;
    }

    @Override
    public void completeData() {

        this.engagement.completeData();
    }

}
