package com.foros.session.admin.currency;

import com.foros.cache.NamedCO;
import com.foros.model.currency.Currency;
import com.foros.service.ByIdLocatorService;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

/**
 * @author alexey_koloskov
 */
@Local
public interface CurrencyService extends ByIdLocatorService<Currency> {
    void create(Currency entity);

    Currency update(Currency entity);

    void refresh(Long id);

    Currency findById(Long id);

    List<Currency> findAll();

    boolean isSourceEditable();

    public Currency getDefault();

    public Collection<NamedCO<Long>> getIndex();

    public Collection<Currency> getAutomaticUpdatableCurrencies();

    public Currency getMyCurrency();

    public Currency getAccountCurrency(Long accountId);

    public Currency getCurrencyByCode(String code);
}
