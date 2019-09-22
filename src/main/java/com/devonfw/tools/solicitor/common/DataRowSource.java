/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface DataRowSource {

    @JsonIgnore
    String[] getHeadRow();

    @JsonIgnore
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