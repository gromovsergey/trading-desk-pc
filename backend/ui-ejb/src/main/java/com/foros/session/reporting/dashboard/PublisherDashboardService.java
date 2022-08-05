package com.foros.session.reporting.dashboard;

import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.security.UserService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.springframework.jdbc.core.RowMapper;

@LocalBean
@Stateless(name = "PublisherDashboardService")
public class PublisherDashboardService {

    @EJB
    private UserService userService;

    @EJB
    private LoggingJdbcTemplate loggingJdbcTemplate;

    public List<SiteDashboardTO> generateSiteDashboard(AccountDashboardParameters parameters) {
        return loggingJdbcTemplate.withAuthContext().query("select * from  report.pub_site_dashboard(?::date, ?::date, ?::integer, ?::boolean, ?::boolean)",
            new Object[] {
                    parameters.getDateRange().getBegin(),
                    parameters.getDateRange().getEnd(),
                    parameters.getAccountId(),
                    userService.getMyUser().isDeletedObjectsVisible(),
                    parameters.isWithActivityOnly()
            }, new RowMapper<SiteDashboardTO>() {
                @Override
                public SiteDashboardTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new SiteDashboardTO.Builder()
                        .siteId(rs.getLong("site_id"))
                        .siteName(rs.getString("site_name"))
                        .siteDisplayStatusId(rs.getInt("site_disp_status_id"))
                        .siteUrl(rs.getString("site_url"))
                        .tagExist(rs.getBoolean("tag_exist"))
                        .creditedImps(rs.getLong("pub_credited_imps"))
                        .imps(rs.getLong("imps"))
                        .ecpm(rs.getBigDecimal("ecpm"))
                        .revenue(rs.getBigDecimal("revenue"))
                        .requests(rs.getLong("requests"))
                        .clicks(rs.getLong("clicks"))
                        .ctr(rs.getBigDecimal("ctr"))
                        .creativesToApprove(rs.getInt("pending_creatives_count"))
                        .build();
                }
            });

    }

    public List<TagDashboardTO> generateTagDashboard(SiteDashboardParameters parameters) {

        return loggingJdbcTemplate.withAuthContext().query("select * from report.pub_tag_dashboard(?::date, ?::date, ?::integer, ?::boolean, ?::boolean)",
            new Object[] {
                    parameters.getDateRange().getBegin(),
                    parameters.getDateRange().getEnd(),
                    parameters.getSiteId(),
                    userService.getMyUser().isDeletedObjectsVisible(),
                    parameters.isWithActivityOnly()

            }, new RowMapper<TagDashboardTO>() {

                @Override
                public TagDashboardTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new TagDashboardTO.Builder()
                        .tagId(rs.getLong("tag_id"))
                        .tagName(rs.getString("tag_name"))
                        .status(rs.getString("tag_status").charAt(0))
                        .tagSizeName(rs.getString("tag_size_name"))
                        .creditedImps(rs.getLong("pub_credited_imps"))
                        .imps(rs.getLong("imps"))
                        .ecpm(rs.getBigDecimal("ecpm"))
                        .revenue(rs.getBigDecimal("revenue"))
                        .requests(rs.getLong("requests"))
                        .clicks(rs.getLong("clicks"))
                        .ctr(rs.getBigDecimal("ctr"))
                        .build();
                }
            });
    }
}
