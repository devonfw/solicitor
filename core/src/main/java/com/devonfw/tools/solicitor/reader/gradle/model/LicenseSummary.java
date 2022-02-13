/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle.model;

import java.util.List;

/**
 * Java representation of LicenseSummary in the Gradle JSON data file.
 */

public class LicenseSummary {
  private List<Dependency> dependencies;

  /**
   * This method gets the field <code>dependencies</code>.
   *
   * @return the field dependencies
   */
  public List<Dependency> getDependencies() {

    return this.dependencies;
  }

  /**
   * This method sets the field <code>dependencies</code>.
   *
   * @param dependencies the new value of the field dependencies
   */
  public void setDependencies(List<Dependency> dependencies) {

    this.dependencies = dependencies;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {

    String ld = "";
    for (Dependency d : this.dependencies) {
      ld += d + "\n";
    }
    return ld;
  }
}
