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

  @JsonProperty
  private List<WriterConfig> additionalWriters = new ArrayList<>();

  /**
   * This method gets the field <code>applications</code>.
   *
   * @return the field applications
   */
  public List<ApplicationConfig> getApplications() {

    return this.applications;
  }

  /**
   * This method gets the field <code>clientName</code>.
   *
   * @return the field clientName
   */
  public String getClientName() {

    return this.clientName;
  }

  /**
   * This method gets the field <code>comment</code>.
   *
   * @return the field comment
   */
  public String getComment() {

    return this.comment;
  }

  /**
   * This method gets the field <code>engagementName</code>.
   *
   * @return the field engagementName
   */
  public String getEngagementName() {

    return this.engagementName;
  }

  /**
   * This method gets the field <code>engagementType</code>.
   *
   * @return the field engagementType
   */
  public EngagementType getEngagementType() {

    return this.engagementType;
  }

  /**
   * This method gets the field <code>goToMarketModel</code>.
   *
   * @return the field goToMarketModel
   */
  public GoToMarketModel getGoToMarketModel() {

    return this.goToMarketModel;
  }

  /**
   * This method gets the field <code>rules</code>.
   *
   * @return the field rules
   */
  public List<RuleConfig> getRules() {

    return this.rules;
  }

  /**
   * This method gets the field <code>version</code>.
   *
   * @return the field version
   */
  public int getVersion() {

    return this.version;
  }

  /**
   * This method gets the field <code>writers</code>.
   *
   * @return the field writers
   */
  public List<WriterConfig> getWriters() {

    return this.writers;
  }

  /**
   * This method gets the field <code>additionalWriters</code>.
   *
   * @return the field additionalWriters
   */
  public List<WriterConfig> getAdditionalWriters() {

    return this.additionalWriters;
  }

  /**
   * This method gets the field <code>contractAllowsOss</code>.
   *
   * @return the field contractAllowsOss
   */
  public boolean isContractAllowsOss() {

    return this.contractAllowsOss;
  }

  /**
   * This method gets the field <code>customerProvidesOss</code>.
   *
   * @return the field customerProvidesOss
   */
  public boolean isCustomerProvidesOss() {

    return this.customerProvidesOss;
  }

  /**
   * This method gets the field <code>ossPolicyFollowed</code>.
   *
   * @return the field ossPolicyFollowed
   */
  public boolean isOssPolicyFollowed() {

    return this.ossPolicyFollowed;
  }

  /**
   * This method sets the field <code>applications</code>.
   *
   * @param applications the new value of the field applications
   */
  public void setApplications(List<ApplicationConfig> applications) {

    this.applications = applications;
  }

  /**
   * This method sets the field <code>clientName</code>.
   *
   * @param clientName the new value of the field clientName
   */
  public void setClientName(String clientName) {

    this.clientName = clientName;
  }

  /**
   * This method sets the field <code>comment</code>.
   *
   * @param comment the new value of the field comment
   */
  public void setComment(String comment) {

    this.comment = comment;
  }

  /**
   * This method sets the field <code>contractAllowsOss</code>.
   *
   * @param contractAllowsOss the new value of the field contractAllowsOss
   */
  public void setContractAllowsOss(boolean contractAllowsOss) {

    this.contractAllowsOss = contractAllowsOss;
  }

  /**
   * This method sets the field <code>customerProvidesOss</code>.
   *
   * @param customerProvidesOss the new value of the field customerProvidesOss
   */
  public void setCustomerProvidesOss(boolean customerProvidesOss) {

    this.customerProvidesOss = customerProvidesOss;
  }

  /**
   * This method sets the field <code>engagementName</code>.
   *
   * @param engagementName the new value of the field engagementName
   */
  public void setEngagementName(String engagementName) {

    this.engagementName = engagementName;
  }

  /**
   * This method sets the field <code>engagementType</code>.
   *
   * @param engagementType the new value of the field engagementType
   */
  public void setEngagementType(EngagementType engagementType) {

    this.engagementType = engagementType;
  }

  /**
   * This method sets the field <code>goToMarketModel</code>.
   *
   * @param goToMarketModel the new value of the field goToMarketModel
   */
  public void setGoToMarketModel(GoToMarketModel goToMarketModel) {

    this.goToMarketModel = goToMarketModel;
  }

  /**
   * This method sets the field <code>ossPolicyFollowed</code>.
   *
   * @param ossPolicyFollowed the new value of the field ossPolicyFollowed
   */
  public void setOssPolicyFollowed(boolean ossPolicyFollowed) {

    this.ossPolicyFollowed = ossPolicyFollowed;
  }

  /**
   * This method sets the field <code>rules</code>.
   *
   * @param rules the new value of the field rules
   */
  public void setRules(List<RuleConfig> rules) {

    this.rules = rules;
  }

  /**
   * This method sets the field <code>version</code>.
   *
   * @param version the new value of the field version
   */
  public void setVersion(int version) {

    this.version = version;
  }

  /**
   * This method sets the field <code>writers</code>.
   *
   * @param writers the new value of the field writers
   */
  public void setWriters(List<WriterConfig> writers) {

    this.writers = writers;
  }

  /**
   * This method sets the field <code>additionalWriters</code>
   *
   * @param additionalWriters new value of the field additionalWriters
   */
  public void setAdditionalWriters(List<WriterConfig> additionalWriters) {

    this.additionalWriters = additionalWriters;
  }

}
