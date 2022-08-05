package com.foros.session.channel.triggerQA;

import com.foros.model.DisplayStatus;
import com.foros.model.channel.trigger.TriggerChannelType;
import com.foros.security.AccountRole;
import com.foros.session.channel.ChannelVisibilityCriteria;
import com.foros.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriggerQASearchParameters {

    private int firstRow;
    private int maxResults;
    private TriggerQAType type;
    private TriggerQASearchFilter filter;
    private String searchPhrase;
    private Character searchStatuses;
    private ChannelVisibilityCriteria visibility;
    private Collection<AccountRole> accountRoles;
    private Long accountId;
    private Long discoverChannelListId;
    private Long channelId;
    private TriggerChannelType triggerChannelType;
    private String country;
    private Long advertiserId;
    private Long campaignId;
    private Long ccgId;
    private DisplayStatus[] displayStatuses;
    private Map<TriggerQAType, SearchPhraseBase> phraseFilters = new HashMap<TriggerQAType, SearchPhraseBase>();
    private TriggerQASortType sortOrder;

    private TriggerQASearchParameters(int firstRow, int maxResults, TriggerQAType type, String searchPhrase, Character searchStatuses, Long accountId,
            Long channelId, TriggerChannelType triggerChannelType, String country, DisplayStatus[] displayStatuses, TriggerQASortType sortOrder) {
        this.firstRow = firstRow;
        this.maxResults = maxResults;
        this.type = type;
        this.searchPhrase = searchPhrase;
        this.searchStatuses = searchStatuses;
        this.accountId = accountId;
        this.channelId = channelId;
        this.triggerChannelType = triggerChannelType;
        this.country = country;
        this.displayStatuses = displayStatuses;
        this.sortOrder = sortOrder;
        initPhraseFilter();
    }

    public TriggerQASearchParameters(int firstRow, int maxResults, TriggerQAType type, TriggerQASearchFilter filter,
                                     String searchPhrase, Character searchStatuses, ChannelVisibilityCriteria visibility,
                                     Collection<AccountRole> accountRoles, Long accountId, Long channelId,
                                     String country, Long advertiserId,
                                     Long campaignId, Long ccgId, DisplayStatus[] displayStatuses, TriggerQASortType sortOrder) {
        this(firstRow, maxResults, type, searchPhrase, searchStatuses, accountId, channelId, TriggerChannelType.ADVERTISING, country, displayStatuses, sortOrder);
        this.visibility = visibility;
        this.advertiserId = advertiserId;
        this.campaignId = campaignId;
        this.ccgId = ccgId;
        this.accountRoles = accountRoles;
        this.filter = filter;
    }

    public TriggerQASearchParameters(int firstRow, int maxResults, TriggerQAType type,
                                     String searchPhrase, Character searchStatuses,
                                     Long accountId, Long discoverChannelListId, Long channelId,
                                     String country, DisplayStatus[] displayStatuses, TriggerQASortType sortOrder) {
        this(firstRow, maxResults, type, searchPhrase, searchStatuses, accountId, channelId, TriggerChannelType.DISCOVER, country, displayStatuses, sortOrder);
        this.discoverChannelListId = discoverChannelListId;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public TriggerQAType getType() {
        return type;
    }

    public TriggerQASearchFilter getFilter() {
        return filter != null ? filter : TriggerQASearchFilter.ALL;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public Character getSearchStatuses() {
        return searchStatuses;
    }

    public TriggerQASortType getSortOrder() {
        return sortOrder;
    }

    public ChannelVisibilityCriteria getVisibility() {
        return visibility;
    }

    public Collection<AccountRole> getAccountRoles() {
        return accountRoles;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public TriggerChannelType getTriggerChannelType() {
        return triggerChannelType;
    }

    public String getCountry() {
        return country;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public Long getCcgId() {
        return ccgId;
    }

    public DisplayStatus[] getDisplayStatuses() {
        return displayStatuses;
    }

    public boolean isFilterByChannel() {
        return getChannelId() != null;
    }

    public boolean isFilterByAccountRole() {
        return !CollectionUtils.isNullOrEmpty(getAccountRoles());
    }

    public boolean isFilterByAdvertiser() {
        return getAdvertiserId() != null;
    }

    public boolean isFilterByCampaign() {
        return getCampaignId() != null;
    }

    public boolean isFilterByCcg() {
        return getCcgId() != null;
    }

    public boolean isFilterByDisplayStatus() {
        return getDisplayStatuses() != null;
    }

    public boolean isFilterByDiscoverList() {
        return getDiscoverChannelListId() != null && getDiscoverChannelListId() > 0;
    }

    private void initPhraseFilter() {
        if (type != null) {
            phraseFilters.put(type, TriggerQASearchPhrase.create(country, searchPhrase, type));
        } else {
            phraseFilters.put(TriggerQAType.KEYWORD, TriggerQASearchPhrase.create(country, searchPhrase, TriggerQAType.KEYWORD));
            phraseFilters.put(TriggerQAType.URL, TriggerQASearchPhrase.create(country, searchPhrase, TriggerQAType.URL));
        }
    }

    public Long getDiscoverChannelListId() {
        return discoverChannelListId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Field fld : this.getClass().getDeclaredFields()) {
            fld.setAccessible(true);
            sb.append(fld.getName());
            sb.append(" = ");
            try {
                sb.append(fld.get(this));
            } catch (IllegalAccessException e) {
                System.err.println("IllegalAccessException caught...");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public List<String> getURLSearchPhrase() {
        return getSearchPhrase(TriggerQAType.URL);
    }

    public List<String> getKeywordSearchPhrase() {
        return getSearchPhrase(TriggerQAType.KEYWORD);
    }

    private List<String> getSearchPhrase(TriggerQAType qaType) {
        SearchPhraseBase phrase = phraseFilters.get(qaType);
        if (phrase == null) {
            return null;
        }
        return phrase.getSearchPhrases();
    }
}
