package com.foros.util;

import com.foros.cache.NamedCO;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.comparator.SimpleResolveComparator;
import com.foros.util.messages.MessageProvider;

import java.util.Comparator;
import java.util.Currency;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vladimir
 */
public class CurrencyHelper {
    private static final Logger logger = Logger.getLogger(CurrencyHelper.class.getName());

    private CurrencyHelper() {}

    private static Currency getCurrency(String currencyCode) {
        try {
            return Currency.getInstance(currencyCode);
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, "Can't create an Currency(" + currencyCode + ")", ex);
            throw ex;
        }
    }

    public static int getCurrencyFractionDigits(String currencyCode) {
        Currency currency = getCurrency(currencyCode);
        return currency.getDefaultFractionDigits();
    }

    public static String getCurrencySymbol(String currencyCode) {
        Currency currency = getCurrency(currencyCode);
        return currency.getSymbol(CurrentUserSettingsHolder.getLocale());
    }

    /**
     * Create and return comparator for currency by resolved from resources names
     *
     * @param provider message provider
     * @return comparator for currencies
     */
    private static Comparator<NamedCO<Long>> getCurrencyComparator(MessageProvider provider) {
        return new SimpleResolveComparator<Long>(getResolver(provider));
    }

    /**
     * Create and return comparator for currency by resolved from resources names
     *
     * @return comparator for currencies
     */
    public static Comparator<NamedCO<Long>> getCurrencyComparator() {
        return getCurrencyComparator(MessageProvider.createMessageProviderAdapter());
    }

    public static Resolver getResolver(MessageProvider provider) {
        return new FormatResolver(provider, "global.currency.{0}.name");
    }

    public static String resolveCurrencyName(MessageProvider provider, String code) {
        return getResolver(provider).resolve(code);
    }

    public static String resolveCurrencyName(String code) {
        return resolveCurrencyName(MessageProvider.createMessageProviderAdapter(), code);
    }
}
