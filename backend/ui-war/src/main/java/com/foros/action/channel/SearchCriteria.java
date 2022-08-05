package com.foros.action.channel;

import com.foros.model.account.Account;
import com.foros.model.channel.ChannelVisibility;
import com.foros.security.AccountRole;
import com.foros.session.channel.ChannelVisibilityCriteria;
import com.foros.session.channel.service.ChannelUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SearchCriteria {

    private String activeTab =  "searchChannels";

    private String searchName;

    private String content;

    private boolean searchMyChannels = true;

    private boolean searchPublicChannels = true;

    private boolean searchCmpChannels = true;

    private boolean disableSearchMyChannels;

    private boolean disableSearchCmpChannels;

    private boolean disableSearchPublicChannels;

    public ChannelVisibilityCriteria getVisibilityCriteria() {
        Set<ChannelVisibility> visibilities = new HashSet<ChannelVisibility>();
        if (getSearchPublicChannels()) {
            visibilities.add(ChannelVisibility.PUB);
        }
        if (getSearchCmpChannels()) {
            visibilities.add(ChannelVisibility.CMP);
        }
        return ChannelVisibilityCriteria.valueOf(visibilities.toArray(new ChannelVisibility[visibilities.size()]));
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setSearchMyChannels(boolean searchMyChannels) {
        this.searchMyChannels = searchMyChannels;
    }

    public boolean getSearchMyChannels() {
        return searchMyChannels;
    }

    public void setSearchPublicChannels(boolean searchPublicChannels) {
        this.searchPublicChannels = searchPublicChannels;
    }

    public boolean getSearchPublicChannels() {
        return searchPublicChannels;
    }

    public void setSearchCmpChannels(boolean searchCmpChannels) {
        this.searchCmpChannels = searchCmpChannels;
    }

    public boolean getSearchCmpChannels() {
        return searchCmpChannels;
    }

    public void setActiveTab(String activeTab) {
        this.activeTab = activeTab;
    }

    public String getActiveTab() {
        return activeTab;
    }

    public void setDisableSearchMyChannels(boolean disableSearchMyChannels) {
        this.disableSearchMyChannels = disableSearchMyChannels;
    }

    public boolean isDisableSearchMyChannels() {
        return disableSearchMyChannels;
    }

    public void setDisableSearchCmpChannels(boolean disableSearchCmpChannels) {
        this.disableSearchCmpChannels = disableSearchCmpChannels;
    }

    public boolean isDisableSearchCmpChannels() {
        return disableSearchCmpChannels;
    }

    public void setDisableSearchPublicChannels(boolean disableSearchPublicChannels) {
        this.disableSearchPublicChannels = disableSearchPublicChannels;
    }

    public boolean isDisableSearchPublicChannels() {
        return disableSearchPublicChannels;
    }
    

    public void populateConditionOfVisibility(Account account) {
        Map<ChannelVisibility, ChannelUtils.ConditionOfVisibility> visibilityMap = ChannelUtils.getChannelUsageRestrictions(account.getRole());

        int countSame = 0;
        for (Map.Entry<ChannelVisibility, ChannelUtils.ConditionOfVisibility> entry : visibilityMap.entrySet()) {
            if (entry.getValue() == ChannelUtils.ConditionOfVisibility.SAME_ACCOUNT) {
                countSame++;
            }
        }

        // If all values are "same account" no other options to choose
        setDisableSearchMyChannels(countSame == visibilityMap.size());
        if (isDisableSearchMyChannels()) {
            setSearchMyChannels(true);
        }

        if (account.getRole() == AccountRole.CMP) {
            setDisableSearchCmpChannels(true);
            setSearchCmpChannels(false);
        } else {
            setDisableSearchCmpChannels(isVisibilityDisabled(visibilityMap, ChannelVisibility.CMP));
            if (isDisableSearchCmpChannels()) {
                setSearchCmpChannels(visibilityMap.containsKey(ChannelVisibility.CMP));
            }
        }

        setDisableSearchPublicChannels(isVisibilityDisabled(visibilityMap, ChannelVisibility.PUB));
        if (isDisableSearchPublicChannels()) {
            setSearchPublicChannels(visibilityMap.containsKey(ChannelVisibility.PUB));
        }
    }

    private static boolean isVisibilityDisabled(Map<ChannelVisibility, ChannelUtils.ConditionOfVisibility> visibilityMap, ChannelVisibility visibility) {
        // enabled only when visibility is allowed and some other visibilities are available
        return !(visibilityMap.containsKey(visibility) && visibilityMap.size() > 1);
    }
}
