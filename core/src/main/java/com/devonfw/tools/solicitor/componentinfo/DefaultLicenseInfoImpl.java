package com.devonfw.tools.solicitor.componentinfo;

/**
 * Default POJO implementation of the {@link LicenseInfo} interface.
 *
 */
public class DefaultLicenseInfoImpl implements LicenseInfo {

  private String spdxid;

  private String licenseFilePath;

  private String givenLicenseText;

  /**
   * The constructor.
   */
  public DefaultLicenseInfoImpl() {

  }

  /**
   * Copy constructor. Creates the new instance by copying the data of the given source.
   *
   * @param source the source instance to copy from
   */
  public DefaultLicenseInfoImpl(LicenseInfo source) {

    this();
    this.spdxid = source.getSpdxid();
    this.licenseFilePath = source.getLicenseFilePath();
    this.givenLicenseText = source.getGivenLicenseText();
  }

  @Override
  public String getSpdxid() {

    return this.spdxid;
  }

  @Override
  public String getLicenseFilePath() {

    return this.licenseFilePath;
  }

  @Override
  public String getGivenLicenseText() {

    return this.givenLicenseText;
  }

  /**
   * @param spdxid new value of {@link #getSpdxid}.
   */
  public void setSpdxId(String spdxid) {

    this.spdxid = spdxid;
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

}
