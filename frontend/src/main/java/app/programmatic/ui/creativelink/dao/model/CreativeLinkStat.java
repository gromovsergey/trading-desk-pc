package app.programmatic.ui.creativelink.dao.model;

import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.math.BigDecimal;

public class CreativeLinkStat {
    private Long creativeId;
    private String creativeName;
    private MajorDisplayStatus creativeDisplayStatus;
    private MajorDisplayStatus displayStatus;
    private Long sizeId;
    private String sizeName;
    private Long templateId;
    private String templateName;
    private Long imps;
    private Long clicks;
    private BigDecimal ctr;
    private Long uniqueUsers;

    public Long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    public String getCreativeName() {
        return creativeName;
    }

    public void setCreativeName(String creativeName) {
        this.creativeName = creativeName;
    }

    public MajorDisplayStatus getCreativeDisplayStatus() {
        return creativeDisplayStatus;
    }

    public void setCreativeDisplayStatus(MajorDisplayStatus creativeDisplayStatus) {
        this.creativeDisplayStatus = creativeDisplayStatus;
    }

    public MajorDisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(MajorDisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Long getImps() {
        return imps;
    }

    public void setImps(Long imps) {
        this.imps = imps;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public BigDecimal getCtr() {
        return ctr;
    }

    public void setCtr(BigDecimal ctr) {
        this.ctr = ctr;
    }

    public Long getUniqueUsers() {
        return uniqueUsers;
    }

    public void setUniqueUsers(Long uniqueUsers) {
        this.uniqueUsers = uniqueUsers;
    }
}
