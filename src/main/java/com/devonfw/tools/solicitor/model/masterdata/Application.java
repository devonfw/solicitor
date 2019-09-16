/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.masterdata;

import java.util.ArrayList;
import java.util.List;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Application extends AbstractDataRowSource
        implements DataRowSource {
    @NonNull
    @JsonProperty
    private String name;

    @NonNull
    @JsonProperty
    private String releaseId;

    @NonNull
    @JsonProperty
    private String releaseDate;

    @NonNull
    @JsonProperty
    private String sourceRepo;

    @NonNull
    @JsonProperty
    private String programmingEcosystem;

    @JsonProperty
    private List<ApplicationComponent> applicationComponents =
            new ArrayList<>();

    @JsonIgnore
    private Engagement engagement;

    public void setEngagement(Engagement engagement) {

        if (this.engagement != null) {
            throw new IllegalStateException(
                    "Once the Engagement is set it can not be changed");
        }
        this.engagement = engagement;
        engagement.getApplications().add(this);
    }

    @Override
    public String[] getHeadElements() {

        return new String[] { "applicationName", "releaseId", "releaseDate",
        "sourceRepo", "programmingEcosystem" };
    }

    @Override
    public String[] getDataElements() {

        return new String[] { name, releaseId, releaseDate, sourceRepo,
        programmingEcosystem };
    }

    @Override
    public AbstractDataRowSource getParent() {

        return engagement;
    }

}
