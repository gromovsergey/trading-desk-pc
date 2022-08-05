package com.foros.session.channel;

import com.foros.model.channel.Channel;
import com.foros.model.channel.KeywordTriggersSource;
import com.foros.model.channel.UrlTriggersSource;
import com.foros.model.channel.trigger.KeywordTrigger;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.model.channel.trigger.UrlTrigger;
import com.foros.reporting.tools.query.parameters.usertype.PostgreTriggerOfChannelUserType;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.util.PersistenceUtils;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.hibernate.Session;

public class ChannelTriggersHibernateHandler {

    private Map<Channel, Timestamp> channels = new HashMap<>();
    private Session session;
    private LoggingJdbcTemplate jdbcTemplate;

    public void initialize(Session session, LoggingJdbcTemplate loggingJdbcTemplate) {
        if (this.session == null) {
            this.session = session;
        }
        if (this.jdbcTemplate == null) {
            this.jdbcTemplate = loggingJdbcTemplate;
        }
    }

    private boolean isInitialized() {
        return session != null;
    }

    public void addToBulkTriggersUpdate(Channel channel) {
        channels.put(channel, channel.getVersion());
    }

    public void updateChannels() {

        if (!isInitialized()) {
            return;
        }

        if (channels.isEmpty()) {
            return;
        }

        try {
            for (Entry<Channel, Timestamp> entry : channels.entrySet()) {
                if (entry.getKey().getVersion().equals(entry.getValue())) {
                    PersistenceUtils.performHibernateLock(session, entry.getKey());
                }
            }

            jdbcTemplate
                    .withAuthContext()
                    .execute("select trigger.bulk_update_channel_triggers(?)",
                        jdbcTemplate.createArray("trigger_of_channel", generateTriggersOfChannels(channels.keySet()))
            );
        } finally {
            channels.clear();
            session = null;
        }
    }

    private Set<PostgreTriggerOfChannelUserType> generateTriggersOfChannels(Set<Channel> channels) {
        Set<PostgreTriggerOfChannelUserType> triggersOfChannels = new HashSet<PostgreTriggerOfChannelUserType>();
        for (Channel channel : channels) {
            Collection<KeywordTrigger> keywordTriggers = getKeywords(channel);

            Collection<UrlTrigger> urlTriggers = getUrls(channel);
            urlTriggers = UrlTrigger.unique(urlTriggers);
            UrlTrigger.calcMasked(urlTriggers);

            if (keywordTriggers.isEmpty() && urlTriggers.isEmpty()) {
                triggersOfChannels.add(new PostgreTriggerOfChannelUserType(
                    channel.getId(),
                    channel.getChannelType(),
                    channel.getCountry() != null ? channel.getCountry().getCountryCode() : "",
                    null,
                    null,
                    null,
                    null,
                    false,
                    false));
            }

            for (KeywordTrigger keywordTrigger : keywordTriggers) {
                String normalized = keywordTrigger.getQANormalized();
                if (normalized.length() > 0) {
                    triggersOfChannels.add(new PostgreTriggerOfChannelUserType(
                        channel.getId(),
                        channel.getChannelType(),
                        channel.getCountry() != null ? channel.getCountry().getCountryCode() : "",
                        keywordTrigger.getTriggerType().getLetter(),
                        keywordTrigger.getOriginal(),
                        normalized,
                        null,
                        null,
                        keywordTrigger.isNegative()));
                }
            }

            for (UrlTrigger urlTrigger : urlTriggers) {
                String normalized = urlTrigger.getQANormalized();
                if (normalized.length() > 0) {
                    triggersOfChannels.add(new PostgreTriggerOfChannelUserType(
                        channel.getId(),
                        channel.getChannelType(),
                        channel.getCountry() != null ? channel.getCountry().getCountryCode() : "",
                        TriggerType.URL.getLetter(),
                        urlTrigger.getOriginal(),
                        normalized,
                        urlTrigger.getGroup(),
                        urlTrigger.isMasked(),
                        urlTrigger.isNegative()));
                }
            }
        }
        return triggersOfChannels;
    }

    private Collection<KeywordTrigger> getKeywords(Channel channel) {
        if (channel instanceof KeywordTriggersSource) {
            return ((KeywordTriggersSource) channel).getAllKeywordTriggers();
        }
        return Collections.emptyList();
    }

    private Collection<UrlTrigger> getUrls(Channel channel) {
        if (channel instanceof UrlTriggersSource) {
            return ((UrlTriggersSource) channel).getAllUrlTriggers();
        }
        return Collections.emptyList();
    }
}
