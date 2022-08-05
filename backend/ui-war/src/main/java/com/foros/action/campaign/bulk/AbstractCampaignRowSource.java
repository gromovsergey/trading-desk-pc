package com.foros.action.campaign.bulk;

import com.foros.action.bulk.CsvRow;
import com.foros.model.EntityBase;
import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.rowsource.RowSource;

import java.util.Iterator;

public abstract class AbstractCampaignRowSource implements RowSource, Iterator<Row> {
    protected Iterator<EntityBase> iterator;
    private EntityBase currentEntity;

    @Override
    public Iterator<Row> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return currentEntity != null || iterator.hasNext();
    }

    @Override
    public Row next() {
        EntityBase entity = currentEntity;
        currentEntity = null;

        if (entity == null) {
            entity = iterator.next();
        }

        if (iterator.hasNext()) {
            EntityBase nextEntity = iterator.next();
            if (isCombinable(entity, nextEntity)) {
                return getRow(entity, nextEntity);
            } else {
                currentEntity = nextEntity;
            }
        }

        return getRow(entity);
    }

    protected abstract Row getRow(EntityBase entity);
    protected abstract Row getRow(EntityBase entity, EntityBase nextEntity);

    protected abstract boolean isCombinable(EntityBase currentEntity, EntityBase nextEntity);

    @Override
    public void remove() {
        iterator.remove();
    }

    protected static class CampaignCsvRow extends CsvRow {
        public CampaignCsvRow() {
            super(CampaignFieldCsv.TOTAL_COLUMNS_COUNT);
        }

        @Override
        public RowType getType() {
            if (isUnparsed()) {
                return UNPARSED_ROW_TYPE;
            } else {
                CampaignLevelCsv level = (CampaignLevelCsv) get(CampaignFieldCsv.Level);
                return level.getRowType();
            }
        }
    }
}
