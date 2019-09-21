/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WriterConfig {

    public WriterConfig() {

    }

    @JsonCreator(mode = Mode.PROPERTIES)
    public WriterConfig(
            @JsonProperty("dataTables") Map<String, String> dataTables) {

        this.dataTables = dataTables;
    }

    @JsonProperty
    private String type;

    @JsonProperty
    private String templateSource;

    @JsonProperty
    private String target;

    @JsonProperty
    private String description;

    @JsonProperty
    private Map<String, String> dataTables;

    /**
     * This method gets the field <tt>type</tt>.
     *
     * @return the field type
     */
    public String getType() {

        return type;
    }

    /**
     * This method sets the field <tt>type</tt>.
     *
     * @param type the new value of the field type
     */
    public void setType(String type) {

        this.type = type;
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
     * This method sets the field <tt>templateSource</tt>.
     *
     * @param templateSource the new value of the field templateSource
     */
    public void setTemplateSource(String templateSource) {

        this.templateSource = templateSource;
    }

    /**
     * This method gets the field <tt>target</tt>.
     *
     * @return the field target
     */
    public String getTarget() {

        return target;
    }

    /**
     * This method sets the field <tt>target</tt>.
     *
     * @param target the new value of the field target
     */
    public void setTarget(String target) {

        this.target = target;
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
     * This method sets the field <tt>description</tt>.
     *
     * @param description the new value of the field description
     */
    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * This method gets the field <tt>dataTables</tt>.
     *
     * @return the field dataTables
     */
    public Map<String, String> getDataTables() {

        return dataTables;
    }

    /**
     * This method sets the field <tt>dataTables</tt>.
     *
     * @param dataTables the new value of the field dataTables
     */
    public void setDataTables(Map<String, String> dataTables) {

        this.dataTables = dataTables;
    }
}
