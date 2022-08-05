package com.foros.session.channel;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.ApproveStatus;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.channel.Channel;
import com.foros.model.channel.trigger.ChannelTrigger;
import com.foros.persistence.hibernate.EvictCacheHibernateInterceptor;
import com.foros.persistence.hibernate.HibernateInterceptor;
import com.foros.reporting.tools.query.parameters.usertype.PostgreTriggerIdQaUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.channel.triggerQA.TriggerQATO;
import com.foros.util.PersistenceUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

@Stateless(name = "TriggerService")
@Interceptors({ RestrictionInterceptor.class })
public class TriggerServiceBean implements TriggerService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private ConfigService config;

    @Override
    public void updateCCGKeywordsStatus(Long ccgId, Collection<Long> ids, String status) {
        jdbcTemplate.execute(
                "select trigger.update_ccg_keyword_status(?::int,?::int[],?::character)",
                ccgId,
                jdbcTemplate.createArray("int", ids),
                status
        );
    }

    @Override
    public void addToBulkTriggersUpdate(Channel channel) {
        Session session = PersistenceUtils.getHibernateSession(em);
        HibernateInterceptor hi = PersistenceUtils.getInterceptor(session);
        EvictCacheHibernateInterceptor evictCacheInterceptor = hi.getEvictCacheInterceptor();
        evictCacheInterceptor.touchTagByEntity(channel);
        ChannelTriggersHibernateHandler handler = hi.getChannelTriggersHibernateInterceptor();
        handler.initialize(session, jdbcTemplate);
        handler.addToBulkTriggersUpdate(channel);
    }

    @Override
    public void addToBulkLinkCCGKeywords(CCGKeyword newKeyword) {
        Session session = PersistenceUtils.getHibernateSession(em);
        HibernateInterceptor hi = PersistenceUtils.getInterceptor(session);
        KeywordChannelsHibernateHandler handler = hi.getKeywordChannelsHibernateInterceptor();
        handler.initialize(session);
        handler.addToBulkLinkCCGKeywords(newKeyword);
    }

    @Override
    public void updateTriggers(List<TriggerQATO> triggers) {
        jdbcTemplate
                .withAuthContext()
                .execute(
                "select trigger.update_trigger_statuses(?)",
                jdbcTemplate.createArray("trigger_id_status", generateTriggerIdQAs(triggers))
        );
        jdbcTemplate.scheduleEviction();
    }

    private List<PostgreTriggerIdQaUserType> generateTriggerIdQAs(List<TriggerQATO> triggers) {
        List<PostgreTriggerIdQaUserType> res = new ArrayList<>();
        for (TriggerQATO triggerQATO : triggers) {
            res.add(new PostgreTriggerIdQaUserType(triggerQATO.getId().intValue(), triggerQATO.getQaStatus().getLetter()));
        }
        return res;
    }

    @Override
    public void forceBulkTriggersUpdate() {
        em.flush();
        HibernateInterceptor hi = PersistenceUtils.getInterceptor(em);
        ChannelTriggersHibernateHandler handler = hi.getChannelTriggersHibernateInterceptor();
        handler.updateChannels();
    }

    @Override
    public Set<ChannelTrigger> getTriggersByChannelId(final Channel channel) {
        final Set<ChannelTrigger> triggers = new LinkedHashSet<>();
        jdbcTemplate.query("select * from trigger.get_triggers_by_channel_id(?::bigint)",
            new Object[] {
                channel.getId()
            },
            new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    ChannelTrigger ct = new ChannelTrigger();
                    ct.setId(rs.getLong("channel_trigger_id"));
                    ct.setTriggerId(rs.getLong("trigger_id"));
                    ct.setChannel(channel);
                    ct.setTriggerType(String.valueOf(rs.getString("trigger_type")).charAt(0));
                    ct.setOriginalTrigger(rs.getString("original_trigger"));
                    ct.setNegative(rs.getBoolean("negative"));
                    ct.setQaStatus(ApproveStatus.valueOf(String.valueOf(rs.getString("qa_status")).charAt(0)));
                    triggers.add(ct);
                }
            });
        return triggers;
    }

    @Override
    public Map<Long, Set<ChannelTrigger>> getTriggersByChannelIds(final List<Long> ids, final boolean allowPartial) {
        final int maxTriggersCount = config.get(ConfigParameters.CHANNEL_CSV_EXPORT_MAX_TRIGGERS_COUNT);

        return jdbcTemplate.query("select * from trigger.get_triggers_by_channel_ids(?::bigint[], ?::integer)",
                new Object[]{
                        jdbcTemplate.createArray("bigint", ids),
                        maxTriggersCount + 1
                },
                new ResultSetExtractor<Map<Long, Set<ChannelTrigger>>>() {
                    @Override
                    public Map<Long, Set<ChannelTrigger>> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        Map<Long, Set<ChannelTrigger>> triggersMap = new LinkedHashMap<>(ids.size());
                        int triggersCount = 0;
                        Long channelId = null;
                        while (rs.next()) {
                            triggersCount++;
                            channelId = rs.getLong("channel_id");
                            Set<ChannelTrigger> triggers = triggersMap.get(channelId);
                            if (triggers == null) {
                                triggers = new HashSet<>();
                                triggersMap.put(channelId, triggers);
                            }
                            ChannelTrigger ct = new ChannelTrigger();
                            ct.setId(rs.getLong("channel_trigger_id"));
                            ct.setTriggerId(rs.getLong("trigger_id"));
                            ct.setTriggerType(String.valueOf(rs.getString("trigger_type")).charAt(0));
                            ct.setOriginalTrigger(rs.getString("original_trigger"));
                            ct.setNegative(rs.getBoolean("negative"));
                            ct.setQaStatus(ApproveStatus.valueOf(String.valueOf(rs.getString("qa_status")).charAt(0)));
                            triggers.add(ct);
                        }
                        Long stopOnChannel = null;
                        if (triggersCount > maxTriggersCount) {
                            if (allowPartial && triggersMap.size() > 1) {
                                triggersMap.remove(channelId);
                                stopOnChannel = channelId;
                            } else {
                                throw new TooManyTriggersException(maxTriggersCount);
                            }
                        }
                        for (Long id : ids) {
                            if (id.equals(stopOnChannel)) {
                                break;
                            }
                            Set<ChannelTrigger> triggers = triggersMap.get(id);
                            if (triggers == null) {
                                triggersMap.put(id, Collections.<ChannelTrigger>emptySet());
                            }
                        }
                        return triggersMap;
                    }
                }
        );
    }
}
