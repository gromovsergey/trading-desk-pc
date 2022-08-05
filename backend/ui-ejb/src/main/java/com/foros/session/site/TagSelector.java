package com.foros.session.site;

import com.foros.model.Status;
import com.foros.model.site.Tag;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;

import java.util.List;

public class TagSelector implements Selector<Tag> {

    private List<Long> tagIds;
    private List<Long> siteIds;
    private List<Status> tagStatuses;
    private Paging paging;

    public List<Long> getTagIds() {
        return tagIds;
    }

    public List<Long> getSiteIds() {
        return siteIds;
    }

    public List<Status> getTagStatuses() {
        return tagStatuses;
    }

    @Override
    public Paging getPaging() {
        return paging;
    }

    public static class Builder {
        private List<Long> tagIds;
        private List<Long> siteIds;
        private List<Status> tagStatuses;
        private Paging paging;

        public Builder tagIds(List<Long> tagIds) {
            this.tagIds = tagIds;
            return this;
        }

        public Builder siteIds(List<Long> siteIds) {
            this.siteIds = siteIds;
            return this;
        }

        public Builder tagStatuses(List<Status> tagStatuses) {
            this.tagStatuses = tagStatuses;
            return this;
        }

        public Builder paging(Paging paging) {
            this.paging = paging;
            return this;
        }

        public TagSelector build() {
            return new TagSelector(this);
        }
    }

    private TagSelector(Builder builder) {
        this.tagIds = builder.tagIds;
        this.siteIds = builder.siteIds;
        this.tagStatuses = builder.tagStatuses;
        this.paging = builder.paging;
    }
}
