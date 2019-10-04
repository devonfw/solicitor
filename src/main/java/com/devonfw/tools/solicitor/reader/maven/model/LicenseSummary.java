/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.maven.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Java representation of LicenseSummary in the Maven XML data file.
 */

@XmlRootElement(name = "licenseSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class LicenseSummary {
    @XmlElementWrapper(name = "dependencies")
    @XmlElement(name = "dependency")
    private ArrayList<Dependency> dependencies;

    /**
     * This method gets the field <tt>dependencies</tt>.
     *
     * @return the field dependencies
     */
    public ArrayList<Dependency> getDependencies() {

        return dependencies;
    }

    /**
     * This method sets the field <tt>dependencies</tt>.
     *
     * @param dependencies the new value of the field dependencies
     */
    public void setDependencies(ArrayList<Dependency> dependencies) {

        this.dependencies = dependencies;
    }

}
