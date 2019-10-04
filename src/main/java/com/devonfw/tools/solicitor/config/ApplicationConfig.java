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
    private List<ReaderConfig> readers = new ArrayList<>();

    /**
     * This method gets the field <tt>name</tt>.
     *
     * @return the field name
     */
    public String getName() {

        return name;
    }

    /**
     * This method gets the field <tt>programmingEcosystem</tt>.
     *
     * @return the field programmingEcosystem
     */
    public String getProgrammingEcosystem() {

        return programmingEcosystem;
    }

    /**
     * This method gets the field <tt>readers</tt>.
     *
     * @return the field readers
     */
    public List<ReaderConfig> getReaders() {

        return readers;
    }

    /**
     * This method gets the field <tt>releaseId</tt>.
     *
     * @return the field releaseId
     */
    public String getReleaseId() {

        return releaseId;
    }

    /**
     * This method gets the field <tt>sourceRepo</tt>.
     *
     * @return the field sourceRepo
     */
    public String getSourceRepo() {

        return sourceRepo;
    }

    /**
     * This method sets the field <tt>name</tt>.
     *
     * @param name the new value of the field name
     */
    public void setName(String name) {

        this.name = name;
    }

    /**
     * This method sets the field <tt>programmingEcosystem</tt>.
     *
     * @param programmingEcosystem the new value of the field
     *        programmingEcosystem
     */
    public void setProgrammingEcosystem(String programmingEcosystem) {

        this.programmingEcosystem = programmingEcosystem;
    }

    /**
     * This method sets the field <tt>readers</tt>.
     *
     * @param readers the new value of the field readers
     */
    public void setReaders(List<ReaderConfig> readers) {

        this.readers = readers;
    }

    /**
     * This method sets the field <tt>releaseId</tt>.
     *
     * @param releaseId the new value of the field releaseId
     */
    public void setReleaseId(String releaseId) {

        this.releaseId = releaseId;
    }

    /**
     * This method sets the field <tt>sourceRepo</tt>.
     *
     * @param sourceRepo the new value of the field sourceRepo
     */
    public void setSourceRepo(String sourceRepo) {

        this.sourceRepo = sourceRepo;
    }
}
