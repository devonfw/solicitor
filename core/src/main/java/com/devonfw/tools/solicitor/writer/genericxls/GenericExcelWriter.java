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
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.writer.Writer;
import com.devonfw.tools.solicitor.writer.data.DataTable;
import com.devonfw.tools.solicitor.writer.data.DataTableField;
import com.devonfw.tools.solicitor.writer.data.DataTableRow;

/**
 * A {@link Writer} which uses a XLS file as a template to create the report.
 */
@Component
public class GenericExcelWriter implements Writer {

  private static final Logger LOG = LoggerFactory.getLogger(GenericExcelWriter.class);

  @Autowired
  private InputStreamFactory inputStreamFactory;

  /**
   * {@inheritDoc}
   *
   * Accepted type is "xls".
   */
  @Override
  public boolean accept(String type) {

    return "genericxls".equals(type);
  }

  private String trimToMaxCellLength(String original) {

    String trimmed = original;
    if (original.length() > 200) {
      trimmed = "HASH: " + original.hashCode() + "----" + original.substring(0, 200);
    }
    return trimmed;

    // if (original.length() > 32767) {
    // LOG.warn(LogMessages.SHORTENING_XLS_CELL_CONTENT.msg());
    // return original.substring(0, 32767);
    // } else {
    // return original;
    // }

  }

  private void addCommentToCell(Cell cell, String commentText) {

    Row row = cell.getRow();
    Sheet sheet = row.getSheet();
    CreationHelper helper = sheet.getWorkbook().getCreationHelper();
    Drawing drawing = sheet.createDrawingPatriarch();
    ClientAnchor anchor = helper.createClientAnchor();
    anchor.setCol1(cell.getColumnIndex());
    anchor.setCol2(cell.getColumnIndex() + 1);
    anchor.setRow1(row.getRowNum());
    anchor.setRow2(row.getRowNum() + 3);
    Comment comment = drawing.createCellComment(anchor);
    RichTextString str = helper.createRichTextString(commentText);
    comment.setString(str);
    comment.setAuthor("Solicitor");
    cell.setCellComment(comment);
  }

  /**
   * {@inheritDoc}
   *
   * This function will generate a report based on the given excel template.
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
            cell.setCellValue(trimToMaxCellLength(textValue));
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
