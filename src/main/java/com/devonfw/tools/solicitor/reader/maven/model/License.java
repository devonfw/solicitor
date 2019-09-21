/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.maven.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class License {
    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "url")
    private String url;

    @XmlElement(name = "distribution")
    private String distribution;

    /**
     * This method gets the field <tt>name</tt>.
     *
     * @return the field name
     */
    public String getName() {

        return name;
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
     * This method gets the field <tt>url</tt>.
     *
     * @return the field url
     */
    public String getUrl() {

        return url;
    }

    /**
     * This method sets the field <tt>url</tt>.
     *
     * @param url the new value of the field url
     */
    public void setUrl(String url) {

        this.url = url;
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
