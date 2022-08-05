package app.programmatic.ui.site.service;

import app.programmatic.ui.common.view.IdName;
import app.programmatic.ui.common.model.OwnedStatusable;
import app.programmatic.ui.common.model.OwnedStatusableImpl;
import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.restriction.model.Restriction;
import app.programmatic.ui.restriction.service.RestrictionService;
import app.programmatic.ui.site.dao.model.Site;
import app.programmatic.ui.site.dao.model.SiteDisplayStatus;
import app.programmatic.ui.site.dao.model.SiteStat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class SiteServiceImpl implements SiteService {

    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private RestrictionService restrictionService;


    @Override
    public List<Site> findSitesByCountry(String countryCode, int limit) {
        return findSitesByCountryAndIds(countryCode, Collections.emptyList(), limit);
    }

    @Override
    public List<Site> findSitesByCountryAndIds(String countryCode, List<Long> ids, int limit) {
        String idsCondition;
        List<Object> params;
        if (ids.isEmpty()) {
            idsCondition = "  1 = 1 ";
            params = Arrays.asList( countryCode, limit);
        } else {
            idsCondition = "  s.site_id = any(?) ";
            Array idsArray = jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", ids.toArray()));
            params = Arrays.asList( idsArray, countryCode, limit);
        }

        return jdbcOperations.query("select s.site_id, s.name site_name, s.site_url, a.name account_name, s.display_status_id, stat.unique_users " +
                        "  from site s join account a on a.account_id=s.account_id " +
                        "  left join stat.siteuserstatstotal stat on stat.site_id=s.site_id " +
                        "  where " +
                        idsCondition +
                        "  and s.status = 'A' " +
                        "  and a.status = 'A' " +
                        "  and not (a.flags::int & x'1'::int)::bool " +
                        "  and a.country_code = ? " +
                        "  order by stat.unique_users desc nulls last " +
                        "  limit ?",
                params.toArray(),
                (ResultSet rs, int index) -> {
                    Site site = new Site();
                    site.setSiteId(rs.getLong("site_id"));
                    site.setName(rs.getString("site_name"));
                    site.setUrl("site_url");
                    site.setAccountName(rs.getString("account_name"));
                    site.setDisplayStatus(SiteDisplayStatus.valueOf(rs.getInt("display_status_id")).getMajorStatus());
                    site.setUniqueUsers(rs.getLong("unique_users"));

                    return site;
                }
        );
    }

    @Override
    public List<SiteStat> getStatsByLineItemId(Long lineItemId) {
        restrictionService.throwIfNotPermitted(Restriction.VIEW_CCG, findCcgId(lineItemId));

        return jdbcOperations.query("select * from statqueries.site_stats_for_line_item(?::int, ?::date, ?::date)",
                new Object[]{
                        lineItemId,
                        null,
                        null
                },
                (ResultSet rs, int rowNum) -> {
                    SiteStat result = new SiteStat();

                    result.setSiteId(rs.getLong("site_id"));
                    result.setName(rs.getString("site_name"));
                    result.setAccountName(rs.getString("account_name"));
                    result.setUrl(rs.getString("site_url"));
                    result.setDisplayStatus(SiteDisplayStatus.valueOf(rs.getInt("site_display_status_id")).getMajorStatus());
                    result.setImps(rs.getLong("imps"));
                    result.setClicks(rs.getLong("clicks"));
                    result.setCtr(rs.getBigDecimal("ctr").setScale(2, BigDecimal.ROUND_HALF_UP));
                    result.setUniqueUsers(rs.getLong("unique_users"));

                    return result;
                });
    }

    @Override
    public List<Long> filterActive(List<Long> ids) {
        if (ids.isEmpty()) {
            return ids;
        }

        Array idsArray = jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", ids.toArray()));
        return jdbcOperations.query("select site_id from site where status = 'A' and site_id = any(?)",
                new Object[] { idsArray },
                (ResultSet rs, int rowNum) -> rs.getLong("site_id"));
    }

    private Long findCcgId(Long lineItemId) {
        return jdbcOperations.queryForObject("select ccg_id from flightccg where flight_id = ?",
                new Object[] {lineItemId},
                Long.class);
    }

    @Override
    public OwnedStatusable findSiteUnchecked(Long siteId) {
        return jdbcOperations.queryForObject("select * from site where site_id = ?",
                new Object[]{siteId},
                (ResultSet rs, int index) -> {
                    OwnedStatusableImpl result = new OwnedStatusableImpl();
                    result.setAccountId(rs.getLong("account_id"));
                    result.setMajorStatus(SiteDisplayStatus.valueOf(rs.getInt("display_status_id")).getMajorStatus());
                    return result;
                });
    }

    @Override
    @Restrict(restriction = "site.viewSiteList")
    public List<IdName> findSitesByAccountId(Long accountId) {
        return jdbcOperations.query("select site_id, name from site where account_id = ? and status != 'D' order by upper(name)",
                new Object[]{accountId},
                (ResultSet rs, int rowNum) -> new IdName(rs.getLong("site_id"), rs.getString("name")));
    }

    @Override
    @Restrict(restriction = "site.viewTags")
    public List<IdName> findTagsBySiteId(Long siteId) {
        return jdbcOperations.query("select tag_id, name from tags where site_id = ? and status != 'D' order by upper(name)",
                new Object[]{siteId},
                (ResultSet rs, int rowNum) -> new IdName(rs.getLong("tag_id"), rs.getString("name")));
    }
}
