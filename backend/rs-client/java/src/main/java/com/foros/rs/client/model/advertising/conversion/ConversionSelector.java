package com.foros.rs.client.model.advertising.conversion;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.util.List;

@QueryEntity
public class ConversionSelector implements PagingSelectorContainer {

    @QueryParameter("paging")
    private PagingSelector paging;

    @QueryParameter("advertiser.ids")
    private List<Long> advertiserIds;

    @QueryParameter("conversion.ids")
    private List<Long> conversionIds;

    @QueryParameter("conversion.statuses")
    private List<Status> conversionStatuses;

    public List<Long> getConversionIds() {
        return conversionIds;
    }

    public void setConversionIds(List<Long> conversionIds) {
        this.conversionIds = conversionIds;
    }

    public List<Status> getConversionStatuses() {
        return conversionStatuses;
    }

    public void setConversionStatuses(List<Status> conversionStatuses) {
        this.conversionStatuses = conversionStatuses;
    }

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
}
