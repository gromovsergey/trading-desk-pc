package com.foros.session.reporting.referrer;

import com.foros.model.account.Account;
import com.foros.model.account.PublisherAccount;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.HtmlCellAccessor;
import com.foros.reporting.serializer.formatter.PercentValueFormatter;
import com.foros.reporting.serializer.formatter.UrlValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.tools.query.ResultSetExecutor;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.ImpalaDbQueryProvider;
import com.foros.session.account.AccountService;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.session.site.SiteService;
import com.foros.session.site.TagsService;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;
import java.sql.Types;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless(name = "ReferrerReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class ReferrerReportService implements GenericReportService<ReferrerReportParameters, SimpleReportData> {

    @EJB
    private ImpalaDbQueryProvider impalaDb;

    @EJB
    private ReportsService reportsService;
    
    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private AccountService accountService;

    @EJB
    private SiteService siteService;

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @EJB
    private TagsService tagsService;

    @Override
    @Restrict(restriction = "Report.ReferrerReport.run", parameters = "#parameters.accountId")
    @Validate(validation = "Reporting.referrer", parameters = "#parameters")
    public void processHtml(ReferrerReportParameters parameters, SimpleReportData data) {
        ValueFormatterRegistry registry = ValueFormatterRegistries.registry()
                .column(ReferrerMeta.DOMAIN, new DomainFormatter())
                .column(ReferrerMeta.CTR, new PercentValueFormatter(3));

        AuditResultHandlerWrapper serializer = reportsService.createHtmlSerializer(data);
        serializer.registry(registry, RowTypes.data());
        processReferrerReport(parameters, serializer);
    }

    @Override
    @Restrict(restriction = "Report.ReferrerReport.run", parameters = "#parameters")
    @Validate(validation = "Reporting.referrer", parameters = "#parameters")
    public void processExcel(ReferrerReportParameters parameters, OutputStream stream) {
        processReferrerReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.ReferrerReport.run", parameters = "#parameters")
    @Validate(validation = "Reporting.referrer", parameters = "#parameters")
    public void processCsv(ReferrerReportParameters parameters, OutputStream stream) {
        processReferrerReport(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processReferrerReport(ReferrerReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer, false));
    }

    private ResultSetExecutor getImpalaSqlTemplate(String functionName) {
        return impalaDb.createFunctionTemplate(functionName).build();
    }

    private class Report extends CommonAuditableReportSupport<ReferrerReportParameters> {
        public Report(ReferrerReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    Long siteId = parameters.getSiteId();
                    Long tagId = parameters.getTagId();
                    Site site;
                    if (siteId != null) {
                        site = siteService.find(siteId);
                    } else {
                        site = tagsService.find(tagId).getSite();
                    }
                    builder.addDateRange(parameters.getDateRange(), TimeZone.getTimeZone("GMT"))
                            .addId("account", Account.class, parameters.getAccountId())
                            .addId("site", Site.class, siteId)
                            .add("siteUrl", site.getSiteUrl())
                            .addId("tag", Tag.class, tagId);
                }
            };

            handler.preparedParameters(preparedParameterBuilderFactory);

            PublisherAccount account = em.find(PublisherAccount.class, parameters.getAccountId());
            String countryCode = account.getCurrency().getCurrencyCode();
            ValueFormatterRegistry registry = ValueFormatterRegistries.registry()
                    .type(ColumnTypes.currency(), new CurrencyValueFormatter(countryCode, 3))
                    .column(ReferrerMeta.CTR, new PercentValueFormatter(3));
            handler.registry(registry, RowTypes.data());

            if (parameters.getTagId() == null) {
                query = impalaDb.query(getImpalaSqlTemplate("sitereferrer_by_site"))
                        .parameter("dateFrom", parameters.getDateRange().getBegin().toString(), Types.VARCHAR)
                        .parameter("dateTo", parameters.getDateRange().getEnd().toString(), Types.VARCHAR)
                        .parameter("siteId", parameters.getSiteId(), Types.INTEGER);
            } else {
                query = impalaDb.query(getImpalaSqlTemplate("sitereferrer_by_tag"))
                        .parameter("dateFrom", parameters.getDateRange().getBegin().toString(), Types.VARCHAR)
                        .parameter("dateTo", parameters.getDateRange().getEnd().toString(), Types.VARCHAR)
                        .parameter("tagId", parameters.getTagId(), Types.INTEGER);
            }

            metaData = ReferrerMeta.META_DATA.resolve(parameters);
            if (currentUserService.isExternal()) {
                PublisherAccount publisher = accountService.findPublisherAccount(currentUserService.getAccountId());
                boolean hideClicksData = !publisher.getAccountType().isClicksDataVisibleToExternal();
                if (hideClicksData) {
                    metaData = metaData.exclude(ReferrerMeta.CLICKS_DATA);
                }
            }
        }

        @Override
        public ReportType getReportType() {
            return ReportType.REFERRER;
        }
    }

    private static class DomainFormatter extends ValueFormatterSupport<String> {
        private ValueFormatter<String> urlFormatter = new UrlValueFormatter();
        @Override
        public String formatText(String value, FormatterContext context) {
            if (value == null) {
                return "NULL";
            } else if (isEmptyDomain(value)) {
                return value;
            } else {
                return urlFormatter.formatText(value, context);
            }
        }

        @Override
        public void formatHtml(HtmlCellAccessor cellAccessor, String value, FormatterContext context) {
            if (value == null) {
                cellAccessor.setHtml("NULL");
            } else if (isEmptyDomain(value)) {
                cellAccessor.setHtml(value);
            } else {
                urlFormatter.formatHtml(cellAccessor, value, context);
            }
        }

        private boolean isEmptyDomain(String value) {
            return value.equals("-");
        }
    }
}
