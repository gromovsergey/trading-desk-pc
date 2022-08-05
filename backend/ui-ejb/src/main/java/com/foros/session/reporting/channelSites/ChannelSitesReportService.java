package com.foros.session.reporting.channelSites;

import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.reporting.RowTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.EntityUrlValueFormatter;
import com.foros.reporting.serializer.formatter.ImpressionValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;
import java.sql.Types;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@LocalBean
@Stateless(name = "ChannelSitesReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class ChannelSitesReportService implements GenericReportService<ChannelSitesReportParameters, SimpleReportData> {

    private static final String TAG_URL_PATTERN = "../../tag/view.action?id=%d";
    private static final String SITE_URL_PATTERN = "../../site/view.action?id=%d";
    private static final String ACCOUNT_URL_PATTERN = "../../account/view.action?id=%d";

    @EJB
    private StatsDbQueryProvider statsDb;

    @EJB
    private ReportsService reportsService;

    private static final ValueFormatterRegistry REGISTRY = ValueFormatterRegistries.registry()
            .column(ChannelSitesMeta.TAG, EntityUrlValueFormatter.html(ChannelSitesMeta.TAG_ID, TAG_URL_PATTERN))
            .column(ChannelSitesMeta.SITE, EntityUrlValueFormatter.html(ChannelSitesMeta.SITE_ID, SITE_URL_PATTERN))
            .column(ChannelSitesMeta.ACCOUNT, EntityUrlValueFormatter.html(ChannelSitesMeta.ACCOUNT_ID, ACCOUNT_URL_PATTERN))
            .column(ChannelSitesMeta.IMPRESSIONS, new ImpressionValueFormatter(ChannelSitesMeta.IMPRESSIONS_PC))
            .column(ChannelSitesMeta.AVG_ADV_CPM, new CurrencyValueFormatter("USD"))
            .column(ChannelSitesMeta.AVG_PUBLISHER_CPM, new CurrencyValueFormatter("USD"));

    @Override
    @Restrict(restriction = "Report.run", parameters = "'channelSites'")
    @Validate(validation = "Reporting.channelSites", parameters = "#parameters")
    public void processHtml(ChannelSitesReportParameters parameters, SimpleReportData data) {
        processChannelSitesReport(parameters, reportsService.createHtmlSerializer(data));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'channelSites'")
    @Validate(validation = "Reporting.channelSites", parameters = "#parameters")
    public void processExcel(ChannelSitesReportParameters parameters, OutputStream stream) {
        processChannelSitesReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'channelSites'")
    @Validate(validation = "Reporting.channelSites", parameters = "#parameters")
    public void processCsv(ChannelSitesReportParameters parameters, OutputStream stream) {
        processChannelSitesReport(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processChannelSitesReport(ChannelSitesReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer, false));
    }

    private class Report extends CommonAuditableReportSupport<ChannelSitesReportParameters> {
        public Report(ChannelSitesReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {
            handler.registry(REGISTRY, RowTypes.data());
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    builder.addDateRange(parameters.getDateRange(), TimeZone.getTimeZone("GMT"))
                            .addId("channelCreatorAccount", Account.class, parameters.getAccountId())
                            .addId("channel", Channel.class, parameters.getChannelId());
                }
            };

            query = statsDb.queryFunction("report.channel_sites")
                    .parameter("p_account_id", parameters.getAccountId(), Types.INTEGER)
                    .parameter("p_channel_id", parameters.getChannelId(), Types.BIGINT)
                    .parameter("p_from_date", parameters.getDateRange().getBegin(), new PostgreLocalDateUserType())
                    .parameter("p_to_date", parameters.getDateRange().getEnd(), new PostgreLocalDateUserType())
                    .parameter("p_max_numrows", handler.getMaxRows() + 1, Types.BIGINT);

            metaData = ChannelSitesMeta.META_DATA.resolve(parameters);

            handler.preparedParameters(preparedParameterBuilderFactory);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.CHANNEL_SITES;
        }
    }
}
