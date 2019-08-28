/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileInputStreamFactory implements InputStreamFactory {

    public FileInputStreamFactory() {

    }

    @Override
    public InputStream createInputStreamFor(String filename)
            throws IOException {

        return new FileInputStream(filename);
    }

}
