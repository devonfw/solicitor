/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the configuration of an application in JSON format.
 */
public class ApplicationConfig {

  @JsonProperty
  private String name;

  @JsonProperty
  private String releaseId;

  @JsonProperty
  private String sourceRepo;

  @JsonProperty
  private String programmingEcosystem;

  @JsonProperty
  private List<String> reportingGroups;

  @JsonProperty
  private List<ReaderConfig> readers = new ArrayList<>();

  /**
   * This method gets the field <code>name</code>.
   *
   * @return the field name
   */
  public String getName() {

    return this.name;
  }

  /**
   * This method gets the field <code>programmingEcosystem</code>.
   *
   * @return the field programmingEcosystem
   */
  public String getProgrammingEcosystem() {

    return this.programmingEcosystem;
  }

  /**
   * This method gets the field <code>readers</code>.
   *
   * @return the field readers
   */
  public List<ReaderConfig> getReaders() {

    return this.readers;
  }

  /**
   * This method gets the field <code>releaseId</code>.
   *
   * @return the field releaseId
   */
  public String getReleaseId() {

    return this.releaseId;
  }

  /**
   * This method gets the field <code>sourceRepo</code>.
   *
   * @return the field sourceRepo
   */
  public String getSourceRepo() {

    return this.sourceRepo;
  }

  /**
   * This method gets the field <code>reportingGroups</code>.
   *
   * @return the field reportingGroups
   */
  public List<String> getReportingGroups() {

    return this.reportingGroups;
  }

  /**
   * This method sets the field <code>name</code>.
   *
   * @param name the new value of the field name
   */
  public void setName(String name) {

    this.name = name;
  }

  /**
   * This method sets the field <code>programmingEcosystem</code>.
   *
   * @param programmingEcosystem the new value of the field programmingEcosystem
   */
  public void setProgrammingEcosystem(String programmingEcosystem) {

    this.programmingEcosystem = programmingEcosystem;
  }

  /**
   * This method sets the field <code>readers</code>.
   *
   * @param readers the new value of the field readers
   */
  public void setReaders(List<ReaderConfig> readers) {

    this.readers = readers;
  }

  /**
   * This method sets the field <code>releaseId</code>.
   *
   * @param releaseId the new value of the field releaseId
   */
  public void setReleaseId(String releaseId) {

    this.releaseId = releaseId;
  }

  /**
   * This method sets the field <code>sourceRepo</code>.
   *
   * @param sourceRepo the new value of the field sourceRepo
   */
  public void setSourceRepo(String sourceRepo) {

    this.sourceRepo = sourceRepo;
  }

  /**
   * This method sets the field <code>reportingGroups</code>.
   *
   * @param reportingGroups the new value of the field reportingGroups
   */
  public void setReportingGroups(List<String> reportingGroups) {

    this.reportingGroups = reportingGroups;
  }
}
