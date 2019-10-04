/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;

/**
 * A factory for finding the appropriate {@link Reader} for the given data
 * format.
 */
@Component
public class ReaderFactory {

    @Autowired
    private Reader[] readers;

    /**
     * Returns the appropriate {@link Reader} for the given data format.
     * 
     * @param type a String which denotes the format of the data format
     * @return the appropriate {@link Reader}
     * @throws SolicitorRuntimeException if no appropriate {@link Reader} could
     *         be found
     */
    public Reader readerFor(String type) {

        for (Reader reader : readers) {
            if (reader.accept(type)) {
                return reader;
            }
        }
        throw new SolicitorRuntimeException("No Reader defined for type '" + type + "'");
    }

}
