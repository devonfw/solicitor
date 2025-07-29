package com.devonfw.tools.solicitor.common;

import org.apache.commons.text.WordUtils;
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

  /**
   * Checks if the lines of the given text exceed the max allowed width. If yes the wrap the text to the given width. If
   * not then return the original text.
   *
   * @param stringToBeWrapped the string that should be line wrapped
   * @param maxAllowedWidth the maximum allowed line width without wrapping
   * @param wrapWidth the new line length when doing wrapping
   * @return the wrapped string
   */
  public static String wrapIfNecessary(String stringToBeWrapped, int maxAllowedWidth, int wrapWidth) {

    if (stringToBeWrapped == null) {
      return stringToBeWrapped;
    }

    String[] lines = stringToBeWrapped.split("\r\n|\r|\n");
    boolean needsWrapping = false;
    for (String line : lines) {
      if (line.length() > maxAllowedWidth) {
        needsWrapping = true;
      }
    }
    if (!needsWrapping) {
      return stringToBeWrapped;
    } else {
      for (int i = 0; i < lines.length; i++) {
        lines[i] = WordUtils.wrap(lines[i], wrapWidth);
      }
      return String.join(System.lineSeparator(), lines);
    }
  }
}
