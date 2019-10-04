/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

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

}
