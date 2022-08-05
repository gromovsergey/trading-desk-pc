package com.foros.session.reporting.siteChannels;

import com.foros.model.account.Account;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
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
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportsService;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.io.OutputStream;
import java.sql.Types;
import java.util.TimeZone;

@LocalBean
@Stateless(name = "SiteChannelsReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class SiteChannelsReportService implements GenericReportService<SiteChannelsReportParameters, SimpleReportData> {

    private static final String CHANNEL_URL_PATTERN = "../../channel/view.action?id=%d";
    private static final String ACCOUNT_URL_PATTERN = "../../account/view.action?id=%d";

    @EJB
    private StatsDbQueryProvider statsDb;

    @EJB
    private ReportsService reportsService;

    private static final ValueFormatterRegistry REGISTRY = ValueFormatterRegistries.registry()
            .column(SiteChannelsMeta.CHANNEL, EntityUrlValueFormatter.html(SiteChannelsMeta.CHANNEL_ID, CHANNEL_URL_PATTERN))
            .column(SiteChannelsMeta.ACCOUNT, EntityUrlValueFormatter.html(SiteChannelsMeta.ACCOUNT_ID, ACCOUNT_URL_PATTERN))
            .column(SiteChannelsMeta.IMPRESSIONS, new ImpressionValueFormatter(SiteChannelsMeta.IMPRESSIONS_PC))
            .column(SiteChannelsMeta.AVG_ADV_CPM, new CurrencyValueFormatter("USD"))
            .column(SiteChannelsMeta.AVG_PUBLISHER_CPM, new CurrencyValueFormatter("USD"));

    @Override
    @Restrict(restriction = "Report.run", parameters = "'siteChannels'")
    @Validate(validation = "Reporting.siteChannels", parameters = "#parameters")
    public void processHtml(SiteChannelsReportParameters parameters, SimpleReportData data) {
        processSiteChannelsReport(parameters, reportsService.createHtmlSerializer(data));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'siteChannels'")
    @Validate(validation = "Reporting.siteChannels", parameters = "#parameters")
    public void processExcel(SiteChannelsReportParameters parameters, OutputStream stream) {
        processSiteChannelsReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'siteChannels'")
    @Validate(validation = "Reporting.siteChannels", parameters = "#parameters")
    public void processCsv(SiteChannelsReportParameters parameters, OutputStream stream) {
        processSiteChannelsReport(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processSiteChannelsReport(SiteChannelsReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer, false));
    }

    private class Report extends CommonAuditableReportSupport<SiteChannelsReportParameters> {
        public Report(SiteChannelsReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {
            handler.registry(REGISTRY, RowTypes.data());
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    builder.addDateRange(parameters.getDateRange(), TimeZone.getTimeZone("GMT"))
                            .addId("publisherAccount", Account.class, parameters.getAccountId())
                            .addId("site", Site.class, parameters.getSiteId())
                            .addId("tag", Tag.class, parameters.getTagId());
                }
            };

            query = statsDb.queryFunction("report.sites_channel")
                    .parameter("p_account_id", parameters.getAccountId(), Types.INTEGER)
                    .parameter("p_site_id", parameters.getSiteId(), Types.INTEGER)
                    .parameter("p_tag_id", parameters.getTagId(), Types.INTEGER)
                    .parameter("p_from_date", parameters.getDateRange().getBegin(), new PostgreLocalDateUserType())
                    .parameter("p_to_date", parameters.getDateRange().getEnd(), new PostgreLocalDateUserType())
                    .parameter("p_max_numrows", handler.getMaxRows() + 1);

            metaData = SiteChannelsMeta.META_DATA.resolve(parameters);

            handler.preparedParameters(preparedParameterBuilderFactory);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.SITE_CHANNELS;
        }
    }
}
