package com.foros.session.reporting.channeltriggers;

import com.foros.reporting.Row;
import com.foros.reporting.TooManyRowsException;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.serializer.ResultHolder;
import com.foros.reporting.serializer.ResultSerializerSupport;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryHolder;
import com.foros.session.reporting.PreparedParameterBuilder;
import java.util.Locale;

public class ChannelTriggerResultHolder extends ResultSerializerSupport<ChannelTriggerResultHolder> {

    private ResultHolder current;

    private int rowsLeft;
    private int maxRows;

    private ChannelTriggerReportData data;
    private Locale locale;

    public ChannelTriggerResultHolder(int maxRows, ValueFormatterRegistryHolder customRegistry, Locale locale, ChannelTriggerReportData data) {
        super(customRegistry, new FormatterContext(locale));
        this.maxRows = this.rowsLeft = maxRows;
        this.locale = locale;
        this.data = data;
    }

    @Override
    public void before(MetaData metaData) {
        super.before(metaData);

        if (current != null) {
            rowsLeft -= current.getReportData().getRows().size();
        }

        SimpleReportData reportData = new SimpleReportData();
        data.add(metaData, reportData);
        current = new ResultHolder(rowsLeft, reportData, registryHolder, locale);
        current.before(metaData);
    }

    @Override
    public void row(Row row) {
        super.row(row);
        try {
            current.row(row);
        } catch (TooManyRowsException e) {
            data.setPartialResult(true);
            throw new TooManyRowsException(maxRows);
        }
    }

    @Override
    public ChannelTriggerResultHolder preparedParameters(PreparedParameterBuilder.Factory factory) {
        this.data.setPreparedParameters(factory.builder(locale).parameters());
        return this;
    }

    @Override
    public int getMaxRows() {
        return maxRows;
    }
}
