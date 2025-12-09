/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.maven.model;

import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

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
   * This method gets the field <code>dependencies</code>.
   *
   * @return the field dependencies
   */
  public ArrayList<Dependency> getDependencies() {

    return this.dependencies;
  }

  /**
   * This method sets the field <code>dependencies</code>.
   *
   * @param dependencies the new value of the field dependencies
   */
  public void setDependencies(ArrayList<Dependency> dependencies) {

    this.dependencies = dependencies;
  }

}
