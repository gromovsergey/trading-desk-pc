package com.foros.action.site.csv;

import com.foros.model.EntityBase;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.util.UploadUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SiteTagsIterator implements Iterator<EntityBase> {

    private Iterator<EntityBase> iterator;

    public SiteTagsIterator(Collection<Site> sites, boolean sort) {
        List<EntityBase> entities = new LinkedList<EntityBase>();
        for (Site site: sites) {
            if (site.getTags().isEmpty()) {
                entities.add(site);
                continue;
            }
            for (Tag tag: site.getTags()) {
                entities.add(tag);
            }
        }

        if (sort) {
            Collections.sort(entities, new Comparator<EntityBase>() {
                @Override
                public int compare(EntityBase eb1, EntityBase eb2) {
                    return (int) (UploadUtils.getRowNumber(eb1) - UploadUtils.getRowNumber(eb2));
                }
            });
        }

        iterator = entities.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public EntityBase next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
