/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.inventory;

import java.util.ArrayList;
import java.util.List;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ApplicationComponent extends AbstractDataRowSource
        implements DataRowSource {

    @JsonIgnore
    private Application application;

    @JsonProperty
    private UsagePattern usagePattern;

    @JsonProperty
    private boolean ossModified;

    @JsonProperty
    private String ossHomepage;

    @JsonProperty
    private String groupId;

    @JsonProperty
    private String artifactId;

    @JsonProperty
    private String version;

    @JsonProperty
    private List<NormalizedLicense> normalizedLicenses = new ArrayList<>();

    @JsonProperty
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
