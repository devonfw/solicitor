/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.model.masterdata;

import java.util.ArrayList;
import java.util.List;

import com.capgemini.solicitor.common.AbstractDataRowSource;
import com.capgemini.solicitor.common.DataRowSource;
import com.capgemini.solicitor.model.inventory.ApplicationComponent;

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
    private String name;

    @NonNull
    private String releaseId;

    @NonNull
    private String releaseDate;

    @NonNull
    private String sourceRepo;

    @NonNull
    private String programmingEcosystem;

    private List<ApplicationComponent> applicationComponents =
            new ArrayList<>();

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
