package com.foros.reporting.serializer.xlsx;

import com.foros.model.security.Language;
import com.foros.util.NumberUtil;

import group.Report;
import group.Unit;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Report.class })
public class ExcelHelperTest extends Assert {

    @Test
    public void test() {
        Currency[] currencies = {
                Currency.getInstance("EUR"),
                Currency.getInstance("USD"),
                Currency.getInstance("GBP"),
                Currency.getInstance("BRL"),
                Currency.getInstance("CNY"),
                Currency.getInstance("JPY"),
                Currency.getInstance("KRW"),
                Currency.getInstance("RUB"),
                Currency.getInstance("RON")
        };

        String[] countries = new String[] {
                "GB",
                "US",
                "BR",
                "KR",
                "RU",
                "RO",
                "CN",
                "JP"
        };

        boolean failed = false;
        for (Currency currency : currencies) {
            String currencyCode = currency.getCurrencyCode();
            for (Language language : Language.values()) {
                for (String country : countries) {
                    Locale locale = new Locale(language.getIsoCode(), country);
                    DecimalFormat nf = NumberUtil.getCurrencyFormat(locale, currencyCode, currency.getDefaultFractionDigits());
                    String sample = nf.format(2.34);
                    String excelFormat = ExcelHelper.getCurrencyFormatString(locale, currencyCode);
                    String msg = currencyCode + "_" + locale + " : " + sample + " -> " + excelFormat;
                    if (!sample.contains(currencyCode)) {
                        if (excelFormat.contains(currencyCode) || !excelFormat.contains("[$" + nf.getCurrency().getSymbol(locale))) {
                            System.err.println(msg);
                            System.err.flush();
                            failed = true;
                        }
                    }
                }
            }
        }
        assertFalse("See messages above", failed);
    }
}
