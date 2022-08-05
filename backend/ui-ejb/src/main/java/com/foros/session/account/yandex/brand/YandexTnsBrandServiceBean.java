package com.foros.session.account.yandex.brand;

import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.account.TnsBrand;
import com.foros.session.account.yandex.YandexTnsHttpClientBuilder;
import com.foros.session.account.yandex.TnsParser.TnsHandler;
import com.foros.util.PersistenceUtils;
import com.foros.util.SQLUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hibernate.FlushMode;
import org.hibernate.Session;

@Singleton(name = "YandexTnsBrandService")
@Startup
public class YandexTnsBrandServiceBean implements YandexTnsBrandService {
    private static final Logger logger = Logger.getLogger(YandexTnsBrandServiceBean.class.getName());

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private ConfigService configService;

    private String url;
    private CloseableHttpClient httpClient;

    @PostConstruct
    public void init() {
        Config config = configService.detach();
        this.httpClient = YandexTnsHttpClientBuilder.build(config);
        this.url = config.get(ConfigParameters.YANDEX_ENDPOINT) + "/dictionaries/tnsbrands";
    }

    @Override
    public void synchronize() {
        logger.log(Level.INFO, "YandexTnsBrandService running.");
        final Session hibernateSession = PersistenceUtils.getHibernateSession(em);
        final Number maxBrandId = (Number) em.createNativeQuery("select max(tns_brand_id) from TNSBRAND").getSingleResult();
        hibernateSession.setFlushMode(FlushMode.MANUAL);

        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new IOException("Unexpected response status from Yandex: " + response.getStatusLine().getStatusCode() + ". URL: " + request.getURI().toString());
            }

            InputStream content = response.getEntity().getContent();
            new TnsBrandParser(new TnsHandler<TnsBrand>() {
                @Override
                protected void mergeObject(TnsBrand tns) {
                    if (maxBrandId == null || maxBrandId.longValue() < tns.getId()) {
                        em.persist(tns);
                    }

                    if (hibernateSession.getStatistics().getEntityCount() > 1000) {
                        em.flush();
                        em.clear();
                    }
                }
            }).parse(content);

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            em.flush();
            em.clear();
            hibernateSession.setFlushMode(FlushMode.AUTO);
        }
    }

    @Override
    public List<TnsBrand> searchBrands(String name, int maxResults) {
        String sql = "SELECT tns FROM TnsBrand tns " +
                " WHERE lower(tns.name) like :name ESCAPE '\\' " +
                " ORDER BY lower(tns.name)";

        name = name == null ? "" : name;

        List<TnsBrand> result = em.createQuery(
                sql, TnsBrand.class)
                .setParameter("name", SQLUtil.getEscapedString(name.toLowerCase(), '\\') + "%")
                .setMaxResults(maxResults)
                .getResultList();

        return result;
    }

    @Override
    public TnsBrand find(Long id) {
        if (id == null) {
            throw new EntityNotFoundException("TnsBrand with id = null not found");
        }

        TnsBrand tnsBrand = em.find(TnsBrand.class, id);
        if (tnsBrand == null) {
            throw new EntityNotFoundException("TnsBrand with id = " + id + " not found");
        }

        return tnsBrand;
    }

  }
