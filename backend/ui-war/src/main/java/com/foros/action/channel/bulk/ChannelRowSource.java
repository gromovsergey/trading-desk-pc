package com.foros.action.channel.bulk;

import com.foros.action.bulk.CsvNodeWriter;
import com.foros.action.bulk.CsvRow;
import com.foros.model.EntityBase;
import com.foros.model.channel.Channel;
import com.foros.reporting.Row;
import com.foros.reporting.rowsource.RowSource;

import java.util.Iterator;

public class ChannelRowSource implements RowSource, Iterator<Row> {

    private Iterator<? extends Channel> iterator;
    private CsvNodeWriter nodeWriter;

    public ChannelRowSource(Iterator<? extends Channel> iterator, CsvNodeWriter nodeWriter) {
        this.iterator = iterator;
        this.nodeWriter = nodeWriter;
    }

    @Override
    public Iterator<Row> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Row next() {
        EntityBase currentEntity = iterator.next();
        CsvRow row = new CsvRow(ChannelFieldCsv.TOTAL_COLUMNS_COUNT);
        nodeWriter.write(row, currentEntity);
        return row;
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
