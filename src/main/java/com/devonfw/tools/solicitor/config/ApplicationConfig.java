/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ApplicationConfig {

    @JsonProperty
    private String name;

    @JsonProperty
    private String releaseId;

    @JsonProperty
    private String sourceRepo;

    @JsonProperty
    private String programmingEcosystem;

    @JsonProperty
    private List<ReaderConfig> readers = new ArrayList<>();
}
