/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReaderConfig {
    @JsonProperty
    private String type;

    @JsonProperty
    private String source;

    @JsonProperty
    private UsagePattern usagePattern;

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
     * This method gets the field <tt>source</tt>.
     *
     * @return the field source
     */
    public String getSource() {

        return source;
    }

    /**
     * This method sets the field <tt>source</tt>.
     *
     * @param source the new value of the field source
     */
    public void setSource(String source) {

        this.source = source;
    }

    /**
     * This method gets the field <tt>usagePattern</tt>.
     *
     * @return the field usagePattern
     */
    public UsagePattern getUsagePattern() {

        return usagePattern;
    }

    /**
     * This method sets the field <tt>usagePattern</tt>.
     *
     * @param usagePattern the new value of the field usagePattern
     */
    public void setUsagePattern(UsagePattern usagePattern) {

        this.usagePattern = usagePattern;
    }
}
