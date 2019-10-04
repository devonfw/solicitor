/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.maven.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * Java representation of Dependency in the Maven XML data file.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Dependency {

    @XmlElement(name = "groupId")
    private String groupId;

    @XmlElement(name = "artifactId")
    private String artifactId;

    @XmlElement(name = "version")
    private String version;

    @XmlElementWrapper(name = "licenses")
    @XmlElement(name = "license")
    private ArrayList<License> licenses;

    /**
     * This method gets the field <tt>artifactId</tt>.
     *
     * @return the field artifactId
     */
    public String getArtifactId() {

        return artifactId;
    }

    /**
     * This method gets the field <tt>groupId</tt>.
     *
     * @return the field groupId
     */
    public String getGroupId() {

        return groupId;
    }

    /**
     * This method gets the field <tt>licenses</tt>.
     *
     * @return the field licenses
     */
    public ArrayList<License> getLicenses() {

        return licenses;
    }

    /**
     * This method gets the field <tt>version</tt>.
     *
     * @return the field version
     */
    public String getVersion() {

        return version;
    }

    /**
     * This method sets the field <tt>artifactId</tt>.
     *
     * @param artifactId the new value of the field artifactId
     */
    public void setArtifactId(String artifactId) {

        this.artifactId = artifactId;
    }

    /**
     * This method sets the field <tt>groupId</tt>.
     *
     * @param groupId the new value of the field groupId
     */
    public void setGroupId(String groupId) {

        this.groupId = groupId;
    }

    /**
     * This method sets the field <tt>licenses</tt>.
     *
     * @param licenses the new value of the field licenses
     */
    public void setLicenses(ArrayList<License> licenses) {

        this.licenses = licenses;
    }

    /**
     * This method sets the field <tt>version</tt>.
     *
     * @param version the new value of the field version
     */
    public void setVersion(String version) {

        this.version = version;
    }
}
