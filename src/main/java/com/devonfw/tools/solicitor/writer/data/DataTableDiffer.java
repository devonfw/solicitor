/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.writer.data;

/**
 * Object which computes the delta between two different {@link DataTable}s.
 */
public interface DataTableDiffer {

    /**
     * Calculate difference between the given DataTable objects.
     *
     * @param newTable the new table
     * @param oldTable the old table
     * @return the difference
     */
    DataTable diff(DataTable newTable, DataTable oldTable);

}
