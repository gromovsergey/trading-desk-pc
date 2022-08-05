package com.foros.session.channel.service;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.DisplayStatus;
import com.foros.model.Timezone;
import com.foros.model.account.Account;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.Channel;
import com.foros.model.security.ActionType;
import com.foros.model.security.User;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.AuthenticationType;
import com.foros.session.CurrentUserService;
import com.foros.session.MailSendingFailedException;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.account.AccountService;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.util.CollectionUtils;
import com.foros.util.MailHelper;
import com.foros.util.bean.Filter;
import com.foros.util.mapper.Converter;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang.StringEscapeUtils;


@Stateless(name = "ChannelMessagingService")
@Interceptors({RestrictionInterceptor.class, PersistenceExceptionInterceptor.class})
public class ChannelMessagingServiceBean implements ChannelMessagingService {
    private static final Logger logger = Logger.getLogger(ChannelMessagingService.class.getName());

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @EJB
    private UserService userService;

    @EJB
    private ConfigService configService;

    @EJB
    private AccountService accountservice;

    @EJB
    private CurrentUserService currentUserService;

    @Override
    @Interceptors({CaptureChangesInterceptor.class})
    @Restrict(restriction = "AdvertisingChannel.contactCMPChannelUser", parameters = "find('Channel', #id)")
    public void sendMessage(Long id, String message, String forosBaseUrl, boolean sendCopy) throws MailSendingFailedException {
        Channel channel = em.find(Channel.class, id);
        Collection<User> users = findAssociatedUsers(channel.getId());

        if (users.size() == 0) {
            throw ConstraintViolationException.newBuilder("cmp.channel.no.users")
                    .withPath("noUsers")
                    .build();
        }

        if (isMaxLimitReached(channel)) {
            throw ConstraintViolationException.newBuilder("cmp.channel.users.exceeds.limit")
                    .withPath("exceedLimit")
                    .build();
        }

        String userChannelLink = getChannelLink("advertiser", channel, forosBaseUrl);
        String myChannelLink = getChannelLink(currentUserService.isInternal() ? "admin" : "cmp", channel, forosBaseUrl);

        message = formatAsHtml(message);
        sendUsersMail(message, forosBaseUrl, userChannelLink, users);

        if (sendCopy) {
            sendCopy(message, myChannelLink, forosBaseUrl);
        }

        channel.setMessageSent(channel.getMessageSent() + 1);
        Account account = channel.getAccount();
        account.setMessageSent(account.getMessageSent() + 1);

        em.merge(channel);

        auditService.audit(channel, ActionType.UPDATE);
    }

    private boolean sendCopy(String message, String channelLink, String forosBaseUrl) {
        User currentUser = userService.find(currentUserService.getUserId());
        try {
            MailHelper.sendChannelMessageAlertMail(currentUser, currentUser, message, channelLink, forosBaseUrl);
        } catch (MailSendingFailedException me) {
            return false;
        }
        return true;
    }

    @Override
    public Collection<User> findAssociatedUsers(Long channelId) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(" Select distinct coalesce (u1 , u2) ")
            .append(" from CampaignCreativeGroup ccg left join ccg.campaign.account.agency.users u1 left join ccg.campaign.account.users u2")
            .append(" where ccg.channel.id =:channelId ")
            .append(" and ccg.displayStatusId in (")
            .append(formatLiveStatuses(CampaignCreativeGroup.getAvailableDisplayStatuses()))
            .append(") and ccg.campaign.displayStatusId in (")
            .append(formatLiveStatuses(Campaign.getAvailableDisplayStatuses()))
            .append(") and ccg.campaign.account.displayStatusId in (")
            .append(formatLiveStatuses(Account.getAvailableDisplayStatuses()))
            .append(") and exists (from User u5 where u5.id = coalesce(u1.id, u2.id) and u5.status = 'A' and u5.authType <> :authType )")
            .append(" and ( ")
                    .append(" exists (from User u6 where u6.id = u2.id and ccg.campaign.account = u6.account) ")
                    .append(" or ")
            .append(" exists (from User u3 where u3.id = u1.id and ( bitand(u3.flags , :flag) <>  0 or exists (from PolicyEntry p where p.userRole = u1.role and p.action = 'edit' and p.type = 'advertising_account'))) ")
                    .append(" or ")
            .append(" exists (from User u4 join u4.advertisers ua where u4.id = coalesce(u1.id, u2.id) and ccg.campaign.account = ua) ")
                    .append(" )");

        Query query = em.createQuery(queryBuilder.toString());
        query.setParameter("channelId", channelId);
        query.setParameter("authType", AuthenticationType.NONE);
        query.setParameter("flag", User.ADV_LEVEL_ACCESS_FLAG);

        return query.getResultList();
    }

    @Override
    public boolean isMaxLimitReached(Channel channel) {
        Config config = configService.detach();
        return (channel.getMessageSent() >= config.get(ConfigParameters.CHANNEL_MESSAGE_SENT_MAX_COUNT) ||
                channel.getAccount().getMessageSent() >= config.get(ConfigParameters.ACCOUNT_CHANNEL_MESSAGE_SENT_MAX_COUNT));
    }

    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public int resetMessageSentCount() {
        int count = 0;
        for (Channel channel : findChannelsForResetting()) {
            try {
                Channel updateChannel = em.find(Channel.class, channel.getId());
                updateChannel.setMessageSent(0);
                updateChannel.getAccount().setMessageSent(0);
            } catch (Exception e) {
                String logMessage = "Resetting message sent count failed with Exception. Channel: " + channel;
                logger.log(Level.WARNING, logMessage, e);
            }
            count++;
        }
        em.flush();
        return count;
    }

    private void sendUsersMail(String message, String forosBaseURL, String channelLink, Collection<User> usersList) throws MailSendingFailedException {
        User currentUser = userService.find(currentUserService.getUserId());
        for (User user : usersList) {
            MailHelper.sendChannelMessageAlertMail(currentUser, user, message, channelLink, forosBaseURL);
        }
    }

    @SuppressWarnings({"unchecked"})
    private Collection<Channel> findChannelsForResetting() {
        Collection<Channel> channelsToReset = new ArrayList<Channel>();

        Collection<Channel> channels = em.createQuery("select c from Channel c where c.messageSent <> 0").getResultList();

        for (Channel channel : channels) {
            if (isNewDay(channel.getAccount().getTimezone())) {
                channelsToReset.add(channel);
            }
        }
        return channelsToReset;
    }

    private boolean isNewDay(Timezone timezone) {
        Calendar accountTimeZoneCalendar = Calendar.getInstance(TimeZone.getTimeZone(timezone.getKey()));
        if (accountTimeZoneCalendar.get(Calendar.HOUR_OF_DAY) == 0 && accountTimeZoneCalendar.get(Calendar.MINUTE) < 30) {
            return true;
        }
        return false;
    }

    private String formatLiveStatuses(Collection<DisplayStatus> displayStatuses) {
        Collection<DisplayStatus> liveStatuses = new ArrayList<DisplayStatus>(displayStatuses);

        CollectionUtils.filter(liveStatuses, new Filter<DisplayStatus>() {
            @Override
            public boolean accept(DisplayStatus element) {
                return DisplayStatus.Major.LIVE.equals(element.getMajor())
                    || DisplayStatus.Major.LIVE_NEED_ATT.equals(element.getMajor());
            }
        });

        return CollectionUtils.join(liveStatuses, ",", new Converter<DisplayStatus, String>(){
            @Override
            public String item(DisplayStatus value) {
                return value.getId().toString();
            }
        });
    }

    private String getChannelLink(String rolePath, Channel channel, String forosBaseURL) {
        String href = forosBaseURL + "/" + rolePath + "/channel/view.action?id=" + channel.getId();
        return MessageFormat.format("<a href=\"{0}\">{1}</a>", href, channel.getName());
    }

    private String formatAsHtml(String messageStr) {
        return StringEscapeUtils.escapeHtml(messageStr)
                .replaceAll("\n", "<br/>")
                .replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
                .replaceAll("\r", "&nbsp");
    }
}
