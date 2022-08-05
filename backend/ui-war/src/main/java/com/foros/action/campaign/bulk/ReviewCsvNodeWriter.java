package com.foros.action.campaign.bulk;

import static com.foros.util.UploadUtils.UPLOAD_CONTEXT;

import com.foros.action.bulk.CsvRow;
import com.foros.model.EntityBase;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.ServiceLocator;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.template.TemplateService;
import com.foros.util.UploadUtils;
import com.foros.util.csv.ErrorMessageBuilder;
import com.foros.validation.constraint.convertion.SimpleConstraintViolationConverter;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class ReviewCsvNodeWriter extends MainCampaignCsvNodeWriter {
    private List<ConstraintViolationRule> rules;

    public ReviewCsvNodeWriter(List<ConstraintViolationRule> rules) {
        this.rules = rules;
    }

    @Override
    protected void write(CsvRow row, Campaign campaign) {
        boolean originalsWritten = writeOriginal(row, campaign);
        if (!originalsWritten) {
            super.write(row, campaign);
        }
        writeStatus(row, campaign);
    }

    @Override
    protected void write(CsvRow row, CampaignCreativeGroup ccg) {
        if (!writeOriginal(row, ccg)) {
            super.write(row, ccg);
        }
        writeStatus(row, ccg, ccg.getCampaign());
    }

    @Override
    protected void write(CsvRow row, CCGKeyword keyword) {
        if (!writeOriginal(row, keyword)) {
            super.write(row, keyword);
        }
        writeStatus(row, keyword, keyword.getCreativeGroup(), keyword.getCreativeGroup().getCampaign());
    }

    @Override
    protected void write(CsvRow row, CCGKeyword keyword, CCGKeyword keyword2) {
        if (!writeOriginal(row, keyword)) {
            super.write(row, keyword, keyword2);
        }

        UploadContext context = keyword.getProperty(UPLOAD_CONTEXT);
        UploadContext context2 = keyword2.getProperty(UPLOAD_CONTEXT);
        UploadUtils.mergeContext(context, context2);

        writeStatus(row, keyword, keyword.getCreativeGroup(), keyword.getCreativeGroup().getCampaign());
    }

    @Override
    protected void write(CsvRow row, CampaignCreative cc) {
        if (!writeOriginal(row, cc)) {
            super.write(row, cc);
        }
        writeStatus(row, cc, cc.getCreativeGroup(), cc.getCreativeGroup().getCampaign());
        writeStatus(row, cc.getCreative());
    }

    private boolean writeOriginal(CsvRow row, EntityBase entity) {
        Object[] originalValues = entity.getProperty(CampaignBulkReader.ORIGINAL_VALUES);
        if (originalValues == null) {
            return false;
        }

        for (CampaignFieldCsv field : CampaignFieldCsv.values()) {
            row.set(field, originalValues[field.ordinal()]);
        }
        row.setUnparsed(true);
        return true;
    }

    private void writeStatus(CsvRow row, EntityBase entity, EntityBase... parents) {
        UploadContext context = entity.getProperty(UPLOAD_CONTEXT);
        boolean rowNotRejected = !UploadStatus.REJECTED.name().equals(row.get(CampaignFieldCsv.ValidationStatus));
        if (rowNotRejected) {
            row.set(CampaignFieldCsv.ValidationStatus, context.getStatus().name());
        }
        if (context.getStatus() == UploadStatus.REJECTED) {
            ErrorMessageBuilder<CampaignFieldCsv> builder = new ErrorMessageBuilder<>(CampaignFieldCsv.values(), entity.getClass());
            SimpleConstraintViolationConverter converter = new SimpleConstraintViolationConverter(builder);
            converter.addToContext("templateService", ServiceLocator.getInstance().lookup(TemplateService.class));
            converter.addToContext("writer", this);
            converter.applyRules(rules, context.getErrors());
            for (EntityBase parent : parents) {
                UploadContext parentContext = parent.getProperty(UPLOAD_CONTEXT);
                if (parentContext.getStatus() == UploadStatus.LINK) {
                    builder.setEntityType(parent.getClass());
                    converter.applyRules(rules, parentContext.getErrors());
                }
            }
            String existingErrors = (String) row.get(CampaignFieldCsv.Errors);
            row.set(CampaignFieldCsv.Errors, existingErrors != null ? StringUtils.join(Arrays.asList(existingErrors, builder.build()), ", ") : builder.build());
        } else if (rowNotRejected) {
            row.set(CampaignFieldCsv.Errors, null);
        }
    }

    public String getPath(String token) {
        return CampaignFieldCsv.TEXT_OPTIONS.get(token).getFieldPath();
    }

}
