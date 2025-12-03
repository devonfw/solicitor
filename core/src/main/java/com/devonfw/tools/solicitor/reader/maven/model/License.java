/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.maven.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * Java representation of License in the Maven XML data file.
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class License {
  @XmlElement(name = "name")
  private String name;

  @XmlElement(name = "url")
  private String url;

  @XmlElement(name = "distribution")
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
   * This method gets the field <code>name</code>.
   *
   * @return the field name
   */
  public String getName() {

    return this.name;
  }

  /**
   * This method gets the field <code>url</code>.
   *
   * @return the field url
   */
  public String getUrl() {

    return this.url;
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
   * This method sets the field <code>name</code>.
   *
   * @param name the new value of the field name
   */
  public void setName(String name) {

    this.name = name;
  }

  /**
   * This method sets the field <code>url</code>.
   *
   * @param url the new value of the field url
   */
  public void setUrl(String url) {

    this.url = url;
  }

}
