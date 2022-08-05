package com.foros.action.campaign.bulk;

import com.foros.action.bulk.CsvRow;
import com.foros.model.EntityBase;
import com.foros.session.campaign.BulkCampaignToolsService;
import com.foros.session.campaign.CampaignStatsTO;
import com.foros.session.campaign.CampaignStatsTO.Builder;

public class StatsCsvNodeWriter implements CombiningCsvNodeWriter<EntityBase> {

    @Override
    public void write(CsvRow row, EntityBase entity) {
        CampaignStatsTO stats = entity.getProperty(BulkCampaignToolsService.CAMPAIGN_STATS);
        if (stats != null) {
            row.set(CampaignFieldCsv.Impressions, stats.getImps());
            row.set(CampaignFieldCsv.Clicks, stats.getClicks());
            row.set(CampaignFieldCsv.CTR, stats.getCtr());
            row.set(CampaignFieldCsv.Cost, stats.getSpentBudget());
        }
    }

    @Override
    public void write(CsvRow row, EntityBase entity, EntityBase entity2) {
        mergeStats(entity, entity2);
        write(row, entity);
    }

    private void mergeStats(EntityBase entityTo, EntityBase entityFrom) {
        CampaignStatsTO statsTo = entityTo.getProperty(BulkCampaignToolsService.CAMPAIGN_STATS);
        CampaignStatsTO statsFrom = entityFrom.getProperty(BulkCampaignToolsService.CAMPAIGN_STATS);
        if (statsTo == null) {
            statsTo = statsFrom;
        } else if (statsFrom != null) {
            statsTo = new Builder()
                    .spentBudget(statsTo.getSpentBudget().add(statsFrom.getSpentBudget()))
                    .imps(statsTo.getImps() + statsFrom.getImps())
                    .clicks(statsTo.getClicks() + statsFrom.getClicks())
                    .build();
        }
        entityTo.setProperty(BulkCampaignToolsService.CAMPAIGN_STATS, statsTo);
    }
}
