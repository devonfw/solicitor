/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.writer.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.writer.data.DataTableField.FieldDiffStatus;
import com.devonfw.tools.solicitor.writer.data.DataTableImpl.DataTableRowImpl;
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
 * Correlation is done on the basis of columns "CORR_KEY_X" of the {@link DataTable}s where X is between 0 and 9. These
 * columns need to be defined in the SQL statement which defines the data table and give the values of the correlation
 * keys for each row. CORR_KEY_0 is the correlation key with the highest priority. The algorithm will iterate over all
 * correlation keys (starting with highest priority) and try to match rows in newTable and oldTable with same
 * correlation key value. Matching will be finished, when
 * </p>
 * <ul>
 * <li>there are no unmatched rows in newTable OR</li>
 * <li>there are no unmatched rows in oldTable OR</li>
 * <li>all existing correlations keys have been processed</li>
 * </ul>
 *
 * <p>
 * <b>Merging the corresponding data of newTable and oldTable</b>
 * </p>
 * <p>
 * For each row in newTable where there is a corresponding row in oldTable merge the value of the fields of the old row
 * to the fields of the new row ({@link DataTableField#getOldValue()}).
 * </p>
 * <p>
 * <b>Setting the diffStatus of rows and fields</b>
 * </p>
 * <ul>
 * <li>For each field in rows where a corresponding old row was found the {@link FieldDiffStatus} will be set to the
 * appropriate value {@link FieldDiffStatus#UNCHANGED} or {@link FieldDiffStatus#CHANGED} depending on whether old and
 * new field value is identical.</li>
 * <li>Fields of rows where no old row existed will stay at {@link FieldDiffStatus#UNAVAILABLE}.</li>
 * <li>For data rows where no corresponding row in oldTable existed the {@link RowDiffStatus} will be set to
 * {@link RowDiffStatus#NEW}.</li>
 * <li>For rows where there was a corresponding old row the {@link RowDiffStatus} will be calculated based on the
 * {@link FieldDiffStatus} of all fields excluding the following fields:
 * <ul>
 * <li>fields starting with "OBJ_" (will contain data model objects and not simply scalar values)</li>
 * <li>fields starting with "PARENT_" (will contain technical foreign key values which might change even if the object
 * itself has not changed)</li>
 * <li>fields starting with "ID_" (will contain technical primary keys which might change even if the object itself has
 * not changed)</li>
 * <li>fields starting with "CORR_" (these are the correlation keys)</li>
 * </ul>
 * </li>
 * </ul>
 * If any of the remaining fields is of status {@link FieldDiffStatus#CHANGED} the {@link RowDiffStatus} will be
 * {@link RowDiffStatus#CHANGED}, otherwise it will be {@link RowDiffStatus#UNCHANGED}.
 *
 */
@Component
public class DataTableDifferImpl implements DataTableDiffer {

  private static final Logger LOG = LoggerFactory.getLogger(DataTableDifferImpl.class);

  /**
   * The Constructor.
   */
  public DataTableDifferImpl() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DataTable diff(DataTable newTable, DataTable oldTable, boolean includeDeletedRows) {

    if (oldTable == null) {
      return newTable;
    }
    if (newTable.isEmpty() && oldTable.isEmpty()) {
      return newTable;
    }

    DataTable correlatedTable = correlateTable(newTable, oldTable, includeDeletedRows);
    evaluateRowDiff(correlatedTable);
    return correlatedTable;
  }

  /**
   * Correlate both tables and merge corresponding rows of the old table to the new table.
   *
   * @param newTable the new table
   * @param oldTable the old table
   * @param includeDeletedRows indicate if deleted row should be included in the result
   * @return the correlated table
   */
  private DataTable correlateTable(DataTable newTable, DataTable oldTable, boolean includeDeletedRows) {

    // handling case that newTable does not contain any data - which even makes the headrow to be empty
    String[] resultHeadRow = newTable.getHeadRow().length > 0 ? newTable.getHeadRow() : oldTable.getHeadRow();
    DataTableImpl correlatedTable = new DataTableImpl(resultHeadRow);
    // map to hold for each index of newTable the corresponding index of oldTable
    Map<Integer, Integer> newTableIndexToOldTableIndexMap = new HashMap<>();
    //
    Set<Integer> oldTableUnmatchedIndexSet = new LinkedHashSet<>();
    // initialize a Map with the index of the newTable as a key and the
    // assigned DataTableRow of the old table as value
    for (int i = 0; i < newTable.size(); i++) {
      newTableIndexToOldTableIndexMap.put(i, null);
    }
    // initialize a Set with not yet assigned rows of the old table
    for (int i = 0; i < oldTable.size(); i++) {
      oldTableUnmatchedIndexSet.add(i);
    }

    // correlate rows based on correlation keys
    Set<String> keySet = new TreeSet<>(Arrays.asList(resultHeadRow));
    boolean correlationKeyExisting = false;
    for (int i = 0; i < 10; i++) {
      String corrKeyName = "CORR_KEY_" + i;
      if (keySet.contains(corrKeyName)) {
        correlationKeyExisting = true;
        boolean completed = assignCorrelatedRows(newTable, oldTable, newTableIndexToOldTableIndexMap,
            oldTableUnmatchedIndexSet, corrKeyName);
        if (completed) {
          break;
        }
      } else {
        break;
      }
    }

    // if no correlation key exists then we could not correlate any rows, will skip delta processing
    // and just return the new table as is
    if (!correlationKeyExisting) {
      for (DataTableRow newTableRow : newTable) {
        newTableRow.setRowDiffStatus(RowDiffStatus.UNKNOWN);
      }
      return newTable;
    }

    if (oldTableUnmatchedIndexSet.size() > 0) {
      for (Integer oldRowIndex : oldTableUnmatchedIndexSet) {
        DataTableRow oldRow = oldTable.getDataRow(oldRowIndex);
        // ((DataTableImpl) newTable).addRow((DataTableRowImpl) oldRow);
        oldRow.setRowDiffStatus(RowDiffStatus.DELETED);
      }
    }

    // first add any trailing deleted rows from the old table to the correlated table
    // (they will otherwise not be added at all)
    appendDeletedRows(includeDeletedRows, oldTable, correlatedTable, 0);

    for (int i = 0; i < newTable.size(); i++) {
      DataTableRow newTableRow = newTable.getDataRow(i);
      correlatedTable.addRow((DataTableRowImpl) newTableRow);
      Integer correspondingOldRowIndex = newTableIndexToOldTableIndexMap.get(i);
      if (correspondingOldRowIndex != null) {
        DataTableRow correspondingOldRow = oldTable.getDataRow(correspondingOldRowIndex);
        mergeRow(newTableRow, correspondingOldRow);
        // possibly append any unmatched (deleted) rows from old table
        appendDeletedRows(includeDeletedRows, oldTable, correlatedTable, correspondingOldRowIndex + 1);

      } else {
        newTableRow.setRowDiffStatus(RowDiffStatus.NEW);
      }
    }

    return correlatedTable;
  }

  /**
   * For a given correlation key name try to map not yet mapped old rows to not yet mapped new rows.
   *
   * @param newTable the new table
   * @param newTableIndexToOldTableIndexMap a map which hold for each index of rows in the new table the corresponding
   *        found old row
   * @param oldTableUnmatchedIndexSet the set of not yet mapped old rows
   * @param corrKeyColumn the name of the correlation key
   * @return <code>true</code> if all rows in the new table are mapped, <code>false</code> otherwise
   */
  private boolean assignCorrelatedRows(DataTable newTable, DataTable oldTable,
      Map<Integer, Integer> newTableIndexToOldTableIndexMap, Set<Integer> oldTableUnmatchedIndexSet,
      String corrKeyColumn) {

    int unmatchedEntries = 0;
    for (Entry<Integer, Integer> entry : newTableIndexToOldTableIndexMap.entrySet()) {
      if (entry.getValue() == null) {
        DataTableRow newTableRow = newTable.getDataRow(entry.getKey());
        Integer matchingOldRow = searchAndRemoveFromOldTableRowSet(newTableRow, oldTable, oldTableUnmatchedIndexSet,
            corrKeyColumn);
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
   * Append any subsequent deleted rows from old table to the correlated table.
   *
   * @param isActive flag to indicate whether this feature is active
   * @param oldTable the old table
   * @param correlatedTable the table fo correlated rows
   * @param oldIndexToCheck the index in the old table to start checking for deleted rows
   */
  private void appendDeletedRows(boolean isActive, DataTable oldTable, DataTableImpl correlatedTable,
      int oldIndexToCheck) {

    if (!isActive) {
      return;
    }
    int i = oldIndexToCheck;
    while (i < oldTable.size()) {
      DataTableRow oldRow = oldTable.getDataRow(i);
      if (oldRow.getRowDiffStatus() != RowDiffStatus.DELETED) {
        break;
      } else {
        correlatedTable.addRow((DataTableRowImpl) oldRow);
      }
      i++;
    }
  }

  /**
   * Calculate the {@link RowDiffStatus} for each row.
   *
   * @param newTable the table where the row diff status should be recalculated.
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
      if (oneRow.getRowDiffStatus() == RowDiffStatus.UNKNOWN) {
        continue;
      }
      if (oneRow.getRowDiffStatus() == RowDiffStatus.NEW) {
        continue;
      }
      if (oneRow.getRowDiffStatus() == RowDiffStatus.DELETED) {
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

    String corrKey = tableRow.get(corrKeyColumn).toString();
    if (corrKey == null) {
      LOG.error(LogMessages.CORRELATION_KEY_NULL.msg(), corrKeyColumn);
      throw new NullPointerException();
    }
    return corrKey;
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
   * Search a corresponding row in the Set of not yet matched old rows. If a row is found remove it from the Set.
   *
   * @param newTableRow the row of the new table to which the corresponding old row is searched
   * @param oldTableUnmatchedIndexSet the set of not yet matched old rows
   * @param corrKeyColumn the name of the correlation key column to check
   * @return the matching old row; <code>null</code> if no match was found
   */
  private Integer searchAndRemoveFromOldTableRowSet(DataTableRow newTableRow, DataTable oldTable,
      Set<Integer> oldTableUnmatchedIndexSet, String corrKeyColumn) {

    String newTableRowCorrKey = extractKey(newTableRow, corrKeyColumn);
    for (Iterator<Integer> iterator = oldTableUnmatchedIndexSet.iterator(); iterator.hasNext();) {
      int oldIndex = iterator.next();
      DataTableRow oldTableRow = oldTable.getDataRow(oldIndex);
      String oldTableRowCorrKey = extractKey(oldTableRow, corrKeyColumn);
      if (newTableRowCorrKey.equals(oldTableRowCorrKey)) {
        // corresponding row found!
        iterator.remove();
        return oldIndex;
      }
    }
    return null;
  }

}
