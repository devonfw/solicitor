/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle.model;

import java.util.List;

import lombok.Data;

@Data
public class LicenseSummary {
    private List<Dependency> dependencies;

    @Override
    public String toString() {
    	String ld = "";
    	for(Dependency d: dependencies)
    		ld += d + "\n";
    	return ld;
    }
}
