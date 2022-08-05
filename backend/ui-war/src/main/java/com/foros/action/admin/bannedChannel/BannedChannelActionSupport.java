package com.foros.action.admin.bannedChannel;

import com.foros.action.BaseActionSupport;
import com.foros.model.channel.BannedChannel;
import com.foros.session.admin.bannedChannel.BannedChannelService;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public class BannedChannelActionSupport extends BaseActionSupport implements ModelDriven<BannedChannel> {

    protected BannedChannel model;
    private String pageSearchKeywordsText = null;
    private String urlsText = null;
    private String urlKeywordsText = null;

    @EJB
    protected BannedChannelService service;

    @Override
    public BannedChannel getModel() {
        return model;
    }

    public String getKeywordsText() {
        if (pageSearchKeywordsText == null) {
            pageSearchKeywordsText = model.getPageKeywords().getPositiveString();
        }
        return pageSearchKeywordsText;
    }

    public void setKeywordsText(String pageSearchKeywordsText) {
        this.pageSearchKeywordsText = pageSearchKeywordsText;
    }

    public String getUrlsText() {
        if (urlsText == null) {
            urlsText = model.getUrls().getPositiveString();
        }
        return urlsText;
    }

    public void setUrlsText(String urlsText) {
        this.urlsText = urlsText;
    }

    public String getUrlKeywordsText() {
        if (urlKeywordsText == null) {
            urlKeywordsText = model.getUrlKeywords().getPositiveString();
        }
        return urlKeywordsText;
    }

    public void setUrlKeywordsText(String urlKeywordsText) {
        this.urlKeywordsText = urlKeywordsText;
    }

    protected void loadPageSearchKeywordsText() {
        pageSearchKeywordsText = model.getPageKeywords().getPositiveString();
    }

    protected void loadUrlsText() {
        urlsText = model.getUrls().getPositiveString();
    }

    protected void loadUrlKeywordsText() {
        urlKeywordsText = model.getUrlKeywords().getPositiveString();
    }
}
