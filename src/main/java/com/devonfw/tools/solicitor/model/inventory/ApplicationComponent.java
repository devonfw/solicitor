package com.devonfw.tools.solicitor.model.inventory;

import java.util.List;

import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

public interface ApplicationComponent {

    void setApplication(Application application);

    /**
     * This method gets the field <tt>application</tt>.
     *
     * @return the field application
     */
    Application getApplication();

    /**
     * This method gets the field <tt>usagePattern</tt>.
     *
     * @return the field usagePattern
     */
    UsagePattern getUsagePattern();

    /**
     * This method sets the field <tt>usagePattern</tt>.
     *
     * @param usagePattern the new value of the field usagePattern
     */
    void setUsagePattern(UsagePattern usagePattern);

    /**
     * This method gets the field <tt>ossModified</tt>.
     *
     * @return the field ossModified
     */
    boolean isOssModified();

    /**
     * This method sets the field <tt>ossModified</tt>.
     *
     * @param ossModified the new value of the field ossModified
     */
    void setOssModified(boolean ossModified);

    /**
     * This method gets the field <tt>ossHomepage</tt>.
     *
     * @return the field ossHomepage
     */
    String getOssHomepage();

    /**
     * This method sets the field <tt>ossHomepage</tt>.
     *
     * @param ossHomepage the new value of the field ossHomepage
     */
    void setOssHomepage(String ossHomepage);

    /**
     * This method gets the field <tt>groupId</tt>.
     *
     * @return the field groupId
     */
    String getGroupId();

    /**
     * This method sets the field <tt>groupId</tt>.
     *
     * @param groupId the new value of the field groupId
     */
    void setGroupId(String groupId);

    /**
     * This method gets the field <tt>artifactId</tt>.
     *
     * @return the field artifactId
     */
    String getArtifactId();

    /**
     * This method sets the field <tt>artifactId</tt>.
     *
     * @param artifactId the new value of the field artifactId
     */
    void setArtifactId(String artifactId);

    /**
     * This method gets the field <tt>version</tt>.
     *
     * @return the field version
     */
    String getVersion();

    /**
     * This method sets the field <tt>version</tt>.
     *
     * @param version the new value of the field version
     */
    void setVersion(String version);

    /**
     * This method gets an unmodifiable copy of the field
     * <tt>normalizedLicenses</tt>.
     *
     * @return the field normalizedLicenses
     */
    List<NormalizedLicense> getNormalizedLicenses();

    /**
     * Adds the given {@link NormalizedLicense} to this ApplicationComponent.
     *
     * @param normalizedLicense
     */
    void addNormalizedLicense(NormalizedLicense normalizedLicense);

    /**
     * This method gets an unmodifiable copy of the field <tt>rawLicenses</tt>.
     *
     * @return the field rawLicenses
     */
    List<RawLicense> getRawLicenses();

    /**
     * Adds the given {@link RawLicense} to this ApplicateComponent.
     *
     * @param rawLicense
     */
    void addRawLicense(RawLicense rawLicense);

}