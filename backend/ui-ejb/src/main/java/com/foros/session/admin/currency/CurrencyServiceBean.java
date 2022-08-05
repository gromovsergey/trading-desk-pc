package com.foros.session.admin.currency;

import com.foros.cache.NamedCO;
import com.foros.model.account.Account;
import com.foros.model.admin.GlobalParam;
import com.foros.model.currency.Currency;
import com.foros.model.currency.CurrencyExchange;
import com.foros.model.currency.CurrencyExchangeRate;
import com.foros.model.currency.Source;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.principal.SecurityContext;
import com.foros.session.BusinessException;
import com.foros.session.BusinessServiceBean;
import com.foros.session.admin.currencyExchange.CurrencyExchangeService;
import com.foros.session.admin.globalParams.GlobalParamsService;
import com.foros.session.db.DBConstraint;
import com.foros.util.VersionCollisionException;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

@Stateless(name = "CurrencyService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class CurrencyServiceBean extends BusinessServiceBean<Currency> implements CurrencyService {

    @EJB
    private CurrencyExchangeService exchangeSvc;

    @EJB
    private GlobalParamsService globalParamsService;

    public CurrencyServiceBean() {
        super(Currency.class);
    }

    @Override
    public Currency getDefault() {
        Currency res = em.find(Currency.class, 1L);
        return res != null ? res : new Currency(1L);
    }

    @Override
    @Restrict(restriction = "Currency.create", parameters = "#entity")
    @Validate(validation = "Currency.create", parameters = "#entity")
    public void create(Currency entity) {
        prepare(entity);
        try {
            super.create(entity);
        } catch (RuntimeException e) {
            if (DBConstraint.CURRENCY_CODE.match(e)) {
                entity.setId(getCurrencyByCode(entity.getCurrencyCode()).getId());
                throw new VersionCollisionException(e);
            }
            throw e;
        }
        exchangeSvc.createCurrency(entity);
    }

    private void prepare(Currency entity) {
        java.util.Currency javaCurrency = java.util.Currency.getInstance(entity.getCurrencyCode());
        entity.setFractionDigits(javaCurrency.getDefaultFractionDigits());
        if (!isSourceEditable()) {
            entity.setSource(Source.MANUAL);
            entity.unregisterChange("source");
        }
        if (entity.getSource() == Source.FEED) {
            try {
                Map<Currency, BigDecimal> rates = exchangeSvc.fetchFromFeed(Arrays.asList(entity));
                BigDecimal rate = rates.get(entity);
                if (rate == null) {
                    throw new BusinessException("source", "rate is absent");
                }
                entity.setRate(rate);
            } catch (Exception e) {
                throw new BusinessException("source", "rate is absent");
            }
        }
    }

    @Override
    @Restrict(restriction = "Currency.update", parameters = "#entity")
    @Validate(validation = "Currency.update", parameters = "#entity")
    public Currency update(Currency entity) {
        prepare(entity);
        Currency updated = super.update(entity);
        exchangeSvc.updateCurrency(entity);
        return updated;
    }

    @Override
    public Currency findById(Long id) {
        Currency currency = super.findById(id);
        CurrencyExchange exchange = exchangeSvc.findLast();

        if (currency.equals(getDefault())) {
            currency.setRate(BigDecimal.ONE);
            currency.setLastUpdated(exchange.getEffectiveDate());
            currency.setEffectiveDate(exchange.getEffectiveDate());
            return currency;
        }

        for (CurrencyExchangeRate rate : exchange.getCurrencyExchangeRates()) {
            if (rate.getCurrency().equals(currency)) {
                currency.setRate(rate.getRate());
                currency.setLastUpdated(rate.getLastUpdated());
                currency.setEffectiveDate(exchange.getEffectiveDate());
                return currency;
            }
        }

        throw new BusinessException("Can't find exchange rate for currency[" + currency.getId() + "]");
    }

    @Override
    @Restrict(restriction = "Currency.view", parameters = "#id")
    public Currency view(Long id) {
        return findById(id);
    }

    @Override
    @Restrict(restriction = "Currency.view")
    public List<Currency> findAll() {
        ArrayList<Currency> currencyList = new ArrayList<Currency>();

        CurrencyExchange exchange = exchangeSvc.findLast();
        for (CurrencyExchangeRate rate : exchange.getCurrencyExchangeRates()) {
            Currency currency = rate.getCurrency();
            currency.setRate(rate.getRate());
            currency.setLastUpdated(rate.getLastUpdated());
            currencyList.add(currency);
        }

        return currencyList;
    }

    @Override
    public Collection<NamedCO<Long>> getIndex() {
        return em.createQuery("SELECT NEW com.foros.cache.NamedCO (c.id, c.currencyCode) FROM Currency c ")
                .setHint("org.hibernate.cacheable", "true").getResultList();
    }

    @Override
    public Collection<Currency> getAutomaticUpdatableCurrencies() {
        return em.createNamedQuery("Currency.findUpdatable").setParameter("source", Source.FEED.getLetter()).getResultList();
    }

    @Override
    public Currency getCurrencyByCode(String code) {
        Query query = em.createNamedQuery("Currency.getByCode");
        query.setParameter("code", code);
        List res = query.getResultList();
        return (Currency)res.get(0);
    }

    @Override
    public Currency getMyCurrency() {
        Long myAccountId = SecurityContext.getPrincipal().getAccountId();
        Account myAccount = em.find(Account.class, myAccountId);

        return myAccount.getCurrency();
    }

    @Override
    public Currency getAccountCurrency(Long accountId) {
        Account account = em.find(Account.class, accountId);
        if (account == null) {
            throw new EntityNotFoundException(Account.class + " with id=" + accountId + " not found");
        }

        return account.getCurrency();
    }

    @Override
    public boolean isSourceEditable() {
        GlobalParam param = globalParamsService.find(GlobalParamsService.CURRENCY_EXCHANGE_RATE_UPDATE);
        return Source.FEED.equals(Source.valueOf(param.getValue()));
    }

}
