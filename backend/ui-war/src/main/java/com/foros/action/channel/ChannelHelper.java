package com.foros.action.channel;

import com.foros.model.Country;
import com.foros.model.Identifiable;
import com.foros.model.LocalizableName;
import com.foros.model.account.Account;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.Channel;
import com.foros.session.EntityTO;
import com.foros.session.ServiceLocator;
import com.foros.session.account.AccountService;
import com.foros.session.admin.CurrencyConverter;
import com.foros.session.admin.categoryChannel.CategoryChannelService;
import com.foros.util.CurrencyExchangeRateUtil;
import com.foros.util.LocalizableNameUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChannelHelper {

    private ChannelHelper() {
    }

    public static List<List<EntityTO>> populateCategories(Channel channel, CategoryChannelService categoryChannelService) {
        List<CategoryChannel> categories = categoryChannelService.getCategories(channel.getId());
        if (categories == null || categories.isEmpty()) {
            return null;
        }

        List<List<EntityTO>> populatedCategories = new ArrayList<List<EntityTO>>(categories.size());
        for (CategoryChannel c : categories) {
            populatedCategories.add(categoryChannelService.getChannelAncestorsChain(c.getId(), true));
        }

        Collections.sort(populatedCategories, new Comparator<List<EntityTO>>() {
            @Override
            public int compare(List<EntityTO> o1, List<EntityTO> o2) {
                int o1pos = 0;
                int o1size = o1.size();

                int o2pos = 0;
                int o2size = o2.size();

                for (; o1pos < o1size && o2pos < o2size; o1pos++, o2pos++) {
                    LocalizableName name1 = o1.get(o1pos).getLocalizableName();
                    LocalizableName name2 = o2.get(o2pos).getLocalizableName();

                    if (name1 == null && name2 == null) {
                        return 0;
                    }

                    if (name1 == null) {
                        return -1;
                    }

                    if (name2 == null) {
                        return 1;
                    }

                    Comparator<LocalizableName> nameComparator = LocalizableNameUtil.getComparator();

                    int compareResult = nameComparator.compare(name1, name2);

                    if (compareResult != 0) {
                        return compareResult;
                    }
                }

                if (o1pos == o1size && o2pos == o2size) {
                    return 0;
                }

                if (o1pos == o1size) {
                    return -1;
                }

                return 1;
            }
        });
        return populatedCategories;
    }

    public static CurrencyConverter getCurrencyConverterForStats(Channel channel) {
        AccountService accountService = ServiceLocator.getInstance().lookup(AccountService.class);
        Account currentUserAccount = accountService.getMyAccount();

        return getCurrencyConverterForStats(channel, currentUserAccount);
    }

    public static CurrencyConverter getCurrencyConverterForStats(Channel channel, Account currentUserAccount) {
        if (!isPropertyEmpty(channel.getAccount())) {
            return CurrencyExchangeRateUtil.getCurrencyExchangeRate(channel.getAccount().getCurrency().getId());
        }

        if (!isPropertyEmpty(channel.getCountry()) && !isPropertyEmpty(channel.getCountry().getCurrency())) {
            return CurrencyExchangeRateUtil.getCurrencyExchangeRate(channel.getCountry().getCurrency().getId());
        }

        return CurrencyExchangeRateUtil.getCurrencyExchangeRate(currentUserAccount);
    }

    private static boolean isPropertyEmpty(Identifiable obj) {
        return obj == null || obj.getId() == null;
    }

    private static boolean isPropertyEmpty(Country country) {
        return country == null || country.getCountryCode() == null;
    }

}
