package com.foros.reporting.rowsource.olap.saiku;

import com.foros.reporting.Row;
import com.phorm.oix.saiku.cellset.SaikuCellSet;
import com.phorm.oix.saiku.cellset.SaikuCellsRow;
import java.util.Iterator;

public class SaikuCellSetRowIterator implements Iterator<Row> {

    private SaikuCellSet cellSet;
    private Iterator<SaikuCellsRow> iterator;
    private SaikuCellSetRow row;

    public SaikuCellSetRowIterator(SaikuCellSet cellSet, Object context, SaikuCellSetValueReaderRegistry readerRegistry) {
        this.cellSet = cellSet;
        this.iterator = this.cellSet.getRows();
        this.row = new SaikuCellSetRow(cellSet, readerRegistry, context);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Row next() {
        row.setCells(iterator.next());
        return row;
    }

    @Override
    public void remove() {
        throw new RuntimeException("Method not supported");
    }

}
