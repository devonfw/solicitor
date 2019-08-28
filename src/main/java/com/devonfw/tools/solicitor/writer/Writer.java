/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.writer;

import java.util.Map;

import com.devonfw.tools.solicitor.common.DataTable;

public interface Writer {

    boolean accept(String type);

    void writeReport(String templateSource, String target,
            Map<String, DataTable> dataTables);

}