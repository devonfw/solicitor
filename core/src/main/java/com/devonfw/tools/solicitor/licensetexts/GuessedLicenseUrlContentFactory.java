/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.licensetexts;

import com.devonfw.tools.solicitor.common.content.ContentFactory;

/**
 * A {@link ContentFactory} for {@link GuessedLicenseUrlContent}.
 */
public class GuessedLicenseUrlContentFactory implements ContentFactory<GuessedLicenseUrlContent> {

  /**
   * {@inheritDoc}
   */
  @Override
  public GuessedLicenseUrlContent fromString(String string) {

    if (string == null || string.isEmpty()) {
      return emptyContent();
    }

    int pos = string.indexOf("\n----");
    String guessedUrl = string.substring(0, pos);
    if (guessedUrl.isEmpty()) {
      guessedUrl = null;
    }
    String reduced = string.substring(pos);
    pos = reduced.indexOf("---\n");
    String auditInfo = reduced.substring(pos + 4);
    if (auditInfo.isEmpty()) {
      auditInfo = null;
    }
    return new GuessedLicenseUrlContent(guessedUrl, auditInfo);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GuessedLicenseUrlContent emptyContent() {

    return new GuessedLicenseUrlContent(null, null);
  }

}
