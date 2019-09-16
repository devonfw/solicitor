/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.masterdata;

import java.util.ArrayList;
import java.util.List;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Engagement extends AbstractDataRowSource implements DataRowSource {

    @NonNull
    @JsonProperty
    private String engagementName;

    @NonNull
    @JsonProperty
    private EngagementType engagementType;

    @NonNull
    @JsonProperty
    private String clientName;

    @NonNull
    @JsonProperty
    private GoToMarketModel goToMarketModel;

    @JsonProperty
    private boolean contractAllowsOss;

    @JsonProperty
    private boolean ossPolicyFollowed;

    @JsonProperty
    private boolean customerProvidesOss;

    @JsonProperty
    private List<Application> applications = new ArrayList<>();

    @Override
    public String[] getHeadElements() {

        return new String[] { "engagementName", "engagementType", "clientName",
        "goToMarketModel", "contractAllowsOss", "ossPolicyFollowed",
        "customerProvidesOss" };
    }

    @Override
    public String[] getDataElements() {

        return new String[] { engagementName, engagementType.toString(),
        clientName, goToMarketModel.toString(),
        contractAllowsOss ? "true" : "false",
        ossPolicyFollowed ? "true" : "false",
        customerProvidesOss ? "true" : "false" };
    }

}
