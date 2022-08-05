package com.foros.session.channel;

import static com.foros.util.StringUtil.trimAndLower;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.session.ServiceLocator;
import com.foros.session.channel.service.KeywordChannelService;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Session;


public class KeywordChannelsHibernateHandler {

    private Set<CCGKeyword> newKeywords;
    private Session session;

    public void initialize(Session session) {
        if (this.session == null) {
            this.session = session;
        }
    }

    private void clear() {
        session = null;
        newKeywords = null;
    }

    public boolean isInitialized() {
        return session != null;
    }

    public void handle() {
        if (!isInitialized()) {
            return;
        }

        updateKeywords();

        clear();
    }

    private void updateKeywords() {
        if (newKeywords == null || newKeywords.isEmpty()) {
            return;
        }

        HashMap<KeywordKey, List<CCGKeyword>> temp = new HashMap<KeywordKey, List<CCGKeyword>>();
        for (CCGKeyword newKeyword : newKeywords) {
            String countryCode = newKeyword.getCreativeGroup().getCountry().getCountryCode();
            Long internalAccountId = newKeyword.getCreativeGroup().getAccount().getInternalAccount().getId();
            KeywordTriggerType triggerType = newKeyword.getTriggerType();
            KeywordKey key = new KeywordKey(countryCode, internalAccountId, triggerType);
            List<CCGKeyword> list = temp.get(key);
            if (list == null) {
                list = new LinkedList<CCGKeyword>();
                temp.put(key, list);
            }
            list.add(newKeyword);
        }

        for (Map.Entry<KeywordKey, List<CCGKeyword>> entry : temp.entrySet()) {
            linkToChannels(entry.getKey(), entry.getValue());
        }
        session.flush();
    }

    public void addToBulkLinkCCGKeywords(CCGKeyword newKeyword) {
        if (newKeywords == null) {
            newKeywords = new HashSet<CCGKeyword>();
        }
        newKeywords.add(newKeyword);
    }

    private void linkToChannels(KeywordKey key, Collection<CCGKeyword> newKeywords) {
        Set<String> modNewKeywords = new HashSet<String>();
        for (CCGKeyword newKeyword : newKeywords) {
            modNewKeywords.add(toPositive(newKeyword.getOriginalKeyword()));
        }
        KeywordChannelService keywordChannelService = ServiceLocator.getInstance().lookup(KeywordChannelService.class);
        Map<String, Long> map = keywordChannelService.findOrCreate(key.getInternalAccountId(), key.getCountryCode(), key.getTriggerType(), modNewKeywords);

        for (CCGKeyword newKeyword : newKeywords) {
            Long channelId = map.get(trimAndLower(toPositive(newKeyword.getOriginalKeyword())));
            newKeyword.setChannelId(channelId);
            session.merge(newKeyword);
        }
    }

    private String toPositive(String keyword) {
        return !keyword.startsWith("-") ? keyword : keyword.substring(1);
    }

    private static class KeywordKey {
        private String countryCode;
        private Long internalAccountId;
        private KeywordTriggerType triggerType;

        private KeywordKey(String countryCode, Long internalAccountId, KeywordTriggerType triggerType) {
            this.countryCode = countryCode;
            this.internalAccountId = internalAccountId;
            this.triggerType = triggerType;
        }

        private String getCountryCode() {
            return countryCode;
        }

        private Long getInternalAccountId() {
            return internalAccountId;
        }

        private KeywordTriggerType getTriggerType() {
            return triggerType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            KeywordKey that = (KeywordKey) o;

            if (!countryCode.equals(that.countryCode)) return false;
            if (!internalAccountId.equals(that.internalAccountId)) return false;
            if (triggerType != that.triggerType) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = countryCode.hashCode();
            result = 31 * result + internalAccountId.hashCode();
            result = 31 * result + triggerType.hashCode();
            return result;
        }
    }
}
