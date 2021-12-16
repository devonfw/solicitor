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
    private static final String DEFAULT_REPO_TYPE = "maven";

    @JsonProperty
    private String type;

    @JsonProperty
    private String source;

    @JsonProperty
    private UsagePattern usagePattern;

    @JsonProperty
    private String repoType;
    
    @JsonProperty
    private Map<String,String> configuration;

    /**
     * Standard constructor. Field {@link #repoType} will be initialized to
     * "maven" to use this if value is not defined in JSON config.
     */
    public ReaderConfig() {

        this.repoType = DEFAULT_REPO_TYPE;
    }

    /**
     * This method gets the field <code>repoType</code>.
     *
     * @return the field repoType
     */
    public String getRepoType() {

        return this.repoType;
    }

    /**
     * This method gets the field <code>configuration</code>.
     *
     * @return the field configuration
     */
    public Map<String,String> getConfiguration() {

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
     * This method sets the field <code>repoType</code>.
     *
     * @param repoType the new value of the field repoType
     */
    public void setRepoType(String repoType) {

        this.repoType = repoType;
    }
    
    /**
     * This method sets the field <code>configuration</code>.
     *
     * @param configuration the new value of the field configuration
     */
    public void setConfiguration(Map<String,String> configuration) {

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
}
