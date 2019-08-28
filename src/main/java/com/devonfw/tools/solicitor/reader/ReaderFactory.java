/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorRuntimeException;

@Component
public class ReaderFactory {

    @Autowired
    private Reader[] readers;

    public Reader readerFor(String type) {

        for (Reader reader : readers) {
            if (reader.accept(type)) {
                return reader;
            }
        }
        throw new SolicitorRuntimeException(
                "No Reader defined for type '" + type + "'");
    }

}
