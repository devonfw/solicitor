/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.impl.masterdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.devonfw.tools.solicitor.model.impl.AbstractModelObject;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Implementation of the {@link Application} model object interface.
 */
public class ApplicationImpl extends AbstractModelObject implements Application {
  private String name;

  private String releaseId;

  private String releaseDate;

  private String sourceRepo;

  private String programmingEcosystem;

  private List<ApplicationComponent> applicationComponents = new ArrayList<>();

  private Engagement engagement;

  /**
   * Constructor.
   *
   * @param name the application name
   * @param releaseId the release id.
   * @param releaseDate the date of the release.
   * @param sourceRepo pointer to the source repo
   * @param programmingEcosystem name of the programming ecosystem
   */
  public ApplicationImpl(String name, String releaseId, String releaseDate, String sourceRepo,
      String programmingEcosystem) {

    super();
    this.name = name;
    this.releaseId = releaseId;
    this.releaseDate = releaseDate;
    this.sourceRepo = sourceRepo;
    this.programmingEcosystem = programmingEcosystem;
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

    return new String[] { this.name, this.releaseId, this.releaseDate, this.sourceRepo, this.programmingEcosystem };
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

    return new String[] { "applicationName", "releaseId", "releaseDate", "sourceRepo", "programmingEcosystem" };
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
  public void completeData() {

    for (ApplicationComponent applicationComponent : this.applicationComponents) {
      applicationComponent.completeData();
    }
  }

}
