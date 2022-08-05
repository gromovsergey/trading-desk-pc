package com.foros.rs.client;

import com.foros.rs.client.data.DefaultResponseHandler;
import com.foros.rs.client.model.advertising.AdvertiserLink;
import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.model.entity.EntityLink;
import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.service.AccountService;
import com.foros.rs.client.service.AdvertisingChannelService;
import com.foros.rs.client.service.CCGKeywordService;
import com.foros.rs.client.service.CampaignCreativeGroupService;
import com.foros.rs.client.service.CampaignService;
import com.foros.rs.client.service.ConversionAssociationsService;
import com.foros.rs.client.service.ConversionService;
import com.foros.rs.client.service.CreativeCategoryService;
import com.foros.rs.client.service.CurrencyService;
import com.foros.rs.client.service.DeviceChannelService;
import com.foros.rs.client.service.DiscoverChannelService;
import com.foros.rs.client.service.GeoChannelService;
import com.foros.rs.client.service.ReportService;
import com.foros.rs.client.service.RestrictionService;
import com.foros.rs.client.service.SiteCreativeApprovalService;
import com.foros.rs.client.service.SiteService;
import com.foros.rs.client.service.TagService;
import com.foros.rs.client.service.ThirdPartyCreativeService;
import com.foros.rs.client.service.TriggerQAService;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.http.HttpHost;
import org.apache.http.auth.ContextAwareAuthScheme;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.junit.Assert;
import org.junit.Before;

public abstract class AbstractUnitTest extends Assert {
    protected Foros foros;

    protected AccountService accountService;
    protected CampaignService campaignService;
    protected CampaignCreativeGroupService groupService;
    protected AdvertisingChannelService advertisingChannelService;
    protected DiscoverChannelService discoverChannelService;
    protected CCGKeywordService ccgKeywordService;
    protected SiteCreativeApprovalService siteCreativeApprovalService;
    protected CreativeCategoryService creativeCategoryService;
    protected ReportService reportService;
    protected TriggerQAService triggerQAService;
    protected ThirdPartyCreativeService thirdPartyCreativeService;
    protected ConversionService conversionService;
    protected ConversionAssociationsService conversionAssociationsService;
    protected GeoChannelService geoChannelService;
    protected DeviceChannelService deviceChannelService;
    protected CurrencyService currencyService;
    protected SiteService siteService;
    protected TagService tagService;
    protected RestrictionService restrictionService;

    protected Properties props;
    protected TestRsClientConfigurator configurator;
    protected RsClient rsClient;

    protected void loadProps() throws IOException {
        props = new Properties(System.getProperties());
        if (props.getProperty("foros.base") == null) {
            URL connectionFile = getClass().getResource("/test.properties");
            props.load(new FileReader(connectionFile.getFile()));
        }
    }

    @Before
    public final void initTest() throws Exception {
        loadProps();

        String forosBase = props.getProperty("foros.base");
        String userToken = props.getProperty("foros.userToken");
        String key = props.getProperty("foros.key");
        String forosProxy = props.getProperty("foros.proxy");

        HttpHost proxy = null;
        if (forosProxy != null) {
            String[] arr = forosProxy.split(":");
            proxy = new HttpHost(arr[0], Integer.parseInt(arr[1]));
        }
        configurator = (TestRsClientConfigurator)(noSSLChecksConfigurator()
                .forosBase(forosBase)
                .userToken(userToken)
                .key(key)
                .proxy(proxy)
                .responseHandler(new DefaultResponseHandler(new ValidatingJAXBResponseHandler())));

        rsClient = configurator.configure();

        foros = new Foros(rsClient);

        accountService = foros.getAccountService();
        campaignService = foros.getCampaignService();
        groupService = foros.getCampaignCreativeGroupService();
        ccgKeywordService = foros.getCCGKeywordService();
        advertisingChannelService = foros.getAdvertisingChannelService();
        discoverChannelService = foros.getDiscoverChannelService();
        siteCreativeApprovalService = foros.getSiteCreativeApprovalService();
        creativeCategoryService = foros.getCreativeCategoryService();
        reportService = foros.getReportService();
        triggerQAService = foros.getTriggerQAService();
        thirdPartyCreativeService = foros.getThirdPartyCreativeService();
        conversionService = foros.getConversionService();
        conversionAssociationsService = foros.getConversionAssociationsService();
        geoChannelService = foros.getGeoChannelService();
        deviceChannelService = foros.getDeviceChannelService();
        currencyService = foros.getCurrencyService();
        siteService = foros.getSiteService();
        tagService = foros.getTagService();
        restrictionService = foros.getRestrictionService();
    }

    public EntityLink link(long id) {
        EntityLink link = new EntityLink();
        link.setId(id);
        return link;
    }

    public AdvertiserLink advertiserLink(long id) {
        AdvertiserLink link = new AdvertiserLink();
        link.setId(id);
        return link;
    }

    public Long longProperty(String name) {
        return Long.valueOf(stringProperty(name));
    }

    public List<Long> longArrayProperty(String name) {
        String val = stringProperty(name);
        String[] strings = val.split(",");

        ArrayList<Long> result = new ArrayList<Long>();
        for (String string : strings) {
            result.add(Long.valueOf(string));
        }

        return result;
    }

    public String stringProperty(String name) {
        String val = props.getProperty(name);
        if (val == null || val.isEmpty()) {
            throw new RuntimeException("property not found: " + name);
        }
        return val;
    }

    public <T extends EntityBase> Operation<T> operation(T t, OperationType operationType) {
        Operation<T> campaignOperation = new Operation<T>();
        campaignOperation.setEntity(t);
        campaignOperation.setType(operationType);
        return campaignOperation;
    }

    public static XMLGregorianCalendar getDateTime() throws DatatypeConfigurationException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        return xmlGregorianCalendar;
    }

    private TestRsClientConfigurator noSSLChecksConfigurator() {
        System.setProperty("jsse.enableSNIExtension", "false");
        return new TestRsClientConfigurator();
    }

    private SSLContext createSSLContext() {
        try {
            return new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected class TestRsClientConfigurator extends RsClientConfigurator {

        private ContextAwareAuthScheme authScheme = new ForosAuthScheme();

        public void setAuthScheme(ContextAwareAuthScheme authScheme) {
            this.authScheme = authScheme;
        }

        @Override
        protected HttpClientBuilder clientBuilder(HttpRequestExecutor requestExecutor, RequestConfig requestConfig) {
            return super.clientBuilder(requestExecutor, requestConfig)
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(createSSLContext(), new AllowAllHostnameVerifier()));
        }

        @Override
        protected HttpClientContext buildHttpContext(URI hostUri) {
            HttpClientContext result = buildHttpContext(hostUri, userToken, key);

            BasicAuthCache authCache = new BasicAuthCache();
            authCache.put(new HttpHost(hostUri.getHost(), hostUri.getPort(), hostUri.getScheme()), authScheme);
            result.setAuthCache(authCache);
            return result;
        }
    }
}
