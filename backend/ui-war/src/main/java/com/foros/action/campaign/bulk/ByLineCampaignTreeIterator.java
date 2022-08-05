package com.foros.action.campaign.bulk;

import com.foros.model.EntityBase;
import com.foros.model.campaign.Campaign;
import com.foros.util.UploadUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ByLineCampaignTreeIterator implements Iterator<EntityBase> {
    private Iterator<Line> iterator;

    public ByLineCampaignTreeIterator(int totalCount, Iterator<Campaign> campaignIterator) {
        List<Line> list = new ArrayList<Line>(totalCount);

        NaturalCampaignTreeIterator it = new NaturalCampaignTreeIterator(campaignIterator);
        while (it.hasNext()) {
            EntityBase entity = it.next();
            Long line = UploadUtils.getRowNumber(entity);
            if (line != null) {
                list.add(new Line(line, entity));
            }
        }

        Collections.sort(list);
        iterator = list.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public EntityBase next() {
        return iterator.next().entity;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private static class Line implements Comparable<Line> {
        private Long line;
        private EntityBase entity;

        private Line(Long line, EntityBase entity) {
            this.line = line;
            this.entity = entity;
        }

        @Override
        public int compareTo(Line that) {
            return line.compareTo(that.line);
        }
    }
}
