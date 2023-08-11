package com.devonfw.tools.solicitor.componentinfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Default POJO implementation of a {@link ComponentInfo}.
 *
 */
public class DefaultComponentInfoImpl implements ComponentInfo {

  private Collection<String> copyrights;

  private Collection<LicenseInfo> licenses;

  private String noticeFilePath;

  private String noticeFileContent;

  private String homepageUrl;

  private String sourceRepoUrl;

  private String packageDownloadUrl;

  private String sourceDownloadUrl;

  private String dataStatus;

  private List<String> traceabilityNotes;

  /**
   * The constructor.
   */
  public DefaultComponentInfoImpl() {

    this.copyrights = new ArrayList<>();
    this.licenses = new ArrayList<>();
    this.traceabilityNotes = new ArrayList<>();
  }

  /**
   * Copy-Constructor. Allows to construct an instance of the class from another {@link ComponentInfo} instance. Members
   * are deep copied. Any changes to the new instance do not affect the original source.
   *
   * @param source the inctance to copy the data from
   *
   */
  public DefaultComponentInfoImpl(ComponentInfo source) {

    this();
    this.dataStatus = source.getDataStatus();
    this.homepageUrl = source.getHomepageUrl();
    this.noticeFileContent = source.getNoticeFileContent();
    this.noticeFilePath = source.getNoticeFilePath();
    this.packageDownloadUrl = source.getPackageDownloadUrl();
    this.sourceDownloadUrl = source.getSourceDownloadUrl();
    this.sourceRepoUrl = source.getSourceRepoUrl();
    for (String copyright : source.getCopyrights()) {
      addCopyright(copyright);
    }
    for (String traceabilityNote : source.getTraceabilityNotes()) {
      addTraceabillityNote(traceabilityNote);
    }
    for (LicenseInfo licenseInfo : source.getLicenses()) {
      addLicense(licenseInfo);
    }
  }

  /**
   * @param noticeFilePath new value of {@link #getNoticeFilePath}.
   */
  public void setNoticeFilePath(String noticeFilePath) {

    this.noticeFilePath = noticeFilePath;
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

  /**
   * @param dataStatus new value of {@link #getDataStatus}.
   */
  public void setDataStatus(String dataStatus) {

    this.dataStatus = dataStatus;
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
  public String getNoticeFilePath() {

    return this.noticeFilePath;
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

  @Override
  public String getDataStatus() {

    return this.dataStatus;
  }

  @Override
  public List<String> getTraceabilityNotes() {

    return Collections.unmodifiableList(this.traceabilityNotes);
  }

  /**
   * Clears the list of tracebilityNotes.
   */
  public void clearTraceabilityNotes() {

    this.traceabilityNotes = new ArrayList<>();
  }

  /**
   * Appends a new traceabilityNote to the already existing list.
   *
   * @param traceabilityNote the note to append
   */
  public void addTraceabillityNote(String traceabilityNote) {

    this.traceabilityNotes.add(traceabilityNote);
  }

}
