package com.foros.reporting.serializer.xlsx;

import java.util.Arrays;
import java.util.Collection;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public class CompositeStylist implements Stylist {
    private Collection<Stylist> stylists;

    public CompositeStylist(Collection<Stylist> stylists) {
        this.stylists = stylists;
    }

    @Override
    public void init(Workbook workbook, CellStyle cellStyle) {
        for (Stylist stylist : stylists) {
            stylist.init(workbook, cellStyle);
        }
    }

    public static Stylist composite(Stylist... stylists) {
        return new CompositeStylist(Arrays.asList(stylists));
    }
}
