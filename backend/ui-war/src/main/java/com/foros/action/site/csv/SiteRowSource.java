package com.foros.action.site.csv;

import static com.foros.action.site.csv.SiteFieldCsv.PUBLISHER_ID;
import static com.foros.action.site.csv.SiteFieldCsv.PUBLISHER_NAME;
import static com.foros.action.site.csv.SiteFieldCsv.SITE_ID;
import static com.foros.action.site.csv.SiteFieldCsv.SITE_NAME;
import static com.foros.action.site.csv.SiteFieldCsv.SITE_STATUS;
import static com.foros.action.site.csv.SiteFieldCsv.SITE_URL;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_ALLOW_EXPANDABLE;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_ID;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_NAME;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_PASSBACK;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_PASSBACK_TYPE;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_PRICING;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_SIZES;
import static com.foros.action.site.csv.SiteFieldCsv.TAG_SIZE_TYPE;
import static com.foros.action.site.csv.SiteFieldCsv.ValidationStatus;
import com.foros.action.bulk.CsvRow;
import com.foros.model.EntityBase;
import com.foros.model.account.PublisherAccount;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.reporting.Row;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.rowsource.RowSource;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.site.SiteUploadService;
import com.foros.session.site.SiteUploadUtil;
import com.foros.util.StringUtil;
import com.foros.util.csv.ErrorMessageBuilder;
import com.foros.validation.constraint.convertion.SimpleConstraintViolationConverter;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.Iterator;
import java.util.List;

public class SiteRowSource implements RowSource, Iterator<Row> {
    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("pricings[(#index)](#path)", "#fmt.formatPricingMsg(groups[0], violation.message)")
            .rules();

    private SiteTagsIterator iterator;
    private int rowSize;
    private boolean includeValidationStatus;
    private boolean isInternalMode;

    public SiteRowSource(MetaData metaData, SiteTagsIterator iterator) {
        this.iterator = iterator;
        rowSize = metaData.getColumns().size();
        includeValidationStatus = SiteFieldCsv.EXTERNAL_REVIEW_METADATA.equals(metaData) || SiteFieldCsv.INTERNAL_REVIEW_METADATA.equals(metaData);
        isInternalMode = SiteFieldCsv.INTERNAL_EXPORT_METADATA.equals(metaData) || SiteFieldCsv.INTERNAL_REVIEW_METADATA.equals(metaData);
    }

    @Override
    public Iterator<Row> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Row next() {
        EntityBase entity = iterator.next();
        SiteCsvRow row = new SiteCsvRow(rowSize, isInternalMode);

        if (entity instanceof Site) {
            row.setRowType(SiteCsvRow.SITE);
            write(row, (Site)entity);
        } else {
            row.setRowType(SiteCsvRow.SITE_TAG);
            write(row, (Tag)entity);
        }

        return row;
    }

    private void write(CsvRow record, Site site) {
        if (SiteUploadUtil.getUploadContext(site).getStatus() == UploadStatus.REJECTED) {
            writeOriginal(site, record);
        } else {
            if (isInternalMode) {
                write(record, site.getAccount());
            }
            record.set(SITE_ID, site.getId());
            record.set(SITE_NAME, site.getName());
            record.set(SITE_STATUS, site.getStatus());
            record.set(SITE_URL, site.getSiteUrl());
        }

        if (includeValidationStatus) {
            record.set(ValidationStatus, fetchValidationStatus(site));
            record.set(SiteFieldCsv.ErrorMessage, fetchValidationMessages(site));
        }
    }

    private void write(CsvRow record, PublisherAccount account) {
        record.set(PUBLISHER_NAME, account.getName());
        record.set(PUBLISHER_ID, account.getId());
    }

    private void write(CsvRow record, Tag tag) {
        boolean isRejected = SiteUploadUtil.getUploadContext(tag.getSite()).getStatus() == UploadStatus.REJECTED ||
                SiteUploadUtil.getUploadContext(tag).getStatus() == UploadStatus.REJECTED;

        if (isRejected) {
            writeOriginal(tag, record);
            writeOriginal(tag.getSite(), record);
        } else {
            write(record, tag.getSite());

            record.set(TAG_NAME, tag.getName());
            record.set(TAG_ID, tag.getId());
            record.set(TAG_SIZE_TYPE, tag.getSizeType().getDefaultName());
            record.set(TAG_SIZES, tag.getSizes());

            // Processing passback or passbackHtml
            record.set(TAG_PASSBACK_TYPE, tag.getPassbackType().name());
            if (StringUtil.isPropertyNotEmpty(tag.getPassbackHtml())) {
                record.set(TAG_PASSBACK, tag.getPassbackHtml());
            } else {
                record.set(TAG_PASSBACK, tag.getPassback());
            }

            record.set(TAG_PRICING, tag.getTagPricings());
            record.set(TAG_ALLOW_EXPANDABLE, tag.isAllowExpandable());
        }

        if (includeValidationStatus) {
            record.set(ValidationStatus, isRejected ? UploadStatus.REJECTED.name() : fetchValidationStatus(tag));

            String siteMessage = fetchValidationMessages(tag.getSite());
            String tagMessage = fetchValidationMessages(tag);
            String newMessage;

            if (siteMessage != null && tagMessage != null) {
                newMessage =  siteMessage + ", " + tagMessage;
            } else {
                newMessage = siteMessage != null ? siteMessage : tagMessage;
            }

            record.set(SiteFieldCsv.ErrorMessage, newMessage);
        }
    }

    private void writeOriginal(EntityBase entity, CsvRow record) {
        String[] originalValues = entity.getProperty(SiteUploadService.ORIGINAL_VALUES);
        List<SiteFieldCsv> exportColumns;
        if (isInternalMode) {
            exportColumns = SiteFieldCsv.INTERNAL_EXPORT_METADATA.getColumns();
        } else {
            exportColumns = SiteFieldCsv.EXTERNAL_EXPORT_METADATA.getColumns();
        }
        for (int i = 0; (i < originalValues.length && i < exportColumns.size()); i++) {
            SiteFieldCsv field = exportColumns.get(i);
            if (entity.getClass().equals(field.getBeanType())) {
                record.set(field, originalValues[i]);
            }
        }
        record.setUnparsed(true);
    }

    private String fetchValidationMessages(EntityBase entity) {
        UploadContext context = SiteUploadUtil.getUploadContext(entity);
        if (context != null && context.getStatus() == UploadStatus.REJECTED) {
            ErrorMessageBuilder<SiteFieldCsv> builder = new ErrorMessageBuilder<>(SiteFieldCsv.values(), entity.getClass());
            SimpleConstraintViolationConverter converter = new SimpleConstraintViolationConverter(builder);
            converter.addToContext("fmt", new Formatter());
            converter.applyRules(RULES, context.getErrors());
            return builder.build();
        } else {
            return null;
        }
    }

    private String fetchValidationStatus(EntityBase entity) {
        UploadContext context = SiteUploadUtil.getUploadContext(entity);
        String status = null;
        if (context != null) {
            status =  context.getStatus().name();
        }
        return status;
    }

    @Override
    public void remove() {
        iterator.remove();
    }

    private static class Formatter {
        public static String formatPricingMsg(int index, String message) {
            return StringUtil.getLocalizedString("enums.SiteCsvHeader.TAG_PRICING") + " [" + (index + 1) + "]: " + message;
        }
    }
}
