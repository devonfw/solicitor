/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.writer.xls;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet; // xssf is the java implementation of excel 2007 (.xlsx format)
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capgemini.solicitor.SolicitorRuntimeException;
import com.capgemini.solicitor.common.DataTable;
import com.capgemini.solicitor.common.DataTableRow;
import com.capgemini.solicitor.common.InputStreamFactory;
import com.capgemini.solicitor.writer.Writer;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExcelSheetWriter implements Writer {

    @Autowired
    private InputStreamFactory inputStreamFactory;

    /*
     * (non-Javadoc)
     * 
     * @see com.capgemini.solicitor.writer.xls.Writer#accept(java.lang.String)
     */
    @Override
    public boolean accept(String type) {

        return "xls".equals(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.capgemini.solicitor.writer.xls.Writer#writeReport(java.lang.String,
     * java.lang.String)
     */

    /**
     * This function will generate a report based on the given excel template.
     * 
     * @param templateSource full path to the template (given in the config
     *        file)
     * @param target file the report will be written to (given in the config
     *        file)
     * @param dataTables
     */
    @Override
    public void writeReport(String templateSource, String target,
            Map<String, DataTable> dataTables) {

        Map<Cell, String> dataIterators = new HashMap<>(); // this map will
                                                           // store
                                                           // all cells of the
                                                           // template which
                                                           // have
                                                           // something like
                                                           // #Level.value# as
                                                           // content

        try (InputStream inp =
                inputStreamFactory.createInputStreamFor(templateSource)) { // read
                                                                           // the
                                                                           // template

            Workbook wb = WorkbookFactory.create(inp);

            // ensure xlsx format of the template
            if (!(wb instanceof XSSFWorkbook)) {
                throw new IllegalArgumentException(
                        "XLS template must be in XSLX format");
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
        } catch (IOException | EncryptedDocumentException
                | InvalidFormatException e) {
            throw new SolicitorRuntimeException(
                    "Processing of XLS report failed", e);
        }

    }

    /**
     * This function will fill in information in the template sheet.
     * 
     * @param wb
     * @param cell
     * @param level
     */
    private void iterateFromCell(Workbook wb, Cell cell, DataTable dataTable,
            String label) {

        // get the information gathered by solicitor
        DataTable dt = dataTable;
        String[] headers = dt.getHeadRow();

        // get the current row in the template
        Row row = cell.getRow();

        // add additional rows if necessary and replace the placeholders
        for (Iterator<DataTableRow> rowIterator = dt.iterator(); rowIterator
                .hasNext();) {
            DataTableRow rowData = rowIterator.next();
            if (rowIterator.hasNext()) { // add an additional template row if
                                         // necessary
                copyRowsDown(row);
            }
            // replace the placeholders
            for (Cell oneCell : row) {
                if (oneCell.getCellTypeEnum() == CellType.STRING) {
                    String text = oneCell.getStringCellValue();
                    // remove # placeholders
                    text = text.replace("#" + label + "#", "");
                    // replace $ placeholders with the corresponding part of the
                    // gathered information
                    for (int i = 0; i < headers.length; i++) {
                        String toReplace = "$" + headers[i] + "$";
                        Object value = rowData.getValueByIndex(i);
                        String textValue =
                                value == null ? "" : value.toString();
                        if (textValue.length() > 32767) {
                            LOG.warn("Shortening text content for XLS");
                            textValue = textValue.substring(0, 32767);
                        }
                        text = text.replace(toReplace, textValue);
                    }
                    oneCell.setCellValue(text);
                }
            }
            if (rowIterator.hasNext()) { // update row for next iteration
                row = row.getSheet().getRow(row.getRowNum() + 1);
            }
        }
    }

    private void copyRowsDown(Row row) {

        // copy the current row to the row directly beneath it
        XSSFSheet worksheet = (XSSFSheet) row.getSheet();
        worksheet.copyRows(row.getRowNum(), row.getRowNum(),
                row.getRowNum() + 1, new CellCopyPolicy());
    }

    private void findCellsToIterate(Map<Cell, String> dataIterators,
            Workbook wb, Collection<String> tableLabels) {

        // add all cells that contain one of the levels ENGAGEMENT, APPLICATION,
        // APPLICATIONCOMPONENT, LICENSE surrounded by # to dataIterators
        // with the cell as key and the level as value

        for (Sheet sheet : wb) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        String cellText = cell.getStringCellValue();
                        for (String tableLabel : tableLabels) {
                            if (cellText != null && cellText
                                    .contains("#" + tableLabel + "#")) {
                                LOG.debug("Found " + cell.getStringCellValue());
                                dataIterators.put(cell, tableLabel);
                            }
                        }
                    }
                }
            }
        }
    }

}
