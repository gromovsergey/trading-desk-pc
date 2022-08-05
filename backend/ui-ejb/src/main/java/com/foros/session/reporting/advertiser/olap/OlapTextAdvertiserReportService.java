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
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportType;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.io.OutputStream;
import java.util.Set;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@LocalBean
@Stateless(name = "OlapTextAdvertiserReportService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class})
public class OlapTextAdvertiserReportService extends OlapAdvertiserReportServiceBase {

    @Override
    @Restrict(restriction = "Report.OlapTextAdvertiser.run", parameters = "#parameters")
    @Validate(validation = "Reporting.textAdvertiser", parameters = "#parameters")
    public void processHtml(OlapAdvertiserReportParameters parameters, SimpleReportData data) {
        run(parameters, reportsService.createHtmlSerializer(data), true);
    }

    @Override
    @Restrict(restriction = "Report.OlapTextAdvertiser.run", parameters = "#parameters")
    @Validate(validation = "Reporting.textAdvertiser", parameters = "#parameters")
    public void processCsv(OlapAdvertiserReportParameters parameters, OutputStream os) {
        parameters.setAddSubtotals(false);
        run(parameters, reportsService.createCsvSerializer(os), false);
    }

    @Override
    @Restrict(restriction = "Report.OlapTextAdvertiser.run", parameters = "#parameters")
    @Validate(validation = "Reporting.textAdvertiser", parameters = "#parameters")
    public void processExcel(OlapAdvertiserReportParameters parameters, OutputStream os) {
        run(parameters, reportsService.createExcelSerializer(os), false);
    }

    private void run(OlapAdvertiserReportParameters parameters, AuditResultHandlerWrapper handler, boolean summary) {
        reportsService.execute(new Report(parameters, handler, summary));
    }

    @Override
    protected OlapAdvertiserReportDescription getDescription(OlapAdvertiserReportParameters params) {
        return params.getReportType().getTextDescription();
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
                        .addIds("adGroups", CampaignCreativeGroup.class, params.getCcgIds())
                        .addIds("textAds", CampaignCreative.class, params.getCampaignCreativeIds())
                        .add("keyword", params.getKeyword());
            }
        };
    }

    @Override
    protected Set<OlapColumn> calculateAvailableMetaData(
            Set<OlapColumn> toExclude,
            OlapAdvertiserReportParameters params, OlapAdvertiserReportDescription description) {
        super.calculateAvailableMetaData(toExclude, params, description);

        if (needExcludeCCGCountry(params)) {
            toExclude.add(OlapAdvertiserMeta.COUNTRY);
        }

        return toExclude;
    }

    @Override
    protected boolean isUniqueUsersAllowed(OlapAdvertiserReportParameters params) {
        if (!super.isUniqueUsersAllowed(params)) {
            return false;
        }

        Set<String> columns = params.getColumns();
        OlapDetailLevel reportType = params.getReportType();

        if (reportType == OlapDetailLevel.Keyword) {
            return false;
        }

        if (reportType == OlapDetailLevel.Campaign) {
            if (!needExcludeCCGCountry(params) && columns.contains(OlapAdvertiserMeta.COUNTRY.getNameKey())) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected CampaignType getCampaignType() {
        return CampaignType.TEXT;
    }

    private boolean needExcludeCCGCountry(OlapAdvertiserReportParameters params) {
        if (params.getReportType() == OlapDetailLevel.Campaign) {
            if (params.getUnitOfTime() == OlapAdvertiserReportParameters.UnitOfTime.DATE) {
                return true;
            }
        }

        return false;
    }

    private class Report extends OlapAdvertiserReportServiceBase.Report {

        public Report(OlapAdvertiserReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        protected String getOlapQueryType() {
            return "T";
        }

        @Override
        public ReportType getReportType() {
            return ReportType.TEXT_ADVERTISING;
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
                            .addIds("adGroups", CampaignCreativeGroup.class, parameters.getCcgIds())
                            .addIds("textAds", CampaignCreative.class, parameters.getCampaignCreativeIds())
                            .add("keyword", parameters.getKeyword());
                }
            };
        }
    }

    @Override
    protected boolean isHIDColumnsAvailable(OlapAdvertiserReportParameters params) {
        return super.isHIDColumnsAvailable(params) || params.getReportType() == OlapDetailLevel.AdGroup;
    }
}
