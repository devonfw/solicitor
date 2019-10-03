/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

/**
 * Implementation of {@link DataTableField}.
 */
public class DataTableFieldImpl implements DataTableField {

    private Object value;

    private Object oldValue;

    private FieldDiffStatus diffStatus;

    /**
     * Constructor which only takes the current value.
     * 
     * @param value the current value
     */
    public DataTableFieldImpl(Object value) {

        this.value = value;
        diffStatus = FieldDiffStatus.UNAVAILABLE;
    }

    /**
     * Constructor which takes the current value and the old value.
     * 
     * @param value the current value
     */
    public DataTableFieldImpl(Object value, Object oldValue) {

        this.value = value;
        this.oldValue = oldValue;
        evaluateDiff();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue() {

        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getOldValue() {

        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    @Override
    public void setOldValue(Object oldValue) {

        this.oldValue = oldValue;
        evaluateDiff();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldDiffStatus getDiffStatus() {

        return diffStatus;
    }

    /**
     * Update {@link #diffStatus}. Only use this if {@link #oldValue} was set
     * because it will otherwise overwrite the default
     * {@link FieldDiffStatus#UNAVAILABLE} value.
     */
    private void evaluateDiff() {

        if (value != null) {
            diffStatus = (value.equals(oldValue)) ? FieldDiffStatus.UNCHANGED
                    : FieldDiffStatus.CHANGED;
        } else {
            diffStatus = (oldValue == null) ? FieldDiffStatus.UNCHANGED
                    : FieldDiffStatus.CHANGED;
        }
    }
}
