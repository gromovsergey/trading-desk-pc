package com.foros.test.factory;

import com.foros.model.ApproveStatus;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.RTBCategory;
import com.foros.model.creative.RTBConnector;
import com.foros.session.creative.CreativeCategoryServiceBean;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class CreativeCategoryTestFactory extends TestFactory<CreativeCategory> {
    @EJB
    private CreativeCategoryServiceBean creativeCategoryService;

    public void populate(CreativeCategory category) {
        category.setDefaultName(getTestEntityRandomName());
    }

    @Override
    public CreativeCategory create() {
        CreativeCategory category = new CreativeCategory();
        populate(category);
        return category;
    }

    public CreativeCategory create(CreativeCategoryType type) {
        CreativeCategory category = create();
        category.setType(type);
        return category;
    }

    @Override
    public void persist(CreativeCategory category) {
        creativeCategoryService.create(category);
        entityManager.flush();
    }

    public void update(CreativeCategory category) {
        creativeCategoryService.update(category);
        entityManager.flush();
    }

    @Override
    public CreativeCategory createPersistent() {
        CreativeCategory category = create(CreativeCategoryType.CONTENT);
        persist(category);
        return category;
    }

    public CreativeCategory createPersistent(CreativeCategoryType type, ApproveStatus qaStatus) {
        CreativeCategory category = create(type);
        category.setQaStatus(qaStatus.getLetter());
        persist(category);
        return category;
    }

    public CreativeCategory populateRtb(CreativeCategory category, int rtbConnectorIndex) {
        RTBConnector rtbConnector = creativeCategoryService.getRTBConnectors().get(rtbConnectorIndex);
        RTBCategory rtbCategory = new RTBCategory();
        rtbCategory.setName(getTestEntityRandomName());
        rtbCategory.setRtbConnector(rtbConnector);
        rtbCategory.setCreativeCategory(category);
        category.getRtbCategories().add(rtbCategory);
        return category;
    }

    public RTBConnector findConnector(String name) {
        return entityManager.createQuery("select c from RTBConnector c where c.name = :name", RTBConnector.class)
                .setParameter("name", name)
                .getSingleResult();
    }
}
