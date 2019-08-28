/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamFactory {

    InputStream createInputStreamFor(String url) throws IOException;

}