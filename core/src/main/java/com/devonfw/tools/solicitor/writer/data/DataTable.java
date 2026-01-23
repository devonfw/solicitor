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

  /**
   * Tests if the DataTable contains data. If it does not contain data then even {@link #getHeadRow()} might not provide
   * any information.
   *
   * @return <code>true</code> if there is no data in the table
   */
  boolean isEmpty();

  /**
   * Get the number of rows in this DataTable
   *
   * @return the number of rows in the DataTable
   */
  int size();
}
