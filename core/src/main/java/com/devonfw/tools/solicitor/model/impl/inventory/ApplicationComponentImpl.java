/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.impl.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.devonfw.tools.solicitor.common.LicenseTextHelper;
import com.devonfw.tools.solicitor.common.content.ContentProvider;
import com.devonfw.tools.solicitor.common.content.web.WebContent;
import com.devonfw.tools.solicitor.common.packageurl.PackageURLSerializer;
import com.devonfw.tools.solicitor.model.impl.AbstractModelObject;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.packageurl.PackageURL;

/**
 * Implementation of the {@link ApplicationComponent} model object interface.
 */
public class ApplicationComponentImpl extends AbstractModelObject implements ApplicationComponent {

  private Application application;

  private UsagePattern usagePattern;

  private boolean ossModified;

  private String ossHomepage;

  private String sourceRepoUrl;

  private String noticeFileUrl;

  private String noticeFileContentKey;

  private String groupId;

  private String artifactId;

  private String version;

  private String repoType;

  private PackageURL packageUrl;

  private String copyrights;

  private String packageDownloadUrl;

  private String sourceDownloadUrl;

  private List<NormalizedLicense> normalizedLicenses = new ArrayList<>();

  private List<RawLicense> rawLicenses = new ArrayList<>();

  private ContentProvider<WebContent> licenseContentProvider;

  private String dataStatus;

  private String traceabilityNotes;

  /** {@inheritDoc} */
  @Override
  public void addNormalizedLicense(NormalizedLicense normalizedLicense) {

    this.normalizedLicenses.add(normalizedLicense);
  }

  /** {@inheritDoc} */
  @Override
  public void addRawLicense(RawLicense rawLicense) {

    this.rawLicenses.add(rawLicense);
  }

  @Override
  public void removeAllRawLicenses() {

    this.rawLicenses = new ArrayList<>();
  }

  /** {@inheritDoc} */
  @Override
  protected Application doGetParent() {

    return this.application;
  }

  /** {@inheritDoc} */
  @Override
  @JsonIgnore
  public Application getApplication() {

    return this.application;
  }

  /** {@inheritDoc} */
  @Override
  public String getArtifactId() {

    return this.artifactId;
  }

  /** {@inheritDoc} */
  @Override
  public String[] getDataElements() {

    return new String[] { this.groupId, this.artifactId, this.version, getRepoType(), getPackageUrl().toString(),
    getOssHomepage(), getSourceRepoUrl(), getNoticeFileUrl(), getNoticeFileContent(), getUsagePattern().toString(),
    isOssModified() ? "true" : "false", getCopyrights(), getPackageDownloadUrl(), getSourceDownloadUrl(),
    getDataStatus(), getTraceabilityNotes() };
  }

  /** {@inheritDoc} */
  @Override
  public String getGroupId() {

    return this.groupId;
  }

  /** {@inheritDoc} */
  @Override
  public String[] getHeadElements() {

    return new String[] { "groupId", "artifactId", "version", "repoType", "packageUrl", "ossHomepage", "sourceRepoUrl",
    "noticeFileUrl", "noticeFileContent", "usagePattern", "ossModified", "copyrights", "packageDownloadUrl",
    "sourceDownloadUrl", "dataStatus", "traceabilityNotes" };
  }

  /** {@inheritDoc} */
  @Override
  public List<NormalizedLicense> getNormalizedLicenses() {

    return Collections.unmodifiableList(this.normalizedLicenses);
  }

  /** {@inheritDoc} */
  @Override
  public String getOssHomepage() {

    return this.ossHomepage;
  }

  /** {@inheritDoc} */
  @Override
  public String getSourceRepoUrl() {

    return this.sourceRepoUrl;
  }

  /** {@inheritDoc} */
  @Override
  public String getNoticeFileUrl() {

    return this.noticeFileUrl;
  }

  /** {@inheritDoc} */
  @Override
  @JsonIgnore
  public String getNoticeFileContent() {

    return LicenseTextHelper.replaceLongHtmlContent(retrieveTextFromPool(this.noticeFileContentKey));
  }

  /**
   * Gets the text pool key of {@link #getNoticeFileContent()}
   *
   * @return the text pool key
   */
  public String getNoticeFileContentKey() {

    return this.noticeFileContentKey;
  }

  /** {@inheritDoc} */
  @Override
  public List<RawLicense> getRawLicenses() {

    return Collections.unmodifiableList(this.rawLicenses);
  }

  /** {@inheritDoc} */
  @Override
  public UsagePattern getUsagePattern() {

    return this.usagePattern;
  }

  /** {@inheritDoc} */
  @Override
  public String getVersion() {

    return this.version;
  }

  /** {@inheritDoc} */
  @Override
  public String getRepoType() {

    return this.repoType;
  }

  /** {@inheritDoc} */
  @Override
  @JsonSerialize(using = PackageURLSerializer.class)
  public PackageURL getPackageUrl() {

    return this.packageUrl;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOssModified() {

    return this.ossModified;
  }

  /** {@inheritDoc} */
  @Override
  public String getPackageDownloadUrl() {

    return this.packageDownloadUrl;
  }

  /** {@inheritDoc} */
  @Override
  public String getSourceDownloadUrl() {

    return this.sourceDownloadUrl;
  }

  /**
   * This method gets the field <code>licenseContentProvider</code>.
   *
   * @return the field licenseContentProvider
   */
  @JsonIgnore
  public ContentProvider<WebContent> getLicenseContentProvider() {

    return this.licenseContentProvider;
  }

  /** {@inheritDoc} */
  @Override
  public void setApplication(Application application) {

    if (this.application != null) {
      throw new IllegalStateException("Once the ApplicationImpl is set it can not be changed");
    }
    this.application = application;
    application.addApplicationComponent(this);
  }

  /** {@inheritDoc} */
  @Override
  public void setArtifactId(String artifactId) {

    this.artifactId = artifactId;
  }

  /** {@inheritDoc} */
  @Override
  public void setGroupId(String groupId) {

    this.groupId = groupId;
  }

  /** {@inheritDoc} */
  @Override
  public void setOssHomepage(String ossHomepage) {

    this.ossHomepage = ossHomepage;
  }

  /** {@inheritDoc} */
  @Override
  public void setSourceRepoUrl(String sourceRepoUrl) {

    this.sourceRepoUrl = sourceRepoUrl;
  }

  /** {@inheritDoc} */
  @Override
  public void setNoticeFileUrl(String noticeFileUrl) {

    this.noticeFileUrl = noticeFileUrl;
  }

  @Override
  public void setNoticeFileContent(String noticeFileContent) {

    this.noticeFileContentKey = storeTextInPool(noticeFileContent);

  }

  /**
   * Sets the text pool key of {@link #getNoticeFileContent()}
   *
   * @param noticeFileContentKey the key to set
   */
  public void setNoticeFileContentKey(String noticeFileContentKey) {

    this.noticeFileContentKey = noticeFileContentKey;

  }

  /** {@inheritDoc} */
  @Override
  public void setOssModified(boolean ossModified) {

    this.ossModified = ossModified;
  }

  /** {@inheritDoc} */
  @Override
  public void setUsagePattern(UsagePattern usagePattern) {

    this.usagePattern = usagePattern;
  }

  /** {@inheritDoc} */
  @Override
  public void setVersion(String version) {

    this.version = version;
  }

  /** {@inheritDoc} */
  @Override
  public void setRepoType(String repoType) {

    this.repoType = repoType;
  }

  /** {@inheritDoc} */
  @Override
  public void setPackageUrl(PackageURL packageUrl) {

    // Assures we have the canonical representation and the packageUrl is valid;
    if (packageUrl != null) {
      this.packageUrl = packageUrl;
      if (this.repoType == null) {
        // if repoType is not set then set it via the type of the PackageURL
        this.repoType = packageUrl.getType();
      }
    } else {
      this.packageUrl = null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public void completeData() {

    for (RawLicense rawLicense : this.rawLicenses) {
      rawLicense.completeData();
    }
    for (NormalizedLicense normalizedLicense : this.normalizedLicenses) {
      normalizedLicense.completeData();
    }

    if (this.noticeFileContentKey == null) {
      setNoticeFileContent(this.licenseContentProvider.getContentForUri(this.noticeFileUrl).getContent());
    }

  }

  @Override
  public String getCopyrights() {

    return this.copyrights;
  }

  @Override
  public void setCopyrights(String copyrights) {

    this.copyrights = copyrights;

  }

  /** {@inheritDoc} */
  @Override
  public void setPackageDownloadUrl(String packageDownloadUrl) {

    this.packageDownloadUrl = packageDownloadUrl;
  }

  /** {@inheritDoc} */
  @Override
  public void setSourceDownloadUrl(String sourceDownloadUrl) {

    this.sourceDownloadUrl = sourceDownloadUrl;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDataStatus() {

    return this.dataStatus;
  }

  /** {@inheritDoc} */

  @Override
  public String getTraceabilityNotes() {

    return this.traceabilityNotes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDataStatus(String dataStatus) {

    this.dataStatus = dataStatus;
  }

  /** {@inheritDoc} */

  @Override
  public void setTraceabilityNotes(String traceabilityNotes) {

    this.traceabilityNotes = traceabilityNotes;
  }

  /**
   * This method sets the field <code>licenseContentProvider</code>.
   *
   * @param licenseContentProvider the new value of the field licenseContentProvider
   */
  public void setLicenseContentProvider(ContentProvider<WebContent> licenseContentProvider) {

    this.licenseContentProvider = licenseContentProvider;
  }

}
