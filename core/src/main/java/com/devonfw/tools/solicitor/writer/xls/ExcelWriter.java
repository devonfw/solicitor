/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.writer.xls;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidations;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.config.WriterConfig;
import com.devonfw.tools.solicitor.writer.Writer;
import com.devonfw.tools.solicitor.writer.data.DataTable;
import com.devonfw.tools.solicitor.writer.data.DataTableField;
import com.devonfw.tools.solicitor.writer.data.DataTableField.FieldDiffStatus;
import com.devonfw.tools.solicitor.writer.data.DataTableRow;
import com.devonfw.tools.solicitor.writer.data.DataTableRow.RowDiffStatus;

/**
 * A {@link Writer} which uses a XLS file as a template to create the report.
 */
@Component
public class ExcelWriter implements Writer {

  private static final Logger LOG = LoggerFactory.getLogger(ExcelWriter.class);

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

  private String trimToMaxCellLength(String original) {

    if (original.length() > 32767) {
      LOG.warn(LogMessages.SHORTENING_XLS_CELL_CONTENT.msg());
      return original.substring(0, 32767);
    } else {
      return original;
    }

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

    int rowNumToCopy = row.getRowNum();
    worksheet.copyRows(rowNumToCopy, rowNumToCopy, rowNumToCopy + 1, new CellCopyPolicy());
    updateConditionalFormattingRegionsIncrementLastRow(worksheet, rowNumToCopy);
    updateValidationRegionsIncrementLastRow(worksheet, rowNumToCopy);

  }

  /**
   * Updates all conditional formatting regions in the given worksheet by incrementing the last row of any region that
   * ends at the given srcLastRowNum. This is necessary to keep conditional formatting working correctly after inserting
   * a new row after srcLastRowNum.
   *
   * @param worksheet
   * @param srcLastRowNum
   */
  private static void updateConditionalFormattingRegionsIncrementLastRow(XSSFSheet worksheet, int srcLastRowNum) {

    SheetConditionalFormatting scf = worksheet.getSheetConditionalFormatting();
    for (int i = 0; i < scf.getNumConditionalFormattings(); i++) {
      ConditionalFormatting cf = scf.getConditionalFormattingAt(i);
      CellRangeAddress[] cras = cf.getFormattingRanges();
      for (CellRangeAddress cra : cras) {
        if (cra.getLastRow() == srcLastRowNum) {
          cra.setLastRow(srcLastRowNum + 1);
        }
      }
      cf.setFormattingRanges(cras);
    }
  }

  /**
   * Updates all data validation regions in the given sheet by incrementing the last row of any region that ends at the
   * given srcLastRowNum. This is necessary to keep data validations working correctly after inserting a new row after
   * srcLastRowNum.
   *
   * @param sheet the sheet to update
   * @param srcLastRowNum the row index which is the last row of any region that should be incremented
   */
  private static void updateValidationRegionsIncrementLastRow(XSSFSheet sheet, int srcLastRowNum) {

    // 1. Extract all existing validations safely
    List<XSSFDataValidation> existing = sheet.getDataValidations();

    record SavedDV(DataValidationConstraint constraint, boolean showError, boolean showPrompt, boolean suppressArrow,
        List<CellRangeAddress> regions) {
    }

    List<SavedDV> saved = new ArrayList<>();

    for (XSSFDataValidation dv : existing) {

      // Extract everything BEFORE touching CT structures
      DataValidationConstraint constraint = dv.getValidationConstraint();
      boolean showError = dv.getShowErrorBox();
      boolean showPrompt = dv.getShowPromptBox();
      boolean suppressArrow = dv.getSuppressDropDownArrow();

      // Copy regions; do NOT keep references
      List<CellRangeAddress> regions = Arrays.stream(dv.getRegions().getCellRangeAddresses())
          .map(r -> new CellRangeAddress(r.getFirstRow(), r.getLastRow(), r.getFirstColumn(), r.getLastColumn()))
          .toList();

      saved.add(new SavedDV(constraint, showError, showPrompt, suppressArrow, regions));
    }

    // 2. Clear all CTDataValidation entries
    CTWorksheet ct = sheet.getCTWorksheet();
    CTDataValidations ctValidations = ct.getDataValidations();

    if (ctValidations != null) {
      ctValidations.getDataValidationList().clear();
      ctValidations.setCount(0);
    }

    XSSFDataValidationHelper helper = new XSSFDataValidationHelper(sheet);

    // 3. Recreate all validations with updated regions
    for (SavedDV data : saved) {

      CellRangeAddressList newList = new CellRangeAddressList();

      for (CellRangeAddress r : data.regions()) {
        int first = r.getFirstRow();
        int last = r.getLastRow();
        int fc = r.getFirstColumn();
        int lc = r.getLastColumn();

        // Conditionally increment last row if it is equal to the target last row (the row where a new row was just
        // inserted)
        if (last == srcLastRowNum) {
          last = last + 1;
        }

        newList.addCellRangeAddress(new CellRangeAddress(first, last, fc, lc));
      }

      XSSFDataValidation newDv = (XSSFDataValidation) helper.createValidation(data.constraint(), newList);

      newDv.setShowErrorBox(data.showError());
      newDv.setShowPromptBox(data.showPrompt());
      newDv.setSuppressDropDownArrow(data.suppressArrow());

      sheet.addValidationData(newDv);
    }
  }

  private void possiblyMoveBelowRowsDownToCreateSpaceForNewRows(Row row, int numberOfRows) {

    XSSFSheet worksheet = (XSSFSheet) row.getSheet();

    // only do anything if the numberOfRows is greater than 1 and there actually exist rows below the current row
    if ((numberOfRows > 1) && (worksheet.getLastRowNum() > row.getRowNum())) {
      LOG.debug(
          "Inserting space for " + (numberOfRows - 1) + " additional rows by shifting rows '" + (row.getRowNum() + 1)
              + "' to '" + worksheet.getLastRowNum() + "' in worksheet'" + worksheet.getSheetName() + "'");
      worksheet.shiftRows(row.getRowNum() + 1, worksheet.getLastRowNum(), numberOfRows - 1, true, false);
    }
  }

  private void findCellsToIterate(Map<Cell, String> dataIterators, Workbook wb, Collection<String> tableLabels) {

    // add all string and formula cells that contain one of the levels ENGAGEMENT, APPLICATION,
    // APPLICATIONCOMPONENT, LICENSE surrounded by # to dataIterators
    // with the cell as key and the level as value

    for (Sheet sheet : wb) {
      for (Row row : sheet) {
        for (Cell cell : row) {
          String cellText;
          if (cell.getCellType() == CellType.STRING) {
            cellText = cell.getStringCellValue();
          } else if (cell.getCellType() == CellType.FORMULA) {
            cellText = cell.getCellFormula();
          } else {
            continue;
          }
          for (String tableLabel : tableLabels) {
            if (cellText != null && cellText.contains("#" + tableLabel + "#")) {
              LOG.debug("Found " + cellText);
              dataIterators.put(cell, tableLabel);
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
   * @param styleCache cache of styles to avoid creating unecessary duplicates of styles (strikethrough)
   */
  private void iterateFromCell(Workbook wb, Cell cell, DataTable dataTable, String label, StyleCache styleCache) {

    // get the information gathered by solicitor
    DataTable dt = dataTable;
    String[] headers = dt.getHeadRow();

    // get the current row in the template
    Row row = cell.getRow();

    // if the data table has more than 1 row, then we might need to create place in in the sheet to insert them
    int numberOfRows = 0;
    Iterator<DataTableRow> it = dt.iterator();
    while (it.hasNext()) {
      it.next();
      numberOfRows++;
    }
    possiblyMoveBelowRowsDownToCreateSpaceForNewRows(row, numberOfRows);

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
      if (rowData.getRowDiffStatus() == RowDiffStatus.UNKNOWN) {
        Cell firstCellInRow = row.getCell(row.getFirstCellNum());
        addCommentToCell(firstCellInRow, "DELTA INFORMATION FOR ROW NOT AVAILABLE");
      }
      // replace the placeholders
      for (Cell oneCell : row) {
        String text;
        CellType cellType = oneCell.getCellType();
        if (cellType == CellType.STRING) {
          text = oneCell.getStringCellValue();
        } else if (cellType == CellType.FORMULA) {
          text = oneCell.getCellFormula();
        } else {
          continue; // only process string and formula cells
        }
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
            text = text.replace(toReplace, textValue);
            if (value.getDiffStatus() == FieldDiffStatus.CHANGED) {
              hasChanged = true;
              String oldTextValue = value.getOldValue() == null ? "" : value.getOldValue().toString();
              oldModelText = oldModelText.replace(toReplace, oldTextValue);
            } else {
              oldModelText = oldModelText.replace(toReplace, textValue);
            }
          }
        }
        if (cellType == CellType.STRING) {
          if (text.isBlank()) {
            oneCell.setBlank();
          } else {
            oneCell.setCellValue(trimToMaxCellLength(text.replace("\r", "")));
          }
          if (hasChanged) {
            addCommentToCell(oneCell, trimToMaxCellLength("Previous value: '" + oldModelText.replace("\r", "") + "'"));
          }
        } else if (cellType == CellType.FORMULA) {
          // for formula cells we do not handle the case that after replacing the placeholders the formula exceeds the
          // maximum size
          oneCell.setCellFormula(text.replace("\r", ""));
          if (hasChanged) {
            addCommentToCell(oneCell, "Previous value (formula): '" + oldModelText.replace("\r", "") + "'");
          }
        }
      }
      if (rowData.getRowDiffStatus() == RowDiffStatus.DELETED) {
        Cell firstCellInRow = row.getCell(row.getFirstCellNum());
        addCommentToCell(firstCellInRow, "DELETED LINE");

        // apply strikethrough style to all cells in the row
        for (Cell cellInRow : row) {
          CellStyle baseStyle = cellInRow.getCellStyle();
          CellStyle strikeThroughStyle = styleCache.getStrikeThroughStyle(baseStyle);
          cellInRow.setCellStyle(strikeThroughStyle);
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
  public void writeReport(WriterConfig config, String target, Map<String, DataTable> dataTables) {

    Map<Cell, String> dataIterators = new HashMap<>(); // this map will
                                                       // store
                                                       // all cells of the
                                                       // template which
                                                       // have
                                                       // something like
                                                       // #Level.value# as
                                                       // content

    try (InputStream inp = this.inputStreamFactory.createInputStreamFor(config.getTemplateSource())) { // read
      // the
      // template

      Workbook wb = WorkbookFactory.create(inp);

      // ensure xlsx format of the template
      if (!(wb instanceof XSSFWorkbook)) {
        throw new IllegalArgumentException("XLS template must be in XSLX format");
      }

      StyleCache styleCache = new StyleCache((XSSFWorkbook) wb);

      // find all cells of the template which have something like
      // #Level.value# as content and add them to dataIterators
      findCellsToIterate(dataIterators, wb, dataTables.keySet()); // key:
                                                                  // cell,
                                                                  // value:
      // Level.value

      for (Cell cell : dataIterators.keySet()) {

        String label = dataIterators.get(cell);
        DataTable dataTable = dataTables.get(label);
        iterateFromCell(wb, cell, dataTable, label, styleCache);
      }

      // force reevaluation of all formulas
      wb.setForceFormulaRecalculation(true);

      // protect all workbook sheets if password is set
      protectWorkbookSheets(config, target, wb);

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
   * Protects/locks all sheets in the workbook if the value given by {@link WriterConfig#getProtectionPassword()} is not
   * <code>null</code> or empty. If the configured value is literal "RANDOM", a random password will be generated and
   * logged. If the configured value is "NONE" the sheets will be protected without defining a password. If the
   * configured password is empty or null, no password will be set and the sheets will not be protected. For all other
   * values the configured value will be used as password.
   *
   * @param config the writer configuration
   * @param target filename of the report (used for logging only)
   * @param wb the workbook to protect
   */
  private void protectWorkbookSheets(WriterConfig config, String target, Workbook wb) {

    // protect sheets if password is set
    String protectionPasswordConfigValue = config.getProtectionPassword();
    String effectivePassword = null;
    if (protectionPasswordConfigValue != null && !protectionPasswordConfigValue.isEmpty()) {
      if (WriterConfig.RANDOM_PASSWORD_MARKER.equals(protectionPasswordConfigValue)) {
        effectivePassword = Long.toHexString(new Random().nextLong());
        LOG.info(LogMessages.XLS_PROTECTION_ACTIVE_RANDOM_PASSWORD.msg(), effectivePassword, target);
      } else if (WriterConfig.NO_PASSWORD_MARKER.equals(protectionPasswordConfigValue)) {
        effectivePassword = "";
        LOG.info(LogMessages.XLS_PROTECTION_ACTIVE_NO_PASSWORD.msg(), target);
      } else {
        effectivePassword = protectionPasswordConfigValue;
        LOG.info(LogMessages.XLS_PROTECTION_ACTIVE_PASSWORD.msg(), target);
      }
    }

    if (effectivePassword != null) {
      for (Sheet sheet : wb) {
        ((XSSFSheet) sheet).lockAutoFilter(false);
        ((XSSFSheet) sheet).lockDeleteColumns(true);
        ((XSSFSheet) sheet).lockDeleteRows(true);
        ((XSSFSheet) sheet).lockFormatCells(true);
        ((XSSFSheet) sheet).lockFormatColumns(false);
        ((XSSFSheet) sheet).lockFormatRows(false);
        ((XSSFSheet) sheet).lockInsertColumns(true);
        ((XSSFSheet) sheet).lockInsertHyperlinks(true);
        ((XSSFSheet) sheet).lockInsertRows(true);
        ((XSSFSheet) sheet).lockObjects(true);
        ((XSSFSheet) sheet).lockPivotTables(true);
        ((XSSFSheet) sheet).lockScenarios(true);
        ((XSSFSheet) sheet).lockSelectLockedCells(false);
        ((XSSFSheet) sheet).lockSelectUnlockedCells(false);
        ((XSSFSheet) sheet).lockSort(true);
        ((XSSFSheet) sheet).protectSheet(effectivePassword);
      }
    }
  }

}
