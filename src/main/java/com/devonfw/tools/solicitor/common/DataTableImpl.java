/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataTableImpl implements DataTable {

    public class DataTableRowImpl implements DataTableRow {

        private Object[] data;

        DataTableRowImpl(Object[] data) {

            this.data = data.clone();
        }

        @Override
        public Object getValueByIndex(int index) {

            return data[index];
        }

        @Override
        public Object get(String fieldName) {

            Integer i = fieldnameToIndexMap.get(fieldName);
            if (i == null) {
                return null;
            }
            return data[i];
        }

        @Override
        public DataTableRow clone() {

            return new DataTableRowImpl(data);
        }

    }

    private String[] headline;

    private Map<String, Integer> fieldnameToIndexMap;

    private List<DataTableRow> data;

    public DataTableImpl(String[] headline) {

        super();
        this.headline = headline;
        fieldnameToIndexMap = new HashMap<>();
        for (int i = 0; i < headline.length; i++) {
            fieldnameToIndexMap.put(headline[i], i);
        }
        data = new ArrayList<>();
    }

    @Override
    public Iterator<DataTableRow> iterator() {

        return new Iterator<DataTableRow>() {
            private Iterator<DataTableRow> delegateIterator = data.iterator();

            @Override
            public boolean hasNext() {

                return delegateIterator.hasNext();
            }

            @Override
            public DataTableRow next() {

                return delegateIterator.next().clone();
            }

        };
    }

    @Override
    public String[] getHeadRow() {

        return headline.clone();
    }

    @Override
    public DataTableRow getDataRow(int rowNum) {

        return data.get(rowNum).clone();
    }

    public void addRow(Object[] dataRow) {

        if (dataRow.length != headline.length) {
            throw new IllegalArgumentException(
                    "Number of data columns must match columns of headline");
        }
        data.add(new DataTableRowImpl(dataRow));
    }

}
