/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReaderConfig {
    @JsonProperty
    private String type;

    @JsonProperty
    private String source;

    @JsonProperty
    private UsagePattern usagePattern;
}
