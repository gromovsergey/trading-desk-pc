package com.foros.reporting.rowsource.olap.saiku;

import com.foros.reporting.meta.olap.OlapColumn;
import com.phorm.oix.saiku.cellset.SaikuCellsRow;

public interface SaikuCellSetValueReader<T> {

    T read(SaikuCellsRow row, OlapColumn column, Object context);

}
