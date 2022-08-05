package com.foros.reporting.tools.query.cache;

import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.meta.Column;
import com.foros.reporting.rowsource.RowSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class CacheRowSource implements RowSource {

    private String[] columns;
    private List<RowStorage> rows = new ArrayList<RowStorage>();

    CacheRowSource() {
    }

    public void setColumns(List<Column> columns) {
        this.columns = new String[columns.size()];
        int index = 0;
        for (Column column : columns) {
            this.columns[index] = column.getNameKey();
            index++;
        }
    }

    public void add(RowType rowType, Object[] row) {
        rows.add(new RowStorage(rowType, row));
    }

    @Override
    public Iterator<Row> iterator() {
        return new IteratorImpl(rows.iterator(), columns);
    }

    private static class IteratorImpl implements Iterator<Row> {

        private RowImpl row;

        private Iterator<RowStorage> iterator;

        public IteratorImpl(Iterator<RowStorage> iterator, String[] columns) {
            this.iterator = iterator;
            this.row = new RowImpl(columns);
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Row next() {
            row.set(iterator.next());
            return row;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class RowImpl implements Row {

        private Map<String, Integer> columns;
        private RowStorage storage;

        private RowImpl(String[] columns) {
            this.columns = map(columns);
        }

        private Map<String, Integer> map(String[] columns) {
            HashMap<String, Integer> result = new HashMap<String, Integer>();

            int index = 0;
            for (String column : columns) {
                result.put(column, index);
                index++;
            }

            return result;
        }

        public void set(RowStorage storage) {
            this.storage = storage;
        }

        @Override
        public Object get(Column column) {
            Integer index = columns.get(column.getNameKey());

            if (index == null) {
                return null;
            }

            return storage.get(index);
        }

        @Override
        public RowType getType() {
            return storage.getRowType();
        }

    }

}
