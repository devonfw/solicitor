/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.writer.data;

/**
 * A data row in a {@link DataTable}.
 */
public interface DataTableRow extends Cloneable {

    /**
     * Possible of the difference of this row and its contained field.
     */
    public static enum RowDiffStatus {
        /**
         * no diff has been done or no old row known
         */
        UNAVAILABLE,

        /**
         * row has changed (at least one of the relevant fields has changed
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
     * The clone method.
     *
     * @return the cloned object.
     * @see Object#clone()
     */
    DataTableRow clone();

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
     * Sets the {@link RowDiffStatus}.
     *
     * @param rowDiffStatus the difference status of this row.
     */
    void setRowDiffStatus(RowDiffStatus rowDiffStatus);
}
