/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle.model;

import java.util.List;

import lombok.Data;

@Data
public class Dependency {
    private String name;
    private String project;
    private String version;
    private String year;
    private String url;
    private String dependency;
    private List<License> licenses;

    @Override
    public String toString() {
    	String licensestring = "";
    	for(License l: licenses)
    		licensestring+=l+"\n";
    	return "";
    }
}
