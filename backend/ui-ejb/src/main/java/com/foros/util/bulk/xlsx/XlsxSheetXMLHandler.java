package com.foros.util.bulk.xlsx;

import java.math.BigDecimal;
import java.util.TimeZone;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

// Grounded on org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler
public class XlsxSheetXMLHandler extends DefaultHandler {

    enum xssfDataType {
        BOOLEAN,
        ERROR,
        INLINE_STRING,
        SST_STRING,
        NUMBER,
    }

    private StylesTable stylesTable;

    private ReadOnlySharedStringsTable sharedStringsTable;

    private final SheetContentsHandler output;

    private TimeZone timeZone;

    private boolean vIsOpen;
    private boolean isIsOpen;

    private xssfDataType nextDataType;

    private short formatIndex;
    private String formatString;
    private String cellRef;

    private StringBuffer value = new StringBuffer();

    public XlsxSheetXMLHandler(
            StylesTable styles,
            ReadOnlySharedStringsTable strings,
            SheetContentsHandler sheetContentsHandler,
            TimeZone timeZone) {
        this.stylesTable = styles;
        this.sharedStringsTable = strings;
        this.output = sheetContentsHandler;
        this.timeZone = timeZone;
        this.nextDataType = xssfDataType.NUMBER;
    }

    private boolean isTextTag(String name) {
        if("v".equals(name)) {
            // Easy, normal v text tag
            return true;
        }
        if("inlineStr".equals(name)) {
            // Easy inline string
            return true;
        }
        if("t".equals(name) && isIsOpen) {
            // Inline string <is><t>...</t></is> pair
            return true;
        }
        // It isn't a text tag
        return false;
    }

    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {

        if (isTextTag(name)) {
            vIsOpen = true;
            // Clear contents cache
            value.setLength(0);
        } else if ("is".equals(name)) {
            // Inline string outer tag
            isIsOpen = true;
        } else if("row".equals(name)) {
            int rowNum = Integer.parseInt(attributes.getValue("r")) - 1;
            output.startRow(rowNum);
        } else if ("c".equals(name)) {
            // Set up defaults.
            this.nextDataType = xssfDataType.NUMBER;
            this.formatIndex = -1;
            this.formatString = null;
            cellRef = attributes.getValue("r");
            String cellType = attributes.getValue("t");
            String cellStyleStr = attributes.getValue("s");
            if ("b".equals(cellType))
                nextDataType = xssfDataType.BOOLEAN;
            else if ("e".equals(cellType))
                nextDataType = xssfDataType.ERROR;
            else if ("inlineStr".equals(cellType) || "str".equals(cellType))
                nextDataType = xssfDataType.INLINE_STRING;
            else if ("s".equals(cellType))
                nextDataType = xssfDataType.SST_STRING;
            else if (cellStyleStr != null) {
                // Number, but almost certainly with a special style or format
                int styleIndex = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                this.formatIndex = style.getDataFormat();
                this.formatString = style.getDataFormatString();
                if (this.formatString == null)
                    this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
            }
        }
    }

    public void endElement(String uri, String localName, String name)
            throws SAXException {
        Object thisObj = null;

        // v => contents of a cell
        if (isTextTag(name)) {
            vIsOpen = false;

            // Process the value contents as required, now we have it all
            switch (nextDataType) {
                case BOOLEAN:
                    char first = value.charAt(0);
                    thisObj = first == '0' ? "FALSE" : "TRUE";
                    break;

                case ERROR:
                    thisObj = "ERROR:" + value.toString();
                    break;

                case INLINE_STRING:
                    // TODO: Can these ever have formatting on them?
                    XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                    thisObj = rtsi.toString();
                    break;

                case SST_STRING:
                    String sstIndex = value.toString();
                    try {
                        int idx = Integer.parseInt(sstIndex);
                        XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                        thisObj = rtss.toString();
                    }
                    catch (NumberFormatException ex) {
                        System.err.println("Failed to parse SST index '" + sstIndex + "': " + ex.toString());
                    }
                    break;

                case NUMBER:
                    BigDecimal n = new BigDecimal(value.toString());

                    if (DateUtil.isADateFormat(formatIndex, formatString)) {
                        thisObj = DateUtil.getJavaDate(n.doubleValue(), timeZone);
                    } else {
                        thisObj = n;
                    }

                    break;

                default:
                    thisObj = "(TODO: Unexpected type: " + nextDataType + ")";
                    break;
            }

            // Output
            output.cell(cellRef, thisObj);
        } else if ("is".equals(name)) {
            isIsOpen = false;
        } else if ("row".equals(name)) {
            output.endRow();
        }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (vIsOpen) {
            value.append(ch, start, length);
        }
    }

    public interface SheetContentsHandler {

        public void startRow(int rowNum);

        public void endRow();

        public void cell(String cellReference, Object value);
    }
}

