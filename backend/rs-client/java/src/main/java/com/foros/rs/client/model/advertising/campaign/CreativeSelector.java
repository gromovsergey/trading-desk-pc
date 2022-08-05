package com.foros.rs.client.model.advertising.campaign;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.util.List;


@QueryEntity
public class CreativeSelector implements PagingSelectorContainer {


    @QueryParameter("paging")
    private PagingSelector paging;


    @QueryParameter("advertiser.ids")
    private List<Long> advertiserIds;


    @QueryParameter("creative.ids")
    private List<Long> creativeIds;


    @QueryParameter("creative.statuses")
    private List<Status> creativeStatuses;


    @QueryParameter("size.ids")
    private List<Long> sizeIds;

    @QueryParameter("size.ids.excluded")
    private List<Long> excludedSizeIds;

    @QueryParameter("template.ids")
    private List<Long> templateIds;

    @QueryParameter("template.ids.excluded")
    private List<Long> excludedTemplateIds;

    @Override
    public PagingSelector getPaging() {
        return this.paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }

    public List<Long> getAdvertiserIds() {
        return this.advertiserIds;
    }

    public void setAdvertiserIds(List<Long> advertiserIds) {
        this.advertiserIds = advertiserIds;
    }

    public List<Long> getCreativeIds() {
        return this.creativeIds;
    }

    public void setCreativeIds(List<Long> creativeIds) {
        this.creativeIds = creativeIds;
    }

    public List<Status> getCreativeStatuses() {
        return this.creativeStatuses;
    }

    public void setCreativeStatuses(List<Status> creativeStatuses) {
        this.creativeStatuses = creativeStatuses;
    }

    public List<Long> getSizeIds() {
        return this.sizeIds;
    }

    public void setSizeIds(List<Long> sizeIds) {
        this.sizeIds = sizeIds;
    }

    public List<Long> getExcludedSizeIds() {
        return excludedSizeIds;
    }

    public void setExcludedSizeIds(List<Long> excludedSizeIds) {
        this.excludedSizeIds = excludedSizeIds;
    }

    public List<Long> getTemplateIds() {
        return this.templateIds;
    }

    public void setTemplateIds(List<Long> templateIds) {
        this.templateIds = templateIds;
    }

    public List<Long> getExcludedTemplateIds() {
        return excludedTemplateIds;
    }

    public void setExcludedTemplateIds(List<Long> excludedTemplateIds) {
        this.excludedTemplateIds = excludedTemplateIds;
    }
}