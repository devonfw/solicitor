/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.componentinfo.scancode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper class which supports extracting a range of lines from a given (multiline) string.
 */
public class MultilineHelper {

  /**
   * Constructor. Prevents instantiation.
   *
   */
  private MultilineHelper() {

  }

  /**
   * Extracts a range of lines from the given (multiline) input.
   *
   * @param input the multiline input
   * @param lineInfo lines to extract, given as <code>#L17-L20</code>. <code>null</code> indicates that the whole input
   *        should be returned.
   * @return the extracted lines.
   */
  public static String possiblyExtractLines(String input, String lineInfo) {

    if (lineInfo == null) {
      return input;
    }
    Pattern pattern = Pattern.compile("#L(\\d+)(-L(\\d+))?");
    Matcher matcher = pattern.matcher(lineInfo);
    if (matcher.find()) {
      int startLine = Integer.parseInt(matcher.group(1));
      int endLine = Integer.parseInt(matcher.group(3) != null ? matcher.group(3) : matcher.group(1));
      String[] splitted = input.split("\\n");
      StringBuffer result = new StringBuffer();
      for (int i = 0; i < splitted.length; i++) {
        if (i + 1 >= startLine && i + 1 <= endLine) {
          result.append(splitted[i]).append("\n");
        }
      }
      return result.toString();
    } else {
      throw new IllegalStateException("Regex did not find line info - this seems to be a bug.");
    }
  }

}
