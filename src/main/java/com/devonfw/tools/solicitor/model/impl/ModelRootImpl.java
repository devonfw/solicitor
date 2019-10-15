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

    private static final int DEFAULT_MODEL_VERSION = 2;

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
        modelVersion = DEFAULT_MODEL_VERSION;
        executionTime = (new Date()).toString();
    }

    /** {@inheritDoc} */
    @Override
    public String[] getDataElements() {

        return new String[] { executionTime, Integer.toString(modelVersion), solicitorVersion, solicitorGitHash,
        solicitorBuilddate, extensionArtifactId, extensionVersion, extensionGitHash, extensionBuilddate };
    }

    /** {@inheritDoc} */
    @Override
    public Engagement getEngagement() {

        return engagement;
    }

    /** {@inheritDoc} */
    @Override
    public String getExecutionTime() {

        return executionTime;
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

        return modelVersion;
    }

    /** {@inheritDoc} */
    @Override
    public String getSolicitorBuilddate() {

        return solicitorBuilddate;
    }

    /** {@inheritDoc} */
    @Override
    public String getSolicitorGitHash() {

        return solicitorGitHash;
    }

    /** {@inheritDoc} */
    @Override
    public String getSolicitorVersion() {

        return solicitorVersion;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtensionArtifactId() {

        return extensionArtifactId;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtensionVersion() {

        return extensionVersion;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtensionGitHash() {

        return extensionGitHash;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtensionBuilddate() {

        return extensionBuilddate;
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

}
