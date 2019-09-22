/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractDataRowSource implements DataRowSource {

    private static long idSingleton = 0;

    // TODO: Map of all instances: leak and can not be cleared!
    private static Map<String, AbstractDataRowSource> allInstances =
            new TreeMap<>();

    private static final DecimalFormat integerFormat =
            new DecimalFormat("000000000");

    public static AbstractDataRowSource getInstance(String id) {

        synchronized (integerFormat) {
            return allInstances.get(id);
        }
    }

    private String id;

    public AbstractDataRowSource() {

        synchronized (integerFormat) {
            id = integerFormat.format(idSingleton++);
            allInstances.put(id, this);
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

    /**
     * Remove this object from the collection of {@link AbstractDataRowSource}s.
     */
    public void remove() {

        synchronized (integerFormat) {
            allInstances.remove(this.id);
        }

    }

}
