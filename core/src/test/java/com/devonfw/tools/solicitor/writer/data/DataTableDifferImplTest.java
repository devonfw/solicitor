package com.devonfw.tools.solicitor.writer.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.writer.data.DataTableField.FieldDiffStatus;
import com.devonfw.tools.solicitor.writer.data.DataTableRow.RowDiffStatus;

/**
 * Tests for {@link DataTableDifferImpl}.
 *
 */
class DataTableDifferImplTest {

  private DataTableDiffer differ;

  private DataTable emptyTableA;

  private DataTable emptyTableB;

  /**
   * <table>
   * <tr>
   * <th>CORR_KEY_0</th>
   * <th>CORR_KEY_1</th>
   * <th>VALUE</th>
   * <th>OBJ_ABC</th>
   * </tr>
   * <tr>
   * <td>key_value_1</td>
   * <td>key_value_1</td>
   * <td>value_a1</td>
   * <td>obj_a1</td>
   * </tr>
   * <tr>
   * <td>key_value_2</td>
   * <td>key_value_1</td>
   * <td>value_a2</td>
   * <td>obj_a2</td>
   * </tr>
   * </table>
   */

  private DataTableImpl tableWith2CorrelationKeysAnd2Rows;

  /**
   * <table>
   * <tr>
   * <th>CORR_KEY_0</th>
   * <th>CORR_KEY_1</th>
   * <th>VALUE</th>
   * <th>OBJ_ABC</th>
   * </tr>
   * <tr>
   * <td>key_value_0</td>
   * <td>key_value_0</td>
   * <td>value_b1</td>
   * <td>obj_b1</td>
   * </tr>
   * <tr>
   * <td>key_value_1</td>
   * <td>key_value_1</td>
   * <td>value_b2</td>
   * <td>obj_b2</td>
   * </tr>
   * <tr>
   * <tr>
   * <td>key_value_3</td>
   * <td>key_value_3</td>
   * <td>value_b3</td>
   * <td>obj_b3</td>
   * </tr>
   * <td>key_value_2</td>
   * <td>key_value_1</td>
   * <td>value_a2</td>
   * <td>obj_b4</td>
   * </tr>
   * <tr>
   * <td>key_value_4</td>
   * <td>key_value_4</td>
   * <td>value_b5</td>
   * <td>obj_b5</td>
   * </tr>
   * </table>
   */
  private DataTableImpl tableWith2CorrelationKeysAnd5Rows;

  /**
   * <table>
   * <tr>
   * <th>VALUE</th>
   * </tr>
   * <tr>
   * <td>value_c1</td>
   * </tr>
   * </table>
   */
  private DataTableImpl tableWith0CorrelationKeysAnd1Row;

  /**
   * <table>
   * <tr>
   * <th>VALUE</th>
   * </tr>
   * <tr>
   * <td>value_d1</td>
   * </tr>
   * <tr>
   * <td>value_c2</td>
   * </tr>
   * </table>
   */
  private DataTableImpl tableWith0CorrelationKeysAnd2Rows;

  @BeforeEach
  public void setUp() {

    // the class under test
    this.differ = new DataTableDifferImpl();

    this.emptyTableA = new DataTableImpl(new String[] {});
    this.emptyTableB = new DataTableImpl(new String[] {});

    this.tableWith2CorrelationKeysAnd2Rows = new DataTableImpl(
        new String[] { "CORR_KEY_0", "CORR_KEY_1", "VALUE", "OBJ_ABC" });
    this.tableWith2CorrelationKeysAnd2Rows.addRow(new DataTableField[] { //
    new DataTableFieldImpl("key_value_1"), //
    new DataTableFieldImpl("key_value_1"), //
    new DataTableFieldImpl("value_a1"), //
    new DataTableFieldImpl("obj_a1") });
    this.tableWith2CorrelationKeysAnd2Rows.addRow(new DataTableField[] { //
    new DataTableFieldImpl("key_value_2"), //
    new DataTableFieldImpl("key_value_1"), //
    new DataTableFieldImpl("value_a2"), //
    new DataTableFieldImpl("obj_a2") });

    this.tableWith2CorrelationKeysAnd5Rows = new DataTableImpl(
        new String[] { "CORR_KEY_0", "CORR_KEY_1", "VALUE", "OBJ_ABC" });
    this.tableWith2CorrelationKeysAnd5Rows.addRow(new DataTableField[] { //
    new DataTableFieldImpl("key_value_0"), //
    new DataTableFieldImpl("key_value_0"), //
    new DataTableFieldImpl("value_b1"), //
    new DataTableFieldImpl("obj_b1") });
    this.tableWith2CorrelationKeysAnd5Rows.addRow(new DataTableField[] { //
    new DataTableFieldImpl("key_value_1"), //
    new DataTableFieldImpl("key_value_1"), //
    new DataTableFieldImpl("value_b2"), //
    new DataTableFieldImpl("obj_b2") });
    this.tableWith2CorrelationKeysAnd5Rows.addRow(new DataTableField[] { //
    new DataTableFieldImpl("key_value_3"), //
    new DataTableFieldImpl("key_value_3"), //
    new DataTableFieldImpl("value_b3"), //
    new DataTableFieldImpl("obj_b3") });
    this.tableWith2CorrelationKeysAnd5Rows.addRow(new DataTableField[] { //
    new DataTableFieldImpl("key_value_2"), //
    new DataTableFieldImpl("key_value_1"), //
    new DataTableFieldImpl("value_a2"), //
    new DataTableFieldImpl("obj_b4") });
    this.tableWith2CorrelationKeysAnd5Rows.addRow(new DataTableField[] { //
    new DataTableFieldImpl("key_value_4"), //
    new DataTableFieldImpl("key_value_4"), //
    new DataTableFieldImpl("value_b5"), //
    new DataTableFieldImpl("obj_b5") });

    this.tableWith0CorrelationKeysAnd1Row = new DataTableImpl(new String[] { "VALUE" });
    this.tableWith0CorrelationKeysAnd1Row.addRow(new DataTableField[] { //
    new DataTableFieldImpl("value_c1") });

    this.tableWith0CorrelationKeysAnd2Rows = new DataTableImpl(new String[] { "VALUE" });
    this.tableWith0CorrelationKeysAnd2Rows.addRow(new DataTableField[] { //
    new DataTableFieldImpl("value_d1") });
    this.tableWith0CorrelationKeysAnd2Rows.addRow(new DataTableField[] { //
    new DataTableFieldImpl("value_d2") });
  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} for both empty tables.
   */
  @Test
  void testDiffBothEmptyIncludeDeletedFalse() {

    DataTable diff = this.differ.diff(this.emptyTableA, this.emptyTableB, false);
    assertTrue(diff.isEmpty());
  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} for both empty tables.
   */
  @Test
  void testDiffBothEmptyIncludeDeletedTrue() {

    DataTable diff = this.differ.diff(this.emptyTableA, this.emptyTableB, true);
    assertTrue(diff.isEmpty());
  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} if old table is null.
   */
  @Test
  void testDiffOldNullIncludeDeletedFalse() {

    DataTable diff = this.differ.diff(this.tableWith2CorrelationKeysAnd2Rows, null, false);
    assertEquals(2, diff.size());
    assertEquals(RowDiffStatus.NOT_APPLICABLE, diff.getDataRow(0).getRowDiffStatus());
    assertEquals(RowDiffStatus.NOT_APPLICABLE, diff.getDataRow(1).getRowDiffStatus());
    assertEquals("value_a1", diff.getDataRow(0).get("VALUE").getValue());
    assertEquals("value_a2", diff.getDataRow(1).get("VALUE").getValue());
  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} if old table is empty.
   */
  @Test
  void testDiffOldEmptyIncludeDeletedFalse() {

    DataTable diff = this.differ.diff(this.tableWith2CorrelationKeysAnd2Rows, this.emptyTableA, false);
    assertEquals(2, diff.size());
    assertEquals(RowDiffStatus.NEW, diff.getDataRow(0).getRowDiffStatus());
    assertEquals(RowDiffStatus.NEW, diff.getDataRow(1).getRowDiffStatus());
    assertEquals("value_a1", diff.getDataRow(0).get("VALUE").getValue());
    assertEquals("value_a2", diff.getDataRow(1).get("VALUE").getValue());
  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} if old table is empty.
   */
  @Test
  void testDiffOldEmptyIncludeDeletedTrue() {

    DataTable diff = this.differ.diff(this.tableWith2CorrelationKeysAnd2Rows, this.emptyTableA, true);
    assertEquals(2, diff.size());
    assertEquals(RowDiffStatus.NEW, diff.getDataRow(0).getRowDiffStatus());
    assertEquals(RowDiffStatus.NEW, diff.getDataRow(1).getRowDiffStatus());
    assertEquals("value_a1", diff.getDataRow(0).get("VALUE").getValue());
    assertEquals("value_a2", diff.getDataRow(1).get("VALUE").getValue());
  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} if new table is empty.
   */
  @Test
  void testDiffNewEmptyIncludeDeletedFalse() {

    DataTable diff = this.differ.diff(this.emptyTableA, this.tableWith2CorrelationKeysAnd2Rows, false);
    assertEquals(0, diff.size());
  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} if new table is empty.
   */
  @Test
  void testDiffNewEmptyIncludeDeletedTrue() {

    DataTable diff = this.differ.diff(this.emptyTableA, this.tableWith2CorrelationKeysAnd2Rows, true);
    assertEquals(2, diff.size());
    assertEquals(RowDiffStatus.DELETED, diff.getDataRow(0).getRowDiffStatus());
    assertEquals(RowDiffStatus.DELETED, diff.getDataRow(1).getRowDiffStatus());
    assertEquals("value_a1", diff.getDataRow(0).get("VALUE").getValue());
    assertEquals("value_a2", diff.getDataRow(1).get("VALUE").getValue());
  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} if new table has more records than old
   * table.
   */
  @Test
  void testDiffOld2New5IncludeDeletedFalse() {

    DataTable diff = this.differ.diff(this.tableWith2CorrelationKeysAnd5Rows, this.tableWith2CorrelationKeysAnd2Rows,
        false);
    assertEquals(5, diff.size());
    assertEquals(RowDiffStatus.NEW, diff.getDataRow(0).getRowDiffStatus());
    assertEquals(RowDiffStatus.CHANGED, diff.getDataRow(1).getRowDiffStatus());
    assertEquals(RowDiffStatus.NEW, diff.getDataRow(2).getRowDiffStatus());
    assertEquals(RowDiffStatus.UNCHANGED, diff.getDataRow(3).getRowDiffStatus());
    assertEquals(RowDiffStatus.NEW, diff.getDataRow(4).getRowDiffStatus());
    assertEquals("value_b1", diff.getDataRow(0).get("VALUE").getValue());
    assertEquals("value_b2", diff.getDataRow(1).get("VALUE").getValue());
    assertEquals("value_b3", diff.getDataRow(2).get("VALUE").getValue());
    assertEquals("value_a2", diff.getDataRow(3).get("VALUE").getValue());
    assertEquals("value_b5", diff.getDataRow(4).get("VALUE").getValue());

    assertEquals(FieldDiffStatus.UNAVAILABLE, diff.getDataRow(0).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.CHANGED, diff.getDataRow(1).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.UNAVAILABLE, diff.getDataRow(2).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.UNCHANGED, diff.getDataRow(3).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.UNAVAILABLE, diff.getDataRow(4).get("VALUE").getDiffStatus());

    assertEquals(null, diff.getDataRow(0).get("VALUE").getOldValue());
    assertEquals("value_a1", diff.getDataRow(1).get("VALUE").getOldValue());
    assertEquals(null, diff.getDataRow(2).get("VALUE").getOldValue());
    assertEquals("value_a2", diff.getDataRow(3).get("VALUE").getOldValue());
    assertEquals(null, diff.getDataRow(4).get("VALUE").getOldValue());

  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} if new table has more records than old
   * table.
   */
  @Test
  void testDiffOld2New5IncludeDeletedTrue() {

    DataTable diff = this.differ.diff(this.tableWith2CorrelationKeysAnd5Rows, this.tableWith2CorrelationKeysAnd2Rows,
        true); // the result should be the same as deleted rows do not exist
    assertEquals(5, diff.size());
    assertEquals(RowDiffStatus.NEW, diff.getDataRow(0).getRowDiffStatus());
    assertEquals(RowDiffStatus.CHANGED, diff.getDataRow(1).getRowDiffStatus());
    assertEquals(RowDiffStatus.NEW, diff.getDataRow(2).getRowDiffStatus());
    assertEquals(RowDiffStatus.UNCHANGED, diff.getDataRow(3).getRowDiffStatus());
    assertEquals(RowDiffStatus.NEW, diff.getDataRow(4).getRowDiffStatus());
    assertEquals("value_b1", diff.getDataRow(0).get("VALUE").getValue());
    assertEquals("value_b2", diff.getDataRow(1).get("VALUE").getValue());
    assertEquals("value_b3", diff.getDataRow(2).get("VALUE").getValue());
    assertEquals("value_a2", diff.getDataRow(3).get("VALUE").getValue());
    assertEquals("value_b5", diff.getDataRow(4).get("VALUE").getValue());

    assertEquals(FieldDiffStatus.UNAVAILABLE, diff.getDataRow(0).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.CHANGED, diff.getDataRow(1).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.UNAVAILABLE, diff.getDataRow(2).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.UNCHANGED, diff.getDataRow(3).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.UNAVAILABLE, diff.getDataRow(4).get("VALUE").getDiffStatus());

    assertEquals(null, diff.getDataRow(0).get("VALUE").getOldValue());
    assertEquals("value_a1", diff.getDataRow(1).get("VALUE").getOldValue());
    assertEquals(null, diff.getDataRow(2).get("VALUE").getOldValue());
    assertEquals("value_a2", diff.getDataRow(3).get("VALUE").getOldValue());
    assertEquals(null, diff.getDataRow(4).get("VALUE").getOldValue());

  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} if new table has less records than old
   * table.
   */
  @Test
  void testDiffOld5New2IncludeDeletedFalse() {

    DataTable diff = this.differ.diff(this.tableWith2CorrelationKeysAnd2Rows, this.tableWith2CorrelationKeysAnd5Rows,
        false);
    assertEquals(2, diff.size());
    assertEquals(RowDiffStatus.CHANGED, diff.getDataRow(0).getRowDiffStatus());
    assertEquals(RowDiffStatus.UNCHANGED, diff.getDataRow(1).getRowDiffStatus());
    assertEquals("value_a1", diff.getDataRow(0).get("VALUE").getValue());
    assertEquals("value_a2", diff.getDataRow(1).get("VALUE").getValue());

    assertEquals(FieldDiffStatus.CHANGED, diff.getDataRow(0).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.UNCHANGED, diff.getDataRow(1).get("VALUE").getDiffStatus());

    assertEquals("value_b2", diff.getDataRow(0).get("VALUE").getOldValue());
    assertEquals("value_a2", diff.getDataRow(1).get("VALUE").getOldValue());

  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} if new table has less records than old
   * table.
   */
  @Test
  void testDiffOld5New2IncludeDeletedTrue() {

    DataTable diff = this.differ.diff(this.tableWith2CorrelationKeysAnd2Rows, this.tableWith2CorrelationKeysAnd5Rows,
        true); // the result should be the same as deleted rows do not exist
    assertEquals(5, diff.size());
    assertEquals(RowDiffStatus.DELETED, diff.getDataRow(0).getRowDiffStatus());
    assertEquals(RowDiffStatus.CHANGED, diff.getDataRow(1).getRowDiffStatus());
    assertEquals(RowDiffStatus.DELETED, diff.getDataRow(2).getRowDiffStatus());
    assertEquals(RowDiffStatus.UNCHANGED, diff.getDataRow(3).getRowDiffStatus());
    assertEquals(RowDiffStatus.DELETED, diff.getDataRow(4).getRowDiffStatus());
    assertEquals("value_b1", diff.getDataRow(0).get("VALUE").getValue());
    assertEquals("value_a1", diff.getDataRow(1).get("VALUE").getValue());
    assertEquals("value_b3", diff.getDataRow(2).get("VALUE").getValue());
    assertEquals("value_a2", diff.getDataRow(3).get("VALUE").getValue());
    assertEquals("value_b5", diff.getDataRow(4).get("VALUE").getValue());

    assertEquals(FieldDiffStatus.UNAVAILABLE, diff.getDataRow(0).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.CHANGED, diff.getDataRow(1).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.UNAVAILABLE, diff.getDataRow(2).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.UNCHANGED, diff.getDataRow(3).get("VALUE").getDiffStatus());
    assertEquals(FieldDiffStatus.UNAVAILABLE, diff.getDataRow(4).get("VALUE").getDiffStatus());

    assertEquals(null, diff.getDataRow(0).get("VALUE").getOldValue());
    assertEquals("value_b2", diff.getDataRow(1).get("VALUE").getOldValue());
    assertEquals(null, diff.getDataRow(2).get("VALUE").getOldValue());
    assertEquals("value_a2", diff.getDataRow(3).get("VALUE").getOldValue());
    assertEquals(null, diff.getDataRow(4).get("VALUE").getOldValue());

  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} if tables have no correlation keys.
   */
  @Test
  void testDiffNoCorrelationKeysIncludeDeletedFalse() {

    DataTable diff = this.differ.diff(this.tableWith0CorrelationKeysAnd1Row, this.tableWith0CorrelationKeysAnd2Rows,
        false); // the result should be the same as deleted rows do not exist
    assertEquals(1, diff.size());
    assertEquals(RowDiffStatus.UNKNOWN, diff.getDataRow(0).getRowDiffStatus());
    assertEquals("value_c1", diff.getDataRow(0).get("VALUE").getValue());

    assertEquals(FieldDiffStatus.UNAVAILABLE, diff.getDataRow(0).get("VALUE").getDiffStatus());

    assertEquals(null, diff.getDataRow(0).get("VALUE").getOldValue());

  }

  /**
   * Test method {@link DataTableDifferImpl#diff(DataTable, DataTable, boolean)} if tables have no correlation keys.
   */
  @Test
  void testDiffNoCorrelationKeysIncludeDeletedTrue() {

    DataTable diff = this.differ.diff(this.tableWith0CorrelationKeysAnd1Row, this.tableWith0CorrelationKeysAnd2Rows,
        true); // the result should be the same as correlation/diff calculation is skippedt
    assertEquals(1, diff.size());
    assertEquals(RowDiffStatus.UNKNOWN, diff.getDataRow(0).getRowDiffStatus());
    assertEquals("value_c1", diff.getDataRow(0).get("VALUE").getValue());

    assertEquals(FieldDiffStatus.UNAVAILABLE, diff.getDataRow(0).get("VALUE").getDiffStatus());

    assertEquals(null, diff.getDataRow(0).get("VALUE").getOldValue());

  }
}
