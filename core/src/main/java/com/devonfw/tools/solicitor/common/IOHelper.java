/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helpers for doing I/O related stuff.
 */
public class IOHelper {

  private static final Logger LOG = LoggerFactory.getLogger(IOHelper.class);

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
   * @see #checkAndCreateLocation(File)
   */
  public static void checkAndCreateLocation(String targetFilename) {

    checkAndCreateLocation(new File(targetFilename));
  }

  /**
   * Assure that the directory in which the given file should be located exists. Try to create the directory if it does
   * not yet exist.
   * 
   * @param targetFile a file
   */
  public static void checkAndCreateLocation(File targetFile) {

    File targetDir = targetFile.getParentFile();
    try {
      if (targetDir != null && !targetDir.exists()) {
        Files.createDirectories(targetDir.toPath());
        LOG.info(LogMessages.CREATED_DIRECTORY.msg(), targetDir.getCanonicalPath());
      }
    } catch (IOException e) {
      try {
        throw new SolicitorRuntimeException("Could not create directory '" + targetDir.getCanonicalPath() + "'", e);
      } catch (IOException e1) {
        // above getCanonicalPath might throw an IOException as well, so
        // falling back to something more robust
        throw new SolicitorRuntimeException("Could not create directory '" + targetDir.getAbsolutePath() + "'", e);
      }
    }

  }

  /**
   * Private constructor which prevents instantiation
   */
  private IOHelper() {

  }

}
