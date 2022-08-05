package com.foros.session.creative;

import com.foros.model.DisplayStatus;
import com.foros.model.LocalizableName;
import com.foros.model.Status;
import com.foros.session.campaign.ImpClickStatsTO.Builder;

public class LinkedCreativeTO extends BaseLinkedTO {

    public LinkedCreativeTO(Builder builder) {
        super(builder);
    }

    private Long id;
    private String creativeName;
    private Long creativeId;
    private DisplayStatus creativeDisplayStatus;
    private Long sizeId;
    private Status status;
    private DisplayStatus displayStatus;
    private LocalizableName sizeName;
    private long uniqueUsers;
    private long templateId;

    public DisplayStatus getCreativeDisplayStatus() {
        return creativeDisplayStatus;
    }

    public Long getCreativeId() {
        return creativeId;
    }

    public String getCreativeName() {
        return creativeName;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public Long getId() {
        return id;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public LocalizableName getSizeName() {
        return sizeName;
    }

    public Status getStatus() {
        return status;
    }

    public long getTemplateId() {
        return templateId;
    }

    public long getUniqueUsers() {
        return uniqueUsers;
    }

    public void setCreativeDisplayStatus(DisplayStatus creativeDisplayStatus) {
        this.creativeDisplayStatus = creativeDisplayStatus;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    public void setCreativeName(String creativeName) {
        this.creativeName = creativeName;
    }

    public void setDisplayStatus(DisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public void setSizeName(LocalizableName sizeName) {
        this.sizeName = sizeName;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public void setUniqueUsers(long uniqueUsers) {
        this.uniqueUsers = uniqueUsers;
    }

    @Override
    public String toString() {
        return "LinkedCreativeTO [id=" + id + ", creativeName=" + creativeName
                + ", creativeId=" + creativeId + ", creativeDisplayStatus="
                + creativeDisplayStatus + ", sizeId=" + sizeId + ", status="
                + status + ", displayStatus=" + displayStatus + ", sizeName="
                + sizeName + ", uniqueUsers=" + uniqueUsers + ", templateId="
                + templateId + "]";
    }
}
