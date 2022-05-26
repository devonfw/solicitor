/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Helpers for doing I/O related stuff. Mainly a simplified copy of {@link com.devonfw.tools.solicitor.common.IOHelper}
 * which was copied here to avoid further dependencies. If at some point
 * {@link com.devonfw.tools.solicitor.common.IOHelper} is moved to a shared lib then this copy should be removed.
 */
public class IOHelperSimple {

  /**
   * Reads text from a given {@link java.io.InputStream} into a String. Uses UTF-8 encoding.
   *
   * @param inp the InputStream to read from
   * @return the read String
   * @throws java.io.IOException if any IOExcption occurs
   */
  public static String readStringFromInputStream(InputStream inp) throws IOException {

    if (inp != null) {
      StringBuilder contentBuilder = new StringBuilder();
      String str;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inp, StandardCharsets.UTF_8))) {
        while ((str = reader.readLine()) != null) {
          contentBuilder.append(str + "\n");
        }
      }
      return contentBuilder.toString();
    } else {
      throw new NullPointerException("Given InputStream must not be null");
    }
  }

  /**
   * Assure that the directory in which the given file should be located exists. Try to create the directory if it does
   * not yet exist.
   *
   * @param targetFilename the name (including path) of a file
   * @throws IOException if something goes wrong
   * @see #checkAndCreateLocation(File)
   */
  public static void checkAndCreateLocation(String targetFilename) throws IOException {

    checkAndCreateLocation(new File(targetFilename));
  }

  /**
   * Assure that the directory in which the given file should be located exists. Try to create the directory if it does
   * not yet exist.
   *
   * @param targetFile a file
   * @throws IOException if something goes wrong
   */
  public static void checkAndCreateLocation(File targetFile) throws IOException {

    File targetDir = targetFile.getParentFile();
    if (targetDir != null && !targetDir.exists()) {
      Files.createDirectories(targetDir.toPath());
    }

  }

  /**
   * Private constructor which prevents instantiation
   */
  private IOHelperSimple() {

  }

}
