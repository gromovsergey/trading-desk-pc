package com.foros.session.channel.service;

import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.restriction.RestrictionService;
import com.foros.session.NamedTO;
import com.foros.session.ServiceLocator;
import com.foros.session.account.AccountService;
import com.foros.session.channel.ExpressionChannelFormatter;
import com.foros.session.channel.exceptions.ChannelNotFoundExpressionException;
import com.foros.session.channel.exceptions.ExpressionConversionException;
import com.foros.session.channel.exceptions.UnreachableExpressionException;
import com.foros.util.SQLUtil;
import com.foros.util.StringUtil;
import com.phorm.oix.util.expression.CDMLParsingError;
import com.foros.util.expression.ExpressionHelper;
import com.foros.util.jpa.JpaQueryWrapper;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;


@Stateless(name = "ExpressionService")
public class ExpressionServiceBean implements ExpressionService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AccountService accountService;

    @Override
    public Collection<Channel> findChannelsFromExpression(String expression) {
        Collection<Long> ids = ExpressionHelper.extractChannels(expression);
        if (!ids.isEmpty()) {
            return new JpaQueryWrapper<Channel>(em, "SELECT c FROM Channel c WHERE c.id in :ids")
                    .setPrimitiveArrayParameter("ids", ids)
                    .getResultList();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String convertToHumanReadable(String cdml) throws ExpressionConversionException {
        ExpressionChannelFormatter channelFormatter = ExpressionHelper.newEditableChannelNameFormatter();

        String result = "";

        if (cdml != null && cdml.length() > 0) {
            result = ExpressionHelper.replaceCDMLOperations(cdml);

            Collection<Channel> channels = findChannelsFromExpression(cdml);
            for (Channel channel : channels) {
                String channelName = channelFormatter.format(channel);
                result = result.replace(channel.getId().toString(), channelName);
            }
        }

        return result;
    }

    @Override
    public void convertToHumanReadable(Collection<ExpressionChannel> channels) {
        Map<String, Collection<Long>> expressions = new HashMap<>(channels.size());
        Set<Long> allIds = new HashSet<>(channels.size() * 3);
        for (Channel channel: channels) {
            String expression = ((ExpressionChannel)channel).getExpression();
            Collection<Long> ids = ExpressionHelper.extractChannels(expression);
            expressions.put(expression, ids);
            allIds.addAll(ids);
        }

        Map<Long, Channel> extractedChannels = findChannelsByIds(allIds);

        ExpressionChannelFormatter channelFormatter = ExpressionHelper.newEditableChannelNameFormatter();
        for (Channel channel: channels) {
            String expression = ((ExpressionChannel)channel).getExpression();
            Collection<Long> ids = expressions.get(expression);

            String humanReadableExpression = ExpressionHelper.replaceCDMLOperations(expression);
            for (Long id: ids) {
                Channel extractedChannel = extractedChannels.get(id);
                String channelName = channelFormatter.format(extractedChannel);
                humanReadableExpression = humanReadableExpression.replace(extractedChannel.getId().toString(), channelName);
            }

            ((ExpressionChannel)channel).setExpression(humanReadableExpression);
        }
    }

    @Override
    public String convertToHumanReadableWithLinks(String cdml, String baseUrl, RestrictionService restrictionService) throws ExpressionConversionException {
        String result = "";

        if (cdml != null && cdml.length() > 0) {
            result = ExpressionHelper.replaceCDMLOperations(cdml);

            Collection<Channel> channels = findChannelsFromExpression(cdml);
            for (Channel channel : channels) {
                String html;
                boolean isPermitted = restrictionService.isPermitted("Channel.view", channel);
                if (isPermitted) {
                    html = ExpressionHelper.formatChannelNameAsLink(channel, baseUrl);
                } else {
                    html = channel.getName();
                }
                result = result.replace(channel.getId().toString(), html);
            }
        }

        return "<span class=\"simpleText\">" + result + "</span>";
    }

    @Override
    public String convertFromHumanReadable(String expression, String ownerCountryCode) throws ExpressionConversionException {
        return convertFromHumanReadable(new SingleChannelConverterContextImpl(), expression, ownerCountryCode);
    }

    @Override
    public String convertFromHumanReadable(ConverterContext context, String expression, String ownerCountryCode) throws ExpressionConversionException {
        String cdml = expression;
        for (String channelName : parseNames(expression)) {
            Long channelId = findChannelByAccountAndName(context, cutStatus(channelName), ownerCountryCode);
            cdml = replaceHumanNameById(cdml, channelName, channelId);
        }

        return replaceHumanOperations(cdml);
    }

    @Override
    public ConverterContext newContext() {
        return new ConverterContextImpl();
    }

    private Long findChannelByAccountAndName(ConverterContext context, String pathName, String countryCode) throws ExpressionConversionException {
        String accountName = ExpressionHelper.parseAccountName(pathName);
        String channelName = ExpressionHelper.parseChannelName(pathName);

        if (StringUtil.isPropertyEmpty(accountName)) {
            accountName = accountService.getMyAccount().getName();
        }

        Long channelId = context.findChannelByAccountAndName(accountName, channelName, countryCode);
        if (channelId == null) {
            throw new ChannelNotFoundExpressionException(
                    "Channel " + channelName + " for account, name: " + accountName + " not found",
                    pathName
            );
        }
        return channelId;
    }

    private Map<Long, Channel> findChannelsByIds(Collection<Long> ids) {
        String sql = "SELECT ch.channel_id, ch.name as channel_name, ch.status, a.account_id, a.name as account_name "
                + " FROM channel ch INNER JOIN account a ON ch.account_id = a.account_id "
                + " where " + SQLUtil.formatINClause("ch.channel_id", ids);

        Query query = em.createNativeQuery(sql);

        @SuppressWarnings("unchecked")
        List<Object[]> list = query.getResultList();

        HashMap<Long, Channel> channels = new HashMap<>(list.size());
        for (Object[] row : list) {
            BehavioralChannel channel = new BehavioralChannel();
            channel.setId(((Number) row[0]).longValue());
            channel.setName((String) row[1]);
            channel.setStatus(Status.valueOf((Character) row[2]));
            channel.setAccount(new AdvertiserAccount(((Number) row[3]).longValue(), (String) row[4]));

            channels.put(channel.getId(), channel);
        }

        return channels;
    }


    private Collection<String> parseNames(String expression) throws ExpressionConversionException {
        try {
            return ExpressionHelper.parseNames(expression);
        } catch (CDMLParsingError ex) {
            throw new UnreachableExpressionException(ex.getMessage(), ex.getMessage());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String cutStatus(String channelNameStatus) {
        String channelName = channelNameStatus.replace(" " + StringUtil.getLocalizedString("suffix.deleted"), "");
        return channelName.replace(" " + StringUtil.getLocalizedString("suffix.inactive"), "");
    }

    private String replaceHumanNameById(String expression, String channelNameStatus, Long id) {
        return expression.replace("[" + channelNameStatus + "]", id.toString());
    }

    private String replaceHumanOperations(String expression) {
        return ExpressionHelper.replaceHumanOperations(expression).replace(" ", "").
                replace("\n", "").replace("\r", "").replace("\t", "");
    }

    private static class ConverterContextImpl implements ConverterContext {

        /** @noinspection unchecked*/
        private Map<AccountKey, Map<String, Long>> accountChannels = LazyMap.decorate(new HashMap(), new Transformer() {
            @Override
            public Object transform(Object input) {
                AccountKey key = (AccountKey) input;
                SearchChannelService searchChannelService = ServiceLocator.getInstance().lookup(SearchChannelService.class);
                Collection<NamedTO> tos = searchChannelService.findAdvertisingChannels(key.getAccountName(), key.countryCode);
                HashMap<String, Long> map = new HashMap<>();
                for (NamedTO to : tos) {
                    map.put(to.getName(), to.getId());
                }
                return map;
            }
        });

        @Override
        public Long findChannelByAccountAndName(String accountName, String channelName, String countryCode) {
            Map<String, Long> channels = accountChannels.get(new AccountKey(accountName, countryCode));
            if (channels == null) {
                return null;
            }
            return channels.get(channelName);
         }
    }

    private static class SingleChannelConverterContextImpl implements ConverterContext {

        @Override
        public Long findChannelByAccountAndName(String accountName, String channelName, String countryCode) {
            SearchChannelService searchChannelService = ServiceLocator.getInstance().lookup(SearchChannelService.class);
            NamedTO result = searchChannelService.findAdvertisingChannel(channelName, accountName, countryCode);
            return result == null ? null : result.getId();
        }
    }

    private static class AccountKey {
        private String accountName;
        private String countryCode;

        private AccountKey(String accountName, String countryCode) {
            this.accountName = accountName;
            this.countryCode = countryCode;
        }

        public String getAccountName() {
            return accountName;
        }

        public String getCountryCode() {
            return countryCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AccountKey that = (AccountKey) o;

            if (!accountName.equals(that.accountName)) return false;
            if (!countryCode.equals(that.countryCode)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = accountName.hashCode();
            result = 31 * result + countryCode.hashCode();
            return result;
        }
     }
}
