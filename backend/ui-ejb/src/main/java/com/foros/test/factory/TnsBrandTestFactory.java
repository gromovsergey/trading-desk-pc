package com.foros.test.factory;

import com.foros.model.account.TnsBrand;
import com.foros.session.account.yandex.brand.YandexTnsBrandService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@LocalBean
public class TnsBrandTestFactory extends TestFactory<TnsBrand> {
    @EJB
    private YandexTnsBrandService yandexTnsBrandServiceBean;

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager entityManager;

    @Override
    public void persist(TnsBrand entity) {
        if (entity.getId() == null) {
            Long id = jdbcTemplate.queryForObject("select coalesce(max(tns_brand_id), 0) + 1 from TnsBrand", Long.class);
            entity.setId(id);
        }
        entityManager.persist(entity);
    }

    @Override
    public void update(TnsBrand entity) {
    }

    @Override
    public TnsBrand create() {
        TnsBrand brand = new TnsBrand();
        brand.setName(getTestEntityRandomName());
        return brand;
    }

    @Override
    public TnsBrand createPersistent() {
        TnsBrand brand = create();
        persist(brand);
        return brand;
    }
}
