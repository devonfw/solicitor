/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.DataTableField.FieldDiffStatus;
import com.devonfw.tools.solicitor.common.DataTableRow.RowDiffStatus;

/**
 * Default implementation of the {@link DataTableDiffer} interface.
 */
@Component
public class DataTableDifferImpl implements DataTableDiffer {

    /**
     * The Constructor.
     */
    public DataTableDifferImpl() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataTable diff(DataTable newTable, DataTable oldTable) {

        if (oldTable == null) {
            return newTable;
        }

        correlateTable(newTable, oldTable);
        evaluateRowDiff(newTable);
        return newTable;
    }

    private void evaluateRowDiff(DataTable newTable) {

        List<String> fieldsRelevantForDiff = new ArrayList<>();
        for (String fieldname : newTable.getHeadRow()) {
            if (fieldname.startsWith("OBJ_") || fieldname.startsWith("PARENT_")
                    || fieldname.startsWith("ID_")
                    || fieldname.startsWith("DIFF_")
                    || fieldname.equals("rowCount")) {
                continue;
            }
            fieldsRelevantForDiff.add(fieldname);
        }
        for (DataTableRow oneRow : newTable) {
            boolean changed = false;
            if (oneRow.getRowDiffStatus() == RowDiffStatus.NEW) {
                continue;
            }
            for (String fieldToCheck : fieldsRelevantForDiff) {
                if (oneRow.get(fieldToCheck)
                        .getDiffStatus() == FieldDiffStatus.CHANGED) {
                    changed = true;
                }
            }
            if (changed) {
                oneRow.setRowDiffStatus(RowDiffStatus.CHANGED);
            } else {
                oneRow.setRowDiffStatus(RowDiffStatus.UNCHANGED);
            }
        }
    }

    private void correlateTable(DataTable newTable, DataTable oldTable) {

        Map<Integer, DataTableRow> newTableIndexToOldTableRowMap =
                new HashMap<>();
        Set<DataTableRow> oldTableRowSet = new HashSet<>();
        // initialize a Map with the index of the newTable as a key and the
        // assigned DataTableRow of the old table as value
        int i = 0;
        for (DataTableRow newRow : newTable) {
            newTableIndexToOldTableRowMap.put(i, null);
            i++;
        }
        // initialize a Set with not yet assigned rows of the old table
        for (DataTableRow oldRow : oldTable) {
            oldTableRowSet.add(oldRow);
        }
        Set<String> keySet =
                new TreeSet<>(Arrays.asList(newTable.getHeadRow()));
        for (i = 0; i < 10; i++) {
            String diffKeyName = "DIFF_KEY_" + i;
            if (keySet.contains(diffKeyName)) {
                boolean completed = assignCorrelatedRows(newTable,
                        newTableIndexToOldTableRowMap, oldTableRowSet,
                        diffKeyName);
                if (completed) {
                    break;
                }
            } else {
                break;
            }
        }

        i = 0;
        for (DataTableRow newTableRow : newTable) {
            DataTableRow correspondingOldRow =
                    newTableIndexToOldTableRowMap.get(i);
            if (correspondingOldRow != null) {
                mergeRow(newTableRow, correspondingOldRow);
            } else {
                newTableRow.setRowDiffStatus(RowDiffStatus.NEW);
            }
            i++;
        }
    }

    private void mergeRow(DataTableRow newTableRow,
            DataTableRow correspondingOldRow) {

        for (int i = 0; i < newTableRow.getSize(); i++) {
            newTableRow.getValueByIndex(i).setOldValue(
                    correspondingOldRow.getValueByIndex(i).getValue());
        }
    }

    private boolean assignCorrelatedRows(DataTable newTable,
            Map<Integer, DataTableRow> newTableIndexToOldTableRowMap,
            Set<DataTableRow> oldTableRowSet, String diffKeyColumn) {

        int unmatchedEntries = 0;
        for (Entry<Integer, DataTableRow> entry : newTableIndexToOldTableRowMap
                .entrySet()) {
            if (entry.getValue() == null) {
                DataTableRow newTableRow = newTable.getDataRow(entry.getKey());
                DataTableRow matchingOldRow = searchAndRemoveFromOldTableRowSet(
                        newTableRow, oldTableRowSet, diffKeyColumn);
                if (matchingOldRow != null) {
                    entry.setValue(matchingOldRow);
                } else {
                    unmatchedEntries++;
                }
            }
        }
        return (unmatchedEntries == 0);

    }

    private DataTableRow searchAndRemoveFromOldTableRowSet(
            DataTableRow newTableRow, Set<DataTableRow> oldTableRowSet,
            String diffKeyColumn) {

        String newTableRowDiffKey = extractKey(newTableRow, diffKeyColumn);
        for (Iterator<DataTableRow> iterator =
                oldTableRowSet.iterator(); iterator.hasNext();) {
            DataTableRow oldTableRow = iterator.next();
            String oldTableRowDiffKey = extractKey(oldTableRow, diffKeyColumn);
            if (newTableRowDiffKey.equals(oldTableRowDiffKey)) {
                // corresponding row found!
                iterator.remove();
                return oldTableRow;
            }
        }
        return null;
    }

    private String extractKey(DataTableRow tableRow, String diffKeyColumn) {

        return tableRow.get(diffKeyColumn).toString();
    }

}
