package com.devonfw.tools.solicitor.model.masterdata;

import java.util.List;

import com.devonfw.tools.solicitor.model.ModelRoot;

/**
 * Represents the Engagement in the Solicitor data model.
 */
public interface Engagement {

    /**
     * Adds the given {@link Application} to this Engagement.
     *
     * @param application a {@link Application} object.
     */
    void addApplication(Application application);

    /**
     * This method gets an unmodifiable copy of the field
     * <code>applications</code>.
     *
     * @return the field applications
     */
    List<Application> getApplications();

    /**
     * This method gets the field <code>clientName</code>.
     *
     * @return the field clientName
     */
    String getClientName();

    /**
     * This method gets the field <code>engagementName</code>.
     *
     * @return the field engagementName
     */
    String getEngagementName();

    /**
     * This method gets the field <code>engagementType</code>.
     *
     * @return the field engagementType
     */
    EngagementType getEngagementType();

    /**
     * This method gets the field <code>goToMarketModel</code>.
     *
     * @return the field goToMarketModel
     */
    GoToMarketModel getGoToMarketModel();

    /**
     * This method gets the value of the field <code>modelRoot</code>.
     *
     * @return the field modelRoot
     */
    ModelRoot getModelRoot();

    /**
     * This method gets the field <code>contractAllowsOss</code>.
     *
     * @return the field contractAllowsOss
     */
    boolean isContractAllowsOss();

    /**
     * This method gets the field <code>customerProvidesOss</code>.
     *
     * @return the field customerProvidesOss
     */
    boolean isCustomerProvidesOss();

    /**
     * This method gets the field <code>ossPolicyFollowed</code>.
     *
     * @return the field ossPolicyFollowed
     */
    boolean isOssPolicyFollowed();

    /**
     * This method sets the field <code>clientName</code>.
     *
     * @param clientName the new value of the field clientName
     */
    void setClientName(String clientName);

    /**
     * This method sets the field <code>contractAllowsOss</code>.
     *
     * @param contractAllowsOss the new value of the field contractAllowsOss
     */
    void setContractAllowsOss(boolean contractAllowsOss);

    /**
     * This method sets the field <code>customerProvidesOss</code>.
     *
     * @param customerProvidesOss the new value of the field customerProvidesOss
     */
    void setCustomerProvidesOss(boolean customerProvidesOss);

    /**
     * This method sets the field <code>engagementName</code>.
     *
     * @param engagementName the new value of the field engagementName
     */
    void setEngagementName(String engagementName);

    /**
     * This method sets the field <code>engagementType</code>.
     *
     * @param engagementType the new value of the field engagementType
     */
    void setEngagementType(EngagementType engagementType);

    /**
     * This method sets the field <code>goToMarketModel</code>.
     *
     * @param goToMarketModel the new value of the field goToMarketModel
     */
    void setGoToMarketModel(GoToMarketModel goToMarketModel);

    /**
     * Sets the field <code>modelRoot</code>.
     *
     * @param modelRoot the new value of the field modelRoot
     */
    void setModelRoot(ModelRoot modelRoot);

    /**
     * This method sets the field <code>ossPolicyFollowed</code>.
     *
     * @param ossPolicyFollowed the new value of the field ossPolicyFollowed
     */
    void setOssPolicyFollowed(boolean ossPolicyFollowed);

}
