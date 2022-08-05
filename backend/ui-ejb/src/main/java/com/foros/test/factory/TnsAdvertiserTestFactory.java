package com.foros.test.factory;

import com.foros.model.account.TnsAdvertiser;
import com.foros.session.account.yandex.advertiser.YandexTnsAdvertiserService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class TnsAdvertiserTestFactory extends TestFactory<TnsAdvertiser> {
    @EJB
    private YandexTnsAdvertiserService yandexTnsAdvertiserServiceBean;


    @Override
    public void persist(TnsAdvertiser entity) {
        if (entity.getId() == null) {
            Long id = jdbcTemplate.queryForObject("select coalesce(max(tns_advertiser_id), 0) + 1 from TnsAdvertiser", Long.class);
            entity.setId(id);
        }
        entityManager.persist(entity);
    }

    @Override
    public void update(TnsAdvertiser entity) {
    }

    @Override
    public TnsAdvertiser create() {
        TnsAdvertiser advertiser = new TnsAdvertiser();
        advertiser.setName(getTestEntityRandomName());
        return advertiser;
    }

    @Override
    public TnsAdvertiser createPersistent() {
        TnsAdvertiser tnsAdvertiser = create();
        persist(tnsAdvertiser);
        return tnsAdvertiser;
    }
}
