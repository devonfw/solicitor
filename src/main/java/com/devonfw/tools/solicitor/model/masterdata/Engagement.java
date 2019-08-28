/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.masterdata;

import java.util.ArrayList;
import java.util.List;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Engagement extends AbstractDataRowSource implements DataRowSource {

    @NonNull
    private String engagementName;

    @NonNull
    private EngagementType engagementType;

    @NonNull
    private String clientName;

    @NonNull
    private GoToMarketModel goToMarketModel;

    private boolean contractAllowsOss;

    private boolean ossPolicyFollowed;

    private boolean customerProvidesOss;

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
