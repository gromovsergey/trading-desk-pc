package com.foros.session.quicksearch;

import com.foros.model.DisplayStatus;
import com.foros.model.account.Account;
import com.foros.model.campaign.Campaign;
import com.foros.model.channel.Channel;
import com.foros.model.quicksearch.QuickSearchResultItem;
import com.foros.model.quicksearch.Type;
import com.foros.model.site.Site;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.account.AccountRestrictions;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.site.PublisherEntityRestrictions;
import com.foros.util.ExceptionUtil;
import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;

@LocalBean
@Stateless(name = "QuickSearchService")
@Interceptors({RestrictionInterceptor.class})
public class QuickSearchService {

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AccountRestrictions accountRestrictions;

    @EJB
    private AdvertiserEntityRestrictions campaignRestrictions;

    @EJB
    private PublisherEntityRestrictions siteRestrictions;

    @EJB
    private AdvertisingChannelRestrictions channelRestrictions;

    @EJB
    LoggingJdbcTemplate jdbcTemplate;

    private final Pattern TOKENIZE = Pattern.compile("(\"[^\"]*\")|([^\"]*)");

    public QuickSearchService() {
    }

    @Restrict(restriction = "QuickSearch.search")
    public Map<Type, Collection<QuickSearchResultItem>> search(String name) {
        List<String> permittedGroups = getPermittedGroups();
        Map<Type, Collection<QuickSearchResultItem>> result = new LinkedHashMap<>();
        String normalized = normalize(name);
        if (normalized.length() > 1 && StringUtil.getBytesCount(normalized) <= 256) {
            try {
                SqlRowSet rs = jdbcTemplate.withAuthContext().queryForRowSet("select * from entityqueries.quick_search(?::varchar[], ?::varchar)",
                        new Object[]{jdbcTemplate.createArray("varchar", permittedGroups), normalized});
                while (rs.next()) {
                    String typeString = rs.getString("type_name");
                    Type type = Type.valueOf(typeString.toUpperCase());
                    Long id = rs.getLong("id");
                    String entityName = rs.getString("name");
                    DisplayStatus displayStatus = findDisplayStatus(type, rs.getLong("display_status_id"));

                    Collection<QuickSearchResultItem> group = result.get(type);
                    if (group == null) {
                        group = new ArrayList<>();
                        result.put(type, group);
                    }
                    group.add(new QuickSearchResultItem(type, id, entityName, displayStatus));
                }
            } catch (Exception e) {
                String rootMessage = ExceptionUtil.getRootMessage(e);
                if (!rootMessage.contains("DRG-50901: text query parser syntax error"))
                    throw e;
            }
        }
        return result;
    }

    private String normalize(String name) {
        int quoteCount = StringUtils.countMatches(name, "\"");
        String evenQuotes = name;
        if (quoteCount % 2 > 0) {
            int pos = name.lastIndexOf('"');
            evenQuotes = name.substring(0, pos) + name.substring(pos + 1);
        }
        Matcher m = TOKENIZE.matcher(evenQuotes);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String g = m.group();
            if (g.replaceAll("\"", "").trim().length() > 1) {
                sb.append(g.trim()).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private List<String> getPermittedGroups() {
        List<String> result = new ArrayList<>();
        if (accountRestrictions.canView(AccountRole.AGENCY)) {
            result.add(Type.AGENCY.toString().toLowerCase());
        }
        if (accountRestrictions.canView(AccountRole.ADVERTISER)) {
            result.add(Type.ADVERTISER.toString().toLowerCase());
        }
        if (accountRestrictions.canView(AccountRole.PUBLISHER)) {
            result.add(Type.PUBLISHER.toString().toLowerCase());
        }
        if (campaignRestrictions.canView()) {
            result.add(Type.CAMPAIGN.toString().toLowerCase());
        }
        if (siteRestrictions.canView()) {
            result.add(Type.SITE.toString().toLowerCase());
        }
        if (channelRestrictions.canView(AccountRole.ADVERTISER)) {
            result.add(Type.CHANNEL.toString().toLowerCase() + "_adv");
        }
        if (channelRestrictions.canView(AccountRole.CMP)) {
            result.add(Type.CHANNEL.toString().toLowerCase() + "_cmp");
        }
        if (channelRestrictions.canView(AccountRole.INTERNAL)) {
            result.add(Type.CHANNEL.toString().toLowerCase() + "_int");
        }

        return result;
    }

    private DisplayStatus findDisplayStatus(Type type, Long id) {
        switch (type) {
            case AGENCY:
            case ADVERTISER:
            case PUBLISHER:
                return Account.getDisplayStatus(id);
            case CAMPAIGN:
                return Campaign.getDisplayStatus(id);
            case CHANNEL:
                return Channel.getDisplayStatus(id);
            case SITE:
                return Site.getDisplayStatus(id);
            default:
                throw new RuntimeException("Unknown quick search result type!");
        }
    }

}
