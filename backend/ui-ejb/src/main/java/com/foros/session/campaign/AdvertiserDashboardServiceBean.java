package com.foros.session.campaign;

import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.reporting.dashboard.AccountDashboardParameters;
import com.foros.session.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "AdvertiserDashboardService")
public class AdvertiserDashboardServiceBean implements AdvertiserDashboardService {

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    protected UserService userService;

    @Override
    public List<AdvertiserDashboardTO> getAdvertiserDashboardStats(final AccountDashboardParameters parameters) {
        return jdbcTemplate.withAuthContext()
                .query("select * from statqueries.advertiser_dashboard(?::date, ?::date, ?::int, ?, ?)",
                new Object[]{
                        parameters.getDateRange().getBegin(),
                        parameters.getDateRange().getEnd(),
                        parameters.getAccountId(),
                        !parameters.isWithActivityOnly(),
                        userService.getMyUser().isDeletedObjectsVisible()
                },
                new CommonCampaignStatsMapper<AdvertiserDashboardTO>() {
                    @Override
                    public AdvertiserDashboardTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        AdvertiserDashboardTO result = fillCommonPart(new AdvertiserDashboardTO(), rs);
                        fillUniquePart(result, rs);
                        fillPendingPart(result, rs);

                        result.setAdvertiserId(rs.getLong("account_id"));
                        result.setAdvertiserName(rs.getString("name"));
                        result.setAdvertiserDisplayStatusId(rs.getInt("display_status_id"));

                        return result;
                    }
                });
    }

    @Override
    public DashboardTO getAdvertiserDashboardTotal(final AccountDashboardParameters parameters) {
        return jdbcTemplate.withAuthContext().queryForObject("select * from statqueries.advertiser_dashboard_total(?::date, ?::date, ?::int, ?)",
                new Object[]{
                        parameters.getDateRange().getBegin(),
                        parameters.getDateRange().getEnd(),
                        parameters.getAccountId(),
                        userService.getMyUser().isDeletedObjectsVisible()
                },
                new CommonCampaignStatsMapper<DashboardTO>() {
                    @Override
                    public DashboardTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return fillCommonPart(new DashboardTO(), rs);
                    }
                });
    }

    @Override
    public List<CampaignDashboardTO> getCampaignDashboardStats(final AccountDashboardParameters parameters) {
        return jdbcTemplate.query("select * from statqueries.campaign_dashboard(?::date, ?::date, ?::int, ?, ?)",
                new Object[]{
                        parameters.getDateRange().getBegin(),
                        parameters.getDateRange().getEnd(),
                        parameters.getAccountId(),
                        !parameters.isWithActivityOnly(),
                        userService.getMyUser().isDeletedObjectsVisible()
                },
                new CommonCampaignStatsMapper<CampaignDashboardTO>() {
                    @Override
                    public CampaignDashboardTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CampaignDashboardTO result = fillCommonPart(new CampaignDashboardTO(), rs);
                        fillUniquePart(result, rs);

                        result.setCampaignId(rs.getLong("campaign_id"));
                        result.setCampaignName(rs.getString("name"));
                        result.setCampaignDisplayStatusId(rs.getInt("display_status_id"));

                        return result;
                    }
                });
    }

    @Override
    public DashboardTO getCampaignDashboardTotal(final AccountDashboardParameters parameters) {
        return jdbcTemplate.queryForObject("select * from statqueries.campaign_dashboard_total(?::date, ?::date, ?::int)",
                new Object[]{
                        parameters.getDateRange().getBegin(),
                        parameters.getDateRange().getEnd(),
                        parameters.getAccountId()
                },
                new CommonCampaignStatsMapper<DashboardTO>() {
                    @Override
                    public DashboardTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        DashboardTO result = fillCommonPart(new DashboardTO(), rs);
                        fillUniquePart(result, rs);
                        return fillPendingPart(result, rs);
                    }
                });
    }

    private abstract class CommonCampaignStatsMapper<T extends DashboardTO> implements RowMapper<T> {
        protected T fillCommonPart(T to, ResultSet rs) throws SQLException {
            to.setImps(rs.getLong("imps"));
            to.setClicks(rs.getLong("clicks"));
            to.setCtr(rs.getBigDecimal("ctr"));
            to.setCampaignCreditUsed(rs.getBigDecimal("campaign_credit_used"));
            to.setTargetingCost(rs.getBigDecimal("targeting_cost"));
            to.setInventoryCost(rs.getBigDecimal("inventory_cost"));
            to.setTotalValue(rs.getBigDecimal("total_value"));
            to.setTotalCost(rs.getBigDecimal("total_cost"));
            to.setEcpm(rs.getBigDecimal("ecpm"));
            to.setSelfServiceCost(rs.getBigDecimal("cost"));

            return to;
        }

        protected T fillUniquePart(T to, ResultSet rs) throws SQLException {
            to.setUniqueUsers(rs.getLong("unique_users_range"));
            return to;
        }

        protected T fillPendingPart(T to, ResultSet rs) throws SQLException {
            to.setCcgsPendingUser(rs.getLong("pending_ccgs_user"));
            to.setCreativesPendingUser(rs.getLong("pending_creatives_user"));
            to.setCreativesPendingForos(rs.getLong("pending_creatives_foros"));
            return to;
        }
    }
}
