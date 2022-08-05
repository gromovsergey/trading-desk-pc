package com.foros.session.admin.currencyExchange;

import static com.foros.config.ConfigParameters.CURRENCY_EXCHANGE_IMPORTER_PERIOD;
import static com.foros.config.ConfigParameters.CURRENCY_EXCHANGE_IMPORTER_SCHEDULE;
import com.foros.config.Config;
import com.foros.config.ConfigService;
import com.foros.model.admin.GlobalParam;
import com.foros.model.currency.Currency;
import com.foros.model.currency.CurrencyExchange;
import com.foros.model.currency.CurrencyExchangeRate;
import com.foros.model.currency.Source;
import com.foros.model.security.ActionType;
import com.foros.model.security.ResultType;
import com.foros.service.timed.AbstractScheduledTimedBean;
import com.foros.session.admin.currency.CurrencyService;
import com.foros.session.admin.globalParams.GlobalParamsService;
import com.foros.session.security.AuditService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import org.apache.commons.lang.ObjectUtils;

@LocalBean
@Singleton
@Startup
public class CurrencyExchangeImporterTimedBean extends AbstractScheduledTimedBean {
    private static final Logger logger = Logger.getLogger(CurrencyExchangeImporterTimedBean.class.getName());
    private static final String CURRENCY_TO = "USD";
    @EJB
    private AuditService auditService;
    @EJB
    private CurrencyService currencyService;
    @EJB
    private CurrencyExchangeService currencyExchangeService;
    @EJB
    private GlobalParamsService globalParamsService;

    @EJB
    private ConfigService configService;


    @PostConstruct
    public void init() {
        Config config = configService.detach();
        String schedule = config.get(CURRENCY_EXCHANGE_IMPORTER_SCHEDULE);
        long period = config.get(CURRENCY_EXCHANGE_IMPORTER_PERIOD);
        init(schedule, period, getClass().getSimpleName());
    }

    @Timeout
    public void timeout(Timer timer) {
        onTimeout(timer);
    }

    @Override
    protected void proceed(Timer timer) {
        proceed();
    }

    public void proceed() {
        GlobalParam param = globalParamsService.find(GlobalParamsService.CURRENCY_EXCHANGE_RATE_UPDATE);
        if (!Source.FEED.equals(Source.valueOf(param.getValue()))) {
            return;
        }

        Collection<Currency> currencies = currencyService.getAutomaticUpdatableCurrencies();
        if (!currencies.isEmpty()) {
            try {
                Set<CurrencyExchangeRate> rates = fetchRatesFromFeed(currencies);
                currencyExchangeService.updateFromFeed(rates);
                logger.info("rates updated successfully");
            } catch (Throwable e) {
                String message = e.getClass().getName() + ": " + ObjectUtils.toString(e.getMessage());
                auditService.logMessage(new CurrencyExchange(), ActionType.UPDATE, ResultType.FAILURE, message);
                logger.log(Level.WARNING, "error importing rates", e);
            }
        }
    }

    private Set<CurrencyExchangeRate> fetchRatesFromFeed(Collection<Currency> currencies) throws IOException {
        Set<CurrencyExchangeRate> newRates = new HashSet<>();
        Map<Currency, BigDecimal> yahooRates = currencyExchangeService.fetchFromFeed(currencies);

        for (Currency currency : currencies) {
            if (CURRENCY_TO.equalsIgnoreCase(currency.getCurrencyCode())) {
                continue;
            }
            BigDecimal yahooRate = yahooRates.get(currency);
            if (yahooRate == null) {
                continue;
            }
            CurrencyExchangeRate rate = new CurrencyExchangeRate(currency, null);
            rate.setRate(yahooRate);
            newRates.add(rate);
        }
        return newRates;
    }
}
