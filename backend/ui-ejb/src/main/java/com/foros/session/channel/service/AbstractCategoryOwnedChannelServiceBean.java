package com.foros.session.channel.service;

import com.foros.model.channel.CategoryChannel;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


public class AbstractCategoryOwnedChannelServiceBean implements CategoryOwnedChannelService {

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @Override
    public List<CategoryChannel> getCategories(Long channelId) {
        List<CategoryChannel> l =  em.createQuery("select c.categories from Channel c where c.id = :id").
                setParameter("id", channelId).getResultList();
        return l;
    }

}
