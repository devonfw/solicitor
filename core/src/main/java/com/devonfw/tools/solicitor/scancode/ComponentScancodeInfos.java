package com.devonfw.tools.solicitor.scancode;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure for holding the scan information from scancode for a single package.
 */
public class ComponentScancodeInfos {

  /**
   * Holds the info about a single found license.
   */
  public class LicenseInfo {

    /**
     * Id for of the license.
     */
    public String id;

    /**
     * SPDX-ID of the license.
     */
    public String spdxid;

    /**
     * The score of the license.
     */
    public double licenseScore;

    /**
     * Path to the license file.
     */
    public String licenseFilePath;

    /**
     * The score of the license file.
     */
    public double licenseFileScore;

    /**
     * The constructor.
     *
     * @param id the id
     * @param spdxid the spdx id
     * @param defaultUrl the default URL of the license text
     * @param licenseScore the score for the license
     * @param licenseFilePath the path to the license file
     * @param licenseFileScore the score of the license file
     */
    public LicenseInfo(String id, String spdxid, String defaultUrl, double licenseScore, String licenseFilePath,
        double licenseFileScore) {

      super();
      this.id = id;
      this.spdxid = spdxid;
      this.licenseScore = licenseScore;
      if (licenseFileScore >= ComponentScancodeInfos.this.minLicensefilePercentage) {
        this.licenseFilePath = licenseFilePath;
        this.licenseFileScore = licenseFileScore;
      } else {
        this.licenseFilePath = defaultUrl;
        this.licenseFileScore = 0.0;
      }
    }
  }

  private static final double MIN_NOTICEFILE_PERCENTAGE = 0.0;

  private SortedSet<String> copyrights = new TreeSet<>();

  private SortedMap<String, LicenseInfo> licenses = new TreeMap<>();

  private double noticeFileScore = 0;

  private String noticeFilePath = null;

  private String url;

  private String sourceRepoUrl;

  private double minLicensefilePercentage;

  private double minLicenseScore;

  /**
   * The constructor.
   *
   * @param minLicenseScore the minimum score to take license findings into account
   * @param minLicensefilePercentage the minimum percentage of license text to possibly use file as license file
   */
  public ComponentScancodeInfos(double minLicenseScore, double minLicensefilePercentage) {

    super();
    this.minLicenseScore = minLicenseScore;
    this.minLicensefilePercentage = minLicensefilePercentage;
  }

  /**
   * Adds a single copyright line.
   *
   * @param copyright the copyright
   */
  public void addCopyright(String copyright) {

    this.copyrights.add(copyright);
  }

  /**
   * Gets all copyrights.
   *
   * @return the copyrights
   */
  public Collection<String> getCopyrights() {

    return Collections.unmodifiableCollection(this.copyrights);
  }

  /**
   * Clears all found copyrights.
   */
  public void clearCopyrights() {

    this.copyrights = new TreeSet<>();
  }

  /**
   * Adds a license or updates the information if the relevant scores exceed the required thresholds and the score is
   * better than the score of already existing information.
   *
   * @param licenseId the license id
   * @param licenseName the name of the license (SPDS-ID)
   * @param licenseDefaultUrl the url of the generic license text
   * @param score the score of the license finding
   * @param filePath path to the license file
   * @param fileScore score of the license file
   */
  public void addLicense(String licenseId, String licenseName, String licenseDefaultUrl, double score, String filePath,
      double fileScore) {

    if (this.licenses.containsKey(licenseId)) {
      LicenseInfo existingLicenseInfo = this.licenses.get(licenseId);

      double resultingScore = Math.max(existingLicenseInfo.licenseScore, score);
      String resultingFilePath = existingLicenseInfo.licenseFilePath;
      double resultingFileScore = existingLicenseInfo.licenseFileScore;
      if (fileScore > existingLicenseInfo.licenseFileScore) {
        resultingFilePath = filePath;
        resultingFileScore = fileScore;
      }
      this.licenses.put(licenseId, new LicenseInfo(licenseId, licenseName, licenseDefaultUrl, resultingScore,
          resultingFilePath, resultingFileScore));

    } else {
      if (score >= this.minLicenseScore) {
        this.licenses.put(licenseId,
            new LicenseInfo(licenseId, licenseName, licenseDefaultUrl, score, filePath, fileScore));
      }
    }
  }

  /**
   * Gets all licenses.
   *
   * @return all licenses
   */
  public Map<String, LicenseInfo> getLicenses() {

    return Collections.unmodifiableSortedMap(this.licenses);
  }

  /**
   * Clears all stored licenses.
   */
  public void clearLicenses() {

    this.licenses = new TreeMap<>();
  }

  /**
   * Stores the path to a NOTICE file if the score exceeds the minimum required score and is higher than the score of
   * already existing information.
   *
   * @param path path to the NOTICE file
   * @param score score of the file
   */
  public void addNoticeFilePath(String path, double score) {

    if (score > this.noticeFileScore && score >= MIN_NOTICEFILE_PERCENTAGE) {
      this.noticeFilePath = path;
      this.noticeFileScore = score;
    }
  }

  /**
   * Gets the path to the notice file (if any)
   *
   * @return path to the notice file
   */
  public String getNoticeFilePath() {

    return this.noticeFilePath;
  }

  /**
   * Gets the url to the license text
   *
   * @return url to the license text
   */
  public String getUrl() {

    return this.url;
  }

  /**
   * Sets the url to the license text
   *
   * @param url new value of {@link #getUrl()}.
   */
  public void setUrl(String url) {

    this.url = url;
  }

  /**
   * Gets the source-Core-Repository-url
   *
   * @return sourceRepoUrl to the license text
   */
  public String getSourceRepoUrl() {

    return this.sourceRepoUrl;
  }

  /**
   * Sets the source-Core-Repository-url
   *
   * @param sourceRepoUrl new value of {@link #getSourceRepoUrl()}.
   */
  public void setSourceRepoUrl(String sourceRepoUrl) {

    this.sourceRepoUrl = sourceRepoUrl;
  }
}
