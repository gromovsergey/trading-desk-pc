package com.foros.action.admin.country.placementsBlacklist;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;
import com.foros.model.EntityBase;
import com.foros.model.ExtensionProperty;
import com.foros.model.channel.placementsBlacklist.BlacklistAction;
import com.foros.model.channel.placementsBlacklist.BlacklistReason;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.reporting.meta.MetaData;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.util.StringUtil;
import com.foros.util.UploadUtils;
import com.foros.util.bulk.BulkReader;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.interpolator.MessageInterpolator;
import com.foros.validation.interpolator.StringUtilsMessageInterpolator;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;


public class PlacementBlacklistCsvReader {

    private static final Pattern CODES_PATTERN = Pattern.compile("[\\s,]*(\\w{1})[\\s,]*");

    public static final ExtensionProperty<String[]> ORIGINAL_VALUES = new ExtensionProperty<String[]>(String[].class);

    private MetaData<PlacementBlacklistFieldCsv> metaData;
    private List<PlacementBlacklistFieldCsv> columns;
    private BulkReader reader;
    private List<PlacementBlacklist> placements;
    private UploadContext currentStatus;
    private BulkReader.BulkReaderRow currentRow;


    public PlacementBlacklistCsvReader(BulkReader reader) {
        this.reader = reader;
        metaData = MetaDataBuilder.UPLOAD_COLUMNS;
        columns = metaData.getColumns();
    }

    public List<PlacementBlacklist> parse() throws IOException {
        placements = new LinkedList<PlacementBlacklist>();
        Locale locale = CurrentUserSettingsHolder.getLocale();
        final MessageInterpolator interpolator = new StringUtilsMessageInterpolator(locale);

        final Set<Integer> allowedColumnsCount = new HashSet<Integer>(Arrays.asList(
                MetaDataBuilder.UPLOAD_COLUMNS.getColumns().size(),
                MetaDataBuilder.UPLOAD_COLUMNS.getColumns().size(),
                MetaDataBuilder.REVIEW_COLUMNS.getColumns().size()
        ));

        reader.setBulkReaderHandler(new BulkReader.BulkReaderHandler() {
            @Override
            public void handleRow(BulkReader.BulkReaderRow row) {
                long line = row.getRowNum();
                if (line == 1) {
                    if (!allowedColumnsCount.contains(row.getColumnCount())) {
                        throw ConstraintViolationException.newBuilder("errors.invalid.header").build();
                    }
                    return;
                }

                currentRow = row;
                currentStatus = new UploadContext();

                if (!allowedColumnsCount.contains(row.getColumnCount())) {
                    String allowed = StringUtils.join(allowedColumnsCount, ", ");
                    String actual = String.valueOf(row.getColumnCount());
                    currentStatus.addFatal("errors.invalid.rowFormat").withParameters(allowed, actual);
                }

                PlacementBlacklist placement = readPlacement();
                UploadUtils.setRowNumber(placement, line);

                currentStatus.flush(interpolator);
                if (currentStatus.getStatus() == UploadStatus.REJECTED) {
                    assignOriginalValues(placement);
                }
                placement.setProperty(UPLOAD_CONTEXT, currentStatus);
                placements.add(placement);
            }
        });

        reader.read();

        return placements;
    }

    private PlacementBlacklist readPlacement() {
        PlacementBlacklist placement = new PlacementBlacklist();
        if (currentStatus.isFatal()) {
            return placement;
        }

        placement.setUrl(readString(PlacementBlacklistFieldCsv.Url));
        placement.setSizeName(readString(PlacementBlacklistFieldCsv.AdSize));
        readAction(placement);
        readReason(placement);

        return placement;
    }

    private void readAction(PlacementBlacklist placement) {
        PlacementBlacklistFieldCsv actionColumn = PlacementBlacklistFieldCsv.Action;
        String value = null;
        try {
            value = readString(actionColumn);
            placement.setAction(BlacklistAction.parse(value));
        } catch (IllegalArgumentException e) {
            currentStatus
                    .addError("admin.placementsBlacklist.csv.error.invalidAction")
                    .withPath(actionColumn.getFieldPath())
                    .withValue(value);
        }
    }

    private void readReason(PlacementBlacklist placement) {
        PlacementBlacklistFieldCsv reasonColumn = PlacementBlacklistFieldCsv.Reason;
        String value = null;
        try {
            value = readString(reasonColumn);
            placement.setReason(getBlacklistReasonSetFromString(value));
        } catch (IllegalArgumentException e) {
            currentStatus
                    .addError("admin.placementsBlacklist.csv.error.invalidReason")
                    .withPath(reasonColumn.getFieldPath())
                    .withValue(value);
        }
    }

    private String readString(PlacementBlacklistFieldCsv column) {
        int index = metaData.getColumns().indexOf(column);
        String str = index != -1 ? StringUtil.trimProperty(currentRow.getStringValue(index)) : null;
        return "".equals(str) ? null : str;
    }

    protected void addError(PlacementBlacklistFieldCsv field, String key) {
        if (!currentStatus.getWrongPaths().contains(field.getFieldPath())) {
            currentStatus.addError(key).withPath(field.getFieldPath());
        }
    }

    private void assignOriginalValues(EntityBase entity) {
        String[] record = new String[PlacementBlacklistFieldCsv.TOTAL_COLUMNS_COUNT];
        for (PlacementBlacklistFieldCsv column : columns) {
            int index = metaData.getColumns().indexOf(column);
            if (currentRow.getColumnCount() > index) {
                record[column.ordinal()] = currentRow.getStringValue(index);
            }
        }
        entity.setProperty(ORIGINAL_VALUES, record);
    }

    private Set<BlacklistReason> getBlacklistReasonSetFromString(String codesList) {
        if (StringUtil.isPropertyEmpty(codesList)) {
            return null;
        }

        HashSet<BlacklistReason> result = new HashSet<>();
        Matcher matcher = CODES_PATTERN.matcher(codesList);
        while (matcher.find()) {
            String code = matcher.group(1).trim();
            if (code.length() == 1) {
                BlacklistReason reason = BlacklistReason.valueOf(code.toUpperCase().charAt(0));
                if (reason != null) {
                    result.add(reason);
                    continue;
                }
                throw new IllegalArgumentException("Can't understand blacklist reason: " + code);
            }
        }

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Can't fetch blacklist reason from: " + codesList);
        }

        return result;
    }
}
