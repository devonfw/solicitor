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
    private String agendaGroup;

    @JsonProperty
    private String description;

    /**
     * This method gets the field <tt>agendaGroup</tt>.
     *
     * @return the field agendaGroup
     */
    public String getAgendaGroup() {

        return agendaGroup;
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
     * This method sets the field <tt>agendaGroup</tt>.
     *
     * @param agendaGroup the new value of the field agendaGroup
     */
    public void setAgendaGroup(String agendaGroup) {

        this.agendaGroup = agendaGroup;
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
