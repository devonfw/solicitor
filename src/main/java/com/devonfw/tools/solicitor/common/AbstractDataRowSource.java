/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.text.DecimalFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractDataRowSource implements DataRowSource {

    private static long idSingleton = 0;

    private static final DecimalFormat integerFormat =
            new DecimalFormat("000000000");

    private String id;

    public AbstractDataRowSource() {

        synchronized (integerFormat) {
            id = integerFormat.format(idSingleton++);
        }
    }

    @JsonIgnore
    public abstract String[] getHeadElements();

    @JsonIgnore
    public abstract String[] getDataElements();

    @JsonIgnore
    public AbstractDataRowSource getParent() {

        return null;
    }

    public String getId() {

        return id;
    }

    @Override
    public String[] getHeadRow() {

        if (getParent() == null) {
            return getHeadElements();
        } else {
            return DataRowSource.concatHeadRow(getParent().getHeadRow(),
                    getHeadElements());
        }

    }

    @Override
    public String[] getDataRow() {

        if (getParent() == null) {
            return getDataElements();
        } else {
            return DataRowSource.concatDataRow(getParent().getDataRow(),
                    getDataElements());
        }

    }

}
