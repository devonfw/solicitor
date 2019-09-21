/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle.model;

import java.util.List;

public class LicenseSummary {
    private List<Dependency> dependencies;

    @Override
    public String toString() {

        String ld = "";
        for (Dependency d : dependencies) {
            ld += d + "\n";
        }
        return ld;
    }

    /**
     * This method gets the field <tt>dependencies</tt>.
     *
     * @return the field dependencies
     */
    public List<Dependency> getDependencies() {

        return dependencies;
    }

    /**
     * This method sets the field <tt>dependencies</tt>.
     *
     * @param dependencies the new value of the field dependencies
     */
    public void setDependencies(List<Dependency> dependencies) {

        this.dependencies = dependencies;
    }
}
