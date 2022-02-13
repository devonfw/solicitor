package com.devonfw.tools.solicitor.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class with helper methods for handling license texts.
 */
public final class LicenseTextHelper {

  private static final String LARGE_HTML_CONTENT_PLACEHOLDER = "### Large HTML content which is not shown here - needs cleanup ###";

  private static final int LARGE_HTML_CONTENT_SIZE_LIMIT = 20000;

  private static final Logger LOG = LoggerFactory.getLogger(LicenseTextHelper.class);

  private static boolean replacingHtmlWarningLogged = false;

  /**
   * Private constructor to prevent instantiation.
   */
  private LicenseTextHelper() {

  }

  /**
   * Replaces long raw html content with a short note that indicates the need for some cleanup.
   *
   * @param rawContent the incoming content
   * @return the incoming content or the note if the incoming was a large html content
   */
  public static String replaceLongHtmlContent(String rawContent) {

    if (rawContent == null || rawContent.isEmpty()) {
      return rawContent;
    }
    if (rawContent.length() > LARGE_HTML_CONTENT_SIZE_LIMIT && rawContent.toLowerCase().contains("</html>")) {
      if (!replacingHtmlWarningLogged) {
        replacingHtmlWarningLogged = true;
        LOG.warn(LogMessages.REPLACING_EXCESSIVE_HTML_CONTENT.msg(), LARGE_HTML_CONTENT_PLACEHOLDER);
      }
      return LARGE_HTML_CONTENT_PLACEHOLDER;
    } else {
      return rawContent;
    }

  }

}
