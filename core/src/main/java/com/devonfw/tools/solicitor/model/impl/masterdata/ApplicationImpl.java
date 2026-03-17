/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.impl.masterdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.devonfw.tools.solicitor.common.ReportingGroupHandler;
import com.devonfw.tools.solicitor.model.impl.AbstractModelObject;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.impl.inventory.ApplicationComponentImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Implementation of the {@link Application} model object interface.
 */
public class ApplicationImpl extends AbstractModelObject implements Application {
  private String name;

  private String releaseId;

  private String releaseDate;

  private String sourceRepo;

  private String programmingEcosystem;

  private String reportingGroups;

  private List<ApplicationComponent> applicationComponents = new ArrayList<>();

  private Engagement engagement;

  /**
   * Constructor.
   *
   */
  public ApplicationImpl() {

    super();
  }

  /** {@inheritDoc} */
  @Override
  public void addApplicationComponent(ApplicationComponent applicationComponent) {

    this.applicationComponents.add(applicationComponent);
  }

  /** {@inheritDoc} */
  @Override
  protected Engagement doGetParent() {

    return this.engagement;
  }

  /** {@inheritDoc} */
  @Override
  public List<ApplicationComponent> getApplicationComponents() {

    return Collections.unmodifiableList(this.applicationComponents);
  }

  /** {@inheritDoc} */
  @Override
  public String[] getDataElements() {

    return new String[] { this.name, this.releaseId, this.releaseDate, this.sourceRepo, this.programmingEcosystem,
    this.reportingGroups };
  }

  /** {@inheritDoc} */
  @Override
  @JsonIgnore
  public Engagement getEngagement() {

    return this.engagement;
  }

  /** {@inheritDoc} */
  @Override
  public String[] getHeadElements() {

    return new String[] { "applicationName", "releaseId", "releaseDate", "sourceRepo", "programmingEcosystem",
    "reportingGroups" };
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {

    return this.name;
  }

  /** {@inheritDoc} */
  @Override
  public String getProgrammingEcosystem() {

    return this.programmingEcosystem;
  }

  /** {@inheritDoc} */
  @Override
  public String getReleaseDate() {

    return this.releaseDate;
  }

  /** {@inheritDoc} */
  @Override
  public String getReleaseId() {

    return this.releaseId;
  }

  /** {@inheritDoc} */
  @Override
  public String getSourceRepo() {

    return this.sourceRepo;
  }

  /** {@inheritDoc} */
  @Override
  public String getReportingGroups() {

    return this.reportingGroups;
  }

  /** {@inheritDoc} */
  @Override
  public void setEngagement(Engagement engagement) {

    if (this.engagement != null) {
      throw new IllegalStateException("Once the EngagementImpl is set it can not be changed");
    }
    this.engagement = engagement;
    engagement.addApplication(this);
  }

  /** {@inheritDoc} */
  @Override
  public void setName(String name) {

    this.name = name;
  }

  /** {@inheritDoc} */
  @Override
  public void setProgrammingEcosystem(String programmingEcosystem) {

    this.programmingEcosystem = programmingEcosystem;
  }

  /** {@inheritDoc} */
  @Override
  public void setReleaseDate(String releaseDate) {

    this.releaseDate = releaseDate;
  }

  /** {@inheritDoc} */
  @Override
  public void setReleaseId(String releaseId) {

    this.releaseId = releaseId;
  }

  /** {@inheritDoc} */
  @Override
  public void setSourceRepo(String sourceRepo) {

    this.sourceRepo = sourceRepo;
  }

  /** {@inheritDoc} */
  @Override
  public void setReportingGroups(String reportingGroups) {

    this.reportingGroups = reportingGroups;
  }

  /** {@inheritDoc} */
  @Override
  public void completeData() {

    for (ApplicationComponent applicationComponent : this.applicationComponents) {
      applicationComponent.completeData();
    }
  }

  /**
   * @param applicationNode
   * @param modelFactory
   * @param readModelVersion
   * @param reportingGroupHandler TODO
   */
  public void readApplicationFromJsonNode(JsonNode applicationNode, ModelFactoryImpl modelFactory, int readModelVersion, ReportingGroupHandler reportingGroupHandler) {
  
    String name = applicationNode.get("name").asText(null);
    String releaseId = applicationNode.get("releaseId").asText(null);
    String releaseDate = applicationNode.get("releaseDate").asText(null);
    String sourceRepo = applicationNode.get("sourceRepo").asText(null);
    String programmingEcosystem = applicationNode.get("programmingEcosystem").asText(null);
    String reportingGroups = ReportingGroupHandler.DEFAULT_REPORTING_GROUP_LIST;
    JsonNode reportingGroupsNode = applicationNode.get("reportingGroups");
    if (reportingGroupsNode != null) {
      reportingGroups = reportingGroupsNode.asText();
      reportingGroupHandler.validateReportingGroupList(reportingGroups);
    }
    setName(name);
    setReleaseId(releaseId);
    setReleaseDate(releaseDate);
    setSourceRepo(sourceRepo);
    setProgrammingEcosystem(programmingEcosystem);
    setReportingGroups(reportingGroups);
    JsonNode applicationComponentsNode = applicationNode.get("applicationComponents");
    for (JsonNode applicationComponentNode : applicationComponentsNode) {
      ApplicationComponentImpl applicationComponent = modelFactory.newApplicationComponent();
      applicationComponent.setApplication(this);
  
      applicationComponent.readApplicationComponentFromJsonNode(applicationComponentNode, modelFactory,
          readModelVersion);
  
    }
  }

}
