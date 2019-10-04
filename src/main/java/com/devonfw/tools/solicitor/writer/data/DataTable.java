/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.writer.data;

/**
 * Interface of objects representing a data table.
 */
public interface DataTable extends Iterable<DataTableRow> {

    /**
     * Get a row. Does not include headline.
     *
     * @param rowNum (zero based)
     * @return the requested row
     */
    DataTableRow getDataRow(int rowNum);

    /**
     * Get the Headline.
     *
     * @return the headline
     */
    String[] getHeadRow();
}
