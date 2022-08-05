package com.foros.session.query.channel;

import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.model.channel.Channel;
import com.foros.persistence.hibernate.criterion.ForosLikeExpression;
import com.foros.session.bulk.IdNameTO;
import com.foros.session.channel.ChannelVisibilityCriteria;
import com.foros.session.query.BusinessQuery;
import com.foros.session.query.BusinessQueryImpl;
import com.foros.session.query.criteria.CompositeCriteria;
import com.foros.session.query.criteria.PaginationCriteria;
import com.foros.session.query.criterion.AnyCriterion;
import com.foros.session.query.criterion.LowerCaseFixSimpleExpression;
import com.foros.util.CollectionUtils;
import com.foros.util.SQLUtil;
import com.foros.util.StringUtil;
import com.foros.util.mapper.Converter;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.ResultTransformer;

public abstract class AbstractChannelQuery<T extends ChannelQuery>
        extends BusinessQueryImpl implements ChannelQuery<T> {

    private boolean asBean = false;

    public AbstractChannelQuery() {
        super(new CompositeCriteria.Builder()
                .dataCriteria(createDefaultChannelCriteria())
                .idCriteria(createDefaultChannelCriteria())
                .countCriteria(createDefaultChannelCriteria())
                .build());
    }

    public AbstractChannelQuery(PaginationCriteria criteria) {
        super(criteria);
    }

    private static DetachedCriteria createDefaultChannelCriteria() {
        DetachedCriteria criteria = DetachedCriteria
                .forClass(Channel.class)
                .createAlias("account", "account")
                .createAlias("account.accountManager", "accountManager", CriteriaSpecification.LEFT_JOIN)
                .createAlias("country", "country");

        return criteria;
    }

    protected T self() {
        return (T) this;
    }

    @Override
    public T asBean() {
        this.asBean = true;

        return self();
    }

    @Override
    public T asTO() {
        this.asBean = false;

        return self();
    }

    @Override
    public T name(String name) {
        if (StringUtil.isPropertyNotEmpty(name)) {
            getCriteria()
                    .add(new LowerCaseFixSimpleExpression("this.name", MatchMode.ANYWHERE.toMatchString(name), " like ")
                            .ignoreCase());
        }

        return self();
    }

    @Override
    public T nameWithEscape(String name) {
        if (StringUtil.isPropertyNotEmpty(name)) {
            getCriteria()
                    .add(new ForosLikeExpression("this.name", SQLUtil.getEscapedString(name, '/'), MatchMode.ANYWHERE, '/', true));
        }

        return self();
    }

    @Override
    public T existingByName(Set<Channel> channels) {
        Disjunction disjunction = Restrictions.disjunction();
        for (Channel channel : channels) {
            disjunction.add(
                    Restrictions.conjunction()
                            .add(Restrictions.eq("this.account.id", channel.getAccount().getId()))
                            .add(Restrictions.eq("this.country.countryCode", channel.getCountry().getCountryCode()))
                            .add(Restrictions.eq("this.name", channel.getName()))
                    );
        }
        getCriteria().add(disjunction);

        return self();
    }

    @Override
    public T asNamedTO(String id, String name) {
        asBean = true;
        getCriteria().setProjection(
                Projections.projectionList()
                        .add(Projections.property(id))
                        .add(Projections.property(name))
                );
        getCriteria().setResultTransformer(new AliasToBeanConstructorResultTransformer(IdNameTO.CONSTRUCTOR));

        return self();
    }

    @Override
    public T accounts(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria()
                    .add(AnyCriterion.anyId("account.id", ids));
        }

        return self();
    }

    @Override
    public T account(Long id) {
        if (id != null) {
            getCriteria()
                    .add(Restrictions.eq("account.id", id));
        }

        return self();
    }

    @Override
    public T channel(Long id) {
        if (id != null) {
            getCriteria()
                    .add(Restrictions.eq("this.id", id));
        }

        return self();
    }

    @Override
    public T channels(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("this.id", ids));
        }

        return self();
    }

    @Override
    public T country(String code) {
        if (StringUtil.isPropertyNotEmpty(code)) {
            getCriteria()
                    .add(Restrictions.eq("country.countryCode", code));
        }

        return self();
    }

    @Override
    public T countries(List<String> codes) {
        if (codes != null && !codes.isEmpty()) {
            getCriteria().add(Restrictions.in("country.countryCode", codes));
        }

        return self();
    }

    private static final class DisplayStatusIdConverter implements Converter<DisplayStatus, Long> {
        @Override
        public Long item(DisplayStatus value) {
            return value.getId();
        }
    }

    @Override
    public T displayStatus(DisplayStatus... statuses) {
        if (statuses != null && statuses.length > 0) {
            Collection<Long> ids = CollectionUtils.convert(new DisplayStatusIdConverter(), statuses);
            getCriteria()
                    .add(Restrictions.in("this.displayStatusId", ids));
        }

        return self();
    }

    @Override
    public T visibility(ChannelVisibilityCriteria visibilityCriteria) {
        if (visibilityCriteria != null) {
            getCriteria()
                    .add(Restrictions.in("this.visibility", visibilityCriteria.getVisibilities()));
        }

        return self();
    }

    @Override
    public T notDeleted() {
        getCriteria()
                .add(Restrictions.ne("this.status", Status.DELETED.getLetter()))
                .add(Restrictions.ne("account.status", Status.DELETED.getLetter()));

        return self();
    }

    @Override
    public T matchedIds(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("this.id", ids));
        }

        return self();
    }

    protected ProjectionList createDefaultTOProjections() {
        return Projections.projectionList()
                .add(Projections.id().as("id"))
                .add(Projections.property("name").as("name"))
                .add(Projections.property("description").as("description"))
                .add(Projections.property("flags").as("flags"))
                .add(Projections.property("class").as("type"))
                .add(Projections.property("visibility").as("channelVisibility"))
                .add(Projections.property("status").as("channelStatus"))
                .add(Projections.property("qaStatus").as("qaStatus"))
                .add(Projections.property("displayStatusId").as("displayStatus"))
                .add(Projections.property("account.id").as("accountId"))
                .add(Projections.property("account.name").as("accountName"))
                .add(Projections.property("account.displayStatusId").as("accountDisplayStatus"))
                .add(Projections.property("account.accountManager.id").as("accountManagerId"))
                .add(Projections.property("account.role").as("accountRole"))
                .add(Projections.property("country.countryCode").as("country"))
                .add(Projections.property("account.flags").as("accountFlag"));
    }

    @Override
    public BusinessQuery preExecute() {
        if (asBean) {
            preExecuteBean();
        } else {
            preExecuteTO();
        }
        return super.preExecute();
    }

    protected void preExecuteBean() {

    }

    protected void preExecuteTO() {
        getCriteria().getDataCriteria()
                .setProjection(createTOProjections())
                .setResultTransformer(createTOTransformer());

    }

    protected abstract ResultTransformer createTOTransformer();

    protected abstract ProjectionList createTOProjections();

}
