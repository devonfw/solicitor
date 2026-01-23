/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the configuration of a {@link com.devonfw.tools.solicitor.writer.Writer} in JSON format.
 */
public class WriterConfig {

  @JsonProperty
  private String type;

  @JsonProperty
  private String templateSource;

  @JsonProperty
  private String target;

  @JsonProperty
  private String description;

  @JsonProperty
  private boolean enableReportingGroups;

  @JsonProperty
  private boolean includeDeletedRowsInDelta;

  @JsonProperty
  private Map<String, String> dataTables;

  /**
   * Constructor.
   */
  public WriterConfig() {

  }

  /**
   * Constructor for WriterConfig to be used when deserializing JSON.
   *
   * @param dataTables a map of the resources which define the SQLs to create the reporting data tables.
   */
  @JsonCreator(mode = Mode.PROPERTIES)
  public WriterConfig(@JsonProperty("dataTables") Map<String, String> dataTables) {

    this.dataTables = dataTables;
  }

  /**
   * This method gets the field <code>dataTables</code>.
   *
   * @return the field dataTables
   */
  public Map<String, String> getDataTables() {

    return this.dataTables;
  }

  /**
   * This method gets the field <code>description</code>.
   *
   * @return the field description
   */
  public String getDescription() {

    return this.description;
  }

  /**
   * This method gets the field <code>target</code>.
   *
   * @return the field target
   */
  public String getTarget() {

    return this.target;
  }

  /**
   * This method gets the field <code>templateSource</code>.
   *
   * @return the field templateSource
   */
  public String getTemplateSource() {

    return this.templateSource;
  }

  /**
   * This method gets the field <code>type</code>.
   *
   * @return the field type
   */
  public String getType() {

    return this.type;
  }

  /**
   * This method gets the field <code>enableReportingGroups</code>.
   *
   * @return enableReportingGroups
   */
  public boolean isEnableReportingGroups() {

    return this.enableReportingGroups;
  }

  /**
   * This method gets the field <code>includeDeletedRowsInDelta</code>.
   *
   * @return includeDeletedRowsInDelta
   */
  public boolean isIncludeDeletesRowsInDelta() {

    return this.includeDeletedRowsInDelta;
  }

  /**
   * This method sets the field <code>dataTables</code>.
   *
   * @param dataTables the new value of the field dataTables
   */
  public void setDataTables(Map<String, String> dataTables) {

    this.dataTables = dataTables;
  }

  /**
   * This method sets the field <code>description</code>.
   *
   * @param description the new value of the field description
   */
  public void setDescription(String description) {

    this.description = description;
  }

  /**
   * This method sets the field <code>target</code>.
   *
   * @param target the new value of the field target
   */
  public void setTarget(String target) {

    this.target = target;
  }

  /**
   * This method sets the field <code>templateSource</code>.
   *
   * @param templateSource the new value of the field templateSource
   */
  public void setTemplateSource(String templateSource) {

    this.templateSource = templateSource;
  }

  /**
   * This method sets the field <code>type</code>.
   *
   * @param type the new value of the field type
   */
  public void setType(String type) {

    this.type = type;
  }

  /**
   * This method sets the field <code>enableReportingGroups</code>.
   *
   * @param enableReportingGroups new value of {@link #isEnableReportingGroups}.
   */
  public void setEnableReportingGroups(boolean enableReportingGroups) {

    this.enableReportingGroups = enableReportingGroups;
  }

  /**
   * This method sets the field <code>includeDeletedRowsInDelta</code>.
   *
   * @param includeDeletedRowsInDelta new value of {@link #includeDeletedRowsInDelta}.
   */
  public void setIncludeDeletedRowsInDelta(boolean includeDeletedRowsInDelta) {

    this.includeDeletedRowsInDelta = includeDeletedRowsInDelta;
  }

}
