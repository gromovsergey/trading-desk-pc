package com.foros.reporting.rowsource.olap.saiku;

import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.olap.OlapColumn;
import com.phorm.oix.saiku.cellset.SaikuCellSet;
import com.phorm.oix.saiku.cellset.SaikuCellsRow;

public class SaikuCellSetRow implements Row<OlapColumn> {

    private SaikuCellSet cellSet;
    private SaikuCellSetValueReaderRegistry readerRegistry;
    private Object context;
    private SaikuCellsRow row;

    public SaikuCellSetRow(SaikuCellSet cellSet, SaikuCellSetValueReaderRegistry readerRegistry, Object context) {
        this.cellSet = cellSet;
        this.readerRegistry = readerRegistry;
        this.context = context;
    }

    public void setCells(SaikuCellsRow row) {
        this.row = row;
    }

    @Override
    public Object get(OlapColumn column) {
        SaikuCellSetValueReader<Object> reader
                = (SaikuCellSetValueReader<Object>) readerRegistry.get(column);

        return reader.read(row, column, context);
    }

    @Override
    public RowType getType() {
        return RowTypes.data();
    }

}
