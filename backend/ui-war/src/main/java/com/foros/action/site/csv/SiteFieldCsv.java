package com.foros.action.site.csv;

import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.MetaDataImpl;
import com.foros.util.csv.PathableCsvField;

public enum SiteFieldCsv implements PathableCsvField {
    PUBLISHER_NAME(ColumnTypes.string(), Site.class, "account.name"),
    PUBLISHER_ID(ColumnTypes.id(), Site.class, "account.id"),
    SITE_NAME(ColumnTypes.string(), Site.class, "name"),
    SITE_ID(ColumnTypes.id(), Site.class, "id"),
    SITE_STATUS(ColumnTypes.status(), Site.class, "status"),
    SITE_URL(ColumnTypes.string(), Site.class, "siteUrl"),
    TAG_NAME(ColumnTypes.string(), Tag.class, "name"),
    TAG_ID(ColumnTypes.id(), Tag.class, "id"),
    TAG_SIZE_TYPE(ColumnTypes.string(), Tag.class, "sizeType"),
    TAG_SIZES(ColumnTypes.string(), Tag.class, "size"),
    TAG_PRICING(ColumnTypes.string(), Tag.class, "pricing"),
    TAG_PASSBACK_TYPE(ColumnTypes.string(), Tag.class, "passbackType"),
    TAG_PASSBACK(ColumnTypes.string(), Tag.class, "passback"),
    TAG_ALLOW_EXPANDABLE(ColumnTypes.bool(), Tag.class, "allowExpandable"),
    // review columns
    ValidationStatus(ColumnTypes.string(), null, null),
    ErrorMessage(ColumnTypes.string(), null, null);

    public static final MetaData<SiteFieldCsv> INTERNAL_REVIEW_METADATA = new MetaDataImpl<>(SiteFieldCsv.values());

    public static final MetaData<SiteFieldCsv> EXTERNAL_REVIEW_METADATA = INTERNAL_REVIEW_METADATA.exclude(PUBLISHER_NAME, PUBLISHER_ID);

    public static final int SHIFT = INTERNAL_REVIEW_METADATA.getColumns().size() - EXTERNAL_REVIEW_METADATA.getColumns().size();

    public static final MetaData<SiteFieldCsv> INTERNAL_EXPORT_METADATA = INTERNAL_REVIEW_METADATA.exclude(ValidationStatus, ErrorMessage);

    public static final MetaData<SiteFieldCsv> EXTERNAL_EXPORT_METADATA = INTERNAL_REVIEW_METADATA.exclude(PUBLISHER_NAME, PUBLISHER_ID, ValidationStatus, ErrorMessage);

    private ColumnType type;
    private Class beanType;
    private String fieldPath;

    SiteFieldCsv(ColumnType type, Class beanType, String fieldPath) {
        this.type = type;
        this.beanType = beanType;
        this.fieldPath = fieldPath;
    }

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public ColumnType getType() {
        return type;
    }

    @Override
    public Class getBeanType() {
        return beanType;
    }

    @Override
    public String getFieldPath() {
        return fieldPath;
    }

    @Override
    public String getNameKey() {
        return "enums.SiteCsvHeader." + name();
    }
}
