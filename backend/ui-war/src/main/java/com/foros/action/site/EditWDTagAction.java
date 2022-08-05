package com.foros.action.site;

import com.foros.action.admin.option.CachedOptionValue;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.feed.Feed;
import com.foros.model.template.DiscoverTemplate;
import com.foros.restriction.annotation.Restrict;
import com.foros.util.StringUtil;
import com.foros.util.UITimestamp;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang.StringUtils;

public class EditWDTagAction extends EditWDTagActionBase implements BreadcrumbsSupport {

    private static final String LINE_SEPARATOR = "\r\n";

    private Breadcrumbs breadcrumbs = new Breadcrumbs();

    @ReadOnly
    @Restrict(restriction = "PublisherEntity.createWDTag", parameters = "find('Site', #target.entity.site.id)")
    public String create() {
        if (wdTag.getSite() == null || wdTag.getSite().getId() == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }

        wdTag.setSite(siteService.view(wdTag.getSite().getId()));
        wdTag.setWidth(DEFAULT_PREVIEW_WIDTH);
        wdTag.setHeight(DEFAULT_PREVIEW_HEIGHT);

        if (hasErrors() && wdTag.getTemplate() != null) {
            DiscoverTemplate template = (DiscoverTemplate) templateService.findById(wdTag.getTemplate().getId());
            if (template != null) {
                wdTag.setTemplate(template);
            }
        }

        processWDTag();

        breadcrumbs.add(new SiteBreadcrumbsElement(wdTag.getSite())).add("site.breadcrumbs.createWdTag");

        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "PublisherEntity.update")
    public String edit() {
        if (wdTag.getId() == null) {
            throw new EntityNotFoundException("Entity with id = null not found");
        }

        wdTag = wdTagService.view(wdTag.getId());

        setGroupStateValues(null);
        setOptionValues(null);

        processWDTag();

        breadcrumbs.add(new SiteBreadcrumbsElement(wdTag.getSite())).add(new WDTagBreadcrumbsElement(wdTag)).add(ActionBreadcrumbs.EDIT);

        return SUCCESS;
    }

    public String changeTemplate() {
        Long templateId = wdTag.getTemplate() == null ? null : wdTag.getTemplate().getId();

        if (templateId != null) {
            DiscoverTemplate template = (DiscoverTemplate) templateService.findById(templateId);
            wdTag.setTemplate(template);
        }

        return SUCCESS;
    }

    private void processWDTag() {
        setAccountId(wdTag.getAccount().getId());
        setOptedInUrls(convertUrls(wdTag.getOptedInFeeds()));
        setOptedOutUrls(convertUrls(wdTag.getOptedOutFeeds()));
    }

    protected void processCache(Collection<PopulatedWDTagOptionValue> previousTemplateOptionValues) {
        Map<Long, CachedOptionValue> cachedOptionValuesMap = new HashMap<Long, CachedOptionValue>(getCachedOptionValues().size());
        for (CachedOptionValue cachedOptionValue : getCachedOptionValues()) {
            cachedOptionValuesMap.put(cachedOptionValue.getOptionId(), cachedOptionValue);
        }

        for (PopulatedWDTagOptionValue wdTagOptionValue : previousTemplateOptionValues) {
            cachedOptionValuesMap.put(StringUtil.convertToLong(wdTagOptionValue.getOptionId()),
                    new CachedOptionValue(StringUtil.convertToLong(wdTagOptionValue.getOptionId()), wdTagOptionValue.getValue(),
                            new UITimestamp(wdTagOptionValue.getVersion())));
        }

        setCachedOptionValues(new LinkedList<CachedOptionValue>(cachedOptionValuesMap.values()));
    }

    private static String convertUrls(Set<Feed> feeds) {
        String[] urls = new String[feeds.size()];
        int i = 0;
        for (Feed feed : feeds) {
            urls[i] = feed.getUrl();
            i++;
        }
        Arrays.sort(urls);
        return StringUtils.join(urls, LINE_SEPARATOR) + (feeds.isEmpty() ? "" : LINE_SEPARATOR);
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}
