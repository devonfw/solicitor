package com.devonfw.tools.solicitor.model.inventory;

import java.util.List;

import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

/**
 * Represents an ApplicationComponent in the Solicitor data model.
 */
public interface ApplicationComponent {

  /**
   * Adds the given {@link com.devonfw.tools.solicitor.model.inventory.NormalizedLicense} to this ApplicationComponent.
   *
   * @param normalizedLicense a {@link com.devonfw.tools.solicitor.model.inventory.NormalizedLicense} object.
   */
  void addNormalizedLicense(NormalizedLicense normalizedLicense);

  /**
   * Adds the given {@link com.devonfw.tools.solicitor.model.inventory.RawLicense} to this ApplicateComponent.
   *
   * @param rawLicense a {@link com.devonfw.tools.solicitor.model.inventory.RawLicense} object.
   */
  void addRawLicense(RawLicense rawLicense);

  /**
   * Removes all raw licenses. Might be used if resetting license information is required.
   */
  void removeAllRawLicenses();

  /**
   * This method gets the field <code>application</code>.
   *
   * @return the field application
   */
  Application getApplication();

  /**
   * This method gets the field <code>artifactId</code>.
   *
   * @return the field artifactId
   */
  String getArtifactId();

  /**
   * This method gets the field <code>groupId</code>.
   *
   * @return the field groupId
   */
  String getGroupId();

  /**
   * This method gets an unmodifiable copy of the field <code>normalizedLicenses</code>.
   *
   * @return the field normalizedLicenses
   */
  List<NormalizedLicense> getNormalizedLicenses();

  /**
   * This method gets the field <code>ossHomepage</code>.
   *
   * @return the field ossHomepage
   */
  String getOssHomepage();

  /**
   * This method gets the field <code>noticeFileUrl</code>.
   *
   * @return the field noticeFileUrl
   */
  String getNoticeFileUrl();

  /**
   * This method gets the field <code>noticeFileContent</code>.
   *
   * @return the field noticeFileContent
   */
  String getNoticeFileContent();

  /**
   * This method gets an unmodifiable copy of the field <code>rawLicenses</code>.
   *
   * @return the field rawLicenses
   */
  List<RawLicense> getRawLicenses();

  /**
   * This method gets the field <code>usagePattern</code>.
   *
   * @return the field usagePattern
   */
  UsagePattern getUsagePattern();

  /**
   * This method gets the field <code>version</code>.
   *
   * @return the field version
   */
  String getVersion();

  /**
   * This method gets the field <code>repoType</code>.
   *
   * @return the field repoType
   */
  String getRepoType();

  /**
   * This methods gets the field <code>packageUrl</code>. This is a technology neutral identifier for the
   * {@link ApplicationComponent}.
   *
   * @see <a href="https://github.com/package-url/purl-spec">purl-spec</a>
   *
   * @return the field packageUrl
   */
  public String getPackageUrl();

  /**
   * This method gets the field <code>ossModified</code>.
   *
   * @return the field ossModified
   */
  boolean isOssModified();

  String getCopyrights();

  /**
   * Sets the {@link Application}.
   *
   * @param application the application to which this {@link ApplicationComponent} belongs to.
   */
  void setApplication(Application application);

  /**
   * This method sets the field <code>artifactId</code>.
   *
   * @param artifactId the new value of the field artifactId
   */
  void setArtifactId(String artifactId);

  /**
   * This method sets the field <code>groupId</code>.
   *
   * @param groupId the new value of the field groupId
   */
  void setGroupId(String groupId);

  /**
   * This method sets the field <code>ossHomepage</code>.
   *
   * @param ossHomepage the new value of the field ossHomepage
   */
  void setOssHomepage(String ossHomepage);

  /**
   * This method sets the field <code>noticeFileUrl</code>.
   *
   * @param noticeFileUrl the new value of the field noticeFileUrl
   */
  void setNoticeFileUrl(String noticeFileUrl);

  /**
   * This method sets the field <code>ossModified</code>.
   *
   * @param ossModified the new value of the field ossModified
   */
  void setOssModified(boolean ossModified);

  /**
   * This method sets the field <code>usagePattern</code>.
   *
   * @param usagePattern the new value of the field usagePattern
   */
  void setUsagePattern(UsagePattern usagePattern);

  /**
   * This method sets the field <code>version</code>.
   *
   * @param version the new value of the field version
   */
  void setVersion(String version);

  /**
   * This method sets the field <code>repoType</code>.
   *
   * @param repoType the new value of the field repoType
   */
  void setRepoType(String repoType);

  /**
   * This method sets the field <code>copyrights</code>.
   *
   * @param copyrights the new value of the field copyrights
   */
  void setCopyrights(String copyrights);

  /**
   * This method sets the field <code>packageUrl</code>.
   *
   * @param packageUrl the new value of the field packageUrl.
   *
   * @see <a href="https://github.com/package-url/purl-spec">purl-spec</a>
   *
   */
  public void setPackageUrl(String packageUrl);

  /**
   * Complete the data of this object by setting members which are derived from other members.
   */
  public void completeData();

}
