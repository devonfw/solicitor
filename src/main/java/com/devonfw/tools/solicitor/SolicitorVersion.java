/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Solicitor version information.
 */
@Component
@PropertySource("classpath:com/devonfw/tools/solicitor/version.properties")
@ConfigurationProperties(prefix = "solicitor")
public class SolicitorVersion {

    private String artifact;

    private String version;

    private String githash;

    private String builddate;

    public SolicitorVersion() {

    }

    /**
     * This method gets the field <tt>artifact</tt>.
     *
     * @return the field artifact
     */
    public String getArtifact() {

        return artifact;
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
     * This method gets the field <tt>githash</tt>.
     *
     * @return the field githash
     */
    public String getGithash() {

        return githash;
    }

    /**
     * This method gets the field <tt>builddate</tt>.
     *
     * @return the field builddate
     */
    public String getBuilddate() {

        return builddate;
    }

    /**
     * This method sets the field <tt>artifact</tt>.
     *
     * @param artifact the new value of the field artifact
     */
    public void setArtifact(String artifact) {

        this.artifact = artifact;
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
     * This method sets the field <tt>githash</tt>.
     *
     * @param githash the new value of the field githash
     */
    public void setGithash(String githash) {

        this.githash = githash;
    }

    /**
     * This method sets the field <tt>builddate</tt>.
     *
     * @param builddate the new value of the field builddate
     */
    public void setBuilddate(String builddate) {

        this.builddate = builddate;
    }

}
