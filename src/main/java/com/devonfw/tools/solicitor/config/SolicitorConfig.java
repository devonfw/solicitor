/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import java.util.ArrayList;
import java.util.List;

import com.devonfw.tools.solicitor.model.masterdata.EngagementType;
import com.devonfw.tools.solicitor.model.masterdata.GoToMarketModel;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the complete Solicitor JSON configuration file.
 * 
 */
public class SolicitorConfig {

    @JsonProperty
    private int version;

    @JsonProperty
    private String comment;

    @JsonProperty
    private String engagementName;

    @JsonProperty
    private EngagementType engagementType;

    @JsonProperty
    private String clientName;

    @JsonProperty
    private GoToMarketModel goToMarketModel;

    @JsonProperty
    private boolean contractAllowsOss;

    @JsonProperty
    private boolean ossPolicyFollowed;

    @JsonProperty
    private boolean customerProvidesOss;

    @JsonProperty
    private List<ApplicationConfig> applications = new ArrayList<>();

    @JsonProperty
    private List<RuleConfig> rules = new ArrayList<>();

    @JsonProperty
    private List<WriterConfig> writers = new ArrayList<>();

    /**
     * This method gets the field <tt>applications</tt>.
     *
     * @return the field applications
     */
    public List<ApplicationConfig> getApplications() {

        return applications;
    }

    /**
     * This method gets the field <tt>clientName</tt>.
     *
     * @return the field clientName
     */
    public String getClientName() {

        return clientName;
    }

    /**
     * This method gets the field <tt>comment</tt>.
     *
     * @return the field comment
     */
    public String getComment() {

        return comment;
    }

    /**
     * This method gets the field <tt>engagementName</tt>.
     *
     * @return the field engagementName
     */
    public String getEngagementName() {

        return engagementName;
    }

    /**
     * This method gets the field <tt>engagementType</tt>.
     *
     * @return the field engagementType
     */
    public EngagementType getEngagementType() {

        return engagementType;
    }

    /**
     * This method gets the field <tt>goToMarketModel</tt>.
     *
     * @return the field goToMarketModel
     */
    public GoToMarketModel getGoToMarketModel() {

        return goToMarketModel;
    }

    /**
     * This method gets the field <tt>rules</tt>.
     *
     * @return the field rules
     */
    public List<RuleConfig> getRules() {

        return rules;
    }

    /**
     * This method gets the field <tt>version</tt>.
     *
     * @return the field version
     */
    public int getVersion() {

        return version;
    }

    /**
     * This method gets the field <tt>writers</tt>.
     *
     * @return the field writers
     */
    public List<WriterConfig> getWriters() {

        return writers;
    }

    /**
     * This method gets the field <tt>contractAllowsOss</tt>.
     *
     * @return the field contractAllowsOss
     */
    public boolean isContractAllowsOss() {

        return contractAllowsOss;
    }

    /**
     * This method gets the field <tt>customerProvidesOss</tt>.
     *
     * @return the field customerProvidesOss
     */
    public boolean isCustomerProvidesOss() {

        return customerProvidesOss;
    }

    /**
     * This method gets the field <tt>ossPolicyFollowed</tt>.
     *
     * @return the field ossPolicyFollowed
     */
    public boolean isOssPolicyFollowed() {

        return ossPolicyFollowed;
    }

    /**
     * This method sets the field <tt>applications</tt>.
     *
     * @param applications the new value of the field applications
     */
    public void setApplications(List<ApplicationConfig> applications) {

        this.applications = applications;
    }

    /**
     * This method sets the field <tt>clientName</tt>.
     *
     * @param clientName the new value of the field clientName
     */
    public void setClientName(String clientName) {

        this.clientName = clientName;
    }

    /**
     * This method sets the field <tt>comment</tt>.
     *
     * @param comment the new value of the field comment
     */
    public void setComment(String comment) {

        this.comment = comment;
    }

    /**
     * This method sets the field <tt>contractAllowsOss</tt>.
     *
     * @param contractAllowsOss the new value of the field contractAllowsOss
     */
    public void setContractAllowsOss(boolean contractAllowsOss) {

        this.contractAllowsOss = contractAllowsOss;
    }

    /**
     * This method sets the field <tt>customerProvidesOss</tt>.
     *
     * @param customerProvidesOss the new value of the field customerProvidesOss
     */
    public void setCustomerProvidesOss(boolean customerProvidesOss) {

        this.customerProvidesOss = customerProvidesOss;
    }

    /**
     * This method sets the field <tt>engagementName</tt>.
     *
     * @param engagementName the new value of the field engagementName
     */
    public void setEngagementName(String engagementName) {

        this.engagementName = engagementName;
    }

    /**
     * This method sets the field <tt>engagementType</tt>.
     *
     * @param engagementType the new value of the field engagementType
     */
    public void setEngagementType(EngagementType engagementType) {

        this.engagementType = engagementType;
    }

    /**
     * This method sets the field <tt>goToMarketModel</tt>.
     *
     * @param goToMarketModel the new value of the field goToMarketModel
     */
    public void setGoToMarketModel(GoToMarketModel goToMarketModel) {

        this.goToMarketModel = goToMarketModel;
    }

    /**
     * This method sets the field <tt>ossPolicyFollowed</tt>.
     *
     * @param ossPolicyFollowed the new value of the field ossPolicyFollowed
     */
    public void setOssPolicyFollowed(boolean ossPolicyFollowed) {

        this.ossPolicyFollowed = ossPolicyFollowed;
    }

    /**
     * This method sets the field <tt>rules</tt>.
     *
     * @param rules the new value of the field rules
     */
    public void setRules(List<RuleConfig> rules) {

        this.rules = rules;
    }

    /**
     * This method sets the field <tt>version</tt>.
     *
     * @param version the new value of the field version
     */
    public void setVersion(int version) {

        this.version = version;
    }

    /**
     * This method sets the field <tt>writers</tt>.
     *
     * @param writers the new value of the field writers
     */
    public void setWriters(List<WriterConfig> writers) {

        this.writers = writers;
    }

}
