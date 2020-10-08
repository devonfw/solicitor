/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle.model;

/**
 * Java representation of License in the Gradle JSON data file.
 */

public class License {
    private String license;

    private String license_url;

    private String distribution;

    /**
     * This method gets the field <code>distribution</code>.
     *
     * @return the field distribution
     */
    public String getDistribution() {

        return this.distribution;
    }

    /**
     * This method gets the field <code>license</code>.
     *
     * @return the field license
     */
    public String getLicense() {

        return this.license;
    }

    /**
     * This method gets the field <code>license_url</code>.
     *
     * @return the field license_url
     */
    public String getLicense_url() {

        return this.license_url;
    }

    /**
     * This method sets the field <code>distribution</code>.
     *
     * @param distribution the new value of the field distribution
     */
    public void setDistribution(String distribution) {

        this.distribution = distribution;
    }

    /**
     * This method sets the field <code>license</code>.
     *
     * @param license the new value of the field license
     */
    public void setLicense(String license) {

        this.license = license;
    }

    /**
     * This method sets the field <code>license_url</code>.
     *
     * @param license_url the new value of the field license_url
     */
    public void setLicense_url(String license_url) {

        this.license_url = license_url;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {

        return "";
    }
}
