package com.foros.session.reporting.inventoryEstimation;

import com.foros.model.account.Account;
import com.foros.model.account.PublisherAccount;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.reporting.RowTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.CurrencyValueFormatter;
import com.foros.reporting.serializer.formatter.ImpressionValueFormatter;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryChain;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.tools.query.Query;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;
import java.sql.Types;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless(name = "InventoryEstimationReportService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class })
public class InventoryEstimationReportService implements GenericReportService<InventoryEstimationReportParameters, SimpleReportData> {
    private static final ValueFormatterRegistry REGISTRY = ValueFormatterRegistries.registry()
        .column(InventoryEstimationMeta.PASSBACKS, new ImpressionValueFormatter(InventoryEstimationMeta.PASSBACKS_PC));

    @EJB
    private StatsDbQueryProvider statsDb;

    @EJB
    private ReportsService reportsService;

    @EJB
    private CurrentUserService currentUserService;

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @Override
    @Restrict(restriction = "Report.InventoryEstimation.run", parameters = "#parameters")
    @Validate(validation = "Reporting.inventoryEstimation", parameters = "#parameters")
    public void processHtml(InventoryEstimationReportParameters parameters, SimpleReportData data) {
        processInventoryEstimationReport(parameters, reportsService.createHtmlSerializer(data));
    }

    @Override
    @Restrict(restriction = "Report.InventoryEstimation.run", parameters = "#parameters")
    @Validate(validation = "Reporting.inventoryEstimation", parameters = "#parameters")
    public void processExcel(InventoryEstimationReportParameters parameters, OutputStream stream) {
        processInventoryEstimationReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.InventoryEstimation.run", parameters = "#parameters")
    @Validate(validation = "Reporting.inventoryEstimation", parameters = "#parameters")
    public void processCsv(InventoryEstimationReportParameters parameters, OutputStream stream) {
        processInventoryEstimationReport(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processInventoryEstimationReport(InventoryEstimationReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer));
    }

    private class Report extends CommonAuditableReportSupport<InventoryEstimationReportParameters> {

        public Report(InventoryEstimationReportParameters parameters, AuditResultHandlerWrapper handler) {
            super(parameters, handler, true);
        }

        @Override
        public void prepare() {
            final PublisherAccount account = em.find(PublisherAccount.class, parameters.getAccountId());

            ValueFormatterRegistryImpl registryImpl = ValueFormatterRegistries.registry()
                .column(InventoryEstimationMeta.CMP_THRESHOLD, new CurrencyValueFormatter(account.getCurrency().getCurrencyCode()))
                .column(InventoryEstimationMeta.REVENUE, new CurrencyValueFormatter(account.getCurrency().getCurrencyCode()));
            ValueFormatterRegistryChain registry = ValueFormatterRegistries.chain()
                .registry(REGISTRY)
                .registry(registryImpl);

            handler.registry(registry, RowTypes.data());
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {

                    builder
                        .addDateRange(parameters.getDateRange(), account.getTimezone().toTimeZone())
                        .addId("account", Account.class, parameters.getAccountId())
                        .addId("site", Site.class, parameters.getSiteId())
                        .addId("tag", Tag.class, parameters.getTagId())
                        .add("reservedPremium", StringUtil.getLocalizedBigDecimal(parameters.getReservedPremium()));
                }
            };
            handler.preparedParameters(preparedParameterBuilderFactory);

            metaData = InventoryEstimationMeta.META_DATA.resolve(parameters);
            summaryMetaData = InventoryEstimationMeta.SUMMARY_META.resolve(parameters);

            query = createQuery("report.inventory_estimation");
            summaryQuery = createQuery("report.inventory_estimation_summary");
        }

        private Query createQuery(String query) {
            return statsDb
                .queryFunction(query)
                .parameter("p_from_date", parameters.getDateRange().getBegin(), PostgreLocalDateUserType.INSTANCE)
                .parameter("p_to_date", parameters.getDateRange().getEnd(), PostgreLocalDateUserType.INSTANCE)
                .parameter("p_account_id", parameters.getAccountId(), Types.INTEGER)
                .parameter("p_site_id", parameters.getSiteId(), Types.INTEGER)
                .parameter("p_tag_id", parameters.getTagId(), Types.INTEGER)
                .parameter("p_reserved_for_premium_pc", parameters.getReservedPremium(), Types.NUMERIC)
                .parameter("p_user_id", currentUserService.isSiteLevelRestricted() ? currentUserService.getUserId() : null, Types.INTEGER);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.INVENTORY_ESTIMATION;
        }
    }
}
