package com.foros.reporting.serializer.xlsx;

import static com.foros.reporting.serializer.xlsx.CompositeStylist.composite;

import com.foros.cache.NamedCO;
import com.foros.session.admin.currency.CurrencyService;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

@LocalBean
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ExcelStylesRegistry {

    public static final Stylist ALIGN_RIGHT = new AlignmentStylist(0x3);

    public static final ExcelStyles DEFAULT_STYLES = new ExcelStylesImpl(null)
            .add(Styles.text(), new StaticStylist(Stylist.NO_STYLE_ID))
            .add(Styles.textAlignRight(), ALIGN_RIGHT)
            .add(Styles.id(), new StaticStylist(Stylist.NO_STYLE_ID))
            .add(Styles.number(), composite(new StaticStylist(0x1), ALIGN_RIGHT))
            .add(Styles.percent(), composite(new StaticStylist(0xA), ALIGN_RIGHT))
            .add(Styles.date(), composite(new StaticStylist(0xE), ALIGN_RIGHT))
            .add(Styles.dateTime(), composite(new StaticStylist(0x16), ALIGN_RIGHT))
            .add(Styles.dateMonth(), new FormatStylist("MMM YYYY"))
            .add(Styles.dateYear(), new FormatStylist("YYYY"))
            .add(Styles.header(), new HeaderStylist())
            .add(Styles.title(), new TitleStylist())
            .add(Styles.subtotal(), new XSSFBackgroundStylist(new Color(0xE0E0E0)))
            .add(Styles.error(), new ErrorStylist())
            .add(Styles.parameterName(), new StaticStylist(Stylist.NO_STYLE_ID))
            .add(Styles.hyperlink(), new HyperlinkStylist())
            .add(Styles.parameterValue(), new StaticStylist(Stylist.NO_STYLE_ID));

    public static final int MAX_FRACTION_DIGITS = 4;

    @EJB
    private CurrencyService currencyService;

    private Map<Locale, ExcelStyles> stylesMap;
    private ExcelStyles defaultStyles;
    private Lock write;
    private Lock read;

    @PostConstruct
    public void init() {
        stylesMap = new HashMap<Locale, ExcelStyles>();
        ReadWriteLock lock = new ReentrantReadWriteLock();
        read = lock.readLock();
        write = lock.writeLock();

        initStyles();
    }

    private void initStyles() {
        defaultStyles = DEFAULT_STYLES;
    }

    public ExcelStyles get(Locale locale) {
        read.lock();
        ExcelStyles styles;
        try {
            styles = stylesMap.get(locale);
        } finally {
            read.unlock();
        }

        if (styles == null) {
            write.lock();
            try {
                styles = stylesMap.get(locale);
                if (styles == null) {
                    styles = prepareStyles(locale);
                    stylesMap.put(locale, styles);
                }
            } finally {
                write.unlock();
            }
        }

        return styles;
    }

    private ExcelStyles prepareStyles(Locale locale) {
        ExcelStylesImpl styles = new ExcelStylesImpl(defaultStyles, locale);
        addCurrencyStyles(locale, styles);
        addNumberStyles(styles);
        return styles;
    }

    private void addCurrencyStyles(Locale locale, ExcelStylesImpl styles) {
        Collection<NamedCO<Long>> cos = currencyService.getIndex();
        for (NamedCO<Long> co : cos) {
            String code = co.getName();
            String name = Styles.currency(code);
            styles.add(name, new CurrencyStylist(code, locale));
        }
        styles.add(Styles.currency("N/A"), new CurrencyStylist("N/A", locale));
    }

    private void addNumberStyles(ExcelStylesImpl styles) {
        for (int i = 1; i <= MAX_FRACTION_DIGITS; i++) {
            String name = Styles.number(i);
            styles.add(name, new FormatStylist("0." + StringUtils.repeat("0", i)));
        }
    }

    public static class StaticStylist implements Stylist {

        private short value;

        public StaticStylist(int value) {
            this.value = (short) value;
        }

        @Override
        public void init(Workbook workbook, CellStyle cellStyle) {
            cellStyle.setDataFormat(value);
        }
    }

    public static class AlignmentStylist implements Stylist {

        private short value;

        public AlignmentStylist(int value) {
            this.value = (short) value;
        }

        @Override
        public void init(Workbook workbook, CellStyle cellStyle) {
            cellStyle.setAlignment(value);
        }
    }

    public static class HeaderStylist implements Stylist {

        @Override
        public void init(Workbook workbook, CellStyle cellStyle) {
            Font font = workbook.createFont();
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            cellStyle.setFont(font);
        }
    }

    public static class TitleStylist implements Stylist {

        @Override
        public void init(Workbook workbook, CellStyle cellStyle) {
            Font font = workbook.createFont();
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            font.setFontHeightInPoints((short) 14);
            cellStyle.setFont(font);
        }
    }

    public static class HyperlinkStylist implements Stylist {
        @Override
        public void init(Workbook workbook, CellStyle cellStyle) {
            Font hlinkFont = workbook.createFont();
            hlinkFont.setUnderline(Font.U_SINGLE);
            hlinkFont.setColor(IndexedColors.BLUE.getIndex());
            cellStyle.setFont(hlinkFont);
        }
    }

    public static class FormatStylist implements Stylist {

        private String format;

        public FormatStylist(String format) {
            this.format = format;
        }

        @Override
        public void init(Workbook workbook, CellStyle cellStyle) {
            DataFormat dataFormat = workbook.createDataFormat();
            cellStyle.setDataFormat(dataFormat.getFormat(format));
        }
    }

    public static class CurrencyStylist implements Stylist {

        private String currencyCode;
        private Locale locale;

        public CurrencyStylist(String currencyCode, Locale locale) {
            this.currencyCode = currencyCode;
            this.locale = locale;
        }

        @Override
        public void init(Workbook workbook, CellStyle cellStyle) {
            cellStyle.setDataFormat(ExcelHelper.getCurrencyFormat(workbook, currencyCode, locale));
        }
    }

    public static class ErrorStylist implements Stylist {

        public ErrorStylist(){
        }

        @Override
        public void init(Workbook workbook, CellStyle cellStyle) {
            Font font = workbook.createFont();
            font.setColor(IndexedColors.RED.getIndex());
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            cellStyle.setFont(font);
        }
    }
}
