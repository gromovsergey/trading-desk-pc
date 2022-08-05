package com.foros.reporting.serializer.xlsx;

import com.foros.reporting.ReportingException;
import com.foros.reporting.Row;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.serializer.ResultSerializerSupport;
import com.foros.reporting.serializer.formatter.ExcelCellAccessor;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryHolder;
import com.foros.util.ExceptionUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class XlsxSerializer extends ResultSerializerSupport<XlsxSerializer> {
    protected final OutputStream stream;
    protected final Workbook workbook;
    protected final ExcelStyles styles;
    protected Sheet sheet;
    protected CreationHelper createHelper;
    protected int rowCounter = -1;
    protected org.apache.poi.ss.usermodel.Row sheetRow;
    protected POIFCellAccessor cellAccessor;

    public XlsxSerializer(OutputStream stream, ValueFormatterRegistryHolder registryHolder, ExcelStyles styles) {
        super(registryHolder, new FormatterContext(styles.getLocale()));
        this.styles = styles;
        this.cellAccessor = new POIFCellAccessor();
        this.workbook = new SXSSFWorkbook();
        this.stream = stream;
    }

    @Override
    public void before(MetaData metaData) {
        super.before(metaData);
        try {
            ReportMetaData reportMetaData = (ReportMetaData) metaData;

            String localName = reportMetaData.getName(styles.getLocale());

            sheet = workbook.createSheet(localName);
        } catch (Exception e) {
            // fallback to default name
            sheet = workbook.createSheet();
        }
        createHelper = workbook.getCreationHelper();

        beforeHeader(metaData);

        serializeHeader();
    }

    protected void beforeHeader(MetaData metaData) {
    }

    private void serializeHeader() {

        List<Column> columns = metaData.getColumns();
        rowCounter++;
        sheetRow = sheet.createRow(rowCounter);
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            formatCell(i, column, column);
        }
    }

    @Override
    public void row(Row row) {
        super.row(row);
        serialize(row);
    }

    private void serialize(Row row)  {
        rowCounter++;
        sheetRow = sheet.createRow(rowCounter);

        List<Column> columns = metaData.getColumns();

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            formatCell(i, column, row.get(column));
        }
    }

    private void formatCell(int cellIndex, Column column, Object value) {
        ValueFormatter<Object> valueFormatter = registry.get(column);
        ExcelCellAccessor cellAccessor = getCellAccessor(cellIndex);
        valueFormatter.formatExcel(cellAccessor, value, context);
    }

    protected ExcelCellAccessor getCellAccessor(int cellIndex) {
        Cell cell = sheetRow.createCell(cellIndex);
        cellAccessor.init(cell);
        return cellAccessor;
    }

    @Override
    public void after() {
        rowCounter = -1;
        super.after();
    }

    @Override
    public void close() {
        cellAccessor.flush();
        try {
            workbook.write(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.close();
    }

    @Override
    public void onError(ReportingException ex) {
        rowCounter++;
        sheetRow = sheet.createRow(rowCounter);

        ExcelCellAccessor cellAccessor = getCellAccessor(0);
        cellAccessor.addStyle(Styles.error());
        cellAccessor.setString(ExceptionUtil.getReportingErrorUserFriendlyMessage(ex));
    }

    private class POIFCellAccessor implements ExcelCellAccessor {
        private Cell cell;
        private List<String> styleNames = new ArrayList<String>();
        private Map<String, CellStyle> cache = new HashMap<String, CellStyle>();

        public void init(Cell cell) {
            if (this.cell != null) {
                flush();
            }
            this.cell = cell;
            styleNames.clear();
        }

        @Override
        public void setString(String text) {
            cell.setCellValue(text == null ? "" : text);
        }

        @Override
        public void setDouble(double v) {
            cell.setCellValue(v);
        }

        @Override
        public void setDate(LocalDate v) {
            Date date = null;
            if (v != null) {
                date = v.toDateTimeAtStartOfDay().toDate();
            }
            setDate(date);
        }

        @Override
        public void setDate(LocalDateTime v) {
            Date date = null;
            if (v != null) {
                date = v.toDateTime().toDate();
            }
            setDate(date);
        }

        public void setDate(Date date) {
            if (date == null) {
                setString("");
            } else {
                cell.setCellValue(date);
            }
        }

        @Override
        public void addStyle(String style) {
            styleNames.add(style);
        }

        public void flush() {
            if (cell != null) {
                cell.setCellStyle(getStyleIdx());
            }
        }

        private CellStyle getStyleIdx() {
            if (styleNames.isEmpty()) {
                return null;
            }

            String fullStyle = StringUtils.join(styleNames, ',');
            CellStyle cellStyle = cache.get(fullStyle);
            if (cellStyle == null) {
                cellStyle = workbook.createCellStyle();
                styles.get(styleNames).init(workbook, cellStyle);
                cache.put(fullStyle, cellStyle);
            }
            return cellStyle;
        }

        @Override
        public void setLink(String href, String text) {
            // Have to use formula, because poi lib stores hyperlinks in map and consumes huge amount of RAM
            StringBuilder linkFormula = new StringBuilder(text.length() + href.length() + 32);
            linkFormula.append("HYPERLINK(\"");
            appendEscaped(linkFormula, href);
            linkFormula.append("\",\"");
            appendEscaped(linkFormula, text);
            linkFormula.append("\")");
            ((SXSSFCell)cell).setCellFormula(linkFormula.toString(), Cell.CELL_TYPE_STRING);
            cell.setCellValue(text);
        }

        private void appendEscaped(StringBuilder linkFormula, String str) {
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch == '"') {
                    linkFormula.append("\"");
                }
                linkFormula.append(ch);
            }
        }
    }
}
