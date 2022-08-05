package com.foros.session.account.yandex.advertiser;

import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.account.TnsAdvertiser;
import com.foros.session.account.yandex.TnsParser.TnsHandler;
import com.foros.session.account.yandex.YandexTnsHttpClientBuilder;
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

@Singleton(name = "YandexTnsAdvertiserService")
@Startup
public class YandexTnsAdvertiserServiceBean implements YandexTnsAdvertiserService {
    private static final Logger logger = Logger.getLogger(YandexTnsAdvertiserServiceBean.class.getName());

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
        this.url = config.get(ConfigParameters.YANDEX_ENDPOINT) + "/dictionaries/tnsarticles";
    }

    @Override
    public void synchronize() {
        logger.log(Level.INFO, "YandexTnsAdvertiserService running.");
        final Session hibernateSession = PersistenceUtils.getHibernateSession(em);
        final Number maxAdvertiserId = (Number) em.createNativeQuery("select max(tns_advertiser_id) from TNSADVERTISER").getSingleResult();

        hibernateSession.setFlushMode(FlushMode.MANUAL);

        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new IOException("Unexpected response status from Yandex: " + response.getStatusLine().getStatusCode() + ". URL: " + request.getURI().toString());
            }

            InputStream content = response.getEntity().getContent();
            new TnsAdvertiserParser(new TnsHandler<TnsAdvertiser>() {
                @Override
                protected void mergeObject(TnsAdvertiser tns) {
                    if (maxAdvertiserId == null || maxAdvertiserId.longValue() < tns.getId()) {
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
    public List<TnsAdvertiser> searchAdvertisers(String name, int maxResults) {
        String sql = "SELECT tns FROM TnsAdvertiser tns " +
                " WHERE lower(tns.name) like :name ESCAPE '\\' " +
                " ORDER BY lower(tns.name)";

        name = name == null ? "" : name;

        List<TnsAdvertiser> result = em.createQuery(sql, TnsAdvertiser.class)
                .setParameter("name", SQLUtil.getEscapedString(name.toLowerCase(), '\\') + "%")
                .setMaxResults(maxResults)
                .getResultList();

        return result;
    }

    @Override
    public TnsAdvertiser find(Long id) {
        if (id == null) {
            throw new EntityNotFoundException("TnsAdvertiser with id = null not found");
        }

        TnsAdvertiser tnsAdvertiser = em.find(TnsAdvertiser.class, id);
        if (tnsAdvertiser == null) {
            throw new EntityNotFoundException("TnsAdvertiser with id = " + id + " not found");
        }

        return tnsAdvertiser;
    }

}
