package com.foros.action.campaign.bulk;

import com.foros.action.bulk.CsvRow;
import com.foros.model.EntityBase;
import com.foros.reporting.Row;
import com.foros.util.UploadUtils;

import java.util.Iterator;

public class CampaignDownloadRowSource extends AbstractCampaignRowSource {
    private MainCampaignCsvNodeWriter writer;

    public CampaignDownloadRowSource(Iterator<EntityBase> iterator, MainCampaignCsvNodeWriter writer) {
        this.iterator = iterator;
        this.writer = writer;
    }

    @Override
    protected Row getRow(EntityBase entity) {
        CsvRow row = new CampaignCsvRow();
        writer.write(row, entity);
        return row;
    }

    @Override
    protected Row getRow(EntityBase entity, EntityBase nextEntity) {
        CsvRow row = new CampaignCsvRow();
        writer.write(row, entity, nextEntity);
        return row;
    }

    @Override
    protected boolean isCombinable(EntityBase currentEntity, EntityBase nextEntity) {
        if (UploadUtils.getRowNumber(currentEntity).equals(UploadUtils.getRowNumber(nextEntity))) {
            return true;
        }
        return false;
    }
}
