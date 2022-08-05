package com.foros.session.channel.service;

public class ChannelTriggersTotalsTO {

    private TotalByTriggerTypeTO pageKeywords = new TotalByTriggerTypeTO();
    private TotalByTriggerTypeTO searchKeywords = new TotalByTriggerTypeTO();
    private TotalByTriggerTypeTO urls = new TotalByTriggerTypeTO();
    private TotalByTriggerTypeTO urlKeywords = new TotalByTriggerTypeTO();

    public TotalByTriggerTypeTO getPageKeywords() {
        return pageKeywords;
    }

    public void setPageKeywords(TotalByTriggerTypeTO pageKeywords) {
        this.pageKeywords = pageKeywords;
    }

    public TotalByTriggerTypeTO getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(TotalByTriggerTypeTO searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public TotalByTriggerTypeTO getUrls() {
        return urls;
    }

    public void setUrls(TotalByTriggerTypeTO urls) {
        this.urls = urls;
    }

    public TotalByTriggerTypeTO getUrlKeywords() {
        return urlKeywords;
    }

    public void setUrlKeywords(TotalByTriggerTypeTO urlKeywords) {
        this.urlKeywords = urlKeywords;
    }

}
