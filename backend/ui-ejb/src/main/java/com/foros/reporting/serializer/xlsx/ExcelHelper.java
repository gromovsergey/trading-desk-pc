package com.foros.reporting.serializer.xlsx;

import com.foros.util.NumberUtil;
import com.foros.util.PropertyHelper;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelHelper {
    private static Properties excelCurrencyCodes = PropertyHelper.readProperties("com/foros/reporting/serializer/xlsx/ExcelCurrencyCodes.properties");

    public static Short getCurrencyFormat(Workbook wb, String currencyCode, Locale locale) {
        try {
            String format = getCurrencyFormatString(locale, currencyCode);
            DataFormat dataFormat = wb.createDataFormat();
            return dataFormat.getFormat(format);
        } catch (Exception e) {
            return 4; //"#,##0.00"
        }
    }

    private static void appendPart(StringBuilder sb, String part, DecimalFormat nf, String excelCurrency) {
        String symbol = nf.getDecimalFormatSymbols().getCurrencySymbol();
        part = part.replace(symbol, excelCurrency);
        sb.append(part);
    }

    private static String getExcelCurrency(Locale locale, String currencyCode) {
        String excelOverride = excelCurrencyCodes.getProperty(currencyCode + "_" + locale.toString());
        String excelCurrency = excelOverride != null ? excelOverride : currencyCode;
        return "[$" + excelCurrency + "]";
    }

    private static void appendNumberPattern(DecimalFormat nf, StringBuilder sb) {
        sb.append("#,##0");
        if (nf.getMaximumFractionDigits() > 0) {
            sb.append(".");
            sb.append(StringUtils.repeat("0", nf.getMaximumFractionDigits()));
        }
    }

    public static String getCurrencyFormatString(Locale locale, String currencyCode) {
        DecimalFormat nf = NumberUtil.getCurrencyFormat(locale, currencyCode);

        StringBuilder sb = new StringBuilder();
        String excelCurrency = getExcelCurrency(locale, currencyCode);

        // positive
        appendPart(sb, nf.getPositivePrefix(), nf, excelCurrency);
        appendNumberPattern(nf, sb);
        appendPart(sb, nf.getPositiveSuffix(), nf, excelCurrency);

        sb.append(";");

        // negative
        appendPart(sb, nf.getNegativePrefix(), nf, excelCurrency);
        appendNumberPattern(nf, sb);
        appendPart(sb, nf.getNegativeSuffix(), nf, excelCurrency);

        return sb.toString();
    }
}
