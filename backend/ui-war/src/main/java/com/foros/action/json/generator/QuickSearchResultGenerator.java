package com.foros.action.json.generator;

import com.foros.action.json.action.QuickSearchResultContainer;
import com.foros.action.xml.generator.Generator;
import com.foros.model.quicksearch.QuickSearchResultItem;
import com.foros.model.quicksearch.Type;
import com.foros.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;

public class QuickSearchResultGenerator implements Generator<QuickSearchResultContainer> {

    private static final int MAX_RESULTS = 5;

    private static final Collection<Type> expandableTypes = Arrays.asList(Type.AGENCY, Type.CHANNEL, Type.PUBLISHER);

    @Override
    public String generate(QuickSearchResultContainer container) {
        Map<Type, Collection<QuickSearchResultItem>> results = container.getResults();
        StringBuilder json = new StringBuilder();
        json.append("[");
        Iterator<Type> groups = results.keySet().iterator();
        while (groups.hasNext()) {
            Type type = groups.next();
            json
                    .append("{")
                    .append(quoted("name")).append(":").append(quoted(StringUtil.getLocalizedString(type.getPluralKey())))
                    .append(",")
                    .append(quoted("items")).append(":[");

            Collection<QuickSearchResultItem> group = results.get(type);
            boolean hasMoreItems = group.size() > MAX_RESULTS;
            Iterator<QuickSearchResultItem> items = hasMoreItems ? new ArrayList<>(group).subList(0, MAX_RESULTS).iterator() : group.iterator();
            while (items.hasNext()) {
                QuickSearchResultItem item = items.next();
                json
                        .append("{")
                        .append(quoted("name"))
                        .append(":").append(quoted(StringEscapeUtils.escapeHtml(item.getName()).replace("\\", "\\\\"))).append(",")
                        .append(quoted("link"))
                        .append(":").append(quoted(generateLink(item))).append(",")
                        .append(quoted("color"))
                        .append(":").append(quoted(pickColor(item)))
                        .append("}");
                if (items.hasNext())
                    json.append(",");
            }
            if (hasMoreItems && expandableTypes.contains(type)) {
                json
                        .append(",")
                        .append("{")
                        .append(quoted("name"))
                        .append(":").append(quoted(StringUtil.getLocalizedString("form.showAll"))).append(",")
                        .append(quoted("link"))
                        .append(":").append(quoted(generateShowAllLink(type, container.getQuery())))
                        .append("}");
            }
            json.append("]}");
            if (groups.hasNext())
                json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    private String generateShowAllLink(Type type, String name) {
        try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder("/admin");
        switch (type) {
            case CHANNEL:
                sb.append("/channel/main.action?name=").append(name).append("#auto");
                break;
            case AGENCY:
                sb.append("/advertiser/account/search.action?accountRoleName=Agency&status=ALL_BUT_DELETED&testOption=EXCLUDE&name=").append(name);
                break;
            case PUBLISHER:
                sb.append("/publisher/account/search.action?status=ALL_BUT_DELETED&testOption=EXCLUDE&name=").append(name);
                break;
            default:
                throw new RuntimeException("unexpected quicksearch result type for 'show all' link!");
        }
        return sb.toString();
    }

    private String generateLink(QuickSearchResultItem item) {
        StringBuilder sb = new StringBuilder("/admin");
        switch (item.getType()) {
            case ADVERTISER:
            case AGENCY:
            case PUBLISHER:
                sb.append("/account/view.action");
                break;
            case CAMPAIGN:
                sb.append("/campaign/view.action");
                break;
            case CHANNEL:
                sb.append("/channel/view.action");
                break;
            case SITE:
                sb.append("/site/view.action");
                break;
            default:
                throw new RuntimeException("Unknown quick search item type!");
        }
        sb.append("?id=").append(item.getId());
        return sb.toString();
    }

    private String pickColor(QuickSearchResultItem item) {
        switch (item.getDisplayStatus().getMajor()) {
            case LIVE:
                return "green";
            case LIVE_NEED_ATT:
                return "amber";
            case NOT_LIVE:
                return "red";
            case INACTIVE:
            case DELETED:
                return "gray";
            default:
                throw new RuntimeException("Unknown display status! No color chosen!");
        }
    }

    private String quoted(String text) {
        return "\"" + text + "\"";
    }
}
