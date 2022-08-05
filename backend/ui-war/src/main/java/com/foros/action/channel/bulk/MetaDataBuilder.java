package com.foros.action.channel.bulk;

import static com.foros.action.channel.bulk.ChannelFieldCsv.Account;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Country;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Description;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Errors;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Expression;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Name;
import static com.foros.action.channel.bulk.ChannelFieldCsv.PageKeyword;
import static com.foros.action.channel.bulk.ChannelFieldCsv.PageKeywordCount;
import static com.foros.action.channel.bulk.ChannelFieldCsv.PageKeywordFrom;
import static com.foros.action.channel.bulk.ChannelFieldCsv.PageKeywordNegative;
import static com.foros.action.channel.bulk.ChannelFieldCsv.PageKeywordTo;
import static com.foros.action.channel.bulk.ChannelFieldCsv.PageKeywordUnit;
import static com.foros.action.channel.bulk.ChannelFieldCsv.SearchKeyword;
import static com.foros.action.channel.bulk.ChannelFieldCsv.SearchKeywordCount;
import static com.foros.action.channel.bulk.ChannelFieldCsv.SearchKeywordFrom;
import static com.foros.action.channel.bulk.ChannelFieldCsv.SearchKeywordNegative;
import static com.foros.action.channel.bulk.ChannelFieldCsv.SearchKeywordTo;
import static com.foros.action.channel.bulk.ChannelFieldCsv.SearchKeywordUnit;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Status;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Url;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlCount;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlFrom;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlNegative;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlTo;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlUnit;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlKeyword;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlKeywordCount;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlKeywordFrom;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlKeywordNegative;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlKeywordTo;
import static com.foros.action.channel.bulk.ChannelFieldCsv.UrlKeywordUnit;
import static com.foros.action.channel.bulk.ChannelFieldCsv.ValidationStatus;
import static com.foros.action.channel.bulk.ChannelFieldCsv.Visibility;

import com.foros.action.bulk.BulkMetaData;
import com.foros.session.channel.service.AdvertisingChannelType;


public class MetaDataBuilder {

    public static final BulkMetaData<ChannelFieldCsv> BEHAVIORAL_UPLOAD_COLUMNS = new BulkMetaData<ChannelFieldCsv>(
            Name,
            Status,
            Description,
            Country,
            Url, UrlCount, UrlFrom, UrlTo, UrlUnit, UrlNegative,
            SearchKeyword, SearchKeywordCount, SearchKeywordFrom, SearchKeywordTo, SearchKeywordUnit, SearchKeywordNegative,
            PageKeyword, PageKeywordCount, PageKeywordFrom, PageKeywordTo, PageKeywordUnit, PageKeywordNegative,
            UrlKeyword, UrlKeywordCount, UrlKeywordFrom, UrlKeywordTo, UrlKeywordUnit, UrlKeywordNegative
    );

    public static final BulkMetaData<ChannelFieldCsv> BEHAVIORAL_UPLOAD_COLUMNS_INTERNAL = new BulkMetaData<ChannelFieldCsv>(
            Account,
            Name,
            Status,
            Description,
            Country,
            Visibility,
            Url, UrlCount, UrlFrom, UrlTo, UrlUnit, UrlNegative,
            SearchKeyword, SearchKeywordCount, SearchKeywordFrom, SearchKeywordTo, SearchKeywordUnit, SearchKeywordNegative,
            PageKeyword, PageKeywordCount, PageKeywordFrom, PageKeywordTo, PageKeywordUnit, PageKeywordNegative,
            UrlKeyword, UrlKeywordCount, UrlKeywordFrom, UrlKeywordTo, UrlKeywordUnit, UrlKeywordNegative
    );

    public static final BulkMetaData<ChannelFieldCsv> EXPRESSION_UPLOAD_COLUMNS = new BulkMetaData<ChannelFieldCsv>(
            Name, Status, Description, Country, Expression);

    public static final BulkMetaData<ChannelFieldCsv> EXPRESSION_UPLOAD_COLUMNS_INTERNAL = new BulkMetaData<ChannelFieldCsv>(
            Account, Name, Status, Description, Country, Visibility, Expression);

    public static final BulkMetaData<ChannelFieldCsv> BEHAVIORAL_REVIEW_COLUMNS = BEHAVIORAL_UPLOAD_COLUMNS.append(
            ValidationStatus, Errors);

    public static final BulkMetaData<ChannelFieldCsv> BEHAVIORAL_REVIEW_COLUMNS_INTERNAL = BEHAVIORAL_UPLOAD_COLUMNS_INTERNAL.append(
            ValidationStatus, Errors);

    public static final BulkMetaData<ChannelFieldCsv> EXPRESSION_REVIEW_COLUMNS = EXPRESSION_UPLOAD_COLUMNS.append(
            ValidationStatus, Errors);

    public static final BulkMetaData<ChannelFieldCsv> EXPRESSION_REVIEW_COLUMNS_INTERNAL = EXPRESSION_UPLOAD_COLUMNS_INTERNAL.append(
            ValidationStatus, Errors);

    private AdvertisingChannelType channelType;
    private boolean isInternalProcessing;

    public MetaDataBuilder(AdvertisingChannelType channelType, boolean isInternalProcessing) {
        this.channelType = channelType;
        this.isInternalProcessing = isInternalProcessing;
    }

    public BulkMetaData<ChannelFieldCsv> forUpload() {
        switch (channelType) {
        case BEHAVIORAL:
            return isInternalProcessing ? BEHAVIORAL_UPLOAD_COLUMNS_INTERNAL : BEHAVIORAL_UPLOAD_COLUMNS;
        case EXPRESSION:
            return isInternalProcessing ? EXPRESSION_UPLOAD_COLUMNS_INTERNAL : EXPRESSION_UPLOAD_COLUMNS;
        default:
            throw new IllegalArgumentException("Only Expression and Behavioral channels are support");
        }
    }

    public BulkMetaData<ChannelFieldCsv> forExport() {
        switch (channelType) {
        case BEHAVIORAL:
            return isInternalProcessing? BEHAVIORAL_UPLOAD_COLUMNS_INTERNAL : BEHAVIORAL_UPLOAD_COLUMNS;
        case EXPRESSION:
            return isInternalProcessing ? EXPRESSION_UPLOAD_COLUMNS_INTERNAL : EXPRESSION_UPLOAD_COLUMNS;
        default:
            throw new IllegalArgumentException("Only Expression and Behavioral channels are support");
        }
    }

    public BulkMetaData<ChannelFieldCsv> forReview() {
        switch (channelType) {
        case BEHAVIORAL:
            return isInternalProcessing ? BEHAVIORAL_REVIEW_COLUMNS_INTERNAL : BEHAVIORAL_REVIEW_COLUMNS;
        case EXPRESSION:
            return isInternalProcessing ? EXPRESSION_REVIEW_COLUMNS_INTERNAL : EXPRESSION_REVIEW_COLUMNS;
        default:
            throw new IllegalArgumentException("Only Expression and Behavioral channels are support");
        }
    }

    public AdvertisingChannelType getChannelType() {
        return channelType;
    }
}
