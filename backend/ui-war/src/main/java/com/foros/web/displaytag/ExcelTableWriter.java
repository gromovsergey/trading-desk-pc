package com.foros.web.displaytag;

import com.foros.util.formatter.CurrencyFormatter;
import com.foros.util.formatter.DateFormatter;
import com.foros.util.formatter.FormatterAdapter;
import com.foros.util.formatter.NumberFormatter;
import com.foros.util.formatter.PercentFormatter;
import com.foros.reporting.serializer.xlsx.ExcelHelper;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;
import com.foros.util.formatter.FieldFormatter;
import com.foros.util.StringUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.displaytag.decorator.TableDecorator;
import org.displaytag.decorator.hssf.DecoratesHssf;
import org.displaytag.model.Column;
import org.displaytag.model.HeaderCell;
import org.displaytag.model.Row;
import org.displaytag.model.TableModel;
import org.displaytag.properties.MediaTypeEnum;
import org.displaytag.render.TableWriterAdapter;

/**
 * A table writer that formats a table in Excel's spreadsheet format, and writes
 * it to an HSSF workbook.
 * 
 * @author Andrey Chernyshov
 * @see org.displaytag.render.TableWriterTemplate
 */
public class ExcelTableWriter extends TableWriterAdapter {
    private static short defaultColumnWidth = 9;
    /**
     * The workbook to which the table is written.
     */
    private HSSFWorkbook wb;
    /**
     * Generated sheet.
     */
    private HSSFSheet sheet;
    /**
     * The first row of table
     */
    private int startRow;
    /**
     * Current row number.
     */
    private int rowNum;
    /**
     * Current row.
     */
    private HSSFRow currentRow;
    /**
     * Current column number.
     */
    private int colNum;
    /**
     * Current cell.
     */
    private HSSFCell currentCell;
    /**
     * Default cell style
     */
    private HSSFCellStyle defaultCellStyle;

    private Map<Short, HSSFCellStyle> cellStyles = new HashMap<Short, HSSFCellStyle>();

    private Map<String, Short> currencyFormats = new HashMap<String, Short>();

    /**
     * This table writer uses an HSSF workbook to write the table.
     * 
     * @param wb
     *                The HSSF workbook to write the table.
     */
    public ExcelTableWriter(HSSFWorkbook wb) {
        this.wb = wb;
    }

    public ExcelTableWriter(HSSFWorkbook wb, HSSFSheet sheet) {
        this(wb);
        this.sheet = sheet;
        this.sheet.setDefaultColumnWidth(defaultColumnWidth);
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeTableOpener(org.displaytag.model.TableModel)
     */
    @Override
    protected void writeTableOpener(TableModel model) throws Exception {
        if (sheet == null) {
            String fileName = model.getProperties().getExportFileName(MediaTypeEnum.EXCEL);
            if (StringUtil.isPropertyNotEmpty(fileName)) {
                this.sheet = wb.createSheet(fileName.substring(0, fileName.length() - 4));
            } else {
                this.sheet = wb.createSheet("-");
            }
            this.sheet.setDefaultColumnWidth(defaultColumnWidth);             
        }
        this.rowNum = startRow;

        defaultCellStyle = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        defaultCellStyle.setFont(font);
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeCaption(org.displaytag.model.TableModel)
     */
    @Override
    protected void writeCaption(TableModel model) throws Exception {
        HSSFCellStyle style = this.wb.createCellStyle();
        HSSFFont bold = this.wb.createFont();
        bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        bold.setFontHeightInPoints((short) 14);
        style.setFont(bold);

        // create first row
        this.colNum = 0;
        this.currentRow = this.sheet.createRow(this.rowNum++);
        this.currentCell = this.currentRow.createCell((short) this.colNum);
        this.currentCell.setCellStyle(style);
        String[] captionData = model.getCaption().split("\n");
        this.currentCell.setCellValue(captionData[0]);

        // create other caption rows
        for (int i = 1; i < captionData.length; i++) {
            this.currentRow = this.sheet.createRow(this.rowNum++);
            this.currentCell = this.currentRow.createCell((short) this.colNum);
            this.currentCell.setCellStyle(defaultCellStyle);
            this.currentCell.setCellValue(captionData[i]);
        }

        // skip a row after caption
        this.currentRow = this.sheet.createRow(this.rowNum++);
    }

    /**
     * Obtain the region over which to merge a cell.
     * 
     * @param first
     *                Column number of first cell from which to merge.
     * @param last
     *                Column number of last cell over which to merge.
     * @return The region over which to merge a cell.
     */
    private Region getMergeCellsRegion(short first, short last) {
        return new Region(this.currentRow.getRowNum(), first, this.currentRow.getRowNum(), last);
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeTableHeader(org.displaytag.model.TableModel)
     */
    @Override
    protected void writeTableHeader(TableModel model) throws Exception {
        this.currentRow = this.sheet.createRow(this.rowNum++);
        this.colNum = 0;
        HSSFCellStyle headerStyle = this.getHeaderFooterStyle();
        for (Iterator iterator = model.getHeaderCellList().iterator(); iterator.hasNext();) {
            HeaderCell headerCell = (HeaderCell) iterator.next();
            String columnHeader = headerCell.getTitle();
            if (columnHeader == null) {
                columnHeader = StringUtils.capitalize(headerCell.getBeanPropertyName());
            }

            this.writeHeaderFooter(columnHeader, this.currentRow, headerStyle);
        }
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeDecoratedRowStart(org.displaytag.model.TableModel)
     */
    @Override
    protected void writeDecoratedRowStart(TableModel model) {
        model.getTableDecorator().startRow();
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeRowOpener(org.displaytag.model.Row)
     */
    @Override
    protected void writeRowOpener(Row row) throws Exception {
        this.currentRow = this.sheet.createRow(rowNum++);
        this.colNum = 0;
    }

    /**
     * Write a column's opening structure to a HSSF document.
     * 
     * @see org.displaytag.render.TableWriterTemplate#writeColumnOpener(org.displaytag.model.Column)
     */
    @Override
    protected void writeColumnOpener(Column column) throws Exception {
        column.getOpenTag(); // has side effect, setting its stringValue,
        // which affects grouping logic.
        this.currentCell = this.currentRow.createCell((short) this.colNum++);
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeColumnValue(Object,org.displaytag.model.Column)
     */
    @Override
    protected void writeColumnValue(Object value, Column column) throws Exception {
        FieldFormatter formatter = null;
        Object rawValue = column.getValue(false);
        if (column.getHeaderCell().getColumnDecorators().length != 0 && rawValue != null) {
            FormatterAdapter columnDecorator = (FormatterAdapter) column.getHeaderCell().getColumnDecorators()[0];
            formatter = columnDecorator.getFormatter();
        }     

        if (formatter instanceof NumberFormatter) {
            if (rawValue instanceof Number) {
                this.currentCell.setCellValue(((Number) rawValue).doubleValue());
            } else {
                this.currentCell.setCellValue(new HSSFRichTextString(rawValue.toString()));
            }
        } else if (formatter instanceof DateFormatter) {
            this.currentCell.setCellValue((Date) rawValue);
            HSSFCellStyle dateStyle = cellStyles.get((short) 0xE);
            if (dateStyle == null) {
                dateStyle = wb.createCellStyle();
                dateStyle.setDataFormat((short) 0xE);
                cellStyles.put((short) 0xE, dateStyle);
            }
            this.currentCell.setCellStyle(dateStyle);
        } else if (formatter instanceof PercentFormatter) {
            if (rawValue instanceof Number) {
                this.currentCell.setCellValue(((Number) rawValue).doubleValue() / 100);
                HSSFCellStyle percentStyle = cellStyles.get((short) 0xA);
                if (percentStyle == null) {
                    percentStyle = wb.createCellStyle();
                    percentStyle.setDataFormat((short) 0xA);
                    cellStyles.put((short) 0xA, percentStyle);
                }
                this.currentCell.setCellStyle(percentStyle);
            } else {
                this.currentCell.setCellValue(new HSSFRichTextString(rawValue.toString()));
            }
        } else if (formatter instanceof CurrencyFormatter) {
            if (rawValue instanceof Number) {
                this.currentCell.setCellValue(((Number) rawValue).doubleValue());
                short currencyFormat = getCurrencyFormat(((CurrencyFormatter) formatter).getCurrencyCode());
                HSSFCellStyle currencyStyle = cellStyles.get(currencyFormat);
                if (currencyStyle == null) {
                    currencyStyle = wb.createCellStyle();
                    currencyStyle.setDataFormat(currencyFormat);
                    cellStyles.put(currencyFormat, currencyStyle);
                }
                this.currentCell.setCellStyle(currencyStyle);
            } else {
                this.currentCell.setCellValue(new HSSFRichTextString(rawValue.toString()));
            }
        } else {
            String val = this.escapeColumnValue(value);
            HSSFRichTextString textString = new HSSFRichTextString(val);
            this.currentCell.setCellValue(textString);
        }
    }

    /**
     * Decorators that help render the table to an HSSF table must implement
     * DecoratesHssf.
     * 
     * @see org.displaytag.render.TableWriterTemplate#writeDecoratedRowFinish(org.displaytag.model.TableModel)
     */
    @Override
    protected void writeDecoratedRowFinish(TableModel model) throws Exception {
        TableDecorator decorator = model.getTableDecorator();
        if (decorator instanceof DecoratesHssf) {
            DecoratesHssf hdecorator = (DecoratesHssf) decorator;
            hdecorator.setSheet(this.sheet);
        }
        decorator.finishRow();
        this.rowNum = this.sheet.getLastRowNum();
        this.rowNum++;
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writePostBodyFooter(org.displaytag.model.TableModel)
     */
    @Override
    protected void writePostBodyFooter(TableModel model) throws Exception {
        this.colNum = 0;
        this.currentRow = this.sheet.createRow(this.rowNum++);
        this.writeHeaderFooter(model.getFooter(), this.currentRow, this.getHeaderFooterStyle());
        this.rowSpanTable(model);
    }

    /**
     * Make a row span the width of the table.
     * 
     * @param model
     *                The table model representing the rendered table.
     */
    private void rowSpanTable(TableModel model) {
        this.sheet.addMergedRegion(this.getMergeCellsRegion(this.currentCell.getCellNum(), (short) (model
                .getNumberOfColumns() - 1)));
    }

    /**
     * @see org.displaytag.render.TableWriterTemplate#writeDecoratedTableFinish(org.displaytag.model.TableModel)
     */
    @Override
    protected void writeDecoratedTableFinish(TableModel model) {
        model.getTableDecorator().finish();
    }

    /**
     * Escape certain values that are not permitted in excel cells.
     * 
     * @param rawValue
     *                the object value
     * @return the escaped value
     */
    protected String escapeColumnValue(Object rawValue) {
        if (rawValue == null) {
            return null;
        }
        String returnString = ObjectUtils.toString(rawValue);

        // escape the String to get the tabs, returns, newline explicit as \t \r
        // \n
        returnString = StringEscapeUtils.escapeJava(StringUtils.trimToEmpty(returnString));

        // remove tabs, insert four whitespaces instead
        returnString = StringUtils.replace(StringUtils.trim(returnString), "\\t", "    ");

        // remove the return, only newline valid in excel
        returnString = StringUtils.replace(StringUtils.trim(returnString), "\\r", " ");

        // unescape so that \n gets back to newline
        returnString = StringEscapeUtils.unescapeJava(returnString);
        return returnString;
    }

    /**
     * Writes a table header or a footer.
     * 
     * @param value
     *                Header or footer value to be rendered.
     * @param row
     *                The row in which to write the header or footer.
     * @param style
     *                Style used to render the header or footer.
     */
    private void writeHeaderFooter(String value, HSSFRow row, HSSFCellStyle style) {
        this.currentCell = row.createCell((short) this.colNum++);
        this.currentCell.setCellValue(value);
        this.currentCell.setCellStyle(style);
    }

    /**
     * Obtain the style used to render a header or footer.
     * 
     * @return The style used to render a header or footer.
     */
    private HSSFCellStyle getHeaderFooterStyle() {
        HSSFCellStyle style = this.wb.createCellStyle();
        HSSFFont bold = this.wb.createFont();
        bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(bold);

        return style;
    }

    public void setTableStartRow(int row) {
        this.startRow = row;
    }

    private short getCurrencyFormat(String currencyCode) {
        Short format = currencyFormats.get(currencyCode);
        if (format != null) {
            return format;
        }

        format = ExcelHelper.getCurrencyFormat(wb, currencyCode, CurrentUserSettingsHolder.getLocale());

        currencyFormats.put(currencyCode, format);

        return format;
    }
}
