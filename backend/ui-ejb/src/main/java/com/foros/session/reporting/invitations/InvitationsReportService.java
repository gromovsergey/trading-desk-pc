package com.foros.session.reporting.invitations;

import com.foros.model.account.Account;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.reporting.tools.query.parameters.usertype.PostgrePatternlistArrayUserType;
import com.foros.reporting.tools.query.parameters.usertype.PostgrePatternlistUserType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.hibernate.usertype.UserType;

@LocalBean
@Stateless(name = "InvitationsReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class InvitationsReportService implements GenericReportService<InvitationsReportParameters, SimpleReportData> {

    private static final UserType LOCAL_DATE_USER_TYPE = new PostgreLocalDateUserType();
    private static final UserType BROWSER_FAMILIES_USER_TYPE = new PostgrePatternlistArrayUserType();

    @EJB
    private StatsDbQueryProvider statsDb;

    @EJB
    private ReportsService reportsService;

    @Override
    @Restrict(restriction = "Report.run", parameters = "'invitations'")
    @Validate(validation = "Reporting.invitations", parameters = "#parameters")
    public void processHtml(InvitationsReportParameters parameters, SimpleReportData data) {
        processInvitationsReport(parameters, reportsService.createHtmlSerializer(data));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'invitations'")
    @Validate(validation = "Reporting.invitations", parameters = "#parameters")
    public void processExcel(InvitationsReportParameters parameters, OutputStream stream) {
        processInvitationsReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'invitations'")
    @Validate(validation = "Reporting.invitations", parameters = "#parameters")
    public void processCsv(InvitationsReportParameters parameters, OutputStream stream) {
        processInvitationsReport(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processInvitationsReport(InvitationsReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer, false));
    }

    private class Report extends CommonAuditableReportSupport<InvitationsReportParameters> {
        public Report(InvitationsReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {
            List<PostgrePatternlistUserType> browserFamilies = generateBrowserFamiliesFilter();

            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    builder.addDateRange(parameters.getDateRange(), TimeZone.getTimeZone("GMT"))
                           .addId("account", Account.class, parameters.getAccountId());
                }
            };

            handler.preparedParameters(preparedParameterBuilderFactory);

            query = statsDb.queryCallable("report.isp_invitations")
                    .parameter("fromDate", parameters.getDateRange().getBegin(), LOCAL_DATE_USER_TYPE)
                    .parameter("toDate", parameters.getDateRange().getEnd(), LOCAL_DATE_USER_TYPE)
                    .parameter("accountId", parameters.getAccountId(), Types.INTEGER)
                    .parameter("browserFamilies", browserFamilies, BROWSER_FAMILIES_USER_TYPE);

            metaData = parameters.getShowBrowserFamilies() ?
                    InvitationsMeta.FAMILIES_META_DATA.resolve(parameters) :
                    InvitationsMeta.TOTAL_META_DATA.resolve(parameters);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.INVITATIONS;
        }

        private List<PostgrePatternlistUserType> generateBrowserFamiliesFilter() {
            List<PostgrePatternlistUserType> browserFamilies = new ArrayList<>();
            browserFamilies.add(new PostgrePatternlistUserType("total", new ArrayList<String>() {{ add("%"); }}) );

            if (parameters.getShowBrowserFamilies()) {
                browserFamilies.add(new PostgrePatternlistUserType("chrome", new ArrayList<String>() {{ add("Chrome %"); }}) );
                browserFamilies.add(new PostgrePatternlistUserType("firefox", new ArrayList<String>() {{ add("Firefox %"); }}) );
                browserFamilies.add(new PostgrePatternlistUserType("msie", new ArrayList<String>() {{ add("MSIE %"); add("IE %"); }}) );
            }

            return browserFamilies;
        }
    }
}
