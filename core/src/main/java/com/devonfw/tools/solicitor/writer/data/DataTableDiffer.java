/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.writer.data;

import com.devonfw.tools.solicitor.writer.data.DataTableRow.RowDiffStatus;

/**
 * Object which computes the delta between two different {@link DataTable}s.
 */
public interface DataTableDiffer {

  /**
   * Calculate difference between the given DataTable objects.
   *
   * @param newTable the new table
   * @param oldTable the old table
   * @param includeDeletedRows flag which indicates if table rows which only exist in the old table shall be included in
   *        the result (marked with {@link RowDiffStatus#DELETED})
   * @return the difference
   */
  DataTable diff(DataTable newTable, DataTable oldTable, boolean includeDeletedRows);

}
