/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle.model;

public class License {
    private String license;

    private String license_url;

    private String distribution;

    @Override
    public String toString() {

        return "";
    }

    /**
     * This method gets the field <tt>license</tt>.
     *
     * @return the field license
     */
    public String getLicense() {

        return license;
    }

    /**
     * This method sets the field <tt>license</tt>.
     *
     * @param license the new value of the field license
     */
    public void setLicense(String license) {

        this.license = license;
    }

    /**
     * This method gets the field <tt>license_url</tt>.
     *
     * @return the field license_url
     */
    public String getLicense_url() {

        return license_url;
    }

    /**
     * This method sets the field <tt>license_url</tt>.
     *
     * @param license_url the new value of the field license_url
     */
    public void setLicense_url(String license_url) {

        this.license_url = license_url;
    }

    /**
     * This method gets the field <tt>distribution</tt>.
     *
     * @return the field distribution
     */
    public String getDistribution() {

        return distribution;
    }

    /**
     * This method sets the field <tt>distribution</tt>.
     *
     * @param distribution the new value of the field distribution
     */
    public void setDistribution(String distribution) {

        this.distribution = distribution;
    }
}
