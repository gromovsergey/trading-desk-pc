package com.foros.reporting.serializer.xlsx;

import java.awt.Color;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class XSSFBackgroundStylist implements Stylist {
    private Color color;

    public XSSFBackgroundStylist(Color color) {
        this.color = color;
    }

    @Override
    public void init(Workbook workbook, CellStyle cellStyle) {
        XSSFCellStyle cs = (XSSFCellStyle) cellStyle;
        cs.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        cs.setFillForegroundColor(new XSSFColor(color));
    }
}
