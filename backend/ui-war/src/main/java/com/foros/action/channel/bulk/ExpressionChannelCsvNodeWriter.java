package com.foros.action.channel.bulk;

import static com.foros.action.channel.bulk.ChannelFieldCsv.Expression;

import com.foros.action.bulk.CsvNodeWriter;
import com.foros.action.bulk.CsvRow;
import com.foros.model.channel.ExpressionChannel;

public class ExpressionChannelCsvNodeWriter extends AbstractChannelCsvNodeWriter implements CsvNodeWriter<ExpressionChannel> {

    @Override
    public void write(CsvRow row, ExpressionChannel entity) {
        super.write(row, entity);
        row.set(Expression, entity.getExpression());
    }
}
