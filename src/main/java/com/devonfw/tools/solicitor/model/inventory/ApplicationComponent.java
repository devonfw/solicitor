package com.devonfw.tools.solicitor.model.inventory;

import java.util.List;

import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

/**
 * Represents an ApplicationComponent in the Solicitor data model.
 */
public interface ApplicationComponent {

    /**
     * Adds the given
     * {@link com.devonfw.tools.solicitor.model.inventory.NormalizedLicense} to
     * this ApplicationComponent.
     *
     * @param normalizedLicense a
     *        {@link com.devonfw.tools.solicitor.model.inventory.NormalizedLicense}
     *        object.
     */
    void addNormalizedLicense(NormalizedLicense normalizedLicense);

    /**
     * Adds the given
     * {@link com.devonfw.tools.solicitor.model.inventory.RawLicense} to this
     * ApplicateComponent.
     *
     * @param rawLicense a
     *        {@link com.devonfw.tools.solicitor.model.inventory.RawLicense}
     *        object.
     */
    void addRawLicense(RawLicense rawLicense);

    /**
     * This method gets the field <tt>application</tt>.
     *
     * @return the field application
     */
    Application getApplication();

    /**
     * This method gets the field <tt>artifactId</tt>.
     *
     * @return the field artifactId
     */
    String getArtifactId();

    /**
     * This method gets the field <tt>groupId</tt>.
     *
     * @return the field groupId
     */
    String getGroupId();

    /**
     * This method gets an unmodifiable copy of the field
     * <tt>normalizedLicenses</tt>.
     *
     * @return the field normalizedLicenses
     */
    List<NormalizedLicense> getNormalizedLicenses();

    /**
     * This method gets the field <tt>ossHomepage</tt>.
     *
     * @return the field ossHomepage
     */
    String getOssHomepage();

    /**
     * This method gets an unmodifiable copy of the field <tt>rawLicenses</tt>.
     *
     * @return the field rawLicenses
     */
    List<RawLicense> getRawLicenses();

    /**
     * This method gets the field <tt>usagePattern</tt>.
     *
     * @return the field usagePattern
     */
    UsagePattern getUsagePattern();

    /**
     * This method gets the field <tt>version</tt>.
     *
     * @return the field version
     */
    String getVersion();

    /**
     * This method gets the field <tt>repoType</tt>.
     *
     * @return the field repoType
     */
    String getRepoType();

    /**
     * This method gets the field <tt>ossModified</tt>.
     *
     * @return the field ossModified
     */
    boolean isOssModified();

    /**
     * Sets the {@link Application}.
     *
     * @param application the application to which this
     *        {@link ApplicationComponent} belongs to.
     */
    void setApplication(Application application);

    /**
     * This method sets the field <tt>artifactId</tt>.
     *
     * @param artifactId the new value of the field artifactId
     */
    void setArtifactId(String artifactId);

    /**
     * This method sets the field <tt>groupId</tt>.
     *
     * @param groupId the new value of the field groupId
     */
    void setGroupId(String groupId);

    /**
     * This method sets the field <tt>ossHomepage</tt>.
     *
     * @param ossHomepage the new value of the field ossHomepage
     */
    void setOssHomepage(String ossHomepage);

    /**
     * This method sets the field <tt>ossModified</tt>.
     *
     * @param ossModified the new value of the field ossModified
     */
    void setOssModified(boolean ossModified);

    /**
     * This method sets the field <tt>usagePattern</tt>.
     *
     * @param usagePattern the new value of the field usagePattern
     */
    void setUsagePattern(UsagePattern usagePattern);

    /**
     * This method sets the field <tt>version</tt>.
     *
     * @param version the new value of the field version
     */
    void setVersion(String version);

    /**
     * This method sets the field <tt>repoType</tt>.
     *
     * @param repoType the new value of the field repoType
     */
    void setRepoType(String repoType);

}
