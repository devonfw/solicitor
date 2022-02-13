/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A factory to create {@link InputStream}s from files.
 *
 */
public class FileInputStreamFactory implements InputStreamFactory {

  /**
   * Constructor.
   */
  public FileInputStreamFactory() {

  }

  /**
   * {@inheritDoc}
   * 
   * Creates the {@link InputStream} from a file given by the filename
   */
  @Override
  public InputStream createInputStreamFor(String filename) throws IOException {

    return new FileInputStream(filename);
  }

  /**
   * {@inheritDoc}
   * 
   * Checks that the given file exists, is not a directory and can be read..
   */
  @Override
  public boolean isExisting(String stringIdentifier) {

    File fileToTest = new File(stringIdentifier);
    return fileToTest.exists() && fileToTest.isFile() && fileToTest.canRead();
  }

}
