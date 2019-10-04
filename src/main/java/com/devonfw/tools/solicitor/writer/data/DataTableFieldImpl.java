/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.writer.data;

/**
 * Implementation of {@link DataTableField}.
 */
public class DataTableFieldImpl implements DataTableField {

    private Object value;

    private Object oldValue;

    private FieldDiffStatus diffStatus;

    /**
     * Constructor which only takes the current value. Difference status will be
     * {@link FieldDiffStatus#UNAVAILABLE} by default.
     *
     * @param value the current value
     */
    public DataTableFieldImpl(Object value) {

        this.value = value;
        diffStatus = FieldDiffStatus.UNAVAILABLE;
    }

    /**
     * Constructor which takes the current value and the old value. The
     * difference status will be calulated depending on the given values. result
     * wll either be {@link FieldDiffStatus#CHANGED} or
     * {@link FieldDiffStatus#UNCHANGED}.
     *
     * @param value the current value
     * @param oldValue the old value to compare with
     */
    public DataTableFieldImpl(Object value, Object oldValue) {

        this.value = value;
        this.oldValue = oldValue;
        evaluateDiff();
    }

    /**
     * Update {@link #diffStatus}. Only use this if {@link #oldValue} was set
     * because it will otherwise overwrite the default
     * {@link FieldDiffStatus#UNAVAILABLE} value.
     */
    private void evaluateDiff() {

        if (value != null) {
            diffStatus = (value.equals(oldValue)) ? FieldDiffStatus.UNCHANGED : FieldDiffStatus.CHANGED;
        } else {
            diffStatus = (oldValue == null) ? FieldDiffStatus.UNCHANGED : FieldDiffStatus.CHANGED;
        }
    }

    /** {@inheritDoc} */
    @Override
    public FieldDiffStatus getDiffStatus() {

        return diffStatus;
    }

    /** {@inheritDoc} */
    @Override
    public Object getOldValue() {

        return oldValue;
    }

    /** {@inheritDoc} */
    @Override
    public Object getValue() {

        return value;
    }

    /** {@inheritDoc} */
    @Override
    public void setOldValue(Object oldValue) {

        this.oldValue = oldValue;
        evaluateDiff();
    }

    /**
     * {@inheritDoc}
     * 
     * Will delegate to the {@link #toString()} method of the (current) value to
     * allow rendering of the current value if the {@link DataTableField} object
     * is referenced in a velocity template. In case that the current value is
     * null then this method will return <code>null</code>.
     */
    @Override
    public String toString() {

        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }
}
