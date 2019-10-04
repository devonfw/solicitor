/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle.model;

import java.util.List;

/**
 * Java representation of Dependency in the Gradle JSON data file.
 */
public class Dependency {
    private String name;

    private String project;

    private String version;

    private String year;

    private String url;

    private String dependency;

    private List<License> licenses;

    /**
     * This method gets the field <tt>dependency</tt>.
     *
     * @return the field dependency
     */
    public String getDependency() {

        return dependency;
    }

    /**
     * This method gets the field <tt>licenses</tt>.
     *
     * @return the field licenses
     */
    public List<License> getLicenses() {

        return licenses;
    }

    /**
     * This method gets the field <tt>name</tt>.
     *
     * @return the field name
     */
    public String getName() {

        return name;
    }

    /**
     * This method gets the field <tt>project</tt>.
     *
     * @return the field project
     */
    public String getProject() {

        return project;
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
     * This method gets the field <tt>version</tt>.
     *
     * @return the field version
     */
    public String getVersion() {

        return version;
    }

    /**
     * This method gets the field <tt>year</tt>.
     *
     * @return the field year
     */
    public String getYear() {

        return year;
    }

    /**
     * This method sets the field <tt>dependency</tt>.
     *
     * @param dependency the new value of the field dependency
     */
    public void setDependency(String dependency) {

        this.dependency = dependency;
    }

    /**
     * This method sets the field <tt>licenses</tt>.
     *
     * @param licenses the new value of the field licenses
     */
    public void setLicenses(List<License> licenses) {

        this.licenses = licenses;
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
     * This method sets the field <tt>project</tt>.
     *
     * @param project the new value of the field project
     */
    public void setProject(String project) {

        this.project = project;
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
     * This method sets the field <tt>version</tt>.
     *
     * @param version the new value of the field version
     */
    public void setVersion(String version) {

        this.version = version;
    }

    /**
     * This method sets the field <tt>year</tt>.
     *
     * @param year the new value of the field year
     */
    public void setYear(String year) {

        this.year = year;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {

        String licensestring = "";
        for (License l : licenses) {
            licensestring += l + "\n";
        }
        return "";
    }
}
