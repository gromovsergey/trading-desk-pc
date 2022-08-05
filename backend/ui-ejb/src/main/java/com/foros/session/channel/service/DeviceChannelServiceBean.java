package com.foros.session.channel.service;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.EntityTO;
import com.foros.session.LoggingInterceptor;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.channel.ChannelTreeNode;
import com.foros.session.channel.DeviceChannelTO;
import com.foros.session.query.PartialList;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.campaign.CampaignCreativeGroupQueryImpl;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.DisplayStatusService;
import com.foros.util.PersistenceUtils;
import com.foros.util.tree.TreeNode;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "DeviceChannelService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class DeviceChannelServiceBean implements DeviceChannelService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private AuditService auditService;

    @EJB
    private QueryExecutorService queryExecutor;

    @EJB
    private DisplayStatusService displayStatusService;

    @EJB
    private UserService userService;

    private Long browsersChannelId;
    private Long applicationsChannelId;
    private Long nonMobileDevicesChannelId;
    private Long mobileDevicesChannelId;

    @PostConstruct
    public void init() {
        applicationsChannelId = findRootIdByName(DeviceChannel.APPLICATIONS);
        browsersChannelId = findRootIdByName(DeviceChannel.BROWSERS);
        nonMobileDevicesChannelId = findByParentIdAndName(DeviceChannel.NON_MOBILE_DEVICES_CHANNEL_NAME, browsersChannelId);
        mobileDevicesChannelId = findByParentIdAndName(DeviceChannel.MOBILE_DEVICES_CHANNEL_NAME, browsersChannelId);
    }

    private Long findRootIdByName(String name) {
        return (Long) em.createNamedQuery("DeviceChannel.findRootIdByName")
                .setParameter("name", name)
                .getSingleResult();
    }

    private Long findByParentIdAndName(String name, Long parentId) {
        return (Long) em.createNamedQuery("DeviceChannel.findByParentIdAndName")
                .setParameter("name", name)
                .setParameter("id", parentId)
                .getSingleResult();
    }

    @Override
    @Restrict(restriction="DeviceChannel.view")
    public DeviceChannel view(Long id) {
        DeviceChannel channel = findById(id);
        PersistenceUtils.initializeCollection(channel.getChildChannels());
        return channel;
    }

    @Override
    @Restrict(restriction="DeviceChannel.create", parameters = "find('DeviceChannel', #channel.parentChannel.id)")
    @Validate(validation = "DeviceChannel.create", parameters = "#channel")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void create(DeviceChannel channel) {
        channel.retainChanges(
                "name",
                "expression",
                "version");
        channel.setParentChannel(findById(channel.getParentChannel().getId()));
        channel.setStatus(channel.getParentChannel().getStatus());
        channel.setDisplayStatus(channel.getStatus().equals(Status.ACTIVE)? Channel.LIVE: Channel.INACTIVE);
        channel.setQaStatus(ApproveStatus.APPROVED);
        channel.setStatusChangeDate(new Date());
        channel.setVisibility(ChannelVisibility.PUB);
        em.persist(channel);
        auditService.audit(channel, ActionType.CREATE);
    }

    @Override
    @Restrict(restriction="DeviceChannel.update", parameters="find('DeviceChannel', #channel.id)")
    @Validate(validation = "DeviceChannel.update", parameters = "#channel")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void update(DeviceChannel channel) {
        channel.retainChanges(
                "name",
                "expression",
                "version");
        DeviceChannel existing = em.merge(channel);
        auditService.audit(existing, ActionType.UPDATE);
    }

    @Override
    public DeviceChannel findById(Long id) {
        DeviceChannel entity = em.find(DeviceChannel.class, id);
        if (entity == null) {
            throw new EntityNotFoundException("DeviceChannel with id=" + id + " not found");
        }
        return entity;
    }

    @Override
    public DeviceChannel getBrowsersChannel() {
        return findById(browsersChannelId);
    }

    @Override
    public DeviceChannel getApplicationsChannel() {
        return findById(applicationsChannelId);
    }

    @Override
    public DeviceChannel getNonMobileDevicesChannel() {
        return findById(nonMobileDevicesChannelId);
    }

    @Override
    public DeviceChannel getMobileDevicesChannel() {
        return findById(mobileDevicesChannelId);
    }

    @Override
    @Restrict(restriction="DeviceChannel.inactivate", parameters="find('DeviceChannel', #id)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void inactivate(Long id) {
        DeviceChannel channel = findById(id);
        if (searchAssociatedCampaigns(id, 0, 1).size() > 0) {
            throw new BusinessException("Unable to inactivate: this device channel is used in creative group(s)");
        }
        channel.setStatus(Status.INACTIVE);
        auditService.audit(channel, ActionType.UPDATE);
        displayStatusService.update(channel);
    }

    @Override
    @Restrict(restriction="DeviceChannel.activate", parameters="find('DeviceChannel', #id)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void activate(Long id) {
        DeviceChannel channel = findById(id);
        channel.setStatus(Status.ACTIVE);
        auditService.audit(channel, ActionType.UPDATE);
        displayStatusService.update(channel);
    }

    @Override
    @Restrict(restriction="DeviceChannel.delete", parameters="find('DeviceChannel', #id)")
    @Interceptors({CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void delete(Long id) {
        DeviceChannel channel = findById(id);
        if (searchAssociatedCampaigns(id, 0, 1).size() > 0) {
            throw new BusinessException("Unable to delete: this device channel is used in creative group(s)");
        }
        channel.setStatus(Status.DELETED);
        auditService.audit(channel, ActionType.UPDATE);
        displayStatusService.update(channel);
    }

    @Override
    @Restrict(restriction="DeviceChannel.undelete", parameters="find('DeviceChannel', #id)")
    @Interceptors({AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class})
    public void undelete(Long id) {
        DeviceChannel channel = findById(id);
        channel.setStatus(Status.INACTIVE);
        auditService.audit(channel, ActionType.UPDATE);
        displayStatusService.update(channel);
    }

    @Override
    @Restrict(restriction="DeviceChannel.view")
    public List<DeviceChannelTO> getChannelList(Long parentChannelId) {
        List<DeviceChannelTO> result = jdbcTemplate.query("select * from entityqueries.get_recursive_channel_list(?, ?, 'V', 'D') ",
            new Object[] { parentChannelId, userService.getMyUser().isDeletedObjectsVisible() },
            new int[] { Types.BIGINT, Types.BOOLEAN },
            new RowMapper<DeviceChannelTO>() {
                @Override
                public DeviceChannelTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    long channelId = rs.getLong(1);
                    String channelName = rs.getString(2);
                    char status = rs.getString(7).charAt(0);
                    char qaStatus = rs.getString(8).charAt(0);
                    long displayStatusId = rs.getLong(9);
                    int level = rs.getInt(12);
                    return new DeviceChannelTO(channelId, channelName, status, qaStatus, displayStatusId, level);
                }
            });
        return result;
    }

    @Override
    public List<EntityTO> getChannelAncestorsChain(Long channelId, boolean keepLastChain) {
        List<EntityTO> result = jdbcTemplate.query("select * from entityqueries.get_recursive_channel_list(?, ?, 'V', 'A') ",
            new Object[] { channelId, userService.getMyUser().isDeletedObjectsVisible() },
            new int[] { Types.BIGINT, Types.BOOLEAN },
            new RowMapper<EntityTO>() {
                @Override
                public EntityTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    long channelId = rs.getLong(1);
                    String channelName = rs.getString(2);
                    char status = rs.getString(7).charAt(0);
                    return new EntityTO(channelId, channelName, status, CategoryChannel.getResoureKey(Long.toString(channelId)));
                }
            });
        if (!keepLastChain && !result.isEmpty()) {
            result.remove(result.size() - 1);
        }

        return result;
    }

    @Override
    public TreeNode<EntityTO> getBrowsersTreeRoot() {
        return getChannelsTreeRoot(browsersChannelId, DeviceChannel.BROWSERS);
    }

    @Override
    public TreeNode<EntityTO> getApplicationsTreeRoot() {
        return getChannelsTreeRoot(applicationsChannelId, DeviceChannel.APPLICATIONS);
    }

    private TreeNode<EntityTO> getChannelsTreeRoot(Long rootChannelId, String rootChannelName) {
        ChannelTreeNode root = new ChannelTreeNode(new EntityTO(rootChannelId, rootChannelName, Status.ACTIVE), null, 0);
        final Map<Long, ChannelTreeNode> treeElements = new HashMap<Long, ChannelTreeNode>();
        treeElements.put(rootChannelId, root);
        jdbcTemplate.query("select * from entityqueries.get_recursive_channel_list(?, ?, 'V', 'D') ",
            new Object[] { rootChannelId, userService.getMyUser().isDeletedObjectsVisible() },
            new int[] { Types.BIGINT, Types.BOOLEAN },
            new RowMapper<ChannelTreeNode>() {
                @Override
                public ChannelTreeNode mapRow(ResultSet rs, int rowNum) throws SQLException {
                    long channelId = rs.getLong(1);
                    String channelName = rs.getString(2);
                    char status = rs.getString(7).charAt(0);
                    long parentChannelId = rs.getLong(11);
                    int level = rs.getInt(12);

                    ChannelTreeNode parentNode = treeElements.get(parentChannelId);
                    ChannelTreeNode node = new ChannelTreeNode(new EntityTO(channelId, channelName,
                        status, ""), parentNode, level);

                    parentNode.getChildren().add(node);

                    treeElements.put(channelId, node);

                    return node;
                }
            });


        return root;
    }

    @Override
    public PartialList<CampaignCreativeGroup> searchAssociatedCampaigns(Long id, int from, int count) {
        CampaignCreativeGroupQueryImpl query = new CampaignCreativeGroupQueryImpl();
        query.deviceChannel(id);
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            query.nonDeleted();
        }
        return query.executor(queryExecutor).partialList(from, count);
    }

    @Override
    public Set<DeviceChannel> getNormalizedDeviceChannelsCollection(Set<Long> deviceChannelIds, Set<Long> allowedChannelIds) {
        DeviceChannelNodesHelper deviceChannelNodesHelper = new DeviceChannelNodesHelper(deviceChannelIds, allowedChannelIds);
        Set<DeviceChannel> result = new HashSet<>(deviceChannelIds.size());
        setMostTopParentChannels(deviceChannelNodesHelper, allowedChannelIds, getBrowsersTreeRoot(), result);
        setMostTopParentChannels(deviceChannelNodesHelper, allowedChannelIds, getApplicationsTreeRoot(), result);
        return result;
    }

    private void setMostTopParentChannels(DeviceChannelNodesHelper deviceChannelNodesHelper, Set<Long> allowedChannelIds,
                                          TreeNode<EntityTO> deviceNode, Set<DeviceChannel> result) {
        if (deviceChannelNodesHelper.isNodeEffectivelySelected(deviceNode)) {
            Long channelId = deviceNode.getElement().getId();
            if (Status.ACTIVE.equals(deviceNode.getElement().getStatus()) && allowedChannelIds.contains(channelId)) {
                result.add(findById(channelId));
                return;
            }
        }
        for (TreeNode<EntityTO> child: deviceNode.getChildren()) {
            setMostTopParentChannels(deviceChannelNodesHelper, allowedChannelIds, child, result);
        }
    }

    private class DeviceChannelNodesHelper {
        private Set<Long> effectivelySelectedIds;

        DeviceChannelNodesHelper(Set<Long> selectedChannelIds, Set<Long> allowedChannelIds) {
            this.effectivelySelectedIds = findTerminalAndAllowedNodes(selectedChannelIds, allowedChannelIds);
        }

        private boolean isNodeEffectivelySelected(TreeNode<EntityTO> deviceNode) {
            if (effectivelySelectedIds.contains(deviceNode.getElement().getId())) {
                return true;
            }
            if (deviceNode.getChildren().isEmpty()) {
                return isSpecialCaseOfSelection(deviceNode);
            }

            boolean allChildrenSelected = true;
            for (TreeNode<EntityTO> child: deviceNode.getChildren()) {
                if (!isNodeEffectivelySelected(child)) {
                    allChildrenSelected = false;
                    break;
                }
            }
            if (allChildrenSelected) {
                effectivelySelectedIds.add(deviceNode.getElement().getId());
                return true;
            }
            return isSpecialCaseOfSelection(deviceNode);
        }

        private boolean isSpecialCaseOfSelection(TreeNode<EntityTO> deviceNode) {
            if (Status.ACTIVE.equals(deviceNode.getElement().getStatus())) {
                return false;
            }
            // Non-Active are always effectively selected
            effectivelySelectedIds.add(deviceNode.getElement().getId());
            return true;
        }

        private Set<Long> findTerminalAndAllowedNodes(Set<Long> selectedChannelIds, Set<Long> allowedChannelIds) {
            Set<Long> result = new HashSet<>(selectedChannelIds);
            result.retainAll(allowedChannelIds);
            unselectNonTerminal(getBrowsersTreeRoot(), null, result);
            unselectNonTerminal(getApplicationsTreeRoot(), null, result);
            return result;
        }

        private void unselectNonTerminal(TreeNode<EntityTO> deviceNode, TreeNode<EntityTO> selectedParentNode, Set<Long> selectedChannelIds) {
            TreeNode<EntityTO> newSelectedParentNode;
            if (selectedChannelIds.contains(deviceNode.getElement().getId())) {
                if (selectedParentNode != null) {
                    selectedChannelIds.remove(selectedParentNode.getElement().getId());
                }
                newSelectedParentNode = deviceNode;
            } else {
                newSelectedParentNode = selectedParentNode;
            }

            if (deviceNode.getChildren().isEmpty()) {
                return;
            }

            for (TreeNode<EntityTO> child : deviceNode.getChildren()) {
                unselectNonTerminal(child, newSelectedParentNode, selectedChannelIds);
            }
        }
    }
}
