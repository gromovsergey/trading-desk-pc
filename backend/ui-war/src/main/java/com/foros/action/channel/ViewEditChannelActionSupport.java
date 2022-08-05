package com.foros.action.channel;

import com.foros.action.BaseActionSupport;
import com.foros.action.LanguageBean;
import com.foros.model.account.Account;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.TriggersChannel;
import com.foros.model.channel.trigger.ChannelTrigger;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.restriction.RestrictionService;
import com.foros.session.channel.service.DiscoverChannelService;
import com.foros.util.StringUtil;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ModelDriven;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.commons.lang.StringUtils;

public abstract class ViewEditChannelActionSupport<T> extends BaseActionSupport implements ModelDriven<T> {
    @EJB
    protected DiscoverChannelService discoverChannelService;

    @EJB
    protected RestrictionService restrictionService;

    protected T model;

    private List<LanguageBean> availableLanguages;

    private String pageKeywords;
    private String pageKeywordsNegative;
    private String searchKeywords;
    private String searchKeywordsNegative;
    private String urls;
    private String urlsNegative;
    private String urlKeywords;
    private String urlKeywordsNegative;

    public String getPageKeywords() {
        return pageKeywords;
    }

    public void setPageKeywords(String pageKeywords) {
        this.pageKeywords = pageKeywords;
    }

    public String getPageKeywordsNegative() {
        return pageKeywordsNegative;
    }

    public void setPageKeywordsNegative(String pageKeywordsNegative) {
        this.pageKeywordsNegative = pageKeywordsNegative;
    }

    public String getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public String getSearchKeywordsNegative() {
        return searchKeywordsNegative;
    }

    public void setSearchKeywordsNegative(String searchKeywordsNegative) {
        this.searchKeywordsNegative = searchKeywordsNegative;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getUrlsNegative() {
        return urlsNegative;
    }

    public void setUrlsNegative(String urlsNegative) {
        this.urlsNegative = urlsNegative;
    }

    public String getUrlKeywords() {
        return urlKeywords;
    }

    public void setUrlKeywords(String urlKeywords) {
        this.urlKeywords = urlKeywords;
    }

    public String getUrlKeywordsNegative() {
        return urlKeywordsNegative;
    }

    public void setUrlKeywordsNegative(String urlKeywordsNegative) {
        this.urlKeywordsNegative = urlKeywordsNegative;
    }

    @Override
    public T getModel() {
        return model;
    }

    // just implement RequestContextsAware to use this method
    public void switchContext(RequestContexts contexts) {
        Account account = getExistingAccount();

        if (account instanceof InternalAccount || !restrictionService.isPermitted("AdvertisingChannel.view", account)) {
            return;
        }

        contexts.switchTo(account);
    }

    public abstract Account getExistingAccount();

    protected List<LanguageBean> populateAvailableLanguages() {
        List<String> languages = discoverChannelService.getAvailableLanguages();
        return sortLanguages(languages);
    }

    public static List<LanguageBean> sortLanguages(List<String> languages) {
        List<LanguageBean> sortedLanguages = new ArrayList<LanguageBean>(languages.size());
        for (String languageCode : languages) {
            sortedLanguages.add(new LanguageBean(languageCode));
        }
        Collections.sort(sortedLanguages);
        return sortedLanguages;
    }

    private class KeywordBuilder {
        private List<ChannelTrigger> declined = new LinkedList<ChannelTrigger>();
        private List<ChannelTrigger> pending = new LinkedList<ChannelTrigger>();
        private List<ChannelTrigger> approved = new LinkedList<ChannelTrigger>();
        private List<ChannelTrigger> declinedNegative = new LinkedList<ChannelTrigger>();
        private List<ChannelTrigger> pendingNegative = new LinkedList<ChannelTrigger>();
        private List<ChannelTrigger> approvedNegative = new LinkedList<ChannelTrigger>();

        public void add(ChannelTrigger trigger) {
            switch (trigger.getQaStatus()) {
                case DECLINED:
                    if (trigger.isNegative()) {
                        declinedNegative.add(trigger);
                    } else {
                        declined.add(trigger);
                    }
                    break;
                case HOLD:
                    if (trigger.isNegative()) {
                        pendingNegative.add(trigger);
                    } else {
                        pending.add(trigger);
                    }
                    break;
                case APPROVED:
                    if (trigger.isNegative()) {
                        approvedNegative.add(trigger);
                    } else {
                        approved.add(trigger);
                    }
                    break;
            }
        }

        public String buildPositive() {
            return build(false);
        }

        public String buildNegative() {
            return build(true);
        }

        private String build(boolean isNegative) {
            List<ChannelTrigger> declinedLoc = isNegative ? declinedNegative : declined;
            List<ChannelTrigger> pendingLoc = isNegative ? pendingNegative : pending;
            List<ChannelTrigger> approvedLoc = isNegative ? approvedNegative : approved;

            StringBuilder tmp = new StringBuilder();
            addTriggers(tmp, "triggers.qastatus.D", declinedLoc);
            addTriggers(tmp, "triggers.qastatus.H", pendingLoc);
            addTriggers(tmp, "triggers.qastatus.A", approvedLoc);

            return StringUtils.chomp(tmp.toString());
        }

        private void addTriggers(StringBuilder triggerBuilder, String status, List<ChannelTrigger> triggers) {
            if (triggers.size() > 0) {
                triggerBuilder.append("-- ").append(getText(status)).append(" --").append("\n");
                for (ChannelTrigger channelTrigger : triggers) {
                    triggerBuilder.append(channelTrigger.getOriginalTrigger()).append("\n");
                }
            }
        }
    }

    protected void loadTriggers() {
        if (!(model instanceof TriggersChannel)) {
            return;
        }

        Collection<ChannelTrigger> triggers = ((TriggersChannel) model).getTriggers();
        KeywordBuilder pageKeywordsBuilder = new KeywordBuilder();
        KeywordBuilder searchKeywordsBuilder = new KeywordBuilder();
        KeywordBuilder urlsBuilder = new KeywordBuilder();
        KeywordBuilder urlKeywordsBuilder = new KeywordBuilder();

        for (ChannelTrigger channelTrigger : triggers) {
            switch (TriggerType.byCode(channelTrigger.getTriggerType())) {
                case PAGE_KEYWORD:
                    pageKeywordsBuilder.add(channelTrigger);
                    break;
                case SEARCH_KEYWORD:
                    searchKeywordsBuilder.add(channelTrigger);
                    break;
                case URL:
                    urlsBuilder.add(channelTrigger);
                    break;
                case URL_KEYWORD:
                    urlKeywordsBuilder.add(channelTrigger);
            }
        }

        pageKeywords = pageKeywordsBuilder.buildPositive();
        pageKeywordsNegative = pageKeywordsBuilder.buildNegative();
        searchKeywords = searchKeywordsBuilder.buildPositive();
        searchKeywordsNegative = searchKeywordsBuilder.buildNegative();
        urls = urlsBuilder.buildPositive();
        urlsNegative = urlsBuilder.buildNegative();
        urlKeywords = urlKeywordsBuilder.buildPositive();
        urlKeywordsNegative = urlKeywordsBuilder.buildNegative();
    }

    protected void flushKeywordsToModel() {
        TriggersChannel ch = (TriggersChannel) model;

        ch.getPageKeywords().setPositive(StringUtil.removeRemarksAndSplit(getPageKeywords()));
        ch.getPageKeywords().setNegative(StringUtil.removeRemarksAndSplit(getPageKeywordsNegative()));

        ch.getSearchKeywords().setPositive(StringUtil.removeRemarksAndSplit(getSearchKeywords()));
        ch.getSearchKeywords().setNegative(StringUtil.removeRemarksAndSplit(getSearchKeywordsNegative()));

        ch.getUrls().setPositive(StringUtil.removeRemarksAndSplit(getUrls()));
        ch.getUrls().setNegative(StringUtil.removeRemarksAndSplit(getUrlsNegative()));

        ch.getUrlKeywords().setPositive(StringUtil.removeRemarksAndSplit(getUrlKeywords()));
        ch.getUrlKeywords().setNegative(StringUtil.removeRemarksAndSplit(getUrlKeywordsNegative()));
    }

    public List<LanguageBean> getAvailableLanguages() {
        if (availableLanguages == null) {
            loadAvailableLanguages();
        }
        return availableLanguages;
    }

    protected void loadAvailableLanguages() {
        availableLanguages = populateAvailableLanguages();
    }
}
