package com.foros.session.channel.service;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.channel.DiscoverChannelsAlreadyExistException;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.LoggingInterceptor;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.StatusAction;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.channel.TriggerService;
import com.foros.session.db.DBConstraint;
import com.foros.session.workflow.WorkflowService;
import com.foros.util.CollectionMerger;
import com.foros.util.CollectionUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.StringUtil;
import com.foros.util.bean.Filter;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.FlushMode;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "DiscoverChannelListService")
@Interceptors( {RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class DiscoverChannelListServiceBean
        extends AbstractChannelServiceBean<DiscoverChannelList> implements BehavioralParametersFinder<DiscoverChannelList>, DiscoverChannelListService {

    @EJB
    private RestrictionService restrictionService;

    @EJB
    private ValidationService validationService;

    @EJB
    private WorkflowService workflowService;

    @EJB
    private DiscoverChannelService discoverChannelService;

    @EJB
    private HibernateWorkExecutorService executorService;

    @EJB
    private TriggerService triggerService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @Override
    @Restrict(restriction = "DiscoverChannel.create", parameters = "find('Account', #dcList.account.id)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public Long create(DiscoverChannelList dcList) {
        return create(dcList, Collections.<DiscoverChannel>emptyList());
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.create", parameters = "find('InternalAccount', #dcList.account.id)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public Long create(DiscoverChannelList dcList, Collection<DiscoverChannel> channelsToLink) {
        DiscoverChannelUtils.createChildChannelsFromKeywordList(dcList);
        prepareToLink(dcList, channelsToLink);
        validationService.validate("DiscoverChannelList.create", dcList, channelsToLink).throwIfHasViolations();

        prePersistCreate(dcList);
        em.persist(dcList);

        // persist children
        for (DiscoverChannel child : dcList.getChildChannels()) {
            // persist new discover channels
            if (child.getId() == null) {
                createChild(child, dcList);
            }
        }

        linkInternal(dcList, channelsToLink);

        flush(dcList, channelsToLink, true);

        postUpdate(dcList, ActionType.CREATE);
        return dcList.getId();
    }

    private void flush(DiscoverChannelList dcList, Collection<DiscoverChannel> channelsToLink, boolean cutDuplicatedChannelsFromList) {
        try {
            em.flush();
        } catch (PersistenceException e) {
            // keep previously duplicated
            PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.MANUAL);
            if (DBConstraint.CHANNEL_NAME.match(e) && !dcList.getChildChannels().isEmpty()) {
                validationService.validateInNewTransaction("DiscoverChannelList.nameConstraintViolation", dcList).throwIfHasViolations();
                checkDuplicates(dcList, channelsToLink, cutDuplicatedChannelsFromList);
            }

            throw e;
        }
    }

    private void checkDuplicates(DiscoverChannelList dcList, Collection<DiscoverChannel> previouslyDuplicated, boolean cutDuplicatedChannelsFromList) {
        List<DiscoverChannel> duplicatedChannels = findDiscoverChannelsByNameNotLinkedToCurrentList(dcList);
        if (duplicatedChannels.isEmpty()) {
            return;
        }

        duplicatedChannels.addAll(previouslyDuplicated);

        String updatedKeywordText = cutDuplicatedChannelsFromList ?
                                    cutDuplicatedChannelsFromList(dcList, duplicatedChannels) :
                                    dcList.getKeywordList();

        throw new DiscoverChannelsAlreadyExistException(duplicatedChannels, updatedKeywordText);
    }

    private String cutDuplicatedChannelsFromList(DiscoverChannelList dcList, List<DiscoverChannel> duplicatedChannels) {
        Set<DiscoverChannel> childChannels = new LinkedHashSet<DiscoverChannel>(dcList.getChildChannels());
        for (DiscoverChannel duplicated : duplicatedChannels) {
            Iterator<DiscoverChannel> it = childChannels.iterator();
            while (it.hasNext()) {
                DiscoverChannel next = it.next();
                if (next.getName().equals(duplicated.getName())) {
                    it.remove();
                }
            }
        }
        dcList.setChildChannels(childChannels);
        dcList.unregisterChange("keywordList");
        return dcList.getKeywordList();
    }

    private List<DiscoverChannel> findDiscoverChannelsByNameNotLinkedToCurrentList(final DiscoverChannelList dcList) {
        final Set<DiscoverChannel> linkedChannels = dcList.getChildChannels();
        final List<String> names = new ArrayList<String>(linkedChannels.size());
        for (DiscoverChannel childChannel : linkedChannels) {
            if (childChannel.getName() != null) {
                names.add(childChannel.getName().trim());
            }
        }

        List<DiscoverChannel> duplicatedChannels = jdbcTemplate.query(new PreparedStatementCreatorFactory("select * from entityqueries.find_discover_channels_by_name_not_linked_to_list(?, ?, ?, ?)",
            new int[] { Types.BIGINT, Types.VARCHAR, Types.INTEGER, Types.ARRAY }).
            newPreparedStatementCreator(
            Arrays.asList(dcList.getId(), dcList.getCountry().getCountryCode(), dcList.getAccount().getId(), jdbcTemplate.createArray("varchar", names)))
            , new RowMapper<DiscoverChannel>() {

                @Override
                public DiscoverChannel mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DiscoverChannel current = new DiscoverChannel();

                    current.setId(rs.getLong(1));
                    current.setName(rs.getString(2));
                    current.setVersion(rs.getTimestamp(3));
                    current.setStatus(Status.valueOf(rs.getString(4).charAt(0)));
                    current.setDisplayStatusId(rs.getLong(5));

                    Country currentCountry = new Country();
                    currentCountry.setCountryId(dcList.getCountry().getCountryId());
                    currentCountry.setCountryCode(dcList.getCountry().getCountryCode());
                    current.setCountry(currentCountry);

                    Long currentListId = rs.getLong(6);
                    if (!currentListId.equals(0L)) {
                        DiscoverChannelList currentList = new DiscoverChannelList();
                        currentList.setId(currentListId);
                        currentList.setName(rs.getString(7));
                        currentList.setDisplayStatusId(rs.getLong(8));
                        current.setChannelList(currentList);
                    }
                    return current;
                }
            });
        return duplicatedChannels;
    }

    private void prePersistCreate(DiscoverChannelList dcList) {
        channelFieldsPreparer.prepareAccount(dcList);
        ChannelFieldsPreparer.initializeVisibilityAndRate(dcList);
        ChannelFieldsPreparer.initializeStatuses(dcList);
        ChannelFieldsPreparer.initializeQaStatus(dcList);
        ChannelFieldsPreparer.initializeId(dcList);
        channelFieldsPreparer.initializeLanguage(dcList);
        prePersist(dcList);
    }

    private void prePersist(DiscoverChannelList dcList) {
        channelFieldsPreparer.prepareCountry(dcList);
        channelFieldsPreparer.prepareBehavParamsList(dcList);
    }

    @Override
    public Collection<BehavioralParameters> getBehavioralParameters(DiscoverChannelList channel) {
        BehavioralParametersList list = channel.getBehavParamsList();
        if (list == null) {
            return Collections.emptyList();
        }

        return list.getBehavioralParameters();
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.update", parameters = "find('DiscoverChannelList', #dcList.id)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public Long update(DiscoverChannelList dcList) {
        return update(dcList, Collections.<DiscoverChannel>emptyList());
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.update", parameters = "find('DiscoverChannelList', #dcList.id)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public Long update(DiscoverChannelList dcList, Collection<DiscoverChannel> channelsToLink) {
        dcList.retainChanges(
                "name",
                "country",
                "language",
                "behavParamsList",
                "channelNameMacro",
                "keywordTriggerMacro",
                "discoverQuery",
                "discoverAnnotation",
                "description",
                "keywordList"
        );

        DiscoverChannelUtils.createChildChannelsFromKeywordList(dcList);
        prepareToLink(dcList, channelsToLink);
        validationService.validate("DiscoverChannelList.update", dcList, channelsToLink).throwIfHasViolations();

        prePersist(dcList);

        // prevent childChannels processing by merge
        Set<DiscoverChannel> copy = dcList.getChildChannels();
        dcList.setChildChannels(Collections.<DiscoverChannel>emptySet());
        dcList.unregisterChange("childChannels");

        DiscoverChannelList existingList = em.merge(dcList);

        // set channels back
        dcList.setChildChannels(copy);

        mergeChildChannels(dcList, existingList, channelsToLink);

        linkInternal(existingList, channelsToLink);

        flush(existingList, channelsToLink, true);

        postUpdate(existingList, ActionType.UPDATE);

        return existingList.getId();
    }

    private void prepareToLink(DiscoverChannelList dcList, Collection<DiscoverChannel> channelsToLink) {
        for (DiscoverChannel channel : channelsToLink) {
            DiscoverChannel existing = em.find(DiscoverChannel.class, channel.getId());
            String trigger = DiscoverChannelUtils.unmacroKeyword(dcList.getChannelNameMacro(), existing.getName());
            channel.setBaseKeyword(DiscoverChannelUtils.getKeywordText(trigger, existing.getPageKeywords(), existing.getSearchKeywords(), existing.getDiscoverQuery()));
            DiscoverChannelUtils.applyMacrosFromBaseKeyword(dcList, channel);
            channel.setName(existing.getName()); // Restore name
            DiscoverChannelUtils.assertParsed(channel);
        }
    }

    private void postUpdate(DiscoverChannelList existingList, ActionType actionType) {
        auditService.audit(existingList, actionType);
        displayStatusService.update(existingList);
    }

    private void mergeChildChannels(final DiscoverChannelList dcList, final DiscoverChannelList existingList, final Collection<DiscoverChannel> doNotProcess) {
        (new CollectionMerger<DiscoverChannel>(existingList.getChildChannels(), dcList.getChildChannels()) {
            @Override
            protected Object getId(DiscoverChannel channel, int index) {
                return StringUtil.trimAndLower(channel.getName());
            }

            @Override
            protected boolean add(DiscoverChannel updated) {
                createChild(updated, existingList);
                return true;
            }

            @Override
            protected void update(DiscoverChannel persistent, DiscoverChannel updated) {
                updated.setId(persistent.getId());
                updateChild(updated, persistent, existingList);
            }

            @Override
            protected boolean delete(DiscoverChannel persistent) {
                for (DiscoverChannel doNotProcessChannel : doNotProcess) {
                    if (doNotProcessChannel.getId().equals(persistent.getId())) {
                        return false;
                    }
                }
                deleteChild(persistent);
                return true;
            }
        }).merge();
        dcList.unregisterChange("childChannels");
    }

    private void createChild(DiscoverChannel updated, DiscoverChannelList dcList) {
        updated.getUrls().setNull();
        copyChannelProperties(updated, dcList);
        updated.setStatus(dcList.getStatus());
        updated.setDisplayStatus(dcList.getDisplayStatus());
        updated.setStatusChangeDate(new Date());
        discoverChannelService.createChild(updated);
    }

    private void deleteChild(DiscoverChannel updated) {
        updated.setChannelList(null);
        statusService.delete(updated);
    }

    private void updateChild(DiscoverChannel updated, DiscoverChannel existing, DiscoverChannelList dcList) {
        preserveUnmodifiedProperties(updated, existing);
        copyChannelProperties(updated, dcList);
        discoverChannelService.updateChild(updated);
    }

    private void preserveUnmodifiedProperties(DiscoverChannel updated, DiscoverChannel existing) {
        updated.getUrls().setNull();
        if (updated.getName() == null) {
            updated.setName(existing.getName());
        }
        if (!updated.isChanged("version")) {
            updated.setVersion(existing.getVersion());
        }
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.update", parameters = "find('DiscoverChannelList', #discoverChannelListId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public Long link(Long discoverChannelListId, Collection<DiscoverChannel> channelsToLink) {
        List<DiscoverChannel> existingChannels = new ArrayList<DiscoverChannel>();
        for (DiscoverChannel channel : channelsToLink) {
            DiscoverChannel existing = em.find(DiscoverChannel.class, channel.getId());
            if (StringUtils.isNotBlank(existing.getBaseKeyword())) {
                channel.setBaseKeyword(existing.getBaseKeyword());
            } else {
                channel.setBaseKeyword(DiscoverChannelUtils.getKeywordText(existing));
            }
            channel.setPageKeywords(existing.getPageKeywords());
            channel.setSearchKeywords(existing.getSearchKeywords());
            existingChannels.add(channel);
        }
        return linkInternal(discoverChannelListId, existingChannels);
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.update", parameters = "find('DiscoverChannelList', #discoverChannelListId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public Long link(Long discoverChannelListId, DiscoverChannel channel, String singleBaseKeyword) {
        List<DiscoverChannel> existingChannels = new ArrayList<DiscoverChannel>();
        DiscoverChannel existing = em.find(DiscoverChannel.class, channel.getId());
        channel.setPageKeywords(existing.getPageKeywords());
        channel.setSearchKeywords(existing.getSearchKeywords());
        channel.setBaseKeyword(singleBaseKeyword);
        existingChannels.add(channel);
        return linkInternal(discoverChannelListId, existingChannels);
    }

    protected Long linkInternal(Long discoverChannelListId, Collection<DiscoverChannel> channelsToLink) {
        DiscoverChannelList existingList = find(discoverChannelListId);
        for (DiscoverChannel channel : channelsToLink) {
            DiscoverChannelUtils.applyMacrosFromBaseKeyword(existingList, channel);
        }
        validationService.validate("DiscoverChannelList.link", existingList, channelsToLink).throwIfHasViolations();
        linkInternal(existingList, channelsToLink);
        flush(existingList, channelsToLink, true);
        postUpdate(existingList, ActionType.UPDATE);
        return existingList.getId();
    }

    private void linkInternal(DiscoverChannelList existingList, Collection<DiscoverChannel> channelsToLink) {
        if (channelsToLink.isEmpty()) {
            return;
        }

        Set<DiscoverChannel> existingListChildChannels = existingList.getChildChannels();
        for (DiscoverChannel channelToLink : channelsToLink) {
            DiscoverChannel existing = em.find(DiscoverChannel.class, channelToLink.getId());
            preserveUnmodifiedProperties(channelToLink, existing);
            if (existing.getChannelList() != null) {
                unlink(existing);
            }
            copyChannelProperties(channelToLink, existingList);
            if (existingList.getStatus() == Status.INACTIVE) {
                channelToLink.setStatus(Status.INACTIVE);
                channelToLink.setStatusChangeDate(new Date());
            }
            discoverChannelService.updateChild(channelToLink);
            existingListChildChannels.add(channelToLink);
        }
        existingList.unregisterChange("keywordList");
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.update", parameters = "find('DiscoverChannel', #childId).channelList != null ? " +
                                                                   "find('DiscoverChannel', #childId).channelList : " +
                                                                   "find('DiscoverChannel', #childId)")
    @Validate(validation = "DiscoverChannelList.unlink", parameters = "find('DiscoverChannel', #childId)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void unlink(Long childId) {
        DiscoverChannel existing = discoverChannelService.view(childId);
        unlink(existing);
        discoverChannelService.updateChild(existing);
    }

    private void unlink(DiscoverChannel existing) {
        final DiscoverChannelList dcList = existing.getChannelList();
        DiscoverChannel toUnlink = findInChildren(dcList, existing);
        dcList.getChildChannels().remove(toUnlink);

        existing.setChannelList(null);
        existing.setBaseKeyword(null);
        postUpdate(dcList, ActionType.UPDATE);
    }

    private void copyChannelProperties(DiscoverChannel toChannel, DiscoverChannelList fromList) {
        toChannel.setAccount(fromList.getAccount());
        toChannel.setBehavParamsList(fromList.getBehavParamsList());
        toChannel.setChannelList(fromList);
        toChannel.setCountry(fromList.getCountry());
        toChannel.setLanguage(fromList.getLanguage());
        toChannel.setCategories(fromList.getCategories());
    }

    @Override
    public Long copy(Long channelId) {
        throw new UnsupportedOperationException("Copying of Discover Channel list is prohibited");
    }

    @Override
    protected Class<DiscoverChannelList> getChannelClass() {
        return DiscoverChannelList.class;
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.view")
    public DiscoverChannelList view(Long channelId) {
        DiscoverChannelList discoverChannelList = find(channelId);
        //fetch linked collections
        PersistenceUtils.initialize(discoverChannelList.getCategories());
        PersistenceUtils.initialize(discoverChannelList.getChildChannels());
        if (discoverChannelList.getBehavParamsList() != null) {
            discoverChannelList.getBehavParamsList().getBehavioralParameters().size();
        }
        for (DiscoverChannel discoverChannel : discoverChannelList.getChildChannels()) {
            discoverChannel.resetTriggers(triggerService.getTriggersByChannelId(discoverChannel));
        }
        return discoverChannelList;
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.delete", parameters = "find('DiscoverChannelList', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void delete(Long channelId) {
        DiscoverChannelList dcList = find(channelId);
        for (DiscoverChannel dc : dcList.getChildChannels()) {
            if (!Status.DELETED.equals(dc.getStatus())) {
                statusService.delete(dc);
            }
        }
        statusService.delete(dcList);
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.delete", parameters = "find('DiscoverChannelList', #discoverChannelListId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void delete(Long discoverChannelListId, Long[] channelIds) {
        for (Long id : channelIds) {
            DiscoverChannel dc = discoverChannelService.view(id);
            if (workflowService.getStatusWorkflow(dc).isActionAvailable(StatusAction.DELETE)) {
                discoverChannelService.delete(id);
            }
        }
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.undelete", parameters = "find('DiscoverChannelList', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void undelete(Long channelId) {
        DiscoverChannelList dcList = find(channelId);
        statusService.undelete(dcList);
        for (DiscoverChannel dc : dcList.getChildChannels()) {
            discoverChannelService.undelete(dc.getId());
        }
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.activate", parameters = "find('DiscoverChannelList', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void activate(Long channelId) {
        statusService.activate(find(channelId));
        DiscoverChannelList dcl = find(channelId);
        for (DiscoverChannel dc : dcl.getChildChannels()) {
            if (restrictionService.isPermitted("DiscoverChannel.activate", dc)) {
                discoverChannelService.activate(dc.getId());
            }
        }
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.update")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void activate(Long discoverChannelListId, Long[] channelIds) {
        for (Long id : channelIds) {
            DiscoverChannel dc = discoverChannelService.view(id);
            if (restrictionService.isPermitted("DiscoverChannel.activate", dc)) {
                discoverChannelService.activate(id);
            }
        }
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.inactivate", parameters = "find('DiscoverChannelList', #channelId)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void inactivate(Long channelId) {
        DiscoverChannelList dcl = find(channelId);
        for (DiscoverChannel dc : dcl.getChildChannels()) {
            if (restrictionService.isPermitted("DiscoverChannel.inactivate", dc)) {
                discoverChannelService.inactivate(dc.getId());
            }
        }
        statusService.inactivate(dcl);
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.update")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void inactivate(Long discoverChannelListId, Long[] channelIds) {
        for (Long id : channelIds) {
            DiscoverChannel dc = discoverChannelService.view(id);
            if (restrictionService.isPermitted("DiscoverChannel.inactivate", dc)) {
                discoverChannelService.inactivate(id);
            }
        }
    }

    @Override
    @Restrict(restriction = "DiscoverChannel.update", parameters = "find('DiscoverChannel', #dc.id)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void updateLinkedChannel(DiscoverChannel dc) {
        DiscoverChannel existing = em.find(DiscoverChannel.class, dc.getId());
        DiscoverChannelList existingList = existing.getChannelList();
        dc.setAccount(existing.getAccount());

        DiscoverChannelUtils.applyMacrosFromBaseKeyword(existingList, dc);

        validationService.validate("DiscoverChannelList.updateLinked", dc).throwIfHasViolations();
        updateChild(dc, existing, existingList);

        DiscoverChannel toUpdate = findInChildren(existingList, existing);
        toUpdate.setBaseKeyword(dc.getBaseKeyword());

        flush(existingList, Collections.<DiscoverChannel>emptyList(), false);

        postUpdate(existingList, ActionType.UPDATE);
    }

    private DiscoverChannel findInChildren(DiscoverChannelList list, final DiscoverChannel channel) {
        DiscoverChannel result = CollectionUtils.find(list.getChildChannels(), new Filter<DiscoverChannel>() {
            @Override
            public boolean accept(DiscoverChannel parsed) {
                return channel.getName().equals(parsed.getName());
            }
        });
        if (result == null) {
            throw new BusinessException("Can't find channel to unlink, name: " + channel.getName());
        }
        return result;
    }
}
