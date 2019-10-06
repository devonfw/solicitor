/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.writer.data;

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

import com.devonfw.tools.solicitor.writer.data.DataTableField.FieldDiffStatus;
import com.devonfw.tools.solicitor.writer.data.DataTableRow.RowDiffStatus;

/**
 * Default implementation of the {@link DataTableDiffer} interface.
 * 
 * <p>
 * The calculation of the diff is done as follows:
 * </p>
 * <p>
 * <b>Correlating the rows in the newTable with the rows in the oldTable</b>
 * </p>
 * <p>
 * Correlation is done on the basis of columns "CORR_KEY_X" of the
 * {@link DataTable}s where X is between 0 and 9. These columns need to be
 * defined in the SQL statement which defines the data table and give the values
 * of the correlation keys for each row. CORR_KEY_0 is the correlation key with
 * the highest priority. The algorithm will iterate over all correlation keys
 * (starting with highest priority) and try to match rows in newTable and
 * oldTable with same correlation key value. Matching will be finished, when
 * </p>
 * <ul>
 * <li>there are no unmatched rows in newTable OR</li>
 * <li>there are no unmatched rows on oldTable OR</li>
 * <li>all existing correlations keys have been processed</li>
 * </ul>
 * 
 * <p>
 * <b>Merging the corresponding data of newTable and oldTable</b>
 * </p>
 * <p>
 * For each row in newTable where there is a corresponding row in oldTable merge
 * the value of the fields of the old row to the fields of the new row
 * ({@link DataTableField#getOldValue()}).
 * </p>
 * <p>
 * <b>Setting the diffStatus of rows and fields</b>
 * </p>
 * <ul>
 * <li>For each field in rows where a corresponding old row was found the
 * {@link FieldDiffStatus} will be set to the appropriate value
 * {@link FieldDiffStatus#UNCHANGED} or {@link FieldDiffStatus#CHANGED}
 * depending on whether old and new field value is identical.</li>
 * <li>Fields of rows where no old row existed will stay at
 * {@link FieldDiffStatus#UNAVAILABLE}.</li>
 * <li>For data rows where no corresponding row in oldTable existed the
 * {@link RowDiffStatus} will be set to {@link RowDiffStatus#NEW}.</li>
 * <li>For rows where there was a corresponding old row the
 * {@link RowDiffStatus} will be calculated based on the {@link FieldDiffStatus}
 * of all fields excluding the following fields:
 * <ul>
 * <li>fields starting with "OBJ_" (will contain data model objects and not
 * simply scalar values)</li>
 * <li>fields starting with "PARENT_" (will contain technical foreign key values
 * which might change even if the object itself has not changed)</li>
 * <li>fields starting with "ID_" (will contain technical primary keys which
 * might change even if the object itself has not changed)</li>
 * <li>fields starting with "CORR_" (these are the correlation keys)</li>
 * </ul>
 * </li>
 * </ul>
 * If any of the remaining fields is of status {@link FieldDiffStatus#CHANGED}
 * the {@link RowDiffStatus} will be {@link RowDiffStatus#CHANGED}, otherwise it
 * will be {@link RowDiffStatus#UNCHANGED}.
 * 
 */
@Component
public class DataTableDifferImpl implements DataTableDiffer {

    /**
     * The Constructor.
     */
    public DataTableDifferImpl() {

    }

    /**
     * For a given correlation key name try to map not yet mapped old rows to
     * not yet mapped new rows.
     * 
     * @param newTable the new table
     * @param newTableIndexToOldTableRowMap a map which hold for each index of
     *        rows in the new table the corresponding found old row
     * @param oldTableRowSet the set of not yet mapped old rows
     * @param corrKeyColumn the name of the correlation key
     * @return <code>true</code> if all rows in the new table are mapped,
     *         <code>false</code> otherwise
     */
    private boolean assignCorrelatedRows(DataTable newTable, Map<Integer, DataTableRow> newTableIndexToOldTableRowMap,
            Set<DataTableRow> oldTableRowSet, String corrKeyColumn) {

        int unmatchedEntries = 0;
        for (Entry<Integer, DataTableRow> entry : newTableIndexToOldTableRowMap.entrySet()) {
            if (entry.getValue() == null) {
                DataTableRow newTableRow = newTable.getDataRow(entry.getKey());
                DataTableRow matchingOldRow =
                        searchAndRemoveFromOldTableRowSet(newTableRow, oldTableRowSet, corrKeyColumn);
                if (matchingOldRow != null) {
                    entry.setValue(matchingOldRow);
                } else {
                    unmatchedEntries++;
                }
            }
        }
        return (unmatchedEntries == 0);

    }

    /**
     * Correlate both tables and merge corresponding rows of the old table to
     * the new table.
     * 
     * @param newTable the new table
     * @param oldTable the old table
     */
    private void correlateTable(DataTable newTable, DataTable oldTable) {

        Map<Integer, DataTableRow> newTableIndexToOldTableRowMap = new HashMap<>();
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
        Set<String> keySet = new TreeSet<>(Arrays.asList(newTable.getHeadRow()));
        for (i = 0; i < 10; i++) {
            String corrKeyName = "CORR_KEY_" + i;
            if (keySet.contains(corrKeyName)) {
                boolean completed =
                        assignCorrelatedRows(newTable, newTableIndexToOldTableRowMap, oldTableRowSet, corrKeyName);
                if (completed) {
                    break;
                }
            } else {
                break;
            }
        }

        i = 0;
        for (DataTableRow newTableRow : newTable) {
            DataTableRow correspondingOldRow = newTableIndexToOldTableRowMap.get(i);
            if (correspondingOldRow != null) {
                mergeRow(newTableRow, correspondingOldRow);
            } else {
                newTableRow.setRowDiffStatus(RowDiffStatus.NEW);
            }
            i++;
        }
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

    /**
     * Calculate the {@link RowDiffStatus} for each row.
     * 
     * @param newTable the table where the row diff status should be
     *        recalculated.
     */
    private void evaluateRowDiff(DataTable newTable) {

        List<String> fieldsRelevantForDiff = new ArrayList<>();
        for (String fieldname : newTable.getHeadRow()) {
            if (fieldname.startsWith("OBJ_") || fieldname.startsWith("PARENT_") || fieldname.startsWith("ID_")
                    || fieldname.startsWith("CORR_") || fieldname.equals("rowCount")) {
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
                if (oneRow.get(fieldToCheck).getDiffStatus() == FieldDiffStatus.CHANGED) {
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

    /**
     * Get the value of the correlation key with the given name from the row.
     * 
     * @param tableRow the row
     * @param corrKeyColumn the name of the correlation key column
     * @return the value of the correlation key
     */
    private String extractKey(DataTableRow tableRow, String corrKeyColumn) {

        return tableRow.get(corrKeyColumn).toString();
    }

    /**
     * Merge a single row.
     * 
     * @param newTableRow the row to merge to
     * @param correspondingOldRow the row to merge from
     */
    private void mergeRow(DataTableRow newTableRow, DataTableRow correspondingOldRow) {

        for (int i = 0; i < newTableRow.getSize(); i++) {
            newTableRow.getValueByIndex(i).setOldValue(correspondingOldRow.getValueByIndex(i).getValue());
        }
    }

    /**
     * Search a corresponding row in the Set of not yet matched old rows. If a
     * row is found remove it from the Set.
     * 
     * @param newTableRow the row of the new table to which the corresponding
     *        old row is searched
     * @param oldTableRowSet the set of not yet matched old rows
     * @param corrKeyColumn the name of the correlation key column to check
     * @return the matching old row; <code>null</code> if no match was found
     */
    private DataTableRow searchAndRemoveFromOldTableRowSet(DataTableRow newTableRow, Set<DataTableRow> oldTableRowSet,
            String corrKeyColumn) {

        String newTableRowCorrKey = extractKey(newTableRow, corrKeyColumn);
        for (Iterator<DataTableRow> iterator = oldTableRowSet.iterator(); iterator.hasNext();) {
            DataTableRow oldTableRow = iterator.next();
            String oldTableRowCorrKey = extractKey(oldTableRow, corrKeyColumn);
            if (newTableRowCorrKey.equals(oldTableRowCorrKey)) {
                // corresponding row found!
                iterator.remove();
                return oldTableRow;
            }
        }
        return null;
    }

}
