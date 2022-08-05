package com.foros.rs.client.model.siteCreative;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.lang.Long;
import java.lang.String;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;


@QueryEntity
public class SiteCreativeApprovalSelector implements PagingSelectorContainer {

    @QueryParameter("site.id")
    private Long siteId;

    @QueryParameter("creative.updatedSince")
    private XMLGregorianCalendar creativeUpdatedSince;

    @QueryParameter("size.name")
    private String sizeName;

    @QueryParameter("paging")
    private PagingSelector paging;

    @QueryParameter("status")
    private List<SiteCreativeApprovalStatus> statuses;

    @QueryParameter("creative.id")
    private List<Long> creativeIds;

    public Long getSiteId() {
        return this.siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public XMLGregorianCalendar getCreativeUpdatedSince() {
        return this.creativeUpdatedSince;
    }

    public void setCreativeUpdatedSince(XMLGregorianCalendar creativeUpdatedSince) {
        this.creativeUpdatedSince = creativeUpdatedSince;
    }

    public String getSizeName() {
        return this.sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    @Override
    public PagingSelector getPaging() {
        return this.paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }

    public List<SiteCreativeApprovalStatus> getStatuses() {
        return this.statuses;
    }

    public void setStatuses(List<SiteCreativeApprovalStatus> statuses) {
        this.statuses = statuses;
    }

    public List<Long> getCreativeIds() {
        return this.creativeIds;
    }

    public void setCreativeIds(List<Long> creativeIds) {
        this.creativeIds = creativeIds;
    }
}