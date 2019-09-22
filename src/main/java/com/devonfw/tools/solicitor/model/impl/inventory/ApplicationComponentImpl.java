/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.impl.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApplicationComponentImpl extends AbstractDataRowSource
        implements DataRowSource, ApplicationComponent {

    private Application application;

    private UsagePattern usagePattern;

    private boolean ossModified;

    private String ossHomepage;

    private String groupId;

    private String artifactId;

    private String version;

    private List<NormalizedLicense> normalizedLicenses = new ArrayList<>();

    private List<RawLicense> rawLicenses = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplication(Application application) {

        if (this.application != null) {
            throw new IllegalStateException(
                    "Once the ApplicationImpl is set it can not be changed");
        }
        this.application = application;
        application.addApplicationComponent(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public Application getApplication() {

        return application;
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

        // TODO: check how to avoid this necessare casting
        return (AbstractDataRowSource) application;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsagePattern getUsagePattern() {

        return usagePattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUsagePattern(UsagePattern usagePattern) {

        this.usagePattern = usagePattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOssModified() {

        return ossModified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOssModified(boolean ossModified) {

        this.ossModified = ossModified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOssHomepage() {

        return ossHomepage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOssHomepage(String ossHomepage) {

        this.ossHomepage = ossHomepage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGroupId() {

        return groupId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGroupId(String groupId) {

        this.groupId = groupId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getArtifactId() {

        return artifactId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setArtifactId(String artifactId) {

        this.artifactId = artifactId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion() {

        return version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVersion(String version) {

        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NormalizedLicense> getNormalizedLicenses() {

        return Collections.unmodifiableList(normalizedLicenses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNormalizedLicense(NormalizedLicense normalizedLicense) {

        normalizedLicenses.add(normalizedLicense);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RawLicense> getRawLicenses() {

        return Collections.unmodifiableList(rawLicenses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRawLicense(RawLicense rawLicense) {

        rawLicenses.add(rawLicense);
    }

}
