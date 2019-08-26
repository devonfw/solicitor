/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.config;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WriterConfig {

    public WriterConfig() {

    }

    @JsonCreator(mode = Mode.PROPERTIES)
    public WriterConfig(
            @JsonProperty("dataTables") Map<String, String> dataTables) {

        this.dataTables = dataTables;
    }

    @JsonProperty
    private String type;

    @JsonProperty
    private String templateSource;

    @JsonProperty
    private String target;

    @JsonProperty
    private String description;

    @JsonProperty
    private Map<String, String> dataTables;
}
