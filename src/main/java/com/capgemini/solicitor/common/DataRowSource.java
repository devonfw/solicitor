/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.common;

import java.util.Arrays;

public interface DataRowSource {

    String[] getHeadRow();

    String[] getDataRow();

    public static String[] concatHeadRow(String[] first, String[] second) {

        String[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static String[] concatDataRow(String[] first, String[] second) {

        String[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}