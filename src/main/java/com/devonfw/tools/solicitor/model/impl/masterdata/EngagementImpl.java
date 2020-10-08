/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.impl.masterdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.impl.AbstractModelObject;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.model.masterdata.EngagementType;
import com.devonfw.tools.solicitor.model.masterdata.GoToMarketModel;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Implementation of the {@link Engagement} model object interface.
 */
public class EngagementImpl extends AbstractModelObject implements Engagement {

  private ModelRoot modelRoot;

  private String engagementName;

  private EngagementType engagementType;

  private String clientName;

  private GoToMarketModel goToMarketModel;

  private boolean contractAllowsOss;

  private boolean ossPolicyFollowed;

  private boolean customerProvidesOss;

  private List<Application> applications = new ArrayList<>();

  /**
   * Constructor.
   *
   * @param engagementName the name of the engagement
   * @param engagementType the type of engagement
   * @param clientName name of the client
   * @param goToMarketModel the model how this goes to market
   */
  public EngagementImpl(String engagementName, EngagementType engagementType, String clientName,
      GoToMarketModel goToMarketModel) {

    super();
    this.engagementName = engagementName;
    this.engagementType = engagementType;
    this.clientName = clientName;
    this.goToMarketModel = goToMarketModel;
  }

  /** {@inheritDoc} */
  @Override
  public void addApplication(Application application) {

    this.applications.add(application);
  }

  /** {@inheritDoc} */
  @Override
  protected ModelRoot doGetParent() {

    return this.modelRoot;
  }

  /** {@inheritDoc} */
  @Override
  public List<Application> getApplications() {

    return Collections.unmodifiableList(this.applications);
  }

  /** {@inheritDoc} */
  @Override
  public String getClientName() {

    return this.clientName;
  }

  /** {@inheritDoc} */
  @Override
  public String[] getDataElements() {

    return new String[] { this.engagementName, this.engagementType.toString(), this.clientName,
    this.goToMarketModel.toString(), this.contractAllowsOss ? "true" : "false",
    this.ossPolicyFollowed ? "true" : "false", this.customerProvidesOss ? "true" : "false" };
  }

  /** {@inheritDoc} */
  @Override
  public String getEngagementName() {

    return this.engagementName;
  }

  /** {@inheritDoc} */
  @Override
  public EngagementType getEngagementType() {

    return this.engagementType;
  }

  /** {@inheritDoc} */
  @Override
  public GoToMarketModel getGoToMarketModel() {

    return this.goToMarketModel;
  }

  /** {@inheritDoc} */
  @Override
  public String[] getHeadElements() {

    return new String[] { "engagementName", "engagementType", "clientName", "goToMarketModel", "contractAllowsOss",
    "ossPolicyFollowed", "customerProvidesOss" };
  }

  /**
   * {@inheritDoc}
   *
   * This method gets the field <code>modelRoot</code>.
   */
  @Override
  @JsonIgnore
  public ModelRoot getModelRoot() {

    return this.modelRoot;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isContractAllowsOss() {

    return this.contractAllowsOss;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isCustomerProvidesOss() {

    return this.customerProvidesOss;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOssPolicyFollowed() {

    return this.ossPolicyFollowed;
  }

  /** {@inheritDoc} */
  @Override
  public void setClientName(String clientName) {

    this.clientName = clientName;
  }

  /** {@inheritDoc} */
  @Override
  public void setContractAllowsOss(boolean contractAllowsOss) {

    this.contractAllowsOss = contractAllowsOss;
  }

  /** {@inheritDoc} */
  @Override
  public void setCustomerProvidesOss(boolean customerProvidesOss) {

    this.customerProvidesOss = customerProvidesOss;
  }

  /** {@inheritDoc} */
  @Override
  public void setEngagementName(String engagementName) {

    this.engagementName = engagementName;
  }

  /** {@inheritDoc} */
  @Override
  public void setEngagementType(EngagementType engagementType) {

    this.engagementType = engagementType;
  }

  /** {@inheritDoc} */
  @Override
  public void setGoToMarketModel(GoToMarketModel goToMarketModel) {

    this.goToMarketModel = goToMarketModel;
  }

  /**
   * {@inheritDoc}
   *
   * This method sets the field <code>modelRoot</code>.
   */
  @Override
  public void setModelRoot(ModelRoot modelRoot) {

    if (this.modelRoot != null) {
      throw new IllegalStateException("Once the ModelImpl is set it can not be changed");
    }
    this.modelRoot = modelRoot;
    modelRoot.setEngagement(this);
  }

  /** {@inheritDoc} */
  @Override
  public void setOssPolicyFollowed(boolean ossPolicyFollowed) {

    this.ossPolicyFollowed = ossPolicyFollowed;
  }

}
