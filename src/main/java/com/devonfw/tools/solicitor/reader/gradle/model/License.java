/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle.model;

import lombok.Data;

@Data
public class License {
    private String license;
    private String license_url;
    private String distribution;

    @Override
    public String toString() {
    	return "";
    }
}
