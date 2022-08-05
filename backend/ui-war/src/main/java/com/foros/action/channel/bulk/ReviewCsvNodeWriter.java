package com.foros.action.channel.bulk;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;

import com.foros.action.bulk.CsvNodeWriter;
import com.foros.action.bulk.CsvRow;
import com.foros.model.EntityBase;
import com.foros.model.channel.Channel;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.channel.service.AdvertisingChannelType;
import com.foros.util.csv.ErrorMessageBuilder;
import com.foros.validation.constraint.convertion.SimpleConstraintViolationConverter;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;

import java.util.List;

public class ReviewCsvNodeWriter implements CsvNodeWriter<Channel> {

    private CsvNodeWriter writer;
    private List<ConstraintViolationRule> rules;
    private MetaDataBuilder metaDataBuilder;

    public ReviewCsvNodeWriter(CsvNodeWriter writer, List<ConstraintViolationRule> rules,
                               AdvertisingChannelType channelType, boolean isInternalProcessing) {
        this.writer = writer;
        this.rules = rules;
        this.metaDataBuilder = new MetaDataBuilder(channelType, isInternalProcessing);
    }

    @Override
    public void write(CsvRow row, Channel channel) {
        boolean originalsWritten = writeOriginal(row, channel);
        if (!originalsWritten) {
            writer.write(row, channel);
        }
        writeStatus(row, channel);
    }

    private boolean writeOriginal(CsvRow row, EntityBase entity) {
        String[] originalValues = entity.getProperty(ChannelCsvReader.ORIGINAL_VALUES);
        if (originalValues == null) {
            return false;
        }

        for (ChannelFieldCsv field : metaDataBuilder.forReview().getColumns()) {
            row.set(field, originalValues[field.ordinal()]);
        }
        row.setUnparsed(true);
        return true;
    }

    private void writeStatus(CsvRow row, EntityBase entity) {
        UploadContext context = entity.getProperty(UPLOAD_CONTEXT);
        row.set(ChannelFieldCsv.ValidationStatus, context.getStatus().name());
        if (context.getStatus() == UploadStatus.REJECTED) {
            ErrorMessageBuilder builder = new ErrorMessageBuilder<>(ChannelFieldCsv.values(), entity.getClass());
            SimpleConstraintViolationConverter converter = new SimpleConstraintViolationConverter(builder);
            converter.applyRules(rules, context.getErrors());
            row.set(ChannelFieldCsv.Errors, builder.build());
        } else {
            row.set(ChannelFieldCsv.Errors, null);
        }
    }
}
