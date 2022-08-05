package com.foros.action.campaign.bulk;

import com.foros.action.bulk.CsvRow;
import com.foros.model.EntityBase;
import com.foros.model.campaign.CCGKeyword;
import com.foros.reporting.Row;

import java.util.Iterator;

public class CampaignExportRowSource extends AbstractCampaignRowSource {
    private static final StatsCsvNodeWriter STATS_WRITER = new StatsCsvNodeWriter();

    private MainCampaignCsvNodeWriter writer;

    public CampaignExportRowSource(Iterator<EntityBase> iterator, MainCampaignCsvNodeWriter writer) {
        this.iterator = iterator;
        this.writer = writer;
    }

    @Override
    protected Row getRow(EntityBase entity) {
        CsvRow row = new CampaignCsvRow();
        writer.write(row, entity);
        STATS_WRITER.write(row, entity);
        return row;
    }

    @Override
    protected Row getRow(EntityBase entity, EntityBase nextEntity) {
        CsvRow row = new CampaignCsvRow();
        writer.write(row, entity, nextEntity);
        STATS_WRITER.write(row, entity, nextEntity);
        return row;
    }

    @Override
    protected boolean isCombinable(EntityBase currentEntity, EntityBase nextEntity) {
        if (!(currentEntity instanceof CCGKeyword) || !(nextEntity instanceof CCGKeyword)) {
            return false;
        }

        CCGKeyword kw1 = (CCGKeyword) currentEntity;
        CCGKeyword kw2 = (CCGKeyword) nextEntity;

        if (kw1 == kw2) {
            return true;
        }

        if (kw1.getStatus() != null ? !kw1.getStatus().equals(kw2.getStatus()) : kw2.getStatus() != null) {
            return false;
        }
        if (kw1.getOriginalKeyword() != null ? !kw1.getOriginalKeyword().equals(kw2.getOriginalKeyword()) : kw2.getOriginalKeyword() != null) {
            return false;
        }
        if (kw1.getMaxCpcBid() != null ? !kw1.getMaxCpcBid().equals(kw2.getMaxCpcBid()) : kw2.getMaxCpcBid() != null) {
            return false;
        }
        if (kw1.getClickURL() != null ? !kw1.getClickURL().equals(kw2.getClickURL()) : kw2.getClickURL() != null) {
            return false;
        }

        return true;
    }
}
