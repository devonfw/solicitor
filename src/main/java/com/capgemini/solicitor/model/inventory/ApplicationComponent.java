/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.model.inventory;

import java.util.ArrayList;
import java.util.List;

import com.capgemini.solicitor.common.AbstractDataRowSource;
import com.capgemini.solicitor.common.DataRowSource;
import com.capgemini.solicitor.model.masterdata.Application;
import com.capgemini.solicitor.model.masterdata.UsagePattern;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ApplicationComponent extends AbstractDataRowSource
        implements DataRowSource {

    private Application application;

    private UsagePattern usagePattern;

    private boolean ossModified;

    private String ossHomepage;

    private String groupId;

    private String artifactId;

    private String version;

    private List<NormalizedLicense> normalizedLicenses = new ArrayList<>();

    private List<RawLicense> rawLicenses = new ArrayList<>();

    public void setApplication(Application application) {

        if (this.application != null) {
            throw new IllegalStateException(
                    "Once the Application is set it can not be changed");
        }
        this.application = application;
        application.getApplicationComponents().add(this);
    }

    public String getKey() {

        return groupId + "/" + artifactId + "/" + version;
    }

    @Override
    public String[] getHeadElements() {

        return new String[] { "groupId", "artifactId", "version", "ossHomepage",
        "usagePattern", "ossModified" };
    }

    @Override
    public String[] getDataElements() {

        return new String[] { groupId, artifactId, version, getOssHomepage(),
        getUsagePattern().toString(), isOssModified() ? "true" : "false" };
    }

    @Override
    public AbstractDataRowSource getParent() {

        return application;
    }
}
