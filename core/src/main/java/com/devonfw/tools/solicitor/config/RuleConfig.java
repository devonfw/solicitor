/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single rule configuration section in JSON format.
 */
public class RuleConfig {
  @JsonProperty
  private String type;

  @JsonProperty
  private boolean optional;

  @JsonProperty
  private String ruleSource;

  @JsonProperty
  private String templateSource;

  @JsonProperty
  private String ruleGroup;

  @JsonProperty
  private String description;

  @JsonProperty
  private boolean deprecationWarnOnly;

  @JsonProperty
  private String deprecationDetails;

  /**
   * This method gets the field <code>ruleGroup</code>.
   *
   * @return the field ruleGroup
   */
  public String getRuleGroup() {

    return this.ruleGroup;
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
   * This method gets the field <code>ruleSource</code>.
   *
   * @return the field ruleSource
   */
  public String getRuleSource() {

    return this.ruleSource;
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
   * This method gets the field <code>optional</code>.
   *
   * @return the field optional
   */
  public boolean isOptional() {

    return this.optional;
  }

  /**
   * Gets deprecationWarnOnly.
   *
   * @return deprecationWarnOnly
   */
  public boolean isDeprecationWarnOnly() {

    return this.deprecationWarnOnly;
  }

  /**
   * Gets deprecationDetails.
   *
   * @return deprecationDetails
   */
  public String getDeprecationDetails() {

    return this.deprecationDetails;
  }

  /**
   * This method sets the field <code>ruleGroup</code>.
   *
   * @param ruleGroup the new value of the field ruleGroup
   */
  public void setRuleGroup(String ruleGroup) {

    this.ruleGroup = ruleGroup;
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
   * This method sets the field <code>ruleSource</code>.
   *
   * @param ruleSource the new value of the field ruleSource
   */
  public void setRuleSource(String ruleSource) {

    this.ruleSource = ruleSource;
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
   * This method sets the field <code>optional</code>.
   *
   * @param optional the new value of the field optional
   */
  public void setOptional(boolean optional) {

    this.optional = optional;
  }

  /**
   * Set deprecationWarnOnly.
   *
   * @param deprecationWarnOnly new value of deprecationWarnOnly.
   */
  public void setDeprecationWarnOnly(boolean deprecationWarnOnly) {

    this.deprecationWarnOnly = deprecationWarnOnly;
  }

  /**
   * @param deprecationDetails new value of {@link #getDeprecationDetails}.
   */
  public void setDeprecationDetails(String deprecationDetails) {

    this.deprecationDetails = deprecationDetails;
  }

}
