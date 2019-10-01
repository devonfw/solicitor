package com.devonfw.tools.solicitor.model.masterdata;

import java.util.List;

import com.devonfw.tools.solicitor.model.ModelRoot;

public interface Engagement {

    /**
     * This method gets the field <tt>engagementName</tt>.
     *
     * @return the field engagementName
     */
    String getEngagementName();

    /**
     * This method sets the field <tt>engagementName</tt>.
     *
     * @param engagementName the new value of the field engagementName
     */
    void setEngagementName(String engagementName);

    /**
     * This method gets the field <tt>engagementType</tt>.
     *
     * @return the field engagementType
     */
    EngagementType getEngagementType();

    /**
     * This method sets the field <tt>engagementType</tt>.
     *
     * @param engagementType the new value of the field engagementType
     */
    void setEngagementType(EngagementType engagementType);

    /**
     * This method gets the field <tt>clientName</tt>.
     *
     * @return the field clientName
     */
    String getClientName();

    /**
     * This method sets the field <tt>clientName</tt>.
     *
     * @param clientName the new value of the field clientName
     */
    void setClientName(String clientName);

    /**
     * This method gets the field <tt>goToMarketModel</tt>.
     *
     * @return the field goToMarketModel
     */
    GoToMarketModel getGoToMarketModel();

    /**
     * This method sets the field <tt>goToMarketModel</tt>.
     *
     * @param goToMarketModel the new value of the field goToMarketModel
     */
    void setGoToMarketModel(GoToMarketModel goToMarketModel);

    /**
     * This method gets the field <tt>contractAllowsOss</tt>.
     *
     * @return the field contractAllowsOss
     */
    boolean isContractAllowsOss();

    /**
     * This method sets the field <tt>contractAllowsOss</tt>.
     *
     * @param contractAllowsOss the new value of the field contractAllowsOss
     */
    void setContractAllowsOss(boolean contractAllowsOss);

    /**
     * This method gets the field <tt>ossPolicyFollowed</tt>.
     *
     * @return the field ossPolicyFollowed
     */
    boolean isOssPolicyFollowed();

    /**
     * This method sets the field <tt>ossPolicyFollowed</tt>.
     *
     * @param ossPolicyFollowed the new value of the field ossPolicyFollowed
     */
    void setOssPolicyFollowed(boolean ossPolicyFollowed);

    /**
     * This method gets the field <tt>customerProvidesOss</tt>.
     *
     * @return the field customerProvidesOss
     */
    boolean isCustomerProvidesOss();

    /**
     * This method sets the field <tt>customerProvidesOss</tt>.
     *
     * @param customerProvidesOss the new value of the field customerProvidesOss
     */
    void setCustomerProvidesOss(boolean customerProvidesOss);

    /**
     * This method gets an unmodifiable copy of the field <tt>applications</tt>.
     *
     * @return the field applications
     */
    List<Application> getApplications();

    /**
     * Adds the given {@link Application} to this EngagementImpl.
     * 
     * @param application
     */
    void addApplication(Application application);

    /**
     * Sets the field <tt>modelRoot</tt>.
     * 
     * @param modelRoot the new value of the field modelRoot
     */
    void setModelRoot(ModelRoot modelRoot);

    /**
     * This method gets the value of the field <tt>modelRoot</tt>.
     * 
     * @return the field modelRoot
     */
    ModelRoot getModelRoot();

}