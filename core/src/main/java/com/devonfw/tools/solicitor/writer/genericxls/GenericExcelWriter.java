/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.writer.genericxls;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.EncryptedDocumentException;
// usermodel api for creating, reading and modifying xls files
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.writer.Writer;
import com.devonfw.tools.solicitor.writer.data.DataTable;
import com.devonfw.tools.solicitor.writer.data.DataTableField;
import com.devonfw.tools.solicitor.writer.data.DataTableRow;

/**
 * A {@link Writer} which creates a generic XLS workbook with a separate sheet for each defined dataTable. Field data
 * will be trimmed when exceeding more than 200 characters. This report is intended for debugging purposes.
 */
@Component
public class GenericExcelWriter implements Writer {

  /**
   * {@inheritDoc}
   *
   * Accepted type is "xls".
   */
  @Override
  public boolean accept(String type) {

    return "genericxls".equals(type);
  }

  private String trimToReasonableLength(String original) {

    String trimmed = original;
    if (original.length() > 200) {
      trimmed = "HASH: " + original.hashCode() + "----" + original.substring(0, 200);
    }
    return trimmed;

  }

  /**
   * {@inheritDoc}
   *
   * This function will generate a generic report.
   */
  @Override
  public void writeReport(String templateSource, String target, Map<String, DataTable> dataTables) {

    try {

      Workbook wb = WorkbookFactory.create(true);

      for (Entry<String, DataTable> tableEntry : dataTables.entrySet()) {
        Sheet sh = wb.createSheet(limitSheetNameLength(tableEntry.getKey()));

        DataTable dataTable = tableEntry.getValue();
        int rowIndex = 0;
        Row row = sh.createRow(rowIndex++);
        int columnIndex = 0;
        for (String columnName : dataTable.getHeadRow()) {
          Cell cell = row.createCell(columnIndex++);
          cell.setCellValue(columnName);
        }
        for (DataTableRow dataTableRow : dataTable) {
          row = sh.createRow(rowIndex++);
          for (int i = 0; i < dataTableRow.getSize(); i++) {
            Cell cell = row.createCell(i);
            DataTableField value = dataTableRow.getValueByIndex(i);
            String textValue = value.toString() == null ? "" : value.toString();
            cell.setCellValue(trimToReasonableLength(textValue));
          }
        }
      }

      // Write the output to a file
      IOHelper.checkAndCreateLocation(target);
      try (OutputStream fileOut = new FileOutputStream(target)) {
        wb.write(fileOut);
      }
    } catch (IOException | EncryptedDocumentException e) {
      throw new SolicitorRuntimeException("Processing of XLS report failed", e);
    }

  }

  /**
   * @param name
   * @return
   */
  private String limitSheetNameLength(String name) {

    if (name.length() > 31) {
      return (name.substring(0, 14) + "..." + name.substring(name.length() - 14));
    } else {
      return name;
    }
  }

}
