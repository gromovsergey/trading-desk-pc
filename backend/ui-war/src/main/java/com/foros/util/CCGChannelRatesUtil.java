package com.foros.util;

import com.foros.session.campaign.ChannelRatesTO;
import com.foros.web.taglib.NumberFormatter;

import java.math.BigDecimal;
import java.util.Map;

public class CCGChannelRatesUtil {
    public static String getPopulatedTotalRates(ChannelRatesTO rates, String ccgCurrencyCode) {
        StringBuilder res = new StringBuilder();
        BigDecimal cpmRate = rates.getCpmRate();
        BigDecimal cpcRate = rates.getCpcRate();
        BigDecimal cpaRate = rates.getCpaRate();

        boolean isFirst = true;
        if (cpmRate.compareTo(BigDecimal.ZERO) != 0) {
            res.append(NumberFormatter.formatCurrency(cpmRate, ccgCurrencyCode));
            res.append(" ").append(StringUtil.getLocalizedString("ccg.cpm"));
            isFirst = false;
        }

        if (cpcRate.compareTo(BigDecimal.ZERO) != 0) {
            if (!isFirst) {
                res.append(", ");
            }

            res.append(NumberFormatter.formatCurrency(cpcRate, ccgCurrencyCode));
            res.append(" ").append(StringUtil.getLocalizedString("ccg.cpc"));
            isFirst = false;
        }

        if (cpaRate.compareTo(BigDecimal.ZERO) != 0) {
            if (!isFirst) {
                res.append(", ");
            }

            res.append(NumberFormatter.formatCurrency(cpaRate, ccgCurrencyCode));
            res.append(" ").append(StringUtil.getLocalizedString("ccg.cpa"));
        }

        return res.toString();
    }

    public static String getPopulatedTargetingRates(ChannelRatesTO rates) {
        Map<String, BigDecimal> cpmRates = rates.getCpmRates();
        Map<String, BigDecimal> cpcRates = rates.getCpcRates();

        StringBuilder res = new StringBuilder();

        boolean isFirst = true;
        for (Map.Entry<String, BigDecimal> entry : cpmRates.entrySet()) {
            if (!isFirst) {
                res.append(", ");
            } else {
                isFirst = false;
            }

            String currency = entry.getKey();
            BigDecimal rate = entry.getValue();

            res.append(NumberFormatter.formatCurrency(rate, currency));
            res.append(" ").append(StringUtil.getLocalizedString("ccg.cpm"));
        }

        for (Map.Entry<String, BigDecimal> entry : cpcRates.entrySet()) {
            if (!isFirst) {
                res.append(", ");
            } else {
                isFirst = false;
            }

            String currency = entry.getKey();
            BigDecimal rate = entry.getValue();

            res.append(NumberFormatter.formatCurrency(rate, currency));
            res.append(" ").append(StringUtil.getLocalizedString("ccg.cpc"));
        }

        return res.toString();
    }
}
