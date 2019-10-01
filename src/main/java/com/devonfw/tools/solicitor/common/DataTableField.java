/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

/**
 * Container for a field within the {@link DataTableRow} of a {@link DataTable}.
 * This might also hold new and old value in case a diff between two
 * {@link DataTable}s has been done.
 */
public interface DataTableField {

    public static enum FieldDiffStatus {
        /**
         * no diff has been done or no old values known
         */
        UNAVAILABLE,

        /**
         * value has changed
         */
        CHANGED,

        /**
         * value is unchanged
         */
        UNCHANGED
    }

    Object getValue();

    Object getOldValue();

    FieldDiffStatus getDiffStatus();

    @Override
    String toString();

    void setOldValue(Object oldValue);

}
