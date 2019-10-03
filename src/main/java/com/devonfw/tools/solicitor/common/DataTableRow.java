/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

/**
 * A data row in a {@link DataTable}.
 *
 */
public interface DataTableRow extends Cloneable {

    public static enum RowDiffStatus {
        /**
         * no diff has been done or no old row known
         */
        UNAVAILABLE,

        /**
         * row has changed
         */
        CHANGED,

        /**
         * row is unchanged
         */
        UNCHANGED,

        /**
         * row is new
         */
        NEW
    }

    /**
     * Gets the number of Fields in this row.
     * 
     * @return the size of this row (number of columns/fields)
     */
    int getSize();

    /**
     * Gets a field value by its index.
     * 
     * @param index the index of the field
     * @return the field value
     */
    DataTableField getValueByIndex(int index);

    /**
     * Gets a field value by its name.
     * 
     * @param fieldName the name of the field
     * @return the field value
     */
    DataTableField get(String fieldName);

    /**
     * Gets the {@link RowDiffStatus}.
     * 
     * @return the diff status of this row
     */
    RowDiffStatus getRowDiffStatus();

    /**
     * Sets the {@link RowDiffStatus}.
     * 
     * @return rowDiffStatus
     */
    void setRowDiffStatus(RowDiffStatus rowDiffStatus);

    /**
     * The clone method.
     * 
     * @return the cloned object.
     * @see Object#clone()
     */
    DataTableRow clone();
}
