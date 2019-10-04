/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface of factories to create {@link InputStream}s.
 */
public interface InputStreamFactory {

    /**
     * Create an input stream for the given id.
     *
     * @param stringIdentifier a stidn which identifies the source. Concrete
     *        meaning depends on the implementation.
     * @return the creates {@link InputStream} object.
     * @throws java.io.IOException if any lowlevel exception occurs
     */
    InputStream createInputStreamFor(String stringIdentifier) throws IOException;

}
