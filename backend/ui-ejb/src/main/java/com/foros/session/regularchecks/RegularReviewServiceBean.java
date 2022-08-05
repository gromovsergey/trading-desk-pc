package com.foros.session.regularchecks;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.RegularCheckable;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.Channel;
import com.foros.model.security.ActionType;
import com.foros.model.time.TimeSpan;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.query.PartialList;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.jdbc.core.RowMapper;


@Stateless(name = "RegularReviewService")
@Interceptors({RestrictionInterceptor.class,  ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class RegularReviewServiceBean implements RegularReviewService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @EJB
    private UserService userService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @Override
    public PartialList<ReviewEntityTO> searchChannelsForReview(String countryCode, int firstRow, int maxResults) {
        List<ReviewEntityTO> values = jdbcTemplate.withAuthContext().query(
                "select * from entityqueries.channels_for_regular_check(?::varchar,?::int,?::int)",
                new Object[]{
                        countryCode,
                        firstRow,
                        maxResults
                },
                new RowMapper<ReviewEntityTO>() {
                    @Override
                    public ReviewEntityTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ReviewEntityTO to = new ReviewEntityTO();
                        to.setEntityId(rs.getLong("entity_id"));
                        to.setEntityName(rs.getString("entity_name"));
                        to.setAccountName(rs.getString("account_name"));
                        to.setHoursAgo(rs.getDouble("hours_ago"));
                        to.setHourlyCheck(rs.getBoolean("hourly_check"));
                        return to;
                    }
                }
        );
        int count = jdbcTemplate.withAuthContext().queryForObject(
                "select * from  entityqueries.channels_for_regular_check_cnt(?::varchar)",
                Integer.class,
                countryCode
        );
        return new PartialList<ReviewEntityTO>(count, firstRow, values);
    }

    @Override
    public PartialList<ReviewEntityTO> searchCCGsForReview(String countryCode, int firstRow, int maxResults) {
        List<ReviewEntityTO> values = jdbcTemplate.withAuthContext().query(
                "select * from entityqueries.ccgs_for_regular_check(?::varchar,?::int,?::int)",
                new Object[]{
                        countryCode,
                        firstRow,
                        maxResults
                },
                new RowMapper<ReviewEntityTO>() {
                    @Override
                    public ReviewEntityTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ReviewEntityTO to = new ReviewEntityTO();
                        to.setEntityId(rs.getLong("entity_id"));
                        to.setEntityName(rs.getString("entity_name"));
                        to.setCampaignName(rs.getString("campaign_name"));
                        to.setAccountName(rs.getString("account_name"));
                        to.setAdvertiserName(rs.getString("advertiser_name"));
                        to.setHoursAgo(rs.getDouble("hours_ago"));
                        to.setHourlyCheck(rs.getBoolean("hourly_check"));
                        return to;
                    }
                }
        );
        int count = jdbcTemplate.withAuthContext().queryForObject(
                "select * from  entityqueries.ccgs_for_regular_check_cnt(?::varchar)",
                Integer.class,
                countryCode
        );
        return new PartialList<ReviewEntityTO>(count, firstRow, values);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction="AdvertisingChannel.updateChannelCheck", parameters="find('Channel', #channel.id)")
    @Validate(validation = "RegularReview.updateCheck", parameters = "#channel")
    public void updateChannelCheck(Channel channel) {
        Channel existing = em.find(channel.getClass(), channel.getId());
        prePersist(channel, existing.getAccount().getAccountType().getChannelCheckByNum(channel.getInterval()));
        channel.setAccount(existing.getAccount());
        channel = em.merge(channel);
        auditService.audit(channel, ActionType.UPDATE);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction="AdvertiserEntity.updateCCGCheck", parameters="find('CampaignCreativeGroup', #group.id)")
    @Validate(validation = "RegularReview.updateCheck", parameters = "#group")
    public void updateCCGCheck(CampaignCreativeGroup group) {
        CampaignCreativeGroup existing = em.find(group.getClass(), group.getId());
        prePersist(group, existing.getAccount().getAccountType().getCampaignCheckByNum(group.getInterval()));
        group.setCampaign(existing.getCampaign());
        group = em.merge(group);
        auditService.audit(group, ActionType.UPDATE);
    }

    private void prePersist(RegularCheckable entity, TimeSpan timeSpan) {
        entity.setCheckUser(userService.getMyUser());
        Date checkDate = new Date();
        entity.setLastCheckDate(checkDate);
        entity.setNextCheckDate(new Date(checkDate.getTime() + timeSpan.getValueInSeconds() * 1000));
    }
}
