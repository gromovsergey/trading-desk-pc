package app.programmatic.ui.creative.dao.model;

import java.util.List;

public class CreativeUploadHtml {
    private Long accountId;
    private String clickUrl;
    private String landingPageUrl;
    private String crAdvTrackPixel;
    private List<Long> categories;
    private List<CreativeImage> imagesList;

    public CreativeUploadHtml() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public String getLandingPageUrl() {
        return landingPageUrl;
    }

    public void setLandingPageUrl(String landingPageUrl) {
        this.landingPageUrl = landingPageUrl;
    }

    public String getCrAdvTrackPixel() {
        return crAdvTrackPixel;
    }

    public void setCrAdvTrackPixel(String crAdvTrackPixel) {
        this.crAdvTrackPixel = crAdvTrackPixel;
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }

    public List<CreativeImage> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<CreativeImage> imagesList) {
        this.imagesList = imagesList;
    }
}
