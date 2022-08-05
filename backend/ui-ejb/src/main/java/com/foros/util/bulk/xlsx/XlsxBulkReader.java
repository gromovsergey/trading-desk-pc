package com.foros.util.bulk.xlsx;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.CollectionUtils;
import com.foros.util.DateHelper;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;
import com.foros.util.bulk.BulkReader;
import com.foros.util.csv.FileFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XlsxBulkReader implements BulkReader {
    private String filename;
    private TimeZone timeZone;
    private BulkReaderHandler handler;

    private InputStream stream;

    private Locale locale = CurrentUserSettingsHolder.getLocale();

    public XlsxBulkReader(String filename, TimeZone timeZone) {
        this.filename = filename;
        this.timeZone = timeZone;
    }

    @Override
    public void setBulkReaderHandler(BulkReaderHandler handler) {
        this.handler = handler;
    }

    @Override
    public void read() throws IOException {
        try {
            OPCPackage pkg = OPCPackage.open(filename);
            XSSFReader xssfReader = new XSSFReader(pkg);

            StylesTable styles = xssfReader.getStylesTable();
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            stream = xssfReader.getSheetsData().next();

            InputSource sheetSource = new InputSource(stream);

            XMLReader parser = XMLReaderFactory.createXMLReader();

            ContentHandler contentHandler = new XlsxSheetXMLHandler(styles, strings, new XlsxSheetXMLHandler.SheetContentsHandler() {
                private XlsxReaderRow row = new XlsxReaderRow();
                private int headersCount;

                @Override
                public void startRow(int rowNum) {
                    row.clear();
                    row.setRowNum(rowNum + 1);
                }

                @Override
                public void endRow() {
                    if (row.rowNum == 1) {
                        headersCount = row.getColumnCount();
                    } else if (row.getColumnCount() < headersCount) {
                        row.increaseValuesCount(headersCount);
                    }

                    handler.handleRow(row);
                }

                @Override
                public void cell(String cellReference, Object value) {
                    CellReference ref = new CellReference(cellReference);
                    row.addValue(ref.getCol(), value);
                }

            }, timeZone);

            parser.setContentHandler(contentHandler);
            parser.parse(sheetSource);

        } catch (OpenXML4JException | OpenXML4JRuntimeException | SAXException e) {
            throw new FileFormatException(StringUtil.getLocalizedString("errors.csv.invalidFormat"));
        }
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    public class XlsxReaderRow implements BulkReaderRow {
        private int rowNum;
        private List<Object> values = new ArrayList<Object>();

        public void clear() {
            values.clear();
        }

        public void addValue(int index, Object value) {
            CollectionUtils.resize(values, index);
            values.set(index, value);
        }

        public void increaseValuesCount(int count) {
            CollectionUtils.resize(values, count - 1);
        }

        public void setRowNum(int rowNum) {
            this.rowNum = rowNum;
        }

        @Override
        public int getRowNum() {
            return rowNum;
        }

        @Override
        public int getColumnCount() {
            return values.size();
        }

        @Override
        public Object getValue(int i) {
            return values.get(i);
        }

        @Override
        public String getStringValue(int i) {
            if (values.get(i) == null) {
                return null;
            }

            if (getValueType(i) == String.class) {
                String str = StringUtil.trimProperty((String) values.get(i));
                return str.isEmpty() ? null : str;
            } else {
                return values.get(i).toString();
            }
        }

        @Override
        public BigDecimal getNumericValue(int i) throws ParseException {
            if (values.get(i) == null) {
                return null;
            }

            if (getValueType(i) == BigDecimal.class) {
                return (BigDecimal) values.get(i);
            } else if (getValueType(i) == String.class) {
                return NumberUtil.parseBigDecimal(getStringValue(i));
            } else {
                throw new ParseException("Parse error", 0);
            }
        }

        @Override
        public Date getDateValue(int i) throws ParseException {
            if (values.get(i) == null) {
                return null;
            }

            if (getValueType(i) == Date.class) {
                return (Date) values.get(i);
            } else if (getValueType(i) == String.class) {
                return DateHelper.parseDateTime(getStringValue(i), timeZone, locale);
            } else {
                throw new ParseException("Parse error", 0);
            }
        }

        public Class getValueType(int i) {
            if (values.get(i) == null) {
                return null;
            }
            return values.get(i).getClass();
        }
    }
}