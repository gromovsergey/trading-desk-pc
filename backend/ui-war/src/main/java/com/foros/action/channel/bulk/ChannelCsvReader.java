package com.foros.action.channel.bulk;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;
import com.foros.model.Country;
import com.foros.model.EntityBase;
import com.foros.model.ExtensionProperty;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersUnits;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.trigger.TriggerType;
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
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

public class ChannelCsvReader {

    public static final ExtensionProperty<String[]> ORIGINAL_VALUES = new ExtensionProperty<String[]>(String[].class);

    private MetaData<ChannelFieldCsv> metaData;
    private MetaDataBuilder metaDataBuilder;
    private List<ChannelFieldCsv> columns;
    private BulkReader reader;

    private List<Channel> channels;
    private UploadContext currentStatus;

    private BulkReader.BulkReaderRow currentRow;

    public ChannelCsvReader(BulkReader reader, MetaDataBuilder builder) {
        this.reader = reader;
        metaDataBuilder = builder;
        metaData = builder.forUpload();
        columns = metaData.getColumns();
    }

    public List<Channel> parse() throws IOException {
        channels = new LinkedList<Channel>();
        Locale locale = CurrentUserSettingsHolder.getLocale();
        final MessageInterpolator interpolator = new StringUtilsMessageInterpolator(locale);

        final Set<Integer> allowedColumnsCount = new HashSet<Integer>(Arrays.asList(
                metaDataBuilder.forUpload().getColumns().size(),
                metaDataBuilder.forExport().getColumns().size(),
                metaDataBuilder.forReview().getColumns().size()
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

                Channel channel = readChannel();
                UploadUtils.setRowNumber(channel, line);

                currentStatus.flush(interpolator);
                if (currentStatus.getStatus() == UploadStatus.REJECTED) {
                    assignOriginalValues(channel);
                }
                channel.setProperty(UPLOAD_CONTEXT, currentStatus);
                channels.add(channel);
            }
        });

        reader.read();

        return channels;
    }

    private Channel readChannel() {
        switch (metaDataBuilder.getChannelType()) {
            case EXPRESSION:
                return readExpressionChannel();
            case BEHAVIORAL:
                return readBehavioralChannel();
        }
        return null;
    }


    private ExpressionChannel readExpressionChannel() {
        ExpressionChannel channel = new ExpressionChannel();
        if (currentStatus.isFatal()) {
            return channel;
        }
        readCommonProperties(channel);
        channel.setExpression(readString(ChannelFieldCsv.Expression, true));
        return channel;
    }

    private BehavioralChannel readBehavioralChannel() {
        BehavioralChannel channel = new BehavioralChannel();
        if (currentStatus.isFatal()) {
            return channel;
        }
        readCommonProperties(channel);

        channel.getUrls().setPositive(readTriggers(ChannelFieldCsv.Url));
        channel.getUrls().setNegative(readTriggers(ChannelFieldCsv.UrlNegative));

        channel.getSearchKeywords().setPositive(readTriggers(ChannelFieldCsv.SearchKeyword));
        channel.getSearchKeywords().setNegative(readTriggers(ChannelFieldCsv.SearchKeywordNegative));

        channel.getPageKeywords().setPositive(readTriggers(ChannelFieldCsv.PageKeyword));
        channel.getPageKeywords().setNegative(readTriggers(ChannelFieldCsv.PageKeywordNegative));

        channel.getUrlKeywords().setPositive(readTriggers(ChannelFieldCsv.UrlKeyword));
        channel.getUrlKeywords().setNegative(readTriggers(ChannelFieldCsv.UrlKeywordNegative));

        channel.setBehavioralParameters(readBehavioralParameters());
        return channel;

    }

    private String[] readTriggers(ChannelFieldCsv column) {
        String str = readString(column);
        if (StringUtil.isPropertyEmpty(str)) {
            return new String[0];
        } else {
            return StringUtil.splitAndTrim(str);
        }
    }

    private Set<BehavioralParameters> readBehavioralParameters() {
        Set<BehavioralParameters> behavioralParameters = new LinkedHashSet<BehavioralParameters>(3);

        addBehavioralParams(
                behavioralParameters,
                ChannelFieldCsv.UrlCount,
                ChannelFieldCsv.UrlFrom,
                ChannelFieldCsv.UrlTo,
                ChannelFieldCsv.UrlUnit,
                TriggerType.URL
        );

        addBehavioralParams(
                behavioralParameters,
                ChannelFieldCsv.PageKeywordCount,
                ChannelFieldCsv.PageKeywordFrom,
                ChannelFieldCsv.PageKeywordTo,
                ChannelFieldCsv.PageKeywordUnit,
                TriggerType.PAGE_KEYWORD
        );

        addBehavioralParams(
                behavioralParameters,
                ChannelFieldCsv.SearchKeywordCount,
                ChannelFieldCsv.SearchKeywordFrom,
                ChannelFieldCsv.SearchKeywordTo,
                ChannelFieldCsv.SearchKeywordUnit,
                TriggerType.SEARCH_KEYWORD
        );

        addBehavioralParams(
                behavioralParameters,
                ChannelFieldCsv.UrlKeywordCount,
                ChannelFieldCsv.UrlKeywordFrom,
                ChannelFieldCsv.UrlKeywordTo,
                ChannelFieldCsv.UrlKeywordUnit,
                TriggerType.URL_KEYWORD
        );

        return behavioralParameters;
    }

    private void addBehavioralParams(Set<BehavioralParameters> behavioralParameters, ChannelFieldCsv countFiled,
            ChannelFieldCsv fromField, ChannelFieldCsv toField, ChannelFieldCsv unitField, TriggerType type)  {
        Long count = readLong(countFiled);
        Long from = readLong(fromField);
        Long to = readLong(toField);
        BehavioralParametersUnits unit = readBehavioralParameterUnit(unitField);

        if (count != null || from != null || to != null || unit != null) {
            BehavioralParameters param = new BehavioralParameters();

            if (unit != null && from != null) {
                param.setTimeFrom(from * unit.getMultiplier());
            } else if (unit == null && from != null && from == 0L) {
                param.setTimeFrom(0L);
            } else {
                if (unit == null) {
                    addError(unitField, "errors.field.required");
                }
                if (from == null) {
                    addError(fromField, "errors.field.required");
                }
            }

            if (unit != null && to != null) {
                param.setTimeTo(to * unit.getMultiplier());
            } else if (unit == null && to != null && to == 0L) {
                param.setTimeTo(0L);
            } else {
                if (unit == null) {
                    addError(unitField, "errors.field.required");
                }
                if (to == null) {
                    addError(toField, "errors.field.required");
                }
            }

            param.setMinimumVisits(count);
            param.setTriggerType(type.getLetter());

            behavioralParameters.add(param);
        }
    }

    private BehavioralParametersUnits readBehavioralParameterUnit(ChannelFieldCsv column) {
        BehavioralParametersUnits unit = null;
        String unitName = readString(column);
        if (StringUtil.isPropertyNotEmpty(unitName)) {
            try {
                unit = BehavioralParametersUnits.byName(unitName);
            } catch (IllegalArgumentException e) {
                addError(column, "errors.field.invalid");
            }
        }
        return unit;
    }

    protected void readCommonProperties(Channel channel) {
        if (metaData.contains(ChannelFieldCsv.Account)) {
            channel.setAccount(new AdvertiserAccount(null, readString(ChannelFieldCsv.Account, true)));
        }
        channel.setName(readString(ChannelFieldCsv.Name, true));
        try {
            channel.setStatus(readStatus(ChannelFieldCsv.Status));
        } catch (IllegalArgumentException e) {
            addError(ChannelFieldCsv.Status, "errors.field.invalid");
            channel.setStatus(Status.ACTIVE);//can't assign null to status property
        }
        channel.setDescription(readString(ChannelFieldCsv.Description));
        channel.setCountry(readCountry(ChannelFieldCsv.Country));
        if (metaData.contains(ChannelFieldCsv.Visibility)) {
            channel.setVisibility(readVisibility(ChannelFieldCsv.Visibility));
        }
    }

    private Status readStatus(ChannelFieldCsv column) {
        String statusName = readString(column, true);
        if (statusName == null) {
            return Status.ACTIVE; //can't assign null to status property
        }

        try {
            return ChannelBulkHelper.parseStatus(statusName);
        } catch (IllegalArgumentException e) {
            addError(column, "errors.field.invalid");
            return Status.ACTIVE;
        }
    }

    private Country readCountry(ChannelFieldCsv field) {
        String countryCode = readString(field, true);
        Country country = StringUtil.isPropertyNotEmpty(countryCode) ? new Country(countryCode) : null;
        return country;
    }

    private Long readLong(ChannelFieldCsv column) {
        BigDecimal bd = readBigDecimal(column);
        if (bd == null) {
            return null;
        }

        try {
            return bd.toBigIntegerExact().longValue();
        } catch (ArithmeticException ex) {
            addError(column, "errors.field.integer");
            return null;
        }
    }

    private BigDecimal readBigDecimal(ChannelFieldCsv column) {
        int index = metaData.getColumns().indexOf(column);
        if (index == -1) {
            return null;
        }

        try {
            return currentRow.getNumericValue(index);
        } catch (ParseException e) {
            addError(column, "errors.field.number");
            return null;
        }
    }

    protected String readString(ChannelFieldCsv column, boolean required) {
        return readString(column, currentStatus, required);
    }

    private String readString(ChannelFieldCsv column, UploadContext context, boolean required) {
        int index = metaData.getColumns().indexOf(column);
        String str = index != -1 ? StringUtil.trimProperty(currentRow.getStringValue(index)) : null;
        if (StringUtil.isPropertyEmpty(str) && required) {
            context.addError("errors.field.required").withPath(column.getFieldPath());
        }
        return "".equals(str) ? null : str;
    }

    protected String readString(ChannelFieldCsv column) {
        return readString(column, false);
    }

    protected void addError(ChannelFieldCsv field, String key) {
        if (!currentStatus.getWrongPaths().contains(field.getFieldPath())) {
            currentStatus.addError(key).withPath(field.getFieldPath());
        }
    }

    private void assignOriginalValues(EntityBase entity) {
        String[] record = new String[ChannelFieldCsv.TOTAL_COLUMNS_COUNT];
        for (ChannelFieldCsv column : columns) {
            int index = metaData.getColumns().indexOf(column);
            if (currentRow.getColumnCount() > index) {
                record[column.ordinal()] = currentRow.getStringValue(index);
            }
        }
        entity.setProperty(ORIGINAL_VALUES, record);
    }

    private ChannelVisibility readVisibility(ChannelFieldCsv field) {
        String visibilityStr = readString(field);
        if (StringUtil.isPropertyEmpty(visibilityStr)) {
            return null;
        }

        try {
            return ChannelBulkHelper.stringToChannelVisibility(visibilityStr);
        } catch (IllegalArgumentException e) {
            addError(field, "errors.field.invalid");
            return null;
        }
    }
}
