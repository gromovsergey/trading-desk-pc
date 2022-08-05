package com.foros.session.campaign;

import com.foros.model.campaign.CcgRate;
import com.foros.model.channel.ChannelRate;
import com.foros.session.admin.CurrencyConverter;
import com.foros.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChannelRatesTO {
    private final Map<String, BigDecimal> cpmRates = new HashMap<String, BigDecimal>();
    private final Map<String, BigDecimal> cpcRates = new HashMap<String, BigDecimal>();

    private BigDecimal cpmRate = BigDecimal.ZERO;
    private BigDecimal cpcRate = BigDecimal.ZERO;
    private BigDecimal cpaRate = BigDecimal.ZERO;

    public ChannelRatesTO(Collection<ChannelRate> channelRates) {
        for (ChannelRate rate : channelRates) {
            String currency = rate.getCurrency().getCurrencyCode();
            BigDecimal currentRate;

            switch (rate.getRateType()) {
                case CPM:
                    currentRate = cpmRates.get(currency);

                    if (currentRate == null) {
                        cpmRates.put(currency, rate.getCpm());
                    } else {
                        currentRate = currentRate.add(rate.getCpm());
                        cpmRates.put(currency, currentRate);
                    }
                    break;

                case CPC:
                    currentRate = cpcRates.get(currency);

                    if (currentRate == null) {
                        cpcRates.put(currency, rate.getCpc());
                    } else {
                        currentRate = currentRate.add(rate.getCpc());
                        cpcRates.put(currency, currentRate);
                    }
                    break;
            }
        }
    }

    public ChannelRatesTO(CcgRate ccgRate, Collection<ChannelRate> channelRates, CurrencyConverter currencyConverter) {
        switch (ccgRate.getRateType()) {
            case CPM:
                cpmRate = ccgRate.getCpm();
                break;
            case CPC:
                cpcRate = ccgRate.getCpc();
                break;
            case CPA:
                cpaRate = ccgRate.getCpa();
                break;
        }

        if (!CollectionUtils.isNullOrEmpty(channelRates)) {
            for (ChannelRate rate : channelRates) {
                Long currencyId = rate.getCurrency().getId();
                String currencyCode = rate.getCurrency().getCurrencyCode();
                BigDecimal currentRate;

                switch (rate.getRateType()) {
                    case CPM:
                        cpmRate = cpmRate.add(currencyConverter.convert(currencyId, rate.getCpm()));

                        currentRate = cpmRates.get(currencyCode);

                        if (currentRate == null) {
                            cpmRates.put(currencyCode, rate.getCpm());
                        } else {
                            currentRate = currentRate.add(rate.getCpm());
                            cpmRates.put(currencyCode, currentRate);
                        }
                        break;

                    case CPC:
                        cpcRate = cpcRate.add(currencyConverter.convert(currencyId, rate.getCpc()));

                        currentRate = cpcRates.get(currencyCode);

                        if (currentRate == null) {
                            cpcRates.put(currencyCode, rate.getCpc());
                        } else {
                            currentRate = currentRate.add(rate.getCpc());
                            cpcRates.put(currencyCode, currentRate);
                        }
                        break;
                }
            }
        }
    }

    public Map<String, BigDecimal> getCpmRates() {
        return cpmRates;
    }

    public Map<String, BigDecimal> getCpcRates() {
        return cpcRates;
    }

    public BigDecimal getCpmRate() {
        return cpmRate;
    }

    public BigDecimal getCpcRate() {
        return cpcRate;
    }

    public BigDecimal getCpaRate() {
        return cpaRate;
    }
}
