/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.impl.masterdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApplicationImpl extends AbstractDataRowSource
        implements DataRowSource, Application {
    private String name;

    private String releaseId;

    private String releaseDate;

    private String sourceRepo;

    private String programmingEcosystem;

    private List<ApplicationComponent> applicationComponents =
            new ArrayList<>();

    private Engagement engagement;

    public ApplicationImpl(String name, String releaseId, String releaseDate,
            String sourceRepo, String programmingEcosystem) {

        super();
        this.name = name;
        this.releaseId = releaseId;
        this.releaseDate = releaseDate;
        this.sourceRepo = sourceRepo;
        this.programmingEcosystem = programmingEcosystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEngagement(Engagement engagement) {

        if (this.engagement != null) {
            throw new IllegalStateException(
                    "Once the EngagementImpl is set it can not be changed");
        }
        this.engagement = engagement;
        engagement.addApplication(this);
    }

    @Override
    public String[] getHeadElements() {

        return new String[] { "applicationName", "releaseId", "releaseDate",
        "sourceRepo", "programmingEcosystem" };
    }

    @Override
    public String[] getDataElements() {

        return new String[] { name, releaseId, releaseDate, sourceRepo,
        programmingEcosystem };
    }

    @Override
    public AbstractDataRowSource getParent() {

        // TODO: How to avoid casting?
        return (AbstractDataRowSource) engagement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {

        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String name) {

        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReleaseId() {

        return releaseId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReleaseId(String releaseId) {

        this.releaseId = releaseId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReleaseDate() {

        return releaseDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReleaseDate(String releaseDate) {

        this.releaseDate = releaseDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourceRepo() {

        return sourceRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSourceRepo(String sourceRepo) {

        this.sourceRepo = sourceRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProgrammingEcosystem() {

        return programmingEcosystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProgrammingEcosystem(String programmingEcosystem) {

        this.programmingEcosystem = programmingEcosystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ApplicationComponent> getApplicationComponents() {

        return Collections.unmodifiableList(applicationComponents);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addApplicationComponent(
            ApplicationComponent applicationComponent) {

        this.applicationComponents.add(applicationComponent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public Engagement getEngagement() {

        return engagement;
    }

}
