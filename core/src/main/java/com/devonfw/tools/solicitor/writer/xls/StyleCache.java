package com.devonfw.tools.solicitor.writer.xls;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class StyleCache {

  private final XSSFWorkbook workbook;

  private final Map<Short, CellStyle> strikeThroughCache = new HashMap<>();

  public StyleCache(XSSFWorkbook workbook) {

    this.workbook = workbook;
  }

  public CellStyle getStrikeThroughStyle(CellStyle baseStyle) {

    short key = baseStyle.getIndex();

    // Cache hit → return cached
    if (this.strikeThroughCache.containsKey(key)) {
      return this.strikeThroughCache.get(key);
    }

    // Cache miss → clone style + font
    CellStyle newStyle = this.workbook.createCellStyle();
    newStyle.cloneStyleFrom(baseStyle);

    XSSFFont oldFont = this.workbook.getFontAt(baseStyle.getFontIndex());
    XSSFFont newFont = this.workbook.createFont();

    // Copy font properties
    newFont.setBold(oldFont.getBold());
    newFont.setFontHeight(oldFont.getFontHeight());
    newFont.setFontName(oldFont.getFontName());
    newFont.setItalic(oldFont.getItalic());
    newFont.setColor(oldFont.getXSSFColor());
    newFont.setUnderline(oldFont.getUnderline());

    // Apply your change
    newFont.setStrikeout(true);

    newStyle.setFont(newFont);

    // Put in cache
    this.strikeThroughCache.put(key, newStyle);

    return newStyle;
  }
}
