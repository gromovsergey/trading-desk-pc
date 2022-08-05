package com.foros.session.channel.service;

import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.security.AccountRole;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.util.CollectionUtils;
import com.foros.util.bean.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.EntityManager;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

public class ChannelUtils {

    private static final List<String> AVAILABLE_LANGUAGES = generateLanguages();


    public static Status getDefaultStatus() {
        return Status.ACTIVE;
    }

    public static ApproveStatus getDefaultQaStatus() {
        return ApproveStatus.HOLD;
    }

    public static ChannelVisibility getDefaultVisibility() {
        return ChannelVisibility.PRI;
    }
    
    public static Map<ChannelVisibility, ConditionOfVisibility> getChannelUsageRestrictions(AccountRole role) {
        Map<ChannelVisibility, ConditionOfVisibility> visibilities = new HashMap<ChannelVisibility, ConditionOfVisibility>();

        switch (role) {
            case INTERNAL:
                visibilities.put(ChannelVisibility.PUB, ConditionOfVisibility.ALL);
                visibilities.put(ChannelVisibility.PRI, ConditionOfVisibility.SAME_ACCOUNT);
                break;
            case ADVERTISER:
            case AGENCY:
                visibilities.put(ChannelVisibility.PUB, ConditionOfVisibility.ALL);
                visibilities.put(ChannelVisibility.PRI, ConditionOfVisibility.SAME_ACCOUNT);
                visibilities.put(ChannelVisibility.CMP, ConditionOfVisibility.ALL);
                break;
            case CMP:
                visibilities.put(ChannelVisibility.PUB, ConditionOfVisibility.ALL);
                visibilities.put(ChannelVisibility.PRI, ConditionOfVisibility.SAME_ACCOUNT);
                visibilities.put(ChannelVisibility.CMP, ConditionOfVisibility.SAME_ACCOUNT);
                break;
        }
        return visibilities;
    }

    public static ChannelVisibility[] getOwnChannelAllowedVisibilities(AccountRole role) {
        switch (role) {
            case INTERNAL:
                return new ChannelVisibility[] { ChannelVisibility.PRI, ChannelVisibility.PUB};
            case ADVERTISER:
            case AGENCY:
                return new ChannelVisibility[] { ChannelVisibility.PRI};
            case CMP:
                return new ChannelVisibility[] { ChannelVisibility.PRI, ChannelVisibility.PUB, ChannelVisibility.CMP};
            default:
                throw new RuntimeException("Wrong account role " + role);
        }
    }

    public enum ConditionOfVisibility {
        ALL,
        SAME_ACCOUNT
    }

    private static final Predicate ANY_CHANNEL = new ChannelPredicate();

    public static Predicate getAnyChannelPredicate() {
        return ANY_CHANNEL;
    }

    private static class ChannelPredicate implements Predicate {
        @Override
        public boolean evaluate(Object object) {
            return object instanceof Channel;
        }
    }

    public static boolean isVisible(Channel channel, Account owner) {
        AccountRole role = owner.getRole();
        Long accountId = owner.getId();

        ChannelVisibility linkedVisibility = channel.getVisibility();
        Map<ChannelVisibility, ConditionOfVisibility> visibilities = ChannelUtils.getChannelUsageRestrictions(role);
        ConditionOfVisibility conditionOfVisibility = visibilities.get(linkedVisibility);
        if (conditionOfVisibility != null) {
            switch (conditionOfVisibility) {
            case ALL:
                return true;
            case SAME_ACCOUNT:
                return channel.getAccount().getId().equals(accountId);
            }
        }
        return false;
    }

    public void prepareToProcess(EntityManager em, Operation<Channel> operation) {
        Channel channel = operation.getEntity();
        if (operation.getOperationType() == OperationType.UPDATE && channel.getId() != null) {
            Channel existing = em.find(Channel.class, channel.getId());
            channel.setAccount(existing.getAccount());
            if (!channel.isChanged("country")) {
                channel.setCountry(existing.getCountry());
                channel.unregisterChange("country");
            }
        }
    }

    public static int countTotal(String value) {
        if (StringUtils.isBlank(value)) {
            return 0;
        }

        Collection<String> values = new ArrayList<String>(Arrays.asList(value.split("[\r\n]")));
        CollectionUtils.filter(values, new Filter<String>() {
            @Override
            public boolean accept(String element) {
                if (!StringUtils.isBlank(element)) {
                    element = element.replaceAll("[ ,\t]", " ").trim();  // Check if the word consits of bracking characters only
                    return element.length() > 0;
                }
                return false;
            }
        });
        return values.size();
    }

    private static List<String> generateLanguages() {
        List<String> languages = Arrays.asList(Locale.getISOLanguages());
        Collections.sort(languages);
        return Collections.unmodifiableList(languages);
    }

    public static List<String> getAvailableLanguages() {
        return AVAILABLE_LANGUAGES;
    }
}
