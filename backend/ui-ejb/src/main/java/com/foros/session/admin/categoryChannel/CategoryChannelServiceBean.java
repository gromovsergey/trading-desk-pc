package com.foros.session.admin.categoryChannel;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AccountRole;
import com.foros.session.BusinessException;
import com.foros.session.EntityTO;
import com.foros.session.LoggingInterceptor;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.channel.ChannelTreeNode;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.DisplayStatusService;
import com.foros.util.EntityUtils;
import com.foros.util.VersionCollisionException;
import com.foros.util.tree.TreeHolder;
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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.springframework.jdbc.core.RowMapper;

@Stateless(name = "CategoryChannelService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class })
public class CategoryChannelServiceBean implements CategoryChannelService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private AuditService auditService;

    @EJB
    private DisplayStatusService displayStatusService;

    @EJB
    private UserService userService;

    public CategoryChannelServiceBean() {
    }

    private void prePersist(CategoryChannel channel) {
        if (channel.getAccount() == null || channel.getAccount().getId() == null) {
            throw new IllegalArgumentException("Account is null");
        }

        Account account = em.getReference(Account.class, channel.getAccount().getId());

        if (!AccountRole.INTERNAL.equals(account.getRole())) {
            throw new BusinessException("Not permitted account.");
        }

        channel.setAccount(account);

        channel.setCountry(null);
    }

    @Override
    @Interceptors({ CaptureChangesInterceptor.class, LoggingInterceptor.class })
    @Restrict(restriction = "CategoryChannel.create", parameters = "#channel.parentChannelId ? find('CategoryChannel', #channel.parentChannelId) : null")
    @Validate(validation = "CategoryChannel.create", parameters = "#channel")
    public Long createChannel(CategoryChannel channel) {
        channel.setStatus(Status.ACTIVE);
        channel.setQaStatus(ApproveStatus.APPROVED);
        channel.setDisplayStatus(Channel.PENDING_FOROS);
        channel.setStatusChangeDate(new Date());
        prePersist(channel);

        auditService.audit(channel, ActionType.CREATE);
        em.persist(channel);
        displayStatusService.update(channel);

        return channel.getId();
    }

    @Override
    @Interceptors({ CaptureChangesInterceptor.class, LoggingInterceptor.class })
    @Restrict(restriction = "CategoryChannel.update", parameters = "find('CategoryChannel', #channel.id)")
    @Validate(validation = "CategoryChannel.update", parameters = "#channel")
    public void updateChannel(CategoryChannel channel) {
        channel.unregisterChange("id", "account", "parentChannelId", "statusChangeDate");

        prePersist(channel);

        CategoryChannel existingChannel = find(channel.getId());
        channel.setQaStatus(ApproveStatus.APPROVED);

        auditService.audit(existingChannel, ActionType.UPDATE);

        EntityUtils.copy(existingChannel, channel);
        existingChannel.setFlags(channel.getFlags());
        displayStatusService.update(channel);
    }

    @Override
    public CategoryChannel find(Long id) {
        CategoryChannel channel = em.find(CategoryChannel.class, id);
        if (channel == null) {
            throw new EntityNotFoundException("Category Channel with id=" + id + " not found");
        }

        return channel;
    }

    @Override
    @Restrict(restriction = "CategoryChannel.view")
    public CategoryChannel view(Long id) {
        return find(id);
    }

    @Override
    @Restrict(restriction = "CategoryChannel.inactivate", parameters = "find('CategoryChannel', #id)")
    @Interceptors({ CaptureChangesInterceptor.class, LoggingInterceptor.class })
    public void inactivate(Long id) {
        CategoryChannel channel = find(id);
        channel.setStatus(Status.INACTIVE);
        auditService.audit(channel, ActionType.UPDATE);
        displayStatusService.update(channel);
    }

    @Override
    @Restrict(restriction = "CategoryChannel.activate", parameters = "find('CategoryChannel', #id)")
    @Interceptors({ CaptureChangesInterceptor.class, LoggingInterceptor.class })
    public void activate(Long id) {
        CategoryChannel channel = find(id);
        channel.setStatus(Status.ACTIVE);
        auditService.audit(channel, ActionType.UPDATE);
        displayStatusService.update(channel);
    }

    @Override
    @Restrict(restriction = "CategoryChannel.delete", parameters = "find('CategoryChannel', #id)")
    @Interceptors({ CaptureChangesInterceptor.class, LoggingInterceptor.class })
    public void delete(Long id) {
        CategoryChannel channel = find(id);
        channel.setStatus(Status.DELETED);
        auditService.audit(channel, ActionType.UPDATE);
        displayStatusService.update(channel);
    }

    @Override
    @Restrict(restriction = "CategoryChannel.undelete", parameters = "find('CategoryChannel', #id)")
    @Interceptors({ AutoFlushInterceptor.class, CaptureChangesInterceptor.class, LoggingInterceptor.class })
    public void undelete(Long id) {
        CategoryChannel channel = find(id);
        channel.setStatus(Status.INACTIVE);
        auditService.audit(channel, ActionType.UPDATE);
        displayStatusService.update(channel);
    }

    @Override
    public List<CategoryChannel> getCategories(Long channelId) {
        StringBuilder buf = new StringBuilder("select cc from Channel c inner join c.categories cc where c.id = :id");
        return em.createQuery(buf.toString()).setParameter("id", channelId).getResultList();
    }

    @Override
    @Restrict(restriction = "CategoryChannel.view")
    public List<CategoryChannelTO> getChannelList(Long parentChannelId) {
        List<CategoryChannelTO> result = jdbcTemplate.query("select * from entityqueries.get_recursive_channel_list(?, ?, 'C', 'D') ",
            new Object[] { parentChannelId, userService.getMyUser().isDeletedObjectsVisible() },
            new int[] { Types.BIGINT, Types.BOOLEAN },
            new RowMapper<CategoryChannelTO>() {
                @Override
                public CategoryChannelTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    long channelId = rs.getLong(1);
                    String channelName = rs.getString(2);
                    long channelAccountId = rs.getLong(3);
                    String accountName = rs.getString(4);
                    int accountRoleId = rs.getInt(5);
                    Long accountManagerId = rs.getLong(6);
                    char status = rs.getString(7).charAt(0);
                    char qaStatus = rs.getString(8).charAt(0);
                    long displayStatusId = rs.getLong(9);
                    long accountStatusId = rs.getLong(10);
                    int level = rs.getInt(11);

                    return new CategoryChannelTO(channelId, channelName, 0L, status, qaStatus,
                        channelAccountId, accountName, accountStatusId, AccountRole.valueOf(accountRoleId), accountManagerId, displayStatusId, level);
                }
            });
        return result;
    }

    @Override
    public List<EntityTO> getChannelAncestorsChain(Long channelId, boolean keepLastChain) {
        return jdbcTemplate.query("select * from entityqueries.get_recursive_channel_list(?, ?, 'C', " + (keepLastChain ? "'A'" : "'AA'") + ") ",
            new Object[] { channelId, true },
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

    }

    @Override
    @Restrict(restriction = "CategoryChannel.view")
    public TreeHolder<EntityTO> getCategoryChannelTree(long channelId) {
        String sql = "select * from entityqueries.get_recursive_channel_list(null, false, 'C', 'D', ?)";

        final ChannelTreeNode root = new ChannelTreeNode(null, null, 0);
        final Map<Long, ChannelTreeNode> treeElements = new HashMap<Long, ChannelTreeNode>();
        jdbcTemplate.query(sql,
            new Object[] { channelId },
            new int[] { Types.BIGINT },
            new RowMapper<ChannelTreeNode>() {
                @Override
                public ChannelTreeNode mapRow(ResultSet rs, int rowNum) throws SQLException {
                    long id = rs.getLong(1);
                    String channelName = rs.getString(2);
                    Long parentChannelId = rs.getLong(11);
                    char channelStatus = rs.getString(7).charAt(0);
                    int level = (int) rs.getLong(12);

                    ChannelTreeNode parentNode = parentChannelId == null ? null : treeElements.get(parentChannelId);
                    ChannelTreeNode node = new ChannelTreeNode(new EntityTO(id, channelName,
                        channelStatus, CategoryChannel.getResoureKey(Long.toString(id))), parentNode, level);

                    if (parentNode == null) {
                        root.getChildren().add(node);
                    } else {
                        parentNode.getChildren().add(node);
                    }

                    treeElements.put(id, node);

                    return node;
                }
            });


        return new TreeHolder<>(root);
    }

    @Override
    @Restrict(restriction = "Channel.editCategories", parameters = "find('Channel', #channel.id)")
    @Interceptors({ CaptureChangesInterceptor.class, LoggingInterceptor.class })
    public void updateChannelCategories(Channel channel) {
        doUpdateChannelCategories(channel);
    }

    private void doUpdateChannelCategories(Channel channel) {
        Channel existingChannel = em.find(Channel.class, channel.getId());
        if (!existingChannel.getVersion().equals(channel.getVersion())) {
            throw new VersionCollisionException();
        }

        Set<CategoryChannel> newCategories = new HashSet<>(channel.getCategories().size());

        auditService.audit(existingChannel, ActionType.UPDATE);

        for (CategoryChannel c : channel.getCategories()) {
            CategoryChannel category = em.find(CategoryChannel.class, c.getId());

            if (category.getStatus() == Status.DELETED && !existingChannel.getCategories().contains(category)) {
                throw new SecurityException("Cannot add a deleted category channel");
            }

            newCategories.add(category);
        }

        existingChannel.setCategories(newCategories);
    }

    @Override
    @Restrict(restriction = "Channel.update", parameters = "find('Channel', #dcList.id)")
    @Interceptors({ CaptureChangesInterceptor.class, LoggingInterceptor.class })
    public void updateDiscoverListCategories(DiscoverChannelList dcList) {
        doUpdateChannelCategories(dcList);
        DiscoverChannelList existingList = em.find(DiscoverChannelList.class, dcList.getId());

        for (DiscoverChannel dc : existingList.getChildChannels()) {
            dc.setCategories(new HashSet<CategoryChannel>(dcList.getCategories().size()));
            dc.getCategories().addAll(dcList.getCategories());
            doUpdateChannelCategories(dc);
        }
    }

}
