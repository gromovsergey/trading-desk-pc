package com.foros.action.admin.country.placementsBlacklist;

import static com.foros.action.admin.country.placementsBlacklist.PlacementBlacklistFieldCsv.Action;
import static com.foros.action.admin.country.placementsBlacklist.PlacementBlacklistFieldCsv.AdSize;
import static com.foros.action.admin.country.placementsBlacklist.PlacementBlacklistFieldCsv.Reason;
import static com.foros.action.admin.country.placementsBlacklist.PlacementBlacklistFieldCsv.Url;
import com.foros.action.bulk.CsvNodeWriter;
import com.foros.action.bulk.CsvRow;
import com.foros.model.channel.placementsBlacklist.BlacklistReason;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;

import java.util.Set;

public class PlacementsBlacklistCsvNodeWriter implements CsvNodeWriter<PlacementBlacklist> {

    @Override
    public void write(CsvRow row, PlacementBlacklist entity) {
        row.set(Url, entity.getUrl());
        row.set(AdSize, entity.getSizeName());
        row.set(Action, entity.getAction() == null ? null : entity.getAction().toString());
        row.set(Reason, getStringFromBlacklistReasonSet(entity.getReason()));
    }

    private String getStringFromBlacklistReasonSet(Set<BlacklistReason> enumList) {
        if (enumList == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (BlacklistReason blacklistReason : enumList) {
            if (first) {
                first = false;
            } else {
                result.append(", ");
            }
            result.append(blacklistReason.getCode());
        }

        return result.toString();
    }
}
