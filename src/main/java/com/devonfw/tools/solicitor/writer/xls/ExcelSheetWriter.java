/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.writer.xls;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
// usermodel api for creating, reading and modifying xls files
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet; // xssf is the java implementation of excel 2007 (.xlsx format)
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.writer.Writer;
import com.devonfw.tools.solicitor.writer.data.DataTable;
import com.devonfw.tools.solicitor.writer.data.DataTableField;
import com.devonfw.tools.solicitor.writer.data.DataTableRow;
import com.devonfw.tools.solicitor.writer.data.DataTableField.FieldDiffStatus;
import com.devonfw.tools.solicitor.writer.data.DataTableRow.RowDiffStatus;

/**
 * A {@link Writer} which uses a XLS file as a template to create the report.
 */
@Component
public class ExcelSheetWriter implements Writer {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelSheetWriter.class);

    @Autowired
    private InputStreamFactory inputStreamFactory;

    /**
     * {@inheritDoc}
     *
     * Accepted type is "xls".
     */
    @Override
    public boolean accept(String type) {

        return "xls".equals(type);
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

    private void copyRowsDown(Row row) {

        // copy the current row to the row directly beneath it
        XSSFSheet worksheet = (XSSFSheet) row.getSheet();
        worksheet.copyRows(row.getRowNum(), row.getRowNum(), row.getRowNum() + 1, new CellCopyPolicy());
    }

    private void findCellsToIterate(Map<Cell, String> dataIterators, Workbook wb, Collection<String> tableLabels) {

        // add all cells that contain one of the levels ENGAGEMENT, APPLICATION,
        // APPLICATIONCOMPONENT, LICENSE surrounded by # to dataIterators
        // with the cell as key and the level as value

        for (Sheet sheet : wb) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        String cellText = cell.getStringCellValue();
                        for (String tableLabel : tableLabels) {
                            if (cellText != null && cellText.contains("#" + tableLabel + "#")) {
                                LOG.debug("Found " + cell.getStringCellValue());
                                dataIterators.put(cell, tableLabel);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This function will fill in information in the template sheet.
     * 
     * @param wb the workbook to work on
     * @param cell the cell to start from
     * @param dataTable the table which contains the data
     * @param label the label value representing the name of the data table
     */
    private void iterateFromCell(Workbook wb, Cell cell, DataTable dataTable, String label) {

        // get the information gathered by solicitor
        DataTable dt = dataTable;
        String[] headers = dt.getHeadRow();

        // get the current row in the template
        Row row = cell.getRow();

        // add additional rows if necessary and replace the placeholders
        for (Iterator<DataTableRow> rowIterator = dt.iterator(); rowIterator.hasNext();) {
            DataTableRow rowData = rowIterator.next();
            if (rowIterator.hasNext()) { // add an additional template row if
                                         // necessary
                copyRowsDown(row);
            }
            if (rowData.getRowDiffStatus() == RowDiffStatus.NEW) {
                Cell firstCellInRow = row.getCell(row.getFirstCellNum());
                addCommentToCell(firstCellInRow, "NEWLY INSERTED LINE");
            }
            // replace the placeholders
            for (Cell oneCell : row) {
                if (oneCell.getCellTypeEnum() == CellType.STRING) {
                    String text = oneCell.getStringCellValue();
                    String oldModelText = text;
                    boolean hasChanged = false;
                    // remove # placeholders
                    text = text.replace("#" + label + "#", "");
                    oldModelText = oldModelText.replace("#" + label + "#", "");
                    // replace $ placeholders with the corresponding part of the
                    // gathered information
                    for (int i = 0; i < headers.length; i++) {
                        String toReplace = "$" + headers[i] + "$";
                        if (text.contains(toReplace)) {
                            DataTableField value = rowData.getValueByIndex(i);
                            String textValue = value.toString() == null ? "" : value.toString();
                            if (textValue.length() > 32767) {
                                LOG.warn("Shortening text content for XLS");
                                textValue = textValue.substring(0, 32767);
                            }
                            text = text.replace(toReplace, textValue);
                            if (value.getDiffStatus() == FieldDiffStatus.CHANGED) {
                                hasChanged = true;
                                String oldTextValue = value.getOldValue() == null ? "" : value.getOldValue().toString();
                                if (oldTextValue.length() > 32767) {
                                    LOG.warn("Shortening text content for XLS");
                                    oldTextValue = oldTextValue.substring(0, 32767);
                                }
                                oldModelText = oldModelText.replace(toReplace, oldTextValue);

                            } else {
                                oldModelText = oldModelText.replace(toReplace, textValue);
                            }
                        }
                    }
                    oneCell.setCellValue(text);
                    if (hasChanged) {
                        addCommentToCell(oneCell, "Previous value: '" + oldModelText + "'");

                    }
                }
            }
            if (rowIterator.hasNext()) { // update row for next iteration
                row = row.getSheet().getRow(row.getRowNum() + 1);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * This function will generate a report based on the given excel template.
     */
    @Override
    public void writeReport(String templateSource, String target, Map<String, DataTable> dataTables) {

        Map<Cell, String> dataIterators = new HashMap<>(); // this map will
                                                           // store
                                                           // all cells of the
                                                           // template which
                                                           // have
                                                           // something like
                                                           // #Level.value# as
                                                           // content

        try (InputStream inp = inputStreamFactory.createInputStreamFor(templateSource)) { // read
                                                                                          // the
                                                                                          // template

            Workbook wb = WorkbookFactory.create(inp);

            // ensure xlsx format of the template
            if (!(wb instanceof XSSFWorkbook)) {
                throw new IllegalArgumentException("XLS template must be in XSLX format");
            }

            // find all cells of the template which have something like
            // #Level.value# as content and add them to dataIterators
            findCellsToIterate(dataIterators, wb, dataTables.keySet()); // key:
                                                                        // cell,
                                                                        // value:
            // Level.value

            for (Cell cell : dataIterators.keySet()) {

                String label = dataIterators.get(cell);
                DataTable dataTable = dataTables.get(label);
                iterateFromCell(wb, cell, dataTable, label);
            }

            // Write the output to a file
            try (OutputStream fileOut = new FileOutputStream(target)) {
                wb.write(fileOut);
            }
        } catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
            throw new SolicitorRuntimeException("Processing of XLS report failed", e);
        }

    }

}
