package com.foros.session.creative;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "livePreviewResult")
@XmlType(propOrder = {
        "height",
        "width",
        "url"
})
public class LivePreviewResult {
    private Long height;
    private Long width;
    private String url;

    public LivePreviewResult() {
    }

    public LivePreviewResult(Long height, Long width, String url) {
        this.height = height;
        this.width = width;
        this.url = url;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
