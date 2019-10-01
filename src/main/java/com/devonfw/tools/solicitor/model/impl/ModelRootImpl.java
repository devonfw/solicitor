/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.model.impl;

import java.util.Date;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;

/**
 * Root class of the Solicitor data model.
 */
public class ModelRootImpl extends AbstractDataRowSource
        implements DataRowSource, ModelRoot {

    private static final int DEFAULT_MODEL_VERSION = 2;

    public ModelRootImpl() {

        super();
        modelVersion = DEFAULT_MODEL_VERSION;
        executionTime = (new Date()).toString();
    }

    private String executionTime;

    private int modelVersion;

    private String solicitorVersion;

    private String solicitorGitHash;

    private String solicitorBuilddate;

    private Engagement engagement;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExecutionTime() {

        return executionTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExecutionTime(String executionTime) {

        this.executionTime = executionTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModelVersion() {

        return modelVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModelVersion(int modelVersion) {

        this.modelVersion = modelVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSolicitorVersion() {

        return solicitorVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSolicitorVersion(String solicitorVersion) {

        this.solicitorVersion = solicitorVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSolicitorGitHash() {

        return solicitorGitHash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSolicitorGitHash(String solicitorGitHash) {

        this.solicitorGitHash = solicitorGitHash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSolicitorBuilddate() {

        return solicitorBuilddate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSolicitorBuilddate(String solicitorBuilddate) {

        this.solicitorBuilddate = solicitorBuilddate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Engagement getEngagement() {

        return engagement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEngagement(Engagement engagement) {

        this.engagement = engagement;
    }

    @Override
    public String[] getHeadElements() {

        return new String[] { "executionTime", "modelVersion",
        "solicitorVersion", "solicitorGitHash", "solicitorBuilddate" };
    }

    @Override
    public String[] getDataElements() {

        return new String[] { executionTime, Integer.toString(modelVersion),
        solicitorVersion, solicitorGitHash, solicitorBuilddate };
    }

}
