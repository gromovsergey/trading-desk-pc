package com.foros.action.xml.model;

public class TagPreviewTO {
    private Long width;

    private Long height;

    private String perview;

    public TagPreviewTO() {}

    public TagPreviewTO(Long width, Long height, String perview) {
        this.width = width;
        this.height = height;
        this.perview = perview;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getPerview() {
        return perview;
    }

    public void setPerview(String perview) {
        this.perview = perview;
    }
}
