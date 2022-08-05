package com.foros.action.creative.csv;

import com.foros.action.bulk.CsvNodeWriter;
import com.foros.model.EntityBase;
import com.foros.model.creative.Creative;
import com.foros.reporting.Row;
import com.foros.reporting.rowsource.RowSource;

import java.util.Collection;
import java.util.Iterator;

public class CreativeRowSource implements RowSource, Iterator<Row> {

    private Iterator<? extends Creative> iterator;
    private CsvNodeWriter nodeWriter;
    private int columnsCount;

    public CreativeRowSource(CsvNodeWriter nodeWriter, Collection<Creative> creatives, int columnsCount) {
        this.iterator = creatives.iterator();
        this.nodeWriter = nodeWriter;
        this.columnsCount = columnsCount;
    }

    protected void init(Iterator<Creative> iterator, CsvNodeWriter nodeWriter, int totalColumnsCount) {
        this.iterator = iterator;
        this.nodeWriter = nodeWriter;
        this.columnsCount = totalColumnsCount;
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
    public CreativeCsvRow next() {
        EntityBase currentEntity = iterator.next();
        CreativeCsvRow row = new CreativeCsvRow(columnsCount);
        nodeWriter.write(row, currentEntity);
        return row;
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
