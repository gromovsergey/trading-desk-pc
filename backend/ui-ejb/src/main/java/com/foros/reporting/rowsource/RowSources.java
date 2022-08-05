package com.foros.reporting.rowsource;

import com.foros.reporting.Row;
import java.util.Collections;
import java.util.Iterator;

public abstract class RowSources {

    private static final RowSource EMPTY = new EmptyRowSource();

    public static RowSource empty() {
        return EMPTY;
    }

    private static final class EmptyRowSource implements RowSource {
        @Override
        public Iterator<Row> iterator() {
            return Collections.emptyIterator();
        }
    }
}
