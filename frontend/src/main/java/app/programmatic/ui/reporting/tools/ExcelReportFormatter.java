package app.programmatic.ui.reporting.tools;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import app.programmatic.ui.reporting.model.ReportColumnType;
import app.programmatic.ui.reporting.model.ReportColumnLocation;
import app.programmatic.ui.reporting.model.ReportRows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExcelReportFormatter implements ReportFormatter {
    private final static ZoneId ZONE_ID = ZoneId.systemDefault();

    @Override
    public ByteArrayOutputStream formatRows(ReportRows reportRows, ReportColumnFormatter formatter) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            CellTypeSetter cellTypeSetter = new CellTypeSetter(workbook);
            XSSFSheet sheet = workbook.createSheet();

            int startIndex = 0;

            XSSFRow row = sheet.createRow(startIndex);
            fillHeaderRow(reportRows.getHeader(), row, cellTypeSetter);

            for (ReportRows.ColumnValue[] values : reportRows.getRows()) {
                row = sheet.createRow(++startIndex);
                fillRow(values, row, cellTypeSetter, formatter);
            }

            if (reportRows.getTotalRow() != null) {
                row = sheet.createRow(++startIndex);
                fillRow(reportRows.getTotalRow(), row, cellTypeSetter, formatter);
            }

            workbook.write(os);
            return os;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillHeaderRow(Object[] src, XSSFRow row, CellTypeSetter cellTypeSetter) {
        for (int j = 0; j < src.length; j++) {
            XSSFCell cell = row.createCell(j);
            cellTypeSetter.setHeaderType(cell);
            cell.setCellValue((String) src[j]);
        }
    }

    private void fillRow(ReportRows.ColumnValue[] values, XSSFRow row, CellTypeSetter cellTypeSetter, ReportColumnFormatter formatter) {
        int i = 0;
        for (ReportRows.ColumnValue value : values) {
            XSSFCell cell = row.createCell(i++);
            if (value != null && value.getValue() != null) {
                String currencySign = value.getCurrencySign() != null ? value.getCurrencySign() : formatter.getCurrencySign();
                fillCell(value.getValue(), cell, cellTypeSetter, value.getColumn().getColumnType(), formatter, currencySign);
            }
        }
    }

    private void fillCell(Object value, XSSFCell cell, CellTypeSetter cellTypeSetter, ReportColumnType columnType,
                          ReportColumnFormatter formatter, String currencySign) {
        switch (columnType) {
            case DATE_COLUMN:
                cellTypeSetter.setDateType(cell, formatter.getDateFormat());
                cell.setCellValue(Date.from(((LocalDate) value).atStartOfDay(ZONE_ID).toInstant()));
                return;
            case TEXT_COLUMN:
                cell.setCellType(CellType.STRING);
                cell.setCellValue(formatter.format(value, columnType));
                return;
            case INT_COLUMN:
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue((Long) value);
                return;
            case FLOAT_COLUMN:
                cellTypeSetter.setFloatType(cell, formatter.getScale());
                cell.setCellValue(scaled((BigDecimal) value, formatter.getScale()).doubleValue());
                return;
            case PERCENT_COLUMN:
                cellTypeSetter.setPercentageType(cell, formatter.getPercentageScale(), formatter.getPercentageSign());
                cell.setCellValue(scaled((BigDecimal) value, formatter.getPercentageScale()).movePointLeft(2).doubleValue());
                return;
            case CURRENCY_COLUMN:
                cellTypeSetter.setCurrencyType(cell, formatter.getScale(), currencySign);
                cell.setCellValue(scaled((BigDecimal) value, formatter.getScale()).doubleValue());
                return;
            default:
                throw new IllegalArgumentException("Unexpected ReportColumnType: " + columnType);
        }
    }

    private BigDecimal scaled(BigDecimal src, int scale) {
        return src.scale() <= scale ? src : src.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    private class CellTypeSetter {
        private static final String SCALE_SIGN = "#";

        private final XSSFWorkbook workbook;

        private XSSFCellStyle headerCellStyle;
        private XSSFCellStyle dateCellStyle;
        private XSSFCellStyle floatCellStyle;
        private XSSFCellStyle percentageCellStyle;
        private XSSFCellStyle currencyCellStyle;

        public CellTypeSetter(XSSFWorkbook workbook) {
            this.workbook = workbook;
        }

        public void setHeaderType(XSSFCell cell) {
            cell.setCellType(CellType.STRING);
            cell.setCellStyle(getHeaderCellStyle());
        }

        public void setDateType(XSSFCell cell, String dateFormat) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellStyle(getDateCellStyle(dateFormat));
        }

        public void setFloatType(XSSFCell cell, int scale) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellStyle(getFloatCellStyle(scale));
        }

        public void setPercentageType(XSSFCell cell, int scale, String percentageSign) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellStyle(getPercentageCellStyle(scale, percentageSign));
        }

        public void setCurrencyType(XSSFCell cell, int scale, String currencySign) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellStyle(getCurrencyCellStyle(scale, currencySign));
        }

        private XSSFCellStyle getHeaderCellStyle() {
            if (headerCellStyle == null) {
                XSSFFont font = workbook.createFont();
                font.setFontHeightInPoints((short) 11);
                font.setFontName("Calibri");
                font.setColor(IndexedColors.BLACK.getIndex());
                font.setBold(true);
                font.setItalic(false);

                XSSFCellStyle style = workbook.createCellStyle();
                style.setFont(font);

                headerCellStyle = style;
            }

            return headerCellStyle;
        }

        private XSSFCellStyle getDateCellStyle(String dateFormat) {
            if (dateCellStyle == null) {
                CreationHelper createHelper = workbook.getCreationHelper();
                XSSFCellStyle style = workbook.createCellStyle();
                style.setDataFormat(createHelper.createDataFormat().getFormat(dateFormat));

                dateCellStyle = style;
            }

            return dateCellStyle;
        }

        private XSSFCellStyle getFloatCellStyle(int scale) {
            if (floatCellStyle == null) {
                floatCellStyle = createFloatCellStyle(scale, "", "");
            }

            return floatCellStyle;
        }

        private XSSFCellStyle getPercentageCellStyle(int scale, String percentageSign) {
            if (percentageCellStyle == null) {
                percentageCellStyle = createFloatCellStyle(scale, "", percentageSign);
            }

            return percentageCellStyle;
        }

        private XSSFCellStyle getCurrencyCellStyle(int scale, String currencySign) {
            if (currencyCellStyle == null) {
                currencyCellStyle = createFloatCellStyle(scale, "[$" + currencySign + "]", "");
            }

            return currencyCellStyle;
        }

        private XSSFCellStyle createFloatCellStyle(int scale, String prefix, String suffix) {
            XSSFCellStyle style = workbook.createCellStyle();
            XSSFDataFormat format = workbook.createDataFormat();

            String formatPattern = IntStream.range(0, scale).boxed()
                    .map(i -> SCALE_SIGN)
                    .collect(Collectors.joining("", prefix + "0.", suffix));

            style.setDataFormat(format.getFormat(formatPattern));

            return style;
        }
    }
}

