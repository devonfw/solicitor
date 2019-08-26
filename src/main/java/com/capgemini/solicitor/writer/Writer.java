/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.writer;

import java.util.Map;

import com.capgemini.solicitor.common.DataTable;

public interface Writer {

    boolean accept(String type);

    void writeReport(String templateSource, String target,
            Map<String, DataTable> dataTables);

}