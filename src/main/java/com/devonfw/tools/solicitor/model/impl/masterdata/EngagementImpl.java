/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.impl.masterdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.model.masterdata.EngagementType;
import com.devonfw.tools.solicitor.model.masterdata.GoToMarketModel;

public class EngagementImpl extends AbstractDataRowSource
        implements DataRowSource, Engagement {

    private String engagementName;

    private EngagementType engagementType;

    private String clientName;

    private GoToMarketModel goToMarketModel;

    private boolean contractAllowsOss;

    private boolean ossPolicyFollowed;

    private boolean customerProvidesOss;

    private List<Application> applications = new ArrayList<>();

    public EngagementImpl(String engagementName, EngagementType engagementType,
            String clientName, GoToMarketModel goToMarketModel) {

        super();
        this.engagementName = engagementName;
        this.engagementType = engagementType;
        this.clientName = clientName;
        this.goToMarketModel = goToMarketModel;
    }

    @Override
    public String[] getHeadElements() {

        return new String[] { "engagementName", "engagementType", "clientName",
        "goToMarketModel", "contractAllowsOss", "ossPolicyFollowed",
        "customerProvidesOss" };
    }

    @Override
    public String[] getDataElements() {

        return new String[] { engagementName, engagementType.toString(),
        clientName, goToMarketModel.toString(),
        contractAllowsOss ? "true" : "false",
        ossPolicyFollowed ? "true" : "false",
        customerProvidesOss ? "true" : "false" };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEngagementName() {

        return engagementName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEngagementName(String engagementName) {

        this.engagementName = engagementName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EngagementType getEngagementType() {

        return engagementType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEngagementType(EngagementType engagementType) {

        this.engagementType = engagementType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientName() {

        return clientName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientName(String clientName) {

        this.clientName = clientName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoToMarketModel getGoToMarketModel() {

        return goToMarketModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGoToMarketModel(GoToMarketModel goToMarketModel) {

        this.goToMarketModel = goToMarketModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContractAllowsOss() {

        return contractAllowsOss;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContractAllowsOss(boolean contractAllowsOss) {

        this.contractAllowsOss = contractAllowsOss;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOssPolicyFollowed() {

        return ossPolicyFollowed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOssPolicyFollowed(boolean ossPolicyFollowed) {

        this.ossPolicyFollowed = ossPolicyFollowed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCustomerProvidesOss() {

        return customerProvidesOss;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomerProvidesOss(boolean customerProvidesOss) {

        this.customerProvidesOss = customerProvidesOss;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Application> getApplications() {

        return Collections.unmodifiableList(applications);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addApplication(Application application) {

        applications.add(application);
    }

}
