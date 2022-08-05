package com.foros.session.query.channel;

import com.foros.model.Status;
import com.foros.model.channel.DeviceChannel;
import com.foros.session.query.SqlOrder;
import com.foros.session.query.criteria.CompositeCriteria;

import java.util.Collection;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;

public class DeviceChannelListsQueryImpl extends AbstractChannelQuery<DeviceChannelListsQueryImpl> {

    public DeviceChannelListsQueryImpl() {
        super(new CompositeCriteria.Builder()
                .dataCriteria(createDeviceChannelCriteria())
                .countCriteria(createDeviceChannelCriteria())
                .build());
        getCriteria().getDataCriteria()
                .createAlias("parentChannel", "pc", CriteriaSpecification.LEFT_JOIN);
    }

    private static DetachedCriteria createDeviceChannelCriteria() {
        DetachedCriteria criteria = DetachedCriteria
                .forClass(DeviceChannel.class);
        return criteria;
    }

    @Override
    protected ProjectionList createTOProjections() {
        return Projections.projectionList()
                .add(Projections.id().as("id"))
                .add(Projections.property("name").as("name"))
                .add(Projections.property("version").as("version"))
                .add(Projections.property("status").as("status"))
                .add(Projections.property("pc.id").as("parentChannelId"))
                .add(Projections.property("expression").as("expression"));
    }

    @Override
    protected ResultTransformer createTOTransformer() {
        return new DeviceChannelApiTOTransformer();
    }

    public DeviceChannelListsQueryImpl orderByName() {
        getCriteria().addOrder(SqlOrder.asc("this.name"));
        return this;
    }

    public DeviceChannelListsQueryImpl parentChannels(Collection<Long> parentChannelIds) {
        if (parentChannelIds != null && !parentChannelIds.isEmpty()) {
            getCriteria().add(Restrictions.in("parentChannel.id", parentChannelIds));
        }
        return self();
    }

    public DeviceChannelListsQueryImpl statuses(Collection<Status> statuses) {
        if (statuses != null && !statuses.isEmpty()) {
            getCriteria().add(Restrictions.in("this.status", Status.getStatusCodes(statuses)));
        }
        return self();
    }
}
