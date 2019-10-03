/**
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

        private DataTableField[] data;

        private RowDiffStatus rowDiffStatus;

        /**
         * Creates a {@link DataTableRow} setting the field
         * {@link #rowDiffStatus} to {@link RowDiffStatus#UNAVAILABLE}.
         * 
         * @param data the row data
         */
        DataTableRowImpl(DataTableField[] data) {

            this(data, RowDiffStatus.UNAVAILABLE);
        }

        /**
         * Creates a {@link DataTableRow}
         * 
         * @param data the row data
         * @param rowDiffStatus
         */
        DataTableRowImpl(DataTableField[] data, RowDiffStatus rowDiffStatus) {

            this.data = data.clone();
            this.rowDiffStatus = rowDiffStatus;
        }

        @Override
        public DataTableField getValueByIndex(int index) {

            return data[index];
        }

        @Override
        public DataTableField get(String fieldName) {

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

        @Override
        public RowDiffStatus getRowDiffStatus() {

            return rowDiffStatus;
        }

        @Override
        public void setRowDiffStatus(RowDiffStatus rowDiffStatus) {

            this.rowDiffStatus = rowDiffStatus;

        }

        @Override
        public int getSize() {

            return data.length;
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

                return delegateIterator.next();
            }

        };
    }

    @Override
    public String[] getHeadRow() {

        return headline.clone();
    }

    @Override
    public DataTableRow getDataRow(int rowNum) {

        return data.get(rowNum);
    }

    public void addRow(DataTableField[] dataRow) {

        if (dataRow.length != headline.length) {
            throw new IllegalArgumentException(
                    "Number of data columns must match columns of headline");
        }
        data.add(new DataTableRowImpl(dataRow));
    }

}
