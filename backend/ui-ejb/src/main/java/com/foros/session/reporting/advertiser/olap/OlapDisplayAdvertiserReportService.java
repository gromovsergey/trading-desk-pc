package com.foros.session.reporting.advertiser.olap;

import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@LocalBean
@Stateless(name = "OlapDisplayAdvertiserReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class OlapDisplayAdvertiserReportService extends OlapAdvertiserReportServiceBase {

    @EJB
    protected WalledGardenService walledGardenService;

    @Override
    protected OlapAdvertiserReportDescription getDescription(OlapAdvertiserReportParameters params) {
        return params.getReportType().getDisplayDescription();
    }

    @Override
    protected PreparedParameterBuilder.Factory getParameterBuilderFactory(final OlapAdvertiserReportParameters params) {
        return new PreparedParameterBuilder.Factory() {
            @Override
            protected void fillParameters(PreparedParameterBuilder builder) {
                AdvertisingAccountBase account = em.find(AdvertisingAccountBase.class, params.getAccountId());
                builder.add("reportType", StringUtil.getLocalizedString(params.getReportType().getNameKey()), params.getReportType())
                        .addDateRange(params.getDateRange(), account.getTimezone().toTimeZone())
                        .addId("account", Account.class, params.getAccountId())
                        .addIds("advertisers", AdvertiserAccount.class, params.getAdvertiserIds())
                        .addIds("campaigns", Campaign.class, params.getCampaignIds())
                        .addIds("creativeGroups", CampaignCreativeGroup.class, params.getCcgIds())
                        .addIds("creatives", CampaignCreative.class, params.getCampaignCreativeIds());
            }
        };
    }

    @Override
    protected Set<OlapColumn> calculateAvailableMetaData(
            Set<OlapColumn> toExclude,
            OlapAdvertiserReportParameters params,
            OlapAdvertiserReportDescription description) {
        super.calculateAvailableMetaData(toExclude, params, description);

        if (!walledGardenService.isAgencyWalledGarden(params.getAccountId())) {
            toExclude.add(OlapAdvertiserMeta.WG_LICENSING_COST);
        }

        if (params.getUnitOfTime() == OlapAdvertiserReportParameters.UnitOfTime.DATE) {
            if (params.getReportType() == OlapDetailLevel.Campaign) {
                toExclude.add(OlapAdvertiserMeta.COUNTRY);
                toExclude.add(OlapAdvertiserMeta.CREATIVE_SIZE);
            }

            if (params.getReportType() == OlapDetailLevel.CreativeGroup) {
                toExclude.add(OlapAdvertiserMeta.CHANNEL_TARGET);
                toExclude.add(OlapAdvertiserMeta.CREATIVE_SIZE);
            }
        }
        return toExclude;
    }

    @Override
    protected boolean isUniqueUsersAllowed(OlapAdvertiserReportParameters params) {
        if (!super.isUniqueUsersAllowed(params)) {
            return false;
        }

        Set<String> columns = params.getColumns();
        if (params.getReportType() == OlapDetailLevel.Campaign) {
            // shouldn't contains
            return !(columns.contains(OlapAdvertiserMeta.COUNTRY.getNameKey())
                || columns.contains(OlapAdvertiserMeta.CHANNEL_TARGET.getNameKey())
                || columns.contains(OlapAdvertiserMeta.CREATIVE_SIZE.getNameKey()));
        }

        return true;
    }

    @Override
    protected CampaignType getCampaignType() {
        return CampaignType.DISPLAY;
    }

    @Override
    protected OlapColumnsSplitter newColumnsSplitter(OlapColumnsSplitter splitter, OlapAdvertiserReportParameters params) {
        if (walledGardenService.isAgencyWalledGarden(params.getAccountId()) && params.isSplitWalledGardenStatistics()) {
            splitter = new OlapColumnsSplitter.WalledGarden(splitter);
        }

        return super.newColumnsSplitter(splitter, params);
    }

    @Override
    @Restrict(restriction = "Report.OlapDisplayAdvertiser.run", parameters = "#parameters")
    @Validate(validation = "Reporting.displayAdvertiser", parameters = "#parameters")
    public void processHtml(OlapAdvertiserReportParameters parameters, SimpleReportData data) {
        run(parameters, reportsService.createHtmlSerializer(data), true);
    }

    @Override
    @Restrict(restriction = "Report.OlapDisplayAdvertiser.run", parameters = "#parameters")
    @Validate(validation = "Reporting.displayAdvertiser", parameters = "#parameters")
    public void processCsv(OlapAdvertiserReportParameters parameters, OutputStream os) {
        parameters.setAddSubtotals(false);
        run(parameters, reportsService.createCsvSerializer(os), false);
    }

    @Override
    @Restrict(restriction = "Report.OlapDisplayAdvertiser.run", parameters = "#parameters")
    @Validate(validation = "Reporting.displayAdvertiser", parameters = "#parameters")
    public void processExcel(OlapAdvertiserReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createExcelSerializer(os), false);
    }

    private void run(OlapAdvertiserReportParameters parameters, AuditResultHandlerWrapper handler, boolean summary) {
        reportsService.execute(new Report(parameters, handler, summary));
    }

    private class Report extends OlapAdvertiserReportServiceBase.Report {

        private Report(OlapAdvertiserReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        protected String getOlapQueryType() {
            return "D";
        }

        @Override
        public ReportType getReportType() {
            return ReportType.DISPLAY_ADVERTISING;
        }

        @Override
        protected PreparedParameterBuilder.Factory newParametersFactory(final OlapAdvertiserReportParameters parameters) {
            return new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    AdvertisingAccountBase account = em.find(AdvertisingAccountBase.class, parameters.getAccountId());
                    builder.add("reportType", StringUtil.getLocalizedString(parameters.getReportType().getNameKey()), parameters.getReportType())
                            .addDateRange(parameters.getDateRange(), account.getTimezone().toTimeZone())
                            .addId("account", Account.class, parameters.getAccountId())
                            .addIds("advertisers", AdvertiserAccount.class, parameters.getAdvertiserIds())
                            .addIds("campaigns", Campaign.class, parameters.getCampaignIds())
                            .addIds("creativeGroups", CampaignCreativeGroup.class, parameters.getCcgIds())
                            .addIds("creatives", CampaignCreative.class, parameters.getCampaignCreativeIds());
                }
            };
        }
    }


    @Override
    protected boolean isHIDColumnsAvailable(OlapAdvertiserReportParameters params) {
        return super.isHIDColumnsAvailable(params) || params.getReportType() == OlapDetailLevel.CreativeGroup;
    }
}
