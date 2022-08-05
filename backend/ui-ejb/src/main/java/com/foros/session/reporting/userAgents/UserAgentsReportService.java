package com.foros.session.reporting.userAgents;

import com.foros.model.channel.Platform;
import com.foros.reporting.RowTypes;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.HtmlCellAccessor;
import com.foros.reporting.serializer.formatter.NumberValueFormatter;
import com.foros.reporting.serializer.formatter.StringValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryImpl;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.NamedTO;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.reporting.GenericReportService;
import com.foros.session.reporting.ReportType;
import com.foros.session.channel.service.PlatformService;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.PreparedParameterBuilder;
import com.foros.session.reporting.ReportsService;
import com.foros.util.StringUtil;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.usertype.UserType;
import org.joda.time.LocalDate;

@LocalBean
@Stateless(name = "UserAgentsReportService")
@Interceptors({RestrictionInterceptor.class})
public class UserAgentsReportService implements GenericReportService<UserAgentsReportParameters, SimpleReportData> {

    private static final String CHANNEL_URL_PATTERN = "/admin/DeviceChannel/view.action?id=%d";
    private static final UserType LOCAL_DATE_USER_TYPE = new PostgreLocalDateUserType();

    @EJB
    private StatsDbQueryProvider statsDb;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private PlatformService platformService;

    @EJB
    private ReportsService reportsService;

    @Override
    @Restrict(restriction = "Report.run", parameters = "'userAgents'")
    public void processHtml(UserAgentsReportParameters parameters, SimpleReportData data) {
        processUserAgentsReport(parameters, reportsService.createHtmlSerializer(data));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'userAgents'")
    public void processExcel(UserAgentsReportParameters parameters, OutputStream stream) {
        processUserAgentsReport(parameters, reportsService.createExcelSerializer(stream));
    }

    @Override
    @Restrict(restriction = "Report.run", parameters = "'userAgents'")
    public void processCsv(UserAgentsReportParameters parameters, OutputStream stream) {
        processUserAgentsReport(parameters, reportsService.createCsvSerializer(stream));
    }

    private void processUserAgentsReport(UserAgentsReportParameters parameters, AuditResultHandlerWrapper serializer) {
        reportsService.execute(new Report(parameters, serializer, false));
    }

    private class Report extends CommonAuditableReportSupport<UserAgentsReportParameters> {
        public Report(UserAgentsReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {
            ValueFormatterRegistryImpl registry = ValueFormatterRegistries.registry()
                .column(UserAgentsMeta.USER_AGENT, new StringValueFormatter())
                .column(UserAgentsMeta.REQUESTS, new NumberValueFormatter())
                .column(UserAgentsMeta.CHANNELS, new ChannelListFormatter(CHANNEL_URL_PATTERN))
                .column(UserAgentsMeta.PLATFORMS, new PlatformListFormatter());

            handler.registry(registry, RowTypes.data());
            preparedParameterBuilderFactory = new PreparedParameterBuilder.Factory() {
                @Override
                protected void fillParameters(PreparedParameterBuilder builder) {
                    LocalDate date = parameters.getDate();
                    builder.addDate(date, TimeZone.getTimeZone("GMT"));
                }
            };

            handler.preparedParameters(preparedParameterBuilderFactory);

            query = statsDb.queryFunction("report.user_agents")
                    .parameter("date", parameters.getDate(), LOCAL_DATE_USER_TYPE);

            metaData = UserAgentsMeta.META_DATA.resolve(parameters);
        }

        @Override
        public ReportType getReportType() {
            return ReportType.USER_AGENTS;
        }
    }

    private class ChannelListFormatter extends ValueFormatterSupport<String> {
        private String urlPattern;

        private Map<Long, String> channels;

        public ChannelListFormatter(String urlPattern) {
            this.urlPattern = urlPattern;

            @SuppressWarnings("unchecked")
            List<NamedTO> deviceChannels = em.createQuery("select new com.foros.session.NamedTO(c.id, c.name) from DeviceChannel c").getResultList();

            channels = new HashMap<Long, String>(deviceChannels.size());

            for (NamedTO channel : deviceChannels) {
                channels.put(channel.getId(), channel.getName());
            }
        }

        @Override
        public String formatText(String value, FormatterContext context) {
            if (StringUtil.isPropertyEmpty(value)) {
                return "";
            }

            StringBuilder res = new StringBuilder();
            StringTokenizer t = new StringTokenizer(value, "|");

            while (t.hasMoreTokens()) {
                Long id = Long.valueOf(t.nextToken());
                String channelName = channels.get(id);

                res.append(channelName);

                if (t.hasMoreTokens()) {
                    res.append(", ");
                }
            }

            return res.toString();
        }

        @Override
        public void formatHtml(HtmlCellAccessor cellAccessor, String value, FormatterContext context) {
            if (StringUtil.isPropertyEmpty(value)) {
                cellAccessor.setHtml("");
                return;
            }

            StringBuilder res = new StringBuilder();
            StringTokenizer t = new StringTokenizer(value, "|");

            while (t.hasMoreTokens()) {
                Long id = Long.valueOf(t.nextToken());
                String channelName = channels.get(id);

                res.append("<a href='").append(getUrl(id)).append("' target=\"blank\">").append(channelName).append("</a>");

                if (t.hasMoreTokens()) {
                    res.append(", ");
                }
            }

            cellAccessor.setHtml(res.toString());
        }

        private String getUrl(Long value) {
            return String.format(urlPattern, value);
        }
    }

    private class PlatformListFormatter extends ValueFormatterSupport<String> {
        private Map<Long, String> platforms;

        public PlatformListFormatter() {
            List<Platform> platformsList = platformService.findAll();

            platforms = new HashMap<Long, String>(platformsList.size());

            for (Platform platform: platformsList) {
                platforms.put(platform.getId(), platform.getName());
            }
        }

        @Override
        public String formatText(String value, FormatterContext context) {
            if (StringUtil.isPropertyEmpty(value)) {
                return "";
            }

            StringBuilder res = new StringBuilder();
            StringTokenizer t = new StringTokenizer(value, "|");

            while (t.hasMoreTokens()) {
                Long id = Long.valueOf(t.nextToken());
                String platformName = platforms.get(id);

                res.append(platformName);

                if (t.hasMoreTokens()) {
                    res.append(", ");
                }
            }

            return res.toString();
        }
    }
}
