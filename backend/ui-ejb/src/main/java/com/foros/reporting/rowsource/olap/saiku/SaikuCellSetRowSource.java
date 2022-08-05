package com.foros.reporting.rowsource.olap.saiku;

import com.foros.reporting.Row;
import com.foros.reporting.rowsource.RowSource;
import com.phorm.oix.saiku.cellset.SaikuCellSet;
import java.util.Iterator;

public class SaikuCellSetRowSource implements RowSource {

    private SaikuCellSet cellSet;
    private Object context;
    private SaikuCellSetValueReaderRegistry readerRegistry;

    public SaikuCellSetRowSource(SaikuCellSet cellSet, Object context, SaikuCellSetValueReaderRegistry readerRegistry) {
        this.cellSet = cellSet;
        this.context = context;
        this.readerRegistry = readerRegistry;
    }

    @Override
    public Iterator<Row> iterator() {
        return new SaikuCellSetRowIterator(cellSet, context, readerRegistry);
    }
}
