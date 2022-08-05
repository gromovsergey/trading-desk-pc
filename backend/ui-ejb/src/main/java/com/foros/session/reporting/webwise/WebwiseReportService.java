package com.foros.session.reporting.webwise;

import com.foros.model.account.Account;
import com.foros.model.isp.Colocation;
import com.foros.reporting.RowTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.LocalDateValueFormatter;
import com.foros.reporting.serializer.formatter.NumberValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.tools.query.Query;
import com.foros.reporting.tools.query.parameters.usertype.PostgreIntArrayUserType;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportsService;
import com.foros.util.CollectionUtils;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.usertype.UserType;
import org.joda.time.Days;

@LocalBean
@Stateless(name = "WebwiseReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class WebwiseReportService {

    private static final UserType IDS_TYPE = new PostgreIntArrayUserType();

    private static final UserType LOCAL_DATE_USER_TYPE = new PostgreLocalDateUserType();

    @EJB
    private ReportsService reportsService;

    @EJB
    private StatsDbQueryProvider db;

    @EJB
    private CurrentUserService currentUserService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    private static final ValueFormatterRegistry REGISTRY = ValueFormatterRegistries.registry()
            .column(WebwiseMeta.DATE, new LocalDateValueFormatter())
            .column(WebwiseMeta.UNIQUE_USERS, new NumberValueFormatter())
            .column(WebwiseMeta.WEEKLY_UNIQUE_USERS, new NumberValueFormatter())
            .column(WebwiseMeta.MONTHLY_UNIQUE_USERS, new NumberValueFormatter())
            .column(WebwiseMeta.SWITCH_ONS, new NumberValueFormatter())
            .column(WebwiseMeta.SWITCH_ONS, new NumberValueFormatter());

    @Restrict(restriction = "Report.run", parameters = "'webwise'")
    @Validate(validation = "Reporting.webwise", parameters = "#parameters")
    public void processExcel(WebwiseReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createExcelSerializer(os));
    }

    @Restrict(restriction = "Report.run", parameters = "'webwise'")
    @Validate(validation = "Reporting.webwise", parameters = "#parameters")
    public void processCsv(WebwiseReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createCsvSerializer(os));
    }

    @Restrict(restriction = "Report.run", parameters = "'webwise'")
    @Validate(validation = "Reporting.webwise", parameters = "#parameters")
    public void processHtml(WebwiseReportParameters parameters, SimpleReportData data) {
        run(parameters, reportsService.createHtmlSerializer(data));
    }

    private void run(WebwiseReportParameters parameters, AuditResultHandlerWrapper handler) {
        reportsService.execute(new Report(parameters, handler, true));
    }

    private class Report extends CommonAuditableReportSupport<WebwiseReportParameters> {
        public Report(WebwiseReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {
            handler.registry(REGISTRY, RowTypes.data());
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    if (parameters.getAccountIds().size() == 1) {
                        Account account = em.find(Account.class, parameters.getAccountIds().iterator().next());
                        builder.addDateRange(parameters.getDateRange(), account.getTimezone().toTimeZone());
                    } else {
                        builder.addDateRange(parameters.getDateRange(), StringUtil.getLocalizedString("report.ISPAccountTimeZone"));
                    }
                    addCountries(builder)
                            .addIds(parameters.getAccountIds().size() == 1 ? "account" : "accounts", Account.class, parameters.getAccountIds())
                            .addIds(parameters.getColocationIds().size() == 1 ? "colocation" : "colocations", Colocation.class, parameters.getColocationIds());
                }

                private PreparedParameterBuilder addCountries(PreparedParameterBuilder builder) {
                    Collection<String> names = new ArrayList<String>(parameters.getCountryCodes().size());
                    for (String code : parameters.getCountryCodes()) {
                        names.add(StringUtil.resolveGlobal("country", code, false, builder.getLocale()));
                    }
                    builder.add(parameters.getCountryCodes().size() == 1 ? "country" : "countries", CollectionUtils.toString(names), parameters.getCountryCodes());
                    return builder;
                }
            };

            query = createQuery("report.webswise_oo");
            summaryQuery = createQuery("report.webswise_oo_total");

            metaData = WebwiseMeta.META.resolve(parameters);
            summaryMetaData = WebwiseMeta.SUMMARY_META.resolve(parameters);
            if (Days.daysBetween(parameters.getDateRange().getBegin(), parameters.getDateRange().getEnd()).getDays() > 31) {
                summaryMetaData = summaryMetaData.exclude(WebwiseMeta.RANGE_UNIQUE_USERS);
            }

            handler.preparedParameters(preparedParameterBuilderFactory);
        }

        private Query createQuery(String functionName) {
            return db.queryFunction(functionName)
                    .parameter("p_colo_ids", parameters.getColocationIds(), IDS_TYPE)
                    .parameter("p_from_date", parameters.getDateRange().getBegin(), LOCAL_DATE_USER_TYPE)
                    .parameter("p_to_date", parameters.getDateRange().getEnd(), LOCAL_DATE_USER_TYPE);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.WEBWISE;
        }
    }
}
