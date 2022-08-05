package com.foros.action.admin.country.placementsBlacklist;

import static com.foros.action.admin.country.placementsBlacklist.PlacementBlacklistFieldCsv.Action;
import static com.foros.action.admin.country.placementsBlacklist.PlacementBlacklistFieldCsv.AdSize;
import static com.foros.action.admin.country.placementsBlacklist.PlacementBlacklistFieldCsv.Errors;
import static com.foros.action.admin.country.placementsBlacklist.PlacementBlacklistFieldCsv.Reason;
import static com.foros.action.admin.country.placementsBlacklist.PlacementBlacklistFieldCsv.Url;
import static com.foros.action.admin.country.placementsBlacklist.PlacementBlacklistFieldCsv.ValidationStatus;

import com.foros.action.bulk.BulkMetaData;

public class MetaDataBuilder {

    public static final BulkMetaData<PlacementBlacklistFieldCsv> UPLOAD_COLUMNS = new BulkMetaData<PlacementBlacklistFieldCsv>(
            Url,
            AdSize,
            Action,
            Reason
    );

    public static final BulkMetaData<PlacementBlacklistFieldCsv> EXPORT_COLUMNS = UPLOAD_COLUMNS;
    public static final BulkMetaData<PlacementBlacklistFieldCsv> REVIEW_COLUMNS = UPLOAD_COLUMNS.append(ValidationStatus, Errors);
}

