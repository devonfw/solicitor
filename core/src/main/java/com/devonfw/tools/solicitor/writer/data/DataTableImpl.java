/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.writer.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a data table according to the {@link DataTable} interface.
 *
 */
public class DataTableImpl implements DataTable {

  /**
   * Implementation of the table row according to the {@link DataTableRow} interface.
   */
  public class DataTableRowImpl implements DataTableRow {

    private DataTableField[] data;

    private RowDiffStatus rowDiffStatus;

    /**
     * Creates a {@link DataTableRow}. The field {@link #rowDiffStatus} is set to {@link RowDiffStatus#NOT_APPLICABLE}.
     *
     * @param data the row data
     */
    DataTableRowImpl(DataTableField[] data) {

      this(data, RowDiffStatus.NOT_APPLICABLE);
    }

    /**
     * Creates a {@link DataTableRow}.
     *
     * @param data the row data
     * @param rowDiffStatus the difference status of the row
     */
    DataTableRowImpl(DataTableField[] data, RowDiffStatus rowDiffStatus) {

      this.data = data.clone();
      this.rowDiffStatus = rowDiffStatus;
    }

    @Override
    public DataTableRow clone() {

      return new DataTableRowImpl(this.data);
    }

    @Override
    public DataTableField get(String fieldName) {

      Integer i = DataTableImpl.this.fieldnameToIndexMap.get(fieldName);
      if (i == null) {
        return null;
      }
      return this.data[i];
    }

    @Override
    public RowDiffStatus getRowDiffStatus() {

      return this.rowDiffStatus;
    }

    @Override
    public int getSize() {

      return this.data.length;
    }

    @Override
    public DataTableField getValueByIndex(int index) {

      return this.data[index];
    }

    @Override
    public void setRowDiffStatus(RowDiffStatus rowDiffStatus) {

      this.rowDiffStatus = rowDiffStatus;

    }

  }

  private String[] headline;

  private Map<String, Integer> fieldnameToIndexMap;

  private List<DataTableRow> data;

  /**
   * Constructor.
   *
   * @param headline an array of {@link java.lang.String} objects.
   */
  public DataTableImpl(String[] headline) {

    super();
    this.headline = headline;
    this.fieldnameToIndexMap = new HashMap<>();
    for (int i = 0; i < headline.length; i++) {
      this.fieldnameToIndexMap.put(headline[i], i);
    }
    this.data = new ArrayList<>();
  }

  /**
   * Adds a row to this table.
   *
   * @param dataRow an array of {@link DataTableField} objects.
   */
  public void addRow(DataTableField[] dataRow) {

    if (dataRow.length != this.headline.length) {
      throw new IllegalArgumentException("Number of data columns must match columns of headline");
    }
    this.addRow(new DataTableRowImpl(dataRow));
  }

  /**
   * Adds a row to this table.
   *
   * @param dataRow a data row.
   */
  public void addRow(DataTableRowImpl dataRow) {

    if (dataRow.getSize() != this.headline.length) {
      throw new IllegalArgumentException("Number of data columns must match columns of headline");
    }
    this.data.add(dataRow);
  }

  /** {@inheritDoc} */
  @Override
  public DataTableRow getDataRow(int rowNum) {

    return this.data.get(rowNum);
  }

  /** {@inheritDoc} */
  @Override
  public String[] getHeadRow() {

    return this.headline.clone();
  }

  /** {@inheritDoc} */
  @Override
  public Iterator<DataTableRow> iterator() {

    return new Iterator<DataTableRow>() {
      private Iterator<DataTableRow> delegateIterator = DataTableImpl.this.data.iterator();

      @Override
      public boolean hasNext() {

        return this.delegateIterator.hasNext();
      }

      @Override
      public DataTableRow next() {

        return this.delegateIterator.next();
      }

    };
  }

  /** {@inheritDoc} */
  @Override
  public boolean isEmpty() {

    return this.data.isEmpty();
  }

  /** {@inheritDoc} */
  @Override
  public int size() {

    return this.data.size();
  }

}
