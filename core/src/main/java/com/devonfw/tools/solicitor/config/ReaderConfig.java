/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import java.util.Map;

import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.Reader;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the configuration of a {@link Reader} in JSON format.
 */
public class ReaderConfig {

  @JsonProperty
  private String type;

  @JsonProperty
  private String source;

  @JsonProperty
  private UsagePattern usagePattern;

  @JsonProperty
  private boolean modified;

  @JsonProperty
  private String packageType;

  @JsonProperty
  private Map<String, String> configuration;

  /**
   * Standard constructor.
   */
  public ReaderConfig() {

  }

  /**
   * This method gets the field <code>packageType</code>.
   *
   * @return the field packageType
   */
  public String getPackageType() {

    return this.packageType;
  }

  /**
   * This method gets the field <code>configuration</code>.
   *
   * @return the field configuration
   */
  public Map<String, String> getConfiguration() {

    return this.configuration;
  }

  /**
   * This method gets the field <code>source</code>.
   *
   * @return the field source
   */
  public String getSource() {

    return this.source;
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
   * This method gets the field <code>usagePattern</code>.
   *
   * @return the field usagePattern
   */
  public UsagePattern getUsagePattern() {

    return this.usagePattern;
  }

  /**
   * This method gets the field <code>modified</code>.
   *
   * @return modified
   */
  public boolean isModified() {

    return this.modified;
  }

  /**
   * This method sets the field <code>packageType</code>.
   *
   * @param packageType the new value of the field packageType
   */
  public void setPackageType(String packageType) {

    this.packageType = packageType;
  }

  /**
   * This method sets the field <code>configuration</code>.
   *
   * @param configuration the new value of the field configuration
   */
  public void setConfiguration(Map<String, String> configuration) {

    this.configuration = configuration;
  }

  /**
   * This method sets the field <code>source</code>.
   *
   * @param source the new value of the field source
   */
  public void setSource(String source) {

    this.source = source;
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
   * This method sets the field <code>usagePattern</code>.
   *
   * @param usagePattern the new value of the field usagePattern
   */
  public void setUsagePattern(UsagePattern usagePattern) {

    this.usagePattern = usagePattern;
  }

  /**
   * This method sets the field <code>modified</code>.
   *
   * @param modified the new value of field modified.
   */
  public void setModified(boolean modified) {

    this.modified = modified;
  }
}
