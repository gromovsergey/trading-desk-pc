package com.foros.model.site;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TagOptGroupStatePK implements Serializable {
    @Column(name = "OPTION_GROUP_ID", nullable = false)
    private long optionGroupId;

    @Column(name = "TAG_ID", nullable = false)
    private long tagId;

    public TagOptGroupStatePK() {
    }

    public TagOptGroupStatePK(long optionGroupId, long tagId) {
        this.optionGroupId = optionGroupId;
        this.tagId = tagId;
    }

    public long getOptionGroupId() {
        return optionGroupId;
    }

    public void setOptionGroupId(long optionGroupId) {
        this.optionGroupId = optionGroupId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagOptGroupStatePK that = (TagOptGroupStatePK) o;

        if (optionGroupId != that.optionGroupId) return false;
        if (tagId != that.tagId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (optionGroupId ^ (optionGroupId >>> 32));
        result = 31 * result + (int) (tagId ^ (tagId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "TagOptGroupStatePK[" +
                "optionGroupId=" + optionGroupId +
                ", tagId=" + tagId +
                ']';
    }
}