package com.foros.session.channel.service;

import com.foros.model.channel.Channel;
import com.foros.model.security.User;
import com.foros.session.MailSendingFailedException;

import javax.ejb.Local;
import java.util.Collection;

@Local
public interface ChannelMessagingService {

    void sendMessage(Long id, String message, String forosBaseUrl, boolean sendCopy) throws MailSendingFailedException;

    Collection<User> findAssociatedUsers(Long channelId);

    int resetMessageSentCount();

    boolean isMaxLimitReached(Channel channel);
}
