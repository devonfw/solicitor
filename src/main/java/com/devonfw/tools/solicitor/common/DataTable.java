/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

public interface DataTable extends Iterable<DataTableRow> {

    /**
     * Get the Headline.
     * 
     * @return the headline
     */
    String[] getHeadRow();

    /**
     * Get a row. Does not include headline.
     * 
     * @param rowNum (zero based
     * @return the requested row
     */
    DataTableRow getDataRow(int rowNum);
}
