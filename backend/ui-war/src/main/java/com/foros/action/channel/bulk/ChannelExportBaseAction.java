package com.foros.action.channel.bulk;

import com.foros.action.BaseActionSupport;
import com.foros.action.bulk.CsvNodeWriter;
import com.foros.action.bulk.CsvRow;
import com.foros.action.download.FileDownloadResult;
import com.foros.framework.MessageStoreInterceptor;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.model.account.Account;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.rowsource.RowSource;
import com.foros.reporting.serializer.BulkFormat;
import com.foros.reporting.serializer.CsvSerializer;
import com.foros.reporting.serializer.ResultSerializerSupport;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.xlsx.ExcelStyles;
import com.foros.reporting.serializer.xlsx.ExcelStylesRegistry;
import com.foros.reporting.serializer.xlsx.XlsxSerializer;
import com.foros.reporting.tools.query.strategy.IterationStrategy;
import com.foros.reporting.tools.query.strategy.SimpleIterationStrategy;
import com.foros.session.channel.BulkChannelToolsService;
import com.foros.session.channel.service.AdvertisingChannelType;
import com.foros.util.AccountUtil;

import com.opensymphony.xwork2.ActionContext;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

public abstract class ChannelExportBaseAction extends BaseActionSupport implements AdvertiserSelfIdAware, ServletResponseAware, ServletRequestAware {
    protected static final int MAX_EXPORT_RESULT_SIZE = 65000;

    private Long advertiserId;

    protected BulkFormat format = BulkFormat.CSV;
    protected AdvertisingChannelType channelTypeHidden = AdvertisingChannelType.BEHAVIORAL;

    private HttpServletRequest request;
    private HttpServletResponse response;

    @EJB
    protected BulkChannelToolsService bulkChannelToolsService;
    private Account advertiserAccount;

    @EJB
    private ExcelStylesRegistry excelStylesRegistry;

    protected void serialize(MetaData<? extends Column> metaData, RowSource rowSource) throws IOException {
        FileDownloadResult.setDownloadHeaders(request, response, "Channels" + formatChannelType() + format.getFormat().getExtension());
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

        serializer.registry(ValueFormatterRegistries.bulkDefaultAnd(null), RowTypes.data());
        serializer.registry(ValueFormatterRegistries.defaultUnparsedRowRegistry(), CsvRow.UNPARSED_ROW_TYPE);
        IterationStrategy iterationStrategy = new SimpleIterationStrategy(metaData);
        iterationStrategy.process(rowSource, serializer);
    }

    private String formatChannelType() {
        return "(" + (getChannelTypeHidden() == AdvertisingChannelType.BEHAVIORAL ? "B" : "E") + ")";
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Long getAccountId() {
        return advertiserId;
    }

    public void setAccountId(Long accountId) {
        this.advertiserId = accountId;
    }

    public BulkFormat getFormat() {
        return format;
    }

    public void setFormat(BulkFormat format) {
        this.format = format;
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
        return new MetaDataBuilder(channelTypeHidden, isInternalProcessing());
    }

    public AdvertisingChannelType getChannelTypeHidden() {
        return channelTypeHidden;
    }

    public void setChannelTypeHidden(AdvertisingChannelType channelType) {
        this.channelTypeHidden = channelType;
    }

    public abstract boolean isInternalProcessing();

    protected CsvNodeWriter createCsvNodeWriter() {
        switch (channelTypeHidden) {
            case EXPRESSION:
                return new ExpressionChannelCsvNodeWriter();
            case BEHAVIORAL:
                return new BehavioralChannelCsvNodeWriter();
            default:
                throw new IllegalArgumentException();
        }
    }

    protected void addErrorAndSave(String actionError) {
        addActionError(actionError);
        MessageStoreInterceptor.saveErrors(ActionContext.getContext(), this);
    }
}
