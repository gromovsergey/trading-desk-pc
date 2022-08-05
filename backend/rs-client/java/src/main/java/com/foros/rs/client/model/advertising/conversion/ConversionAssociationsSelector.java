package com.foros.rs.client.model.advertising.conversion;

import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

@QueryEntity
public class ConversionAssociationsSelector {

    @QueryParameter("conversion.id")
    private Long conversionId;

    public Long getConversionId() {
        return conversionId;
    }

    public void setConversionId(Long conversionId) {
        this.conversionId = conversionId;
    }
}
