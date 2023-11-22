package com.devonfw.tools.solicitor.componentinfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Default POJO implementation of a {@link ComponentInfoData}.
 *
 */
public class DefaultComponentInfoDataImpl implements ComponentInfoData {

  private Collection<String> copyrights;

  private Collection<LicenseInfo> licenses;

  private String noticeFileUrl;

  private String noticeFileContent;

  private String homepageUrl;

  private String sourceRepoUrl;

  private String packageDownloadUrl;

  private String sourceDownloadUrl;

  /**
   * The constructor.
   */
  public DefaultComponentInfoDataImpl() {

    this.copyrights = new ArrayList<>();
    this.licenses = new ArrayList<>();
  }

  /**
   * Copy-Constructor. Allows to construct an instance of the class from another {@link ComponentInfoData} instance.
   * Members are deep copied. Any changes to the new instance do not affect the original source.
   *
   * @param source the instance to copy the data from
   *
   */
  public DefaultComponentInfoDataImpl(ComponentInfoData source) {

    this();
    this.homepageUrl = source.getHomepageUrl();
    this.noticeFileContent = source.getNoticeFileContent();
    this.noticeFileUrl = source.getNoticeFileUrl();
    this.packageDownloadUrl = source.getPackageDownloadUrl();
    this.sourceDownloadUrl = source.getSourceDownloadUrl();
    this.sourceRepoUrl = source.getSourceRepoUrl();
    for (String copyright : source.getCopyrights()) {
      addCopyright(copyright);
    }
    for (LicenseInfo licenseInfo : source.getLicenses()) {
      addLicense(licenseInfo);
    }
  }

  /**
   * @param noticeFileUrl new value of {@link #getNoticeFileUrl}.
   */
  public void setNoticeFileUrl(String noticeFileUrl) {

    this.noticeFileUrl = noticeFileUrl;
  }

  /**
   * @param noticeFileContent new value of {@link #getNoticeFileContent}.
   */
  public void setNoticeFileContent(String noticeFileContent) {

    this.noticeFileContent = noticeFileContent;
  }

  /**
   * @param homepageUrl new value of {@link #getHomepageUrl}.
   */
  public void setHomepageUrl(String homepageUrl) {

    this.homepageUrl = homepageUrl;
  }

  /**
   * @param sourceRepoUrl new value of {@link #getSourceRepoUrl}.
   */
  public void setSourceRepoUrl(String sourceRepoUrl) {

    this.sourceRepoUrl = sourceRepoUrl;
  }

  /**
   * @param packageDownloadUrl new value of {@link #getPackageDownloadUrl}.
   */
  public void setPackageDownloadUrl(String packageDownloadUrl) {

    this.packageDownloadUrl = packageDownloadUrl;
  }

  /**
   * @param sourceDownloadUrl new value of {@link #getSourceDownloadUrl}.
   */
  public void setSourceDownloadUrl(String sourceDownloadUrl) {

    this.sourceDownloadUrl = sourceDownloadUrl;
  }

  @Override
  public Collection<String> getCopyrights() {

    return Collections.unmodifiableCollection(this.copyrights);
  }

  /**
   * Clears the collection of copyrights.
   */
  public void clearCopyrights() {

    this.copyrights = new ArrayList<>();
  }

  /**
   * Adds a copyright string to the list of already existing copyrights.
   *
   * @param copyright the copyrigt sring to add
   */
  public void addCopyright(String copyright) {

    this.copyrights.add(copyright);
  }

  @Override
  public Collection<LicenseInfo> getLicenses() {

    return Collections.unmodifiableCollection(this.licenses);
  }

  /**
   * Clears the collection of licenses.
   */
  public void clearLicenses() {

    this.licenses = new ArrayList<>();
  }

  /**
   * Adds a {@link LicenseInfo} instance to the collection of licenses.
   *
   * @param licenseInfo the license to add
   */
  public void addLicense(LicenseInfo licenseInfo) {

    this.licenses.add(new DefaultLicenseInfoImpl(licenseInfo));
  }

  @Override
  public String getNoticeFileUrl() {

    return this.noticeFileUrl;
  }

  @Override
  public String getNoticeFileContent() {

    return this.noticeFileContent;
  }

  @Override
  public String getHomepageUrl() {

    return this.homepageUrl;
  }

  @Override
  public String getSourceRepoUrl() {

    return this.sourceRepoUrl;
  }

  @Override
  public String getPackageDownloadUrl() {

    return this.packageDownloadUrl;
  }

  @Override
  public String getSourceDownloadUrl() {

    return this.sourceDownloadUrl;
  }

}
