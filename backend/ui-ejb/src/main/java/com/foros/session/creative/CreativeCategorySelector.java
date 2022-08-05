package com.foros.session.creative;

import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;

import java.util.Collection;

public class CreativeCategorySelector implements Selector<CreativeCategory> {
    private Collection<Long> ids;
    private CreativeCategoryType type;
    private Paging paging = new Paging();

    public Collection<Long> getIds() {
        return ids;
    }

    public CreativeCategoryType getType() {
        return type;
    }

    @Override
    public Paging getPaging() {
        return paging;
    }

    public static class Builder {
        private Collection<Long> ids;
        private CreativeCategoryType type;
        private Integer pagingFirst;
        private Integer pagingCount;

        public Builder ids(Collection<Long> ids) {
            this.ids = ids;
            return this;
        }

        public Builder type(CreativeCategoryType type) {
            this.type = type;
            return this;
        }

        public Builder first(Integer pagingFirst) {
            this.pagingFirst = pagingFirst;
            return this;
        }

        public Builder count(Integer pagingCount) {
            this.pagingCount = pagingCount;
            return this;
        }

        public CreativeCategorySelector build() {
            return new CreativeCategorySelector(this);
        }
    }

    public CreativeCategorySelector(Builder builder) {
        this.ids = builder.ids;
        this.type = builder.type;
        if (builder.pagingFirst != null || builder.pagingCount != null) {
            this.paging = new Paging(builder.pagingFirst, builder.pagingCount);
        }
    }
}
