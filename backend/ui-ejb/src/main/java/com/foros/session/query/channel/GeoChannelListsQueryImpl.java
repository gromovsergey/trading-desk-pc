package com.foros.session.query.channel;

import com.foros.model.Status;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.GeoType;
import com.foros.session.query.SqlOrder;
import com.foros.session.query.criteria.CompositeCriteria;
import com.foros.session.query.criterion.AnyCriterion;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;

public class GeoChannelListsQueryImpl
        extends AbstractChannelQuery<GeoChannelListsQuery> implements GeoChannelListsQuery {

    public GeoChannelListsQueryImpl() {
        super(new CompositeCriteria.Builder()
                .dataCriteria(createGeoChannelCriteria())
                .countCriteria(createGeoChannelCriteria())
                .build());
        getCriteria().getDataCriteria()
                .createAlias("parentChannel", "pc", CriteriaSpecification.LEFT_JOIN);
    }

    private static DetachedCriteria createGeoChannelCriteria() {
        DetachedCriteria criteria = DetachedCriteria
                .forClass(GeoChannel.class)
                .add(Restrictions.in("this.class", new String[] { "G" }));
        return criteria;
    }

    @Override
    protected ProjectionList createTOProjections() {
        return Projections.projectionList()
                .add(Projections.id().as("id"))
                .add(Projections.property("name").as("name"))
                .add(Projections.property("status").as("status"))
                .add(Projections.property("geoType").as("geoType"))
                .add(Projections.property("country.countryCode").as("countryCode"))
                .add(Projections.property("pc.name").as("parent"))
                .add(Projections.property("pc.geoType").as("parentGeoType"));
    }

    @Override
    protected void preExecuteBean() {
        getCriteria().getDataCriteria()
                .setProjection(createApiProjections())
                .setResultTransformer(new GeoChannelTransformer());
    }

    @Override
    protected ResultTransformer createTOTransformer() {
        return new GeoChannelListsTOTransformer();
    }

    private ProjectionList createApiProjections() {
        return Projections.projectionList()
                .add(Projections.id().as("id"))
                .add(Projections.property("name").as("name"))
                .add(Projections.property("status").as("status"))
                .add(Projections.property("geoType").as("geoType"))
                .add(Projections.property("country.countryCode").as("countryCode"))
                .add(Projections.property("pc.id").as("parentChannelId"))
                .add(Projections.property("coordinates.latitude").as("latitude"))
                .add(Projections.property("coordinates.longitude").as("longitude"))
                .add(Projections.property("radius.distance").as("distance"))
                .add(Projections.property("radius.radiusUnit").as("radiusUnit"));

    }

    @Override
    public GeoChannelListsQuery orderByName() {
        getCriteria()
                .addOrder(SqlOrder.asc("this.name"));

        return this;
    }

    @Override
    public GeoChannelListsQuery parentChannels(List<Long> parentChannelIds) {
        if (parentChannelIds != null && !parentChannelIds.isEmpty()) {
            getCriteria()
                    .add(AnyCriterion.anyId("parentChannel.id", parentChannelIds));
        }

        return self();
    }

    @Override
    public GeoChannelListsQuery geoTypes(List<GeoType> geoTypes) {
        if (geoTypes != null && !geoTypes.isEmpty()) {
            getCriteria()
                    .add(Restrictions.in("geoType", geoTypes));
        }

        return self();

    }

    @Override
    public GeoChannelListsQuery notAddress() {
        getCriteria()
                .add(Restrictions.ne("geoType", GeoType.ADDRESS));
        return self();
    }

    @Override
    public GeoChannelListsQuery notDeleted() {
        getCriteria().add(Restrictions.ne("this.status", Status.DELETED.getLetter()));

        return self();
    }
}
