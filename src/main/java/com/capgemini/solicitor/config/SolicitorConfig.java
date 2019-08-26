/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.config;

import java.util.ArrayList;
import java.util.List;

import com.capgemini.solicitor.model.masterdata.EngagementType;
import com.capgemini.solicitor.model.masterdata.GoToMarketModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SolicitorConfig {

    @JsonProperty
    private String comment;

    @JsonProperty
    private String engagementName;

    @JsonProperty
    private EngagementType engagementType;

    @JsonProperty
    private String clientName;

    @JsonProperty
    private GoToMarketModel goToMarketModel;

    @JsonProperty
    private boolean contractAllowsOss;

    @JsonProperty
    private boolean ossPolicyFollowed;

    @JsonProperty
    private boolean customerProvidesOss;

    @JsonProperty
    private List<ApplicationConfig> applications = new ArrayList<>();

    @JsonProperty
    private List<RuleConfig> rules = new ArrayList<>();

    @JsonProperty
    private List<WriterConfig> writers = new ArrayList<>();

}
