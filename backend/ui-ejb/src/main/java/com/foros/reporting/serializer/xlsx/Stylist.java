package com.foros.reporting.serializer.xlsx;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public interface Stylist {

    short NO_STYLE_ID = -1;

    void init(Workbook workbook, CellStyle cellStyle);
}
