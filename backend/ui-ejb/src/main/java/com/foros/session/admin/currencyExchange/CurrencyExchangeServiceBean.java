package com.foros.session.admin.currencyExchange;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.foros.changes.CaptureChangesInterceptor;
import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.currency.Currency;
import com.foros.model.currency.CurrencyExchange;
import com.foros.model.currency.CurrencyExchangeAuditWrapper;
import com.foros.model.currency.CurrencyExchangeRate;
import com.foros.model.currency.CurrencyExchangeRateAuditWrapper;
import com.foros.model.currency.CurrencyExchangeRatePK;
import com.foros.model.currency.Source;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.admin.CurrencyConverter;
import com.foros.session.admin.currency.CurrencyService;
import com.foros.session.security.AuditService;
import com.foros.util.CollectionMerger;
import com.foros.util.PersistenceUtils;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import org.springframework.jdbc.core.ResultSetExtractor;

@Stateless(name = "CurrencyExchangeService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class CurrencyExchangeServiceBean implements CurrencyExchangeService {
    private static final Logger logger = Logger.getLogger(CurrencyExchangeServiceBean.class.getName());
    private static final String USD_CODE = "USD";
    private static final String FEED_BASE_CURRENCY_CODE = "EUR";
    private static final int CURRENCY_VALUE_SCALE = 6;

    private String CURRENCY_FEED_URL;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private ConfigService configService;

    @EJB
    private CurrencyService currencySvc;

    @EJB
    private AuditService auditService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        Config config = configService.detach();
        this.CURRENCY_FEED_URL = config.get(ConfigParameters.CURRENCY_FEED_URL);
    }

    private void prePersist(CurrencyExchange currencyExchange) {
        Timestamp now = new Timestamp((new Date()).getTime());
        currencyExchange.setEffectiveDate(now);

        Currency defaultCurrency = currencySvc.getDefault();
        CurrencyExchangeRate defaultUSD = new CurrencyExchangeRate(defaultCurrency, currencyExchange);
        defaultUSD.setRate(BigDecimal.ONE);
        defaultUSD.setLastUpdated(now);
        currencyExchange.getCurrencyExchangeRates().add(defaultUSD);
    }

    @Override
    @Restrict(restriction = "CurrencyExchange.view")
    public CurrencyExchange viewLast() {
        return findLast();
    }

    @Override
    public CurrencyExchange findLast() {
        CurrencyExchange exchange = new CurrencyExchange();
        List exchanges = em.createNamedQuery("CurrencyExchange.findLast").getResultList();
        Collection<CurrencyExchangeRate> rates;
        if (exchanges != null && exchanges.size() > 0) {
            CurrencyExchange tempExchange = (CurrencyExchange)exchanges.get(0);
            exchange.setEffectiveDate(tempExchange.getEffectiveDate());
            exchange.setId(tempExchange.getId());
            rates = tempExchange.getCurrencyExchangeRates();
        } else {
            rates = new ArrayList<CurrencyExchangeRate>();
        }

        Currency defaultCurrency = currencySvc.getDefault();
        Set<CurrencyExchangeRate> copyRates = new LinkedHashSet<CurrencyExchangeRate>();

        for (CurrencyExchangeRate rate : rates) {
            if (!rate.getCurrency().equals(defaultCurrency)) {
                copyRates.add(new CurrencyExchangeRate(rate, exchange));
            }
        }

        exchange.setCurrencyExchangeRates(copyRates);
        return exchange;
    }

    @Override
    public CurrencyConverter getCrossRate(Long currencyId, Date date) {
        Currency currency = currencySvc.findById(currencyId);
        return getCrossRate(date, currency);
    }

    @Override
    public CurrencyConverter getCrossRate(String currencyCode, Date date) {
        Currency currency = currencySvc.getCurrencyByCode(currencyCode);
        return getCrossRate(date, currency);
    }

    private CurrencyConverter getCrossRate(Date date, Currency currency) {
        Map<Long, BigDecimal> rates = jdbcTemplate.query(
                "select currency_id, rate from country.get_currency_rates(?)",
                new Object[]{date},
                new ResultSetExtractor<Map<Long, BigDecimal>>() {
                    @Override
                    public Map<Long, BigDecimal> extractData(ResultSet rs) throws SQLException {
                        Map<Long, BigDecimal> res = new HashMap<Long, BigDecimal>();
                        while (rs.next()) {
                            res.put(rs.getLong("currency_id"), rs.getBigDecimal("rate"));
                        }
                        return res;
                    }
                }
        );
        return new CurrencyConverter(currency, rates);
    }

    @Override
    @Restrict(restriction = "CurrencyExchange.update")
    @Validate(validation = "CurrencyExchange.manual", parameters = {"#rates", "#previousEffectiveDate"})
    @Interceptors(CaptureChangesInterceptor.class)
    public void update(Collection<CurrencyExchangeRate> rates, Timestamp previousEffectiveDate) {
        internalUpdate(rates, previousEffectiveDate, Source.MANUAL);
    }

    @Override
    @Validate(validation = "CurrencyExchange.feed", parameters = "#rates")
    @Interceptors(CaptureChangesInterceptor.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateFromFeed(Collection<CurrencyExchangeRate> rates) {
        internalUpdate(rates, null, Source.FEED);
    }

    @Override
    @Restrict(restriction = "CurrencyExchange.update")
    public void switchExchangeUpdateTo(Source source) {
        List<Currency> currencies = currencySvc.findAll();
        if (Source.MANUAL.equals(source)) {
            for (Currency currency : currencies) {
                currency.setSource(Source.MANUAL);
            }
        } else if (Source.FEED.equals(source)) {
            Set<Currency> existingCurrencies = checkCurrencyExistInFeed(currencies);
            for (Currency currency : currencies) {
                if (existingCurrencies.contains(currency)) {
                    currency.setSource(Source.FEED);
                } else {
                    currency.setSource(Source.MANUAL);
                }
            }
        }
    }

    @Override
    public Set<Currency> checkCurrencyExistInFeed(Collection<Currency> currencies) {
        Map<Currency, BigDecimal> rates = null;
        try {
            rates = fetchFromFeed(currencies);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        if (rates == null) {
            return Collections.emptySet();
        }

        return rates.keySet();
    }

    private void internalUpdate(Collection<CurrencyExchangeRate> rates, Timestamp previousEffectiveDate, Source source) {
        CurrencyExchange lastExchange = findLast();
        if (lastExchange.getEffectiveDate() != null && previousEffectiveDate != null && !lastExchange.getEffectiveDate().equals(previousEffectiveDate)) {
            throw new OptimisticLockException("Currency exchange rates are out of date");
        }

        RatesMerger merger = new RatesMerger(lastExchange.getCurrencyExchangeRates(), rates);
        merger.merge();
        Collection<String> changedCodes = merger.getChangedCodes();

        if (changedCodes.isEmpty()) {
            return;
        }

        CurrencyExchange exchange = merger.getExchange();
        prePersist(exchange);

        CurrencyExchangeAuditWrapper exchangeWrapper = new CurrencyExchangeAuditWrapper(exchange, source);
        if (rates != null) {
            for (CurrencyExchangeRate rate : rates) {
                Long currencyId = rate.getId().getCurrencyId();
                Currency currency = em.find(Currency.class, currencyId);
                rate.setId(new CurrencyExchangeRatePK(currency, exchange));
                rate.setLastUpdated(merger.getLastUpdated());
                String code = rate.getCurrency().getCurrencyCode();
                CurrencyExchangeRateAuditWrapper rateWrapper = new CurrencyExchangeRateAuditWrapper(rate);
                rateWrapper.setUpdated(changedCodes.contains(code));
                exchangeWrapper.getRates().add(rateWrapper);
            }
        }

        auditService.audit(exchangeWrapper, ActionType.CREATE);

        em.persist(exchange);
        em.flush();
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void createCurrency(Currency currency) {
        CurrencyExchangeRate exchRate = new CurrencyExchangeRate(currency, null);
        exchRate.setRate(currency.getRate());
        internalUpdate(Collections.singleton(exchRate), null, Source.MANUAL);
    }

    @Override
    @Restrict(restriction = "CurrencyExchange.update")
    @Interceptors(CaptureChangesInterceptor.class)
    public void updateCurrency(Currency currency) {
        CurrencyExchangeRate exchRate = new CurrencyExchangeRate(currency, null);
        exchRate.setRate(currency.getRate());
        internalUpdate(Collections.singleton(exchRate), currency.getEffectiveDate(), Source.MANUAL);
    }

    @Override
    public Map<Currency, BigDecimal> fetchFromFeed(Collection<Currency> currencies) throws IOException {
        if (com.foros.util.CollectionUtils.isNullOrEmpty(currencies)) {
            return Collections.emptyMap();
        }

        URL url = new URL(CURRENCY_FEED_URL);
        Map<Currency, BigDecimal> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = url.openStream(); JsonParser parser = mapper.getFactory().createParser(is)) {
            JsonNode rates = parseAndValidateFeed(mapper, parser, FEED_BASE_CURRENCY_CODE);

            BigDecimal baseUsd = fetchCurrencyValue(rates, USD_CODE, FEED_BASE_CURRENCY_CODE);
            for (Currency currency : currencies) {
                BigDecimal baseCurrValue = fetchCurrencyValue(rates, currency.getCurrencyCode(), FEED_BASE_CURRENCY_CODE);
                if (baseCurrValue != null) {
                    map.put(currency, baseCurrValue.divide(baseUsd, CURRENCY_VALUE_SCALE, RoundingMode.HALF_UP));
                } else {
                    logger.warning("Currency " + currency.getCurrencyCode() + " was not found in response: " + rates.asText());
                }
            }

        } catch (IOException ioex) {
            throw ioex;
        } catch (Exception ex) {
            logger.warning("An exception for " + url.toString());
            ex.printStackTrace();
        }

        return map;
    }

    private BigDecimal fetchCurrencyValue(JsonNode ratesNode, String currencyCode, String baseCurrencyCode) {
        if (baseCurrencyCode.equals(currencyCode)) {
            return BigDecimal.ONE;
        }

        List<String> rateValueSrc = ratesNode.findValuesAsText(currencyCode);
        if (rateValueSrc.isEmpty() || rateValueSrc.size() > 1) {
            return null;
        }
        return new BigDecimal(rateValueSrc.get(0));
    }

    private JsonNode parseAndValidateFeed(ObjectMapper mapper, JsonParser parser, String baseCurrency) throws IOException {
        ObjectNode node = mapper.readTree(parser);
        if (node == null) {
            throw new IOException("Response is empty from " + CURRENCY_FEED_URL);
        }

        JsonNode success = node.findValue("success");
        if (success == null || !success.asBoolean()) {
            throw new IOException("Can't load data (success = false) from " + CURRENCY_FEED_URL);
        }

        JsonNode base = node.findValue("base");
        if (base == null || !baseCurrency.equals(base.asText())) {
            throw new JsonParseException(baseCurrency + " is expected as base currency, but real base currency is " + base, null);
        }

        JsonNode ratesNode = node.findValue("rates");
        if (ratesNode == null) {
            throw new IOException("Can't find field 'rates' in response: " + ratesNode.asText());
        }

        BigDecimal baseUsd = fetchCurrencyValue(ratesNode, USD_CODE, baseCurrency);
        if (baseUsd == null) {
            throw new JsonParseException("Pair " + baseCurrency + " / " + USD_CODE + " is absent in response: " + ratesNode.findValue("base"), null);
        }

        return ratesNode;
    }

    private static class RatesMerger extends CollectionMerger<CurrencyExchangeRate> {
        private final Timestamp lastUpdated;
        private CurrencyExchange exchange;
        private Collection<String> changedCodes;


        public RatesMerger(Collection<CurrencyExchangeRate> persisted, Collection<CurrencyExchangeRate> updated) {
            super(persisted, updated);
            exchange = new CurrencyExchange();
            lastUpdated = new Timestamp(new Date().getTime());
            exchange.setEffectiveDate(lastUpdated);
            changedCodes = new ArrayList<>();
        }

        @Override
        protected Object getId(CurrencyExchangeRate cer, int index) {
            return cer.getCurrency().getId();
        }

        @Override
        protected boolean add(CurrencyExchangeRate updated) {
            addRate(updated, lastUpdated);
            changedCodes.add(updated.getCurrency().getCurrencyCode());
            return false;
                }

        @Override
        protected void update(CurrencyExchangeRate persistent, CurrencyExchangeRate updated) {
            addRate(updated, lastUpdated);
            if (persistent.getRate().compareTo(updated.getRate()) != 0) {
                changedCodes.add(persistent.getCurrency().getCurrencyCode());
            }
        }

        @Override
        protected boolean delete(CurrencyExchangeRate persistent) {
            CurrencyExchangeRate copy = new CurrencyExchangeRate(persistent.getId().getCurrency(), null);
            copy.setRate(persistent.getRate());
            addRate(copy, persistent.getLastUpdated());
            return false;
                }

        private void addRate(CurrencyExchangeRate rate, Timestamp lastUpdated) {
            rate.setRate(rate.getRate().stripTrailingZeros());
            rate.setLastUpdated(lastUpdated);
            rate.getId().setCurrencyExchange(exchange);
            exchange.getCurrencyExchangeRates().add(rate);
            }

        private CurrencyExchange getExchange() {
            return exchange;
        }

        private Collection<String> getChangedCodes() {
            return changedCodes;
    	}

        private Timestamp getLastUpdated() {
            return lastUpdated;
        }
    }

    private CurrencyExchange find(Long id) {
        CurrencyExchange currencyExchange = (id == null) ? null : em.find(CurrencyExchange.class, id);
        if (currencyExchange == null) {
            throw new EntityNotFoundException(CurrencyExchange.class.getSimpleName() + " with id=" + id + " not found");
        }
        PersistenceUtils.initializeCollection(currencyExchange.getCurrencyExchangeRates());
        return currencyExchange;
    }

    @Override
    @Restrict(restriction = "CurrencyExchange.view")
    public CurrencyExchange get(Long currencyExchangeId) {
        return (currencyExchangeId == null) ? findLast() : find(currencyExchangeId);
    }
}
