package com.foros.reporting.tools;

import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.serializer.ResultSerializer;

public class CustomSummaryRowTypeWrapper extends ResultSerializerWrapper {

    private RowType rowType;

    public CustomSummaryRowTypeWrapper(ResultSerializer resultSerializer, RowType rowType) {
        super(resultSerializer);
        this.rowType = rowType;
    }

    @Override
    public ResultSerializer summary(MetaData metaData, final Row row) {
        Row summaryRow = new Row() {

            @Override
            public Object get(Column column) {
                return row.get(column);
            }

            @Override
            public RowType getType() {
                return rowType;
            }
        };

        return super.summary(metaData, summaryRow);
    }
}
