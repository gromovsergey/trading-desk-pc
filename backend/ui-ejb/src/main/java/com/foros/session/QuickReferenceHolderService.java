package com.foros.session;

import com.foros.model.template.Template;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@LocalBean
@Singleton
public class QuickReferenceHolderService {

    @PersistenceContext(unitName = "AdServerPU")
    EntityManager em;


    private Long textTemplateId;

    private Long textSizeId;

    public Long getTextTemplateId() {
        if (textTemplateId == null) {
            textTemplateId = findTextTemplateId();
        }
        return textTemplateId;
    }

    private Long findTextTemplateId() {
        Query query = em.createQuery("SELECT ct.id FROM CreativeTemplate ct WHERE ct.defaultName = :text");
        return (Long) query
                .setParameter("text", Template.TEXT_TEMPLATE)
                .getSingleResult();
    }

    public Long getTextSizeId() {
        if (textSizeId == null) {
            textSizeId = findTextSizeId();
        }
        return textSizeId;
    }

    private Long findTextSizeId() {
        Query query = em.createQuery("SELECT cs.id FROM CreativeSize cs WHERE cs.defaultName = :text");
        return (Long) query
                .setParameter("text", Template.TEXT_TEMPLATE)
                .getSingleResult();
    }

}
