package com.foros.model.channel;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.jaxb.adapters.CategoryLinkXmlAdapter;
import com.foros.model.channel.trigger.ChannelTrigger;
import com.foros.model.channel.trigger.KeywordTrigger;
import com.foros.model.channel.trigger.PageKeywordTrigger;
import com.foros.model.channel.trigger.PageKeywordsHolder;
import com.foros.model.channel.trigger.SearchKeywordTrigger;
import com.foros.model.channel.trigger.SearchKeywordsHolder;
import com.foros.model.channel.trigger.UrlKeywordTrigger;
import com.foros.model.channel.trigger.UrlKeywordsHolder;
import com.foros.model.channel.trigger.UrlTrigger;
import com.foros.model.channel.trigger.UrlsHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@XmlType(propOrder = {
        "pageKeywords",
        "searchKeywords",
        "urls",
        "urlKeywords",
        "categoriesWrapper"
})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public abstract class TriggersChannel extends Channel implements KeywordTriggersSource, UrlTriggersSource {
    @Transient
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private final PageKeywordsHolder pageKeywords = new PageKeywordsHolder(this);

    @Transient
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private final SearchKeywordsHolder searchKeywords = new SearchKeywordsHolder(this);

    @Transient
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private final UrlsHolder urls = new UrlsHolder(this);

    @Transient
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    protected UrlKeywordsHolder urlKeywords = new UrlKeywordsHolder(this);

    @Transient
    private Set<ChannelTrigger> triggers;

    @Override
    @XmlTransient
    public Set<CategoryChannel> getCategories() {
        return super.getCategories();
    }

    @XmlElement(name = "categories")
    public CategoryChannelsWrapper getCategoriesWrapper() {
        Set<CategoryChannel> categories = getCategories();
        if (categories == null) {
            return null;
        }

        CategoryChannelsWrapper categoryChannelsWrapper = new CategoryChannelsWrapper();
        categoryChannelsWrapper.setCategories(categories);
        return categoryChannelsWrapper;
    }

    public void setCategoriesWrapper(CategoryChannelsWrapper channelsWrapper) {
        this.setCategories(channelsWrapper.getCategories());
    }

    @XmlElement(name = "pageKeywords")
    public PageKeywordsHolder getPageKeywords() {
        return pageKeywords;
    }

    @XmlTransient
    public Set<ChannelTrigger> getTriggers() {
        if (triggers == null) {
            throw new IllegalStateException("Triggers is not initialized");
        }
        return Collections.unmodifiableSet(triggers);
    }

    @XmlTransient
    public boolean isTriggersInitialized() {
        return triggers != null;
    }

    public void resetTriggers(Set<ChannelTrigger> triggers) {
        this.triggers = triggers;
    }

    public void setPageKeywords(PageKeywordsHolder holder) {
        this.pageKeywords.set(holder);
    }

    @XmlElement(name = "searchKeywords")
    public SearchKeywordsHolder getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(SearchKeywordsHolder holder) {
        this.searchKeywords.set(holder);
    }

    @XmlElement(name = "urls")
    public UrlsHolder getUrls() {
        return urls;
    }

    public void setUrls(UrlsHolder holder) {
        this.urls.set(holder);
    }

    @XmlElement(name = "urlKeywords")
    public UrlKeywordsHolder getUrlKeywords() {
        return urlKeywords;
    }

    public void setUrlKeywords(UrlKeywordsHolder holder) {
        this.urlKeywords.set(holder);
    }

    @Override
    @XmlTransient
    public Collection<KeywordTrigger> getAllKeywordTriggers() {
        List<PageKeywordTrigger> allPageKeywords = getPageKeywords().getAll();
        List<SearchKeywordTrigger> allSearchKeywords = getSearchKeywords().getAll();
        List<UrlKeywordTrigger> allUrlKeywords = getUrlKeywords().getAll();

        List<KeywordTrigger> allKeywords = new ArrayList<KeywordTrigger>(allPageKeywords.size() + allSearchKeywords.size() + allUrlKeywords.size());
        allKeywords.addAll(allPageKeywords);
        allKeywords.addAll(allSearchKeywords);
        allKeywords.addAll(allUrlKeywords);

        return allKeywords;
    }

    @Override
    @XmlTransient
    public Collection<UrlTrigger> getAllUrlTriggers() {
        return getUrls().getAll();
    }

    @XmlType
    @XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
    public static class CategoryChannelsWrapper {

        private Set<CategoryChannel> categories = new HashSet<CategoryChannel>();

        public CategoryChannelsWrapper() {
        }

        @XmlElement(name = "category")
        @XmlJavaTypeAdapter(CategoryLinkXmlAdapter.class)
        public Set<CategoryChannel> getCategories() {
            return categories;
        }

        public void setCategories(Set<CategoryChannel> categories) {
            this.categories = categories;
        }

    }
}
