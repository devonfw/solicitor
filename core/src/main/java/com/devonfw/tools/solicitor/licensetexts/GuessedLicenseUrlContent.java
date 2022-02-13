/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.licensetexts;

import com.devonfw.tools.solicitor.common.content.Content;

/**
 * A {@link Content} which represents a guessed Url of a license.
 */
public class GuessedLicenseUrlContent implements Content {
  private String guessedUrl;

  private String auditInfo;

  /**
   * The Constructor.
   *
   * @param guessedUrl the guessed url
   * @param auditInfo some info for auditing the guessing
   */
  public GuessedLicenseUrlContent(String guessedUrl, String auditInfo) {

    this.guessedUrl = guessedUrl;
    this.auditInfo = auditInfo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String asString() {

    if (this.guessedUrl == null && this.auditInfo == null) {
      return null;
    }
    String s1 = this.guessedUrl != null ? this.guessedUrl : "";
    String s2 = this.auditInfo != null ? this.auditInfo : "";

    return s1 + "\n-------------------------\n" + s2;
  }

  /**
   * This method gets the field <code>guessedUrl</code>.
   *
   * @return the field guessedUrl
   */
  public String getGuessedUrl() {

    return this.guessedUrl;
  }

  /**
   * This method gets the field <code>auditInfo</code>.
   *
   * @return the field auditInfo
   */
  public String getAuditInfo() {

    return this.auditInfo;
  }

}
