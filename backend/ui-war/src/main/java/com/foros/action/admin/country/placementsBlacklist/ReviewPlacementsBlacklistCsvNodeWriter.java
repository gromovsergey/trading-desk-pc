package com.foros.action.admin.country.placementsBlacklist;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;

import com.foros.action.bulk.CsvRow;
import com.foros.model.EntityBase;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.util.csv.ErrorMessageBuilder;
import com.foros.validation.constraint.convertion.SimpleConstraintViolationConverter;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;

import java.util.ArrayList;


public class ReviewPlacementsBlacklistCsvNodeWriter extends PlacementsBlacklistCsvNodeWriter {

    @Override
    public void write(CsvRow row, PlacementBlacklist placementBlacklist) {
        boolean originalsWritten = writeOriginal(row, placementBlacklist);
        if (!originalsWritten) {
            super.write(row, placementBlacklist);
        }
        writeStatus(row, placementBlacklist);
    }

    private boolean writeOriginal(CsvRow row, EntityBase entity) {
        String[] originalValues = entity.getProperty(PlacementBlacklistCsvReader.ORIGINAL_VALUES);
        if (originalValues == null) {
            return false;
        }

        for (PlacementBlacklistFieldCsv field : MetaDataBuilder.REVIEW_COLUMNS.getColumns()) {
            row.set(field, originalValues[field.ordinal()]);
        }
        row.setUnparsed(true);
        return true;
    }

    private void writeStatus(CsvRow row, EntityBase entity) {
        UploadContext context = entity.getProperty(UPLOAD_CONTEXT);
        row.set(PlacementBlacklistFieldCsv.ValidationStatus, context.getStatus().name());
        if (context.getStatus() == UploadStatus.REJECTED) {
            ErrorMessageBuilder builder = new ErrorMessageBuilder<>(PlacementBlacklistFieldCsv.values(), entity.getClass());
            SimpleConstraintViolationConverter converter = new SimpleConstraintViolationConverter(builder);
            converter.applyRules(new ArrayList<ConstraintViolationRule>(), context.getErrors());
            row.set(PlacementBlacklistFieldCsv.Errors, builder.build());
        } else {
            row.set(PlacementBlacklistFieldCsv.Errors, null);
        }
    }
}
