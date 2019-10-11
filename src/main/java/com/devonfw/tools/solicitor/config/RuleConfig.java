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
    private String ruleSource;

    @JsonProperty
    private String templateSource;

    @JsonProperty
    private String ruleGroup;

    @JsonProperty
    private String description;

    /**
     * This method gets the field <tt>ruleGroup</tt>.
     *
     * @return the field ruleGroup
     */
    public String getRuleGroup() {

        return ruleGroup;
    }

    /**
     * This method gets the field <tt>description</tt>.
     *
     * @return the field description
     */
    public String getDescription() {

        return description;
    }

    /**
     * This method gets the field <tt>ruleSource</tt>.
     *
     * @return the field ruleSource
     */
    public String getRuleSource() {

        return ruleSource;
    }

    /**
     * This method gets the field <tt>templateSource</tt>.
     *
     * @return the field templateSource
     */
    public String getTemplateSource() {

        return templateSource;
    }

    /**
     * This method gets the field <tt>type</tt>.
     *
     * @return the field type
     */
    public String getType() {

        return type;
    }

    /**
     * This method sets the field <tt>ruleGroup</tt>.
     *
     * @param ruleGroup the new value of the field ruleGroup
     */
    public void setRuleGroup(String ruleGroup) {

        this.ruleGroup = ruleGroup;
    }

    /**
     * This method sets the field <tt>description</tt>.
     *
     * @param description the new value of the field description
     */
    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * This method sets the field <tt>ruleSource</tt>.
     *
     * @param ruleSource the new value of the field ruleSource
     */
    public void setRuleSource(String ruleSource) {

        this.ruleSource = ruleSource;
    }

    /**
     * This method sets the field <tt>templateSource</tt>.
     *
     * @param templateSource the new value of the field templateSource
     */
    public void setTemplateSource(String templateSource) {

        this.templateSource = templateSource;
    }

    /**
     * This method sets the field <tt>type</tt>.
     *
     * @param type the new value of the field type
     */
    public void setType(String type) {

        this.type = type;
    }
}
