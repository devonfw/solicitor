/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Helpers for doin I/O related stuff.
 *
 */
public class IOHelper {

    /**
     * Private constructor which prevents instantiation
     */
    private IOHelper() {

    }

    /**
     * Reads text from a given {@link InputStream} into a String. Uses UTF-8
     * encoding.
     * 
     * @param inp the InputStream to read from
     * @return the read String
     * @throws IOException if any IOExcption occurs
     */
    public static String readStringFromInputStream(InputStream inp)
            throws IOException {

        if (inp != null) {
            StringBuilder contentBuilder = new StringBuilder();
            String str;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inp, StandardCharsets.UTF_8))) {
                while ((str = reader.readLine()) != null) {
                    contentBuilder.append(str + "\n");
                }
            }
            return contentBuilder.toString();
        } else {
            throw new NullPointerException(
                    "Given InputStream must not be null");
        }
    }

}
