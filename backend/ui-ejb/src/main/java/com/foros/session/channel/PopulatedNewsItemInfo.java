package com.foros.session.channel;

public class PopulatedNewsItemInfo {
    private String newsId;
    private String title;
    private String link;

    public PopulatedNewsItemInfo(String newsId, String title, String link) {
        this.newsId = newsId;
        this.title = title;
        this.link = link;
    }

    public String getNewsId() {
        return newsId;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
}
