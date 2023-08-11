package com.devonfw.tools.solicitor.componentinfo.scancode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.LicenseInfo;

/**
 * Data structure for holding the scan information from scancode for a single package.
 */
public class ScancodeComponentInfo implements ComponentInfo {

  /**
   * Holds the info about a single found license.
   */
  public class ScancodeLicenseInfo implements LicenseInfo {

    /**
     * Id for of the license.
     */
    private String id;

    /**
     * SPDX-ID of the license.
     */
    private String spdxid;

    /**
     * The score of the license.
     */
    private double licenseScore;

    /**
     * Path to the license file.
     */
    private String licenseFilePath;

    /**
     * Text of license.
     */
    private String givenLicenseText;

    /**
     * The score of the license file. This is measured as number of lines which contain license info
     */
    private int licenseFileScore;

    /**
     * The constructor.
     *
     * @param id the id
     * @param spdxid the spdx id
     * @param defaultUrl the default URL of the license text
     * @param licenseScore the score for the license
     * @param licenseFilePath the path to the license file
     * @param givenLicenseText the given license text (might be <code>null</code>)
     * @param licenseFileScore the score of the license file - number of lines with license info
     */
    public ScancodeLicenseInfo(String id, String spdxid, String defaultUrl, double licenseScore, String licenseFilePath,
        String givenLicenseText, int licenseFileScore) {

      super();
      this.id = id;
      this.spdxid = spdxid;
      this.licenseScore = licenseScore;
      if (licenseFileScore >= ScancodeComponentInfo.this.minLicensefileNumberOfLines) {
        this.licenseFilePath = licenseFilePath;
        this.licenseFileScore = licenseFileScore;
        this.givenLicenseText = givenLicenseText;
      } else {
        this.licenseFilePath = defaultUrl;
        this.licenseFileScore = 0;
      }
    }

    /**
     * @return id
     */
    public String getId() {

      return this.id;
    }

    /**
     * @return spdxid
     */
    @Override
    public String getSpdxid() {

      return this.spdxid;
    }

    /**
     * @return licenseScore
     */
    public double getLicenseScore() {

      return this.licenseScore;
    }

    /**
     * @return licenseFilePath
     */
    @Override
    public String getLicenseFilePath() {

      return this.licenseFilePath;
    }

    /**
     * @return givenLicenseText
     */
    @Override
    public String getGivenLicenseText() {

      return this.givenLicenseText;
    }

    /**
     * @return licenseFileScore
     */
    public int getLicenseFileScore() {

      return this.licenseFileScore;
    }

    /**
     * @param id new value of {@link #getId}.
     */
    public void setId(String id) {

      this.id = id;
    }

    /**
     * @param spdxid new value of {@link #getSpdxid}.
     */
    public void setSpdxid(String spdxid) {

      this.spdxid = spdxid;
    }

    /**
     * @param licenseScore new value of {@link #getLicenseScore}.
     */
    public void setLicenseScore(double licenseScore) {

      this.licenseScore = licenseScore;
    }

    /**
     * @param licenseFilePath new value of {@link #getLicenseFilePath}.
     */
    public void setLicenseFilePath(String licenseFilePath) {

      this.licenseFilePath = licenseFilePath;
    }

    /**
     * @param givenLicenseText new value of {@link #getGivenLicenseText}.
     */
    public void setGivenLicenseText(String givenLicenseText) {

      this.givenLicenseText = givenLicenseText;
    }

    /**
     * @param licenseFileScore new value of {@link #getLicenseFileScore}.
     */
    public void setLicenseFileScore(int licenseFileScore) {

      this.licenseFileScore = licenseFileScore;
    }

  }

  private static final double MIN_NOTICEFILE_PERCENTAGE = 0.0;

  private SortedSet<String> copyrights = new TreeSet<>();

  private SortedMap<String, ScancodeLicenseInfo> licenses = new TreeMap<>();

  private double noticeFileScore = 0;

  private String noticeFilePath = null;

  private String noticeFileContent;

  private String url;

  private String sourceRepoUrl;

  private int minLicensefileNumberOfLines;

  private double minLicenseScore;

  private String packageDownloadUrl;

  private String sourceDownloadUrl;

  private String dataStatus;

  private List<String> traceabilityNotes = new ArrayList<>();

  /**
   * The constructor.
   *
   * @param minLicenseScore the minimum score to take license findings into account
   * @param minLicensefileNumberOfLines the minimum number of licens text lines to possibly use file as license file
   */
  public ScancodeComponentInfo(double minLicenseScore, int minLicensefileNumberOfLines) {

    super();
    this.minLicenseScore = minLicenseScore;
    this.minLicensefileNumberOfLines = minLicensefileNumberOfLines;
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
  @Override
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
   * @param givenLicenseText the license text
   * @param fileScore score of the license file - measured as number of lines which were detected as license text
   */
  public void addLicense(String licenseId, String licenseName, String licenseDefaultUrl, double score, String filePath,
      String givenLicenseText, int fileScore) {

    if (this.licenses.containsKey(licenseId)) {
      ScancodeLicenseInfo existingLicenseInfo = this.licenses.get(licenseId);

      double resultingScore = Math.max(existingLicenseInfo.getLicenseScore(), score);
      String resultingFilePath = existingLicenseInfo.getLicenseFilePath();
      String resultingGivenText = existingLicenseInfo.getGivenLicenseText();
      int resultingFileScore = existingLicenseInfo.getLicenseFileScore();
      if (fileScore > existingLicenseInfo.getLicenseFileScore()) {
        resultingFilePath = filePath;
        resultingFileScore = fileScore;
        resultingGivenText = givenLicenseText;
      }
      this.licenses.put(licenseId, new ScancodeLicenseInfo(licenseId, licenseName, licenseDefaultUrl, resultingScore,
          resultingFilePath, resultingGivenText, resultingFileScore));

    } else {
      if (score >= this.minLicenseScore) {
        this.licenses.put(licenseId, new ScancodeLicenseInfo(licenseId, licenseName, licenseDefaultUrl, score, filePath,
            givenLicenseText, fileScore));
      }
    }
  }

  /**
   * Gets all licenses.
   *
   * @return all licenses
   */
  @Override
  public Collection<ScancodeLicenseInfo> getLicenses() {

    return Collections.unmodifiableSortedMap(this.licenses).values();
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
   * @param noticeFileContent new value of {@link #getNoticeFileContent}.
   */
  public void setNoticeFileContent(String noticeFileContent) {

    this.noticeFileContent = noticeFileContent;
  }

  /**
   * Gets the path to the notice file (if any).
   *
   * @return path to the notice file
   */
  @Override
  public String getNoticeFilePath() {

    return this.noticeFilePath;
  }

  @Override
  public String getNoticeFileContent() {

    return this.noticeFileContent;
  }

  /**
   * Gets the url of the projects homepage.
   *
   * @return url to the projects homepage
   */
  @Override
  public String getHomepageUrl() {

    return this.url;
  }

  /**
   * Sets the url of the projects homepage.
   *
   * @param url new value of {@link #getHomepageUrl()}.
   */
  public void setHomepageUrl(String url) {

    this.url = url;
  }

  /**
   * Gets the url of the source code repository.
   *
   * @return sourceRepoUrl to the license text
   */
  @Override
  public String getSourceRepoUrl() {

    return this.sourceRepoUrl;
  }

  /**
   * Sets the url of the source code repository.
   *
   * @param sourceRepoUrl new value of {@link #getSourceRepoUrl()}.
   */
  public void setSourceRepoUrl(String sourceRepoUrl) {

    this.sourceRepoUrl = sourceRepoUrl;
  }

  /**
   * Gets the url to download the package.
   *
   * @return url to download the package
   */
  @Override
  public String getPackageDownloadUrl() {

    return this.packageDownloadUrl;
  }

  /**
   * Sets the url to download the package.
   *
   * @param packageDownloadUrl new value of {@link #getPackageDownloadUrl()}.
   */
  public void setPackageDownloadUrl(String packageDownloadUrl) {

    this.packageDownloadUrl = packageDownloadUrl;
  }

  /**
   * Gets the url to download the sources of the package.
   *
   * @return url to download the sources of the package
   */
  @Override
  public String getSourceDownloadUrl() {

    return this.sourceDownloadUrl;
  }

  /**
   * Sets the url to download the sources of the package.
   *
   * @param sourceDownloadUrl new value of {@link #getSourceDownloadUrl()}.
   */
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

  /**
   * Gets the traceability notes of the component.
   *
   * @return the traceability notes
   */
  @Override
  public List<String> getTraceabilityNotes() {

    return this.traceabilityNotes;
  }

  /**
   * @param dataStatus new value of {@link #getDataStatus}.
   */
  public void setDataStatus(String dataStatus) {

    this.dataStatus = dataStatus;
  }

  /**
   * @param traceabilityNotes new value of {@link #getTraceabilityNotes}.
   */
  public void setTraceabilityNotes(List<String> traceabilityNotes) {

    this.traceabilityNotes = traceabilityNotes;
  }
}
