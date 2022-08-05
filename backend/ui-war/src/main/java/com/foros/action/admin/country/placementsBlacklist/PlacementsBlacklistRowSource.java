package com.foros.action.admin.country.placementsBlacklist;

import com.foros.action.bulk.CsvNodeWriter;
import com.foros.action.bulk.CsvRow;
import com.foros.model.EntityBase;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.reporting.Row;
import com.foros.reporting.rowsource.RowSource;

import java.util.Iterator;

public class PlacementsBlacklistRowSource  implements RowSource, Iterator<Row> {

    private Iterator<? extends PlacementBlacklist> iterator;
    private CsvNodeWriter nodeWriter;

    public PlacementsBlacklistRowSource(Iterator<? extends PlacementBlacklist> iterator, CsvNodeWriter nodeWriter) {
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
        CsvRow row = new CsvRow(PlacementBlacklistFieldCsv.TOTAL_COLUMNS_COUNT);
        nodeWriter.write(row, currentEntity);
        return row;
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
