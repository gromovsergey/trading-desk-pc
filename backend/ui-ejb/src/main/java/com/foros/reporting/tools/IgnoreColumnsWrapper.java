package com.foros.reporting.tools;

import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.serializer.ResultSerializer;

import java.util.Set;

public class IgnoreColumnsWrapper extends ResultSerializerWrapper {
    private Set<? extends Column> columns;
    private RowWrapper rowWrapper;

    public IgnoreColumnsWrapper(ResultSerializer serializer, Set<? extends Column> columns) {
        super(serializer);
        this.columns = columns;
        this.rowWrapper = new RowWrapper();
    }

    @Override
    public void row(Row row) {
        rowWrapper.setTarget(row);
        super.row(rowWrapper);
    }

    @Override
    public ResultSerializer summary(MetaData metaData, Row row) {
        rowWrapper.setTarget(row);
        return super.summary(metaData, rowWrapper);
    }

    private class RowWrapper implements Row {
        private Row target;

        @Override
        public Object get(Column column) {
            if (columns.contains(column)) {
                return null;
            }
            return target.get(column);
        }

        @Override
        public RowType getType() {
            return target.getType();
        }

        public void setTarget(Row target) {
            this.target = target;
        }
    }
}
