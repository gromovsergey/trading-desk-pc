package com.foros.action.admin.keywordChannel;

import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.Country;
import com.foros.model.FrequencyCap;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersUnits;
import com.foros.model.channel.KeywordChannel;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.model.time.TimeSpan;
import com.foros.reporting.serializer.BulkFormat;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.channel.service.KeywordChannelService;
import com.foros.util.StringUtil;
import com.foros.util.UploadUtils;
import com.foros.util.csv.BaseBulkHelper;
import com.foros.util.csv.CsvFormatException;
import com.foros.util.csv.CsvReader;
import com.foros.util.csv.FileFormatException;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class UploadKeywordChannelAction extends BaseActionSupport implements BreadcrumbsSupport {

    private static final int MAX_UPLOAD_CHANNELS = 65000;

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add(
                    "channels[(#index)].frequencyCap",
                    "messageForColumn(groups[0], getText('frequency.caps'), violation.message)"
            )
            .add(
                    "channels[(#index)].behavioralParameters[(#key)]",
                    "messageForColumn(groups[0], behavioralParametersFieldName(null), violation.message)"
            )
            .add(
                    "channels[(#index)].behavioralParameters[(#key)].(#property)",
                    "messageForColumn(groups[0], behavioralParametersFieldName(groups[2]), violation.message)"
            )
            .add(
                    "channels[(#index)].(#path)",
                    "messageForColumn(groups[0], fieldName(groups[1]), violation.message)"
            )
            .rules();

    @EJB
    private KeywordChannelService keywordChannelService;

    private File csvFile;
    private BulkFormat format = BulkFormat.CSV;

    private String csvFileFileName;

    private List<String> lastLineErrors = new ArrayList<String>(4);

    private List<KeywordChannel> channels;

    @ReadOnly
    @Restrict(restriction = "KeywordChannel.update")
    public String upload() throws Exception {
        return SUCCESS;
    }

    @Validations(
        requiredFields = {
                @RequiredFieldValidator(fieldName = "csvFile", key = "errors.field.required"),
                @RequiredFieldValidator(fieldName = "format", key = "errors.field.required")
        },
        customValidators = {
            @CustomValidator(type = "fileLength", fieldName = "csvFile", key = "errors.fileEmptyNotExist")
        }
    )
    public String doUpload() throws Exception {
        try {
            readCsv();
        } catch (CsvFormatException e) {
            addLineError(e.getLine() - 1, e.getMessage());
        } catch (FileFormatException e) {
            addActionError(e.getMessage());
        }

        if (!hasErrors()) {
            try {
                keywordChannelService.updateAll(channels);
                addActionMessage(getText("KeywordChannel.upload.successful", Arrays.asList(channels.size())));
            } catch (BusinessException e) {
                for (String error : e.getEntityErrors()) {
                    addActionError(error);
                }
            }
        }

        return INPUT;
    }

    private void readCsv() throws IOException {
        Reader fileReader = null;
        try {
            fileReader = new InputStreamReader(new FileInputStream(csvFile), format.getEncoding());
            CsvReader csvReader = new CsvReader(fileReader, format.getDelimiter());

            if (!csvReader.readHeaders()) {
                addActionError(getText("errors.invalid.header"));
                return;
            }

            if (csvReader.getHeaderCount() != KeywordChannelFieldCsv.TOTAL_COLUMNS_COUNT) {
                addActionError(getText("errors.invalid.header"));
                return;
            }

            readLines(csvReader);
        } finally {
            IOUtils.closeQuietly(fileReader);
        }
    }

    private void readLines(CsvReader csvReader) throws IOException {
        long line = 1;
        long linesWithErrors = 0;
        channels = new ArrayList<KeywordChannel>(1000);

        while (csvReader.readRecord()) {
            lastLineErrors.clear();

            if (line > MAX_UPLOAD_CHANNELS) {
                addActionError(getText("channel.upload.tooMany.message", Arrays.asList(Integer.toString(MAX_UPLOAD_CHANNELS))));
                break;
            }

            if (csvReader.getColumnCount() != csvReader.getHeaderCount()) {
                addLineError(line++, getText("errors.invalid.rowFormat", new String[] {String.valueOf(csvReader.getHeaderCount()), String.valueOf(csvReader.getColumnCount())}));
                continue;
            }

            BehavioralParameters parameters = new BehavioralParameters();

            KeywordChannel channel = new KeywordChannel();
            UploadUtils.setRowNumber(channel, line);
            channel.setName(readString(csvReader, KeywordChannelFieldCsv.Keyword));
            channel.setTriggerType(readKeywordTriggerType(csvReader, KeywordChannelFieldCsv.Type));

            InternalAccount account = new InternalAccount();
            channel.setAccount(account);
            account.setName(readString(csvReader, KeywordChannelFieldCsv.InternalAccount));
            account.setCountry(new Country(readString(csvReader, KeywordChannelFieldCsv.Country)));

            BehavioralParametersUnits units = readScale(csvReader, KeywordChannelFieldCsv.BPScale);
            parameters.setMinimumVisits(readLong(csvReader, KeywordChannelFieldCsv.BPFrequency));
            parameters.setTimeFrom(readTime(csvReader, KeywordChannelFieldCsv.BPFrom, units));
            parameters.setTimeTo(readTime(csvReader, KeywordChannelFieldCsv.BPTo, units));

            channel.setFrequencyCap(readFrequencyCap(csvReader));

            if (!lastLineErrors.isEmpty()) {
                linesWithErrors++;
                String errors = StringUtils.join(lastLineErrors, ", ");
                addLineError(line, errors);
            }

            if (linesWithErrors >= KeywordChannelService.MAX_ERRORS) {
                break;
            }

            if (linesWithErrors == 0) {
                if (!empty(parameters)) {
                    parameters.setTriggerType(channel.getTriggerType().getLetter());
                    channel.getBehavioralParameters().add(parameters);
                }
                channels.add(channel);
            }

            line++;
        }
    }

    private void addLineError(long line, String errors) {
        addActionError(getText("errors.lineNumber", Arrays.asList(line, errors)));
    }

    private boolean empty(BehavioralParameters param) {
        return param.getMinimumVisits() == null && param.getTimeFrom() == null && param.getTimeTo() == null;
    }

    private Long readTime(CsvReader csvReader, KeywordChannelFieldCsv column, BehavioralParametersUnits units) throws IOException {
        Long timeInUnits = readLong(csvReader, column);
        if (timeInUnits == null || units == null) {
            return null;
        }
        return timeInUnits * units.getMultiplier();
    }

    private String readString(CsvReader csvReader, KeywordChannelFieldCsv column) throws IOException {
        String str = StringUtil.trimProperty(csvReader.get(column.getIndex()));
        return StringUtil.isPropertyEmpty(str) ? null : str;
    }

    private BehavioralParametersUnits readScale(CsvReader csvReader, KeywordChannelFieldCsv column) throws IOException {
        String value = readString(csvReader, column);

        if (StringUtil.isPropertyEmpty(value)) {
            return null;
        }

        try {
            return BehavioralParametersUnits.byName(value);
        } catch (IllegalArgumentException e) {
            addLineError(getText("errors.invalid", new String[]{getText(column.getNameKey())}));
            return null;
        }
    }

    private Long readLong(CsvReader csvReader, KeywordChannelFieldCsv column) throws IOException {
        String value = readString(csvReader, column);
        try {
            return BaseBulkHelper.parseInteger(value);
        } catch (Exception e) {
            addLineError(getText("errors.integer", new String[]{getText(column.getNameKey())}));
            return null;
        }
    }

    private Integer readInteger(CsvReader csvReader, KeywordChannelFieldCsv column) throws IOException {
        String value = readString(csvReader, column);
        Long number;
        try {
            number = BaseBulkHelper.parseInteger(value);
        } catch (Exception e) {
            addLineError(getText("errors.integer", new String[]{getText(column.getNameKey())}));
            return null;
        }
        return convertLongToInt(number, column);
    }

    private KeywordTriggerType readKeywordTriggerType(CsvReader csvReader, KeywordChannelFieldCsv column) throws IOException {
        try {
            return KeywordTriggerType.byName(readString(csvReader, column));
        } catch (IllegalArgumentException e) {
            addLineError(getText("errors.invalid", new String[] {getText(column.getNameKey())}));
        }
        return null;
    }

    private FrequencyCap readFrequencyCap(CsvReader csvReader) throws IOException {
        FrequencyCap frequencyCap = new FrequencyCap();
        frequencyCap.setPeriodSpan(readIntWithTimeUnit(csvReader, KeywordChannelFieldCsv.FCPeriod));
        frequencyCap.setWindowCount(readInteger(csvReader, KeywordChannelFieldCsv.FCWindowLimit));
        frequencyCap.setWindowLengthSpan(readIntWithTimeUnit(csvReader, KeywordChannelFieldCsv.FCWindowLength));
        frequencyCap.setLifeCount(readInteger(csvReader, KeywordChannelFieldCsv.FCLife));

        if (frequencyCap.isEmpty()) {
            return null;
        }

        return frequencyCap;
    }

    private TimeSpan readIntWithTimeUnit(CsvReader csvReader, KeywordChannelFieldCsv column) throws IOException {
        String string = readString(csvReader, column);
        TimeSpan timeSpan;
        try {
            timeSpan = BaseBulkHelper.parseTimeSpan(string);
        } catch (Exception e) {
            addLineError(getText("errors.intWithTimeUnit", new String[]{getText(column.getNameKey())}));
            return null;
        }
        return timeSpan;
    }

    private Integer convertLongToInt(Long number, KeywordChannelFieldCsv column) {
        if (number != null) {
            if (number > Integer.MAX_VALUE) {
                addLineError(getText("errors.tooLarge", new String[]{getText(column.getNameKey())}));
                return null;
            }
            return number.intValue();
        }
        return null;
    }

    private void addLineError(String text) {
        lastLineErrors.add(text);
    }

    public File getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
    }

    public void setCsvFileFileName(String csvFileFileName) {
        this.csvFileFileName = csvFileFileName;
    }

    public String getCsvFileFileName() {
        return csvFileFileName;
    }

    public BulkFormat getFormat() {
        return format;
    }

    public void setFormat(BulkFormat format) {
        this.format = format;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public List<KeywordChannel> getChannels() {
        return channels;
    }

    public String messageForColumn(int index, String columnName, String message) {
        if (columnName == null) {
            return getText("errors.lineNumber", Arrays.asList(index + 1, message));
        } else {
            return getText("errors.fieldError.withLineNumber", Arrays.asList(index + 1, columnName, message));
        }
    }

    public String fieldName(String path) {
        KeywordChannelFieldCsv column = KeywordChannelFieldCsv.findColumn(path);
        if (column == null) {
            return null;
        }
        return getText(column.getNameKey());
    }

    public String behavioralParametersFieldName(String field) {
        if (field != null) {
            return fieldName("behavioralParameters." + field);
        } else {
            return getText("KeywordChannel.csv.column.BehavioralParams");
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new KeywordChannelsBreadcrumbsElement()).add(new UploadKeywordChannelBreadcrumbsElement());
    }
}
