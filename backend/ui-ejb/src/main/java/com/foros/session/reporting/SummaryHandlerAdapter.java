package com.foros.session.reporting;

import com.foros.reporting.Row;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.serializer.ResultSerializerSupport;
import com.foros.reporting.serializer.formatter.FormatterContext;

public class SummaryHandlerAdapter extends ResultSerializerSupport<SummaryHandlerAdapter> {

    private ResultSerializer target;

    public SummaryHandlerAdapter(ResultSerializer target) {
        super(null, new FormatterContext(null));
        this.target = target;
    }

    @Override
    public void row(Row row) {
        super.row(row);
        target.summary(metaData, row);
    }


}
