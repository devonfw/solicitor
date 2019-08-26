/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.capgemini.solicitor.common;

import java.text.DecimalFormat;

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

    public abstract String[] getHeadElements();

    public abstract String[] getDataElements();

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
