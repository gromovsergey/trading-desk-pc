package com.foros.session.channel.service;

import com.foros.cache.generic.CacheProviderService;
import com.foros.cache.generic.CacheRegion;
import com.foros.cache.generic.CreateValueCallback;
import com.foros.cache.generic.EntityIdTag;
import com.foros.model.channel.Channel;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LoggingJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "ChannelTriggersStatsService")
@Interceptors({ RestrictionInterceptor.class })
public class ChannelTriggersStatsServiceBean implements ChannelTriggersStatsService {

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private CacheProviderService cacheProviderService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Override
    @Restrict(restriction = "Channel.view", parameters = "find('TriggersChannel', #channelId)")
    public ChannelTriggersTotalsTO getTriggersTotals(Long channelId) {
        final ChannelTriggersTotalsTO totals = new ChannelTriggersTotalsTO();
        jdbcTemplate.query(
            "select negative, trigger_type, qa_status, triggers_count from statqueries.triggers_totals(?::bigint)",
            new Object[] { channelId },
            new RowMapper<ChannelTriggersTotalsTO>() {
                @Override
                public ChannelTriggersTotalsTO mapRow(ResultSet rs, int rowNum) throws SQLException {

                    boolean negative = rs.getBoolean(1);
                    char type = rs.getString(2).charAt(0);
                    char qaStatus = rs.getString(3).charAt(0);
                    int count = rs.getInt(4);

                    TotalByTriggerTypeTO totalByType;
                    switch (type) {
                    case 'P':
                        totalByType = totals.getPageKeywords();
                        break;
                    case 'S':
                        totalByType = totals.getSearchKeywords();
                        break;
                    case 'R':
                        totalByType = totals.getUrlKeywords();
                        break;
                    default:
                        totalByType = totals.getUrls();

                        }
                    if (!negative) {
                        switch (qaStatus) {
                        case 'A':
                            totalByType.addApproved(count);
                            break;
                        case 'D':
                            totalByType.addDeclined(count);
                            break;
                        default:
                            totalByType.addPending(count);
                        }
                    } else {
                        totalByType.addNegative(count);
                    }
                    return null;
                    }
                }
            );
        return totals;
    }

    @Override
    @Restrict(restriction = "Channel.view", parameters = "find('TriggersChannel', #channelId)")
    public List<TriggerStatsTO> getTriggers(final Long channelId, TriggersFilter filter) {
        CacheRegion region = cacheProviderService.getCache().getRegion("statqueries.triggers");
        Object[] parameters = {
                channelId,
                filter.getTriggerType().getLetter(),
                filter.getQaStatus(),
                filter.getSortKey(),
                filter.getSortOrder(),
                filter.getPageSize(),
                filter.getPage()
        };
        Collection<?> tags = EntityIdTag.create(em, Channel.class, channelId).asCollection();
        return region.get(parameters, tags, new CreateValueCallback<List<TriggerStatsTO>, Object[]>() {
            @Override
            public List<TriggerStatsTO> create(Object[] parameters, Collection<?> tags) {
                return jdbcTemplate.query("select original_trigger, hits, impressions, clicks, ctr from statqueries.triggers(?, ?, ?, ?, ?, ?, ?)",
                    parameters,
                    new int[] {
                            Types.BIGINT,
                            Types.CHAR,
                            Types.CHAR,
                            Types.VARCHAR,
                            Types.VARCHAR,
                            Types.INTEGER,
                            Types.INTEGER
                    },
                    new RowMapper<TriggerStatsTO>() {
                        @Override
                        public TriggerStatsTO mapRow(ResultSet rs, int i) throws SQLException {
                            return new TriggerStatsTO(
                                rs.getString(1),
                                rs.getBigDecimal(2),
                                rs.getBigDecimal(3),
                                rs.getBigDecimal(4),
                                rs.getBigDecimal(5)
                            );
                        }
                    });
            }
        });

    }
}
