package com.foros.action.campaign.bulk;

import com.foros.action.BaseActionSupport;
import com.foros.action.bulk.CsvRow;
import com.foros.action.download.FileDownloadResult;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.model.account.Account;
import com.foros.model.campaign.TGTType;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.rowsource.RowSource;
import com.foros.reporting.serializer.BulkFormat;
import com.foros.reporting.serializer.CsvSerializer;
import com.foros.reporting.serializer.ResultSerializerSupport;
import com.foros.reporting.serializer.formatter.DateTimeValueFormatter;
import com.foros.reporting.serializer.formatter.DefaultValueFormatter;
import com.foros.reporting.serializer.formatter.NullValueFormatterWrapper;
import com.foros.reporting.serializer.formatter.NumberValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.registry.FilteringValueFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.serializer.xlsx.ExcelStyles;
import com.foros.reporting.serializer.xlsx.ExcelStylesRegistry;
import com.foros.reporting.serializer.xlsx.XlsxSerializer;
import com.foros.reporting.tools.query.strategy.IterationStrategy;
import com.foros.reporting.tools.query.strategy.SimpleIterationStrategy;
import com.foros.session.campaign.BulkCampaignToolsService;
import com.foros.util.AccountUtil;
import java.io.IOException;
import java.util.Set;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

public class CampaignExportBaseAction extends BaseActionSupport implements CampaignExportDataProvider,
        AdvertiserSelfIdAware, ServletResponseAware, ServletRequestAware {
    protected static final MainCampaignCsvNodeWriter MAIN_WRITER = new MainCampaignCsvNodeWriter();

    private Long advertiserId;

    private BulkFormat format = BulkFormat.CSV;
    private TGTType tgtType = TGTType.CHANNEL;

    private HttpServletRequest request;
    private HttpServletResponse response;

    @EJB
    private ExcelStylesRegistry excelStylesRegistry;

    @EJB
    protected BulkCampaignToolsService bulkCampaignToolsService;
    private Account advertiserAccount;

    protected void serialize(MetaData<Column> metaData, RowSource rowSource, boolean formatDecimals) throws IOException {
        FileDownloadResult.setDownloadHeaders(request, response, "Campaigns (" + tgtType.getLetter() + ")" + format.getFormat().getExtension());
        response.setHeader("Content-type", format.getFormat().getMime());

        ResultSerializerSupport serializer;
        switch (format) {
        case XLSX:
            ExcelStyles excelStyles = excelStylesRegistry.get(getLocale());
            serializer = new XlsxSerializer(response.getOutputStream(), null, excelStyles);
            break;

        default:
            serializer = new CsvSerializer(response.getOutputStream(), getLocale(), Integer.MAX_VALUE, format);
            break;
        }
        initRegistries(serializer, formatDecimals);
        IterationStrategy iterationStrategy = new SimpleIterationStrategy(metaData);
        iterationStrategy.process(rowSource, serializer);
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public BulkFormat getFormat() {
        return format;
    }

    public void setFormat(BulkFormat format) {
        this.format = format;
    }

    public TGTType getTgtType() {
        return tgtType;
    }

    public void setTgtType(TGTType tgtType) {
        this.tgtType = tgtType;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public Account getAdvertiserAccount() {
        if (advertiserAccount == null) {
            advertiserAccount = AccountUtil.extractAccountById(getAdvertiserId());
        }
        return advertiserAccount;
    }

    protected MetaDataBuilder getMetaDataBuilder() {
        return new MetaDataBuilder(getAdvertiserAccount(), getTgtType());
    }

    private void initRegistries(ResultSerializerSupport serializer, boolean formatDecimals) {
        ValueFormatterRegistry registry = registry(formatDecimals);
        addRegistry(serializer, registry, CampaignLevelCsv.Campaign, MetaDataBuilder.ALL_CAMPAIGN_COLUMNS);
        addRegistry(serializer, registry, CampaignLevelCsv.AdGroup, MetaDataBuilder.ALL_AD_GROUP_COLUMNS);
        addRegistry(serializer, registry, CampaignLevelCsv.TextAd, MetaDataBuilder.ALL_CREATIVES_COLUMNS);
        addRegistry(serializer, registry, CampaignLevelCsv.Keyword, MetaDataBuilder.ALL_KEYWORD_COLUMNS);
        serializer.registry(new UnparsedRowRegistry(registry), CsvRow.UNPARSED_ROW_TYPE);
    }

    private void addRegistry(ResultSerializerSupport serializer, ValueFormatterRegistry registry, CampaignLevelCsv level, Set<Column> includeColumns) {
        ValueFormatterRegistry target = ValueFormatterRegistries.bulkDefaultAnd(registry);
        serializer.registry(new FilteringValueFormatterRegistry(target, includeColumns), level.getRowType());
    }

    public ValueFormatterRegistry registry(boolean formatDecimals) {
        Account account = getAdvertiserAccount();
        TimeZone timeZone = TimeZone.getTimeZone(account.getTimezone().getKey());

        NumberValueFormatter currencyFormatter = formatDecimals ?
                new NumberValueFormatter(account.getCurrency().getFractionDigits()) :
                new NumberValueFormatter(-1);

        ValueFormatterRegistryImpl registry = ValueFormatterRegistries.registry()
                .type(ColumnTypes.dateTime(), new DateTimeValueFormatter(timeZone))
                .type(ColumnTypes.currency(), new NullValueFormatterWrapper(currencyFormatter))
                .column(CampaignFieldCsv.Level, new LevelValueFormatter())
                .column(CampaignFieldCsv.CampaignEndDate, new DateTimeValueFormatter(timeZone, CampaignBulkHelper.getNotSetPhrase(CampaignFieldCsv.CampaignEndDate)))
                .column(CampaignFieldCsv.AdGroupEndDate, new DateTimeValueFormatter(timeZone, CampaignBulkHelper.getNotSetPhrase(CampaignFieldCsv.AdGroupEndDate)))
                .column(CampaignFieldCsv.CampaignDailyBudget, new CampaignDailyBudgetFormatter(currencyFormatter))
                .column(CampaignFieldCsv.AdGroupChannelTarget, new AdGroupChannelTargetFormatter());

        return ValueFormatterRegistries.bulkDefaultAnd(registry);
    }

    private class UnparsedRowRegistry implements ValueFormatterRegistry {
        private ValueFormatterRegistry target;

        private UnparsedRowRegistry(ValueFormatterRegistry target) {
            this.target = target;
        }

        @Override
        public <T> ValueFormatter<T> get(Column column) {
            if (getMetaDataBuilder().forReview().getColumnsMeta().contains(column)) {
                target.get(column);
            }
            //noinspection unchecked
            return (ValueFormatter<T>) DefaultValueFormatter.INSTANCE;
        }
    }
}
