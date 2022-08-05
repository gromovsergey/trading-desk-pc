package app.programmatic.ui.channel.tool;

import com.foros.rs.client.model.advertising.channel.ExpressionChannel;
import com.foros.rs.client.model.entity.EntityLink;
import app.programmatic.ui.channel.dao.model.*;
import app.programmatic.ui.channel.service.ChannelServiceImpl;
import app.programmatic.ui.channel.view.ExpressionChannelView;
import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChannelBuilder {
    private static final Logger logger = Logger.getLogger(ChannelBuilder.class.getName());

    public static Channel build(ResultSet rs) throws SQLException {
        Channel channel = new Channel();

        setCommonPart(rs, channel);
        channel.setName(rs.getString("channel_name"));
        channel.setVisibility(ChannelVisibility.valueOf(rs.getString("channel_visibility")));
        channel.setUniqueUsers(rs.getLong("user_count"));

        return channel;
    }

    public static Channel buildLocalized(ResultSet rs) throws SQLException {
        Channel channel = build(rs);
        channel.setLocalizedName(rs.getString("localized_name"));
        return channel;
    }

    public static ChannelStat buildStatForLineItem(ResultSet rs, ChangeStatusChecker statusChecker) throws SQLException {
        ChannelStat channelStat = new ChannelStat();

        setCommonPart(rs, channelStat);
        channelStat.setName(rs.getString("localized_name"));
        channelStat.setChannelName(rs.getString("channel_name"));
        channelStat.setUniqueUsers(rs.getLong("average_weekly_users"));
        channelStat.setImps(rs.getLong("imps"));
        channelStat.setClicks(rs.getLong("clicks"));
        channelStat.setCtr(rs.getBigDecimal("ctr").setScale(2, BigDecimal.ROUND_HALF_UP));

        statusChecker.fillCanChange(channelStat);

        return channelStat;
    }

    public static ChannelStat buildStatForFlight(ResultSet rs) throws SQLException {
        ChannelStat channelStat = new ChannelStat();

        setCommonPart(rs, channelStat);
        channelStat.setName(rs.getString("localized_name"));
        channelStat.setChannelName(rs.getString("channel_name"));
        channelStat.setUniqueUsers(rs.getLong("average_weekly_users"));

        return channelStat;
    }

    private static void setCommonPart(ResultSet rs, Channel channel) throws SQLException {
        channel.setId(rs.getLong("channel_id"));
        channel.setAccountId(rs.getLong("account_id"));
        channel.setAccountName(rs.getString("account_name"));
        channel.setDisplayStatus(ChannelDisplayStatus.valueOf(rs.getInt("channel_display_status_id")).getMajorStatus());
        channel.setType(ChannelType.valueOf(rs.getString("channel_type")));
    }

    public interface ChangeStatusChecker {
        void fillCanChange(ChannelStat channelStat);
    }

    public static ExpressionChannel buildExpressionChannel(ExpressionChannelView channelView) {
        ExpressionChannel channel = new ExpressionChannel();

        channel.setId(channelView.getId());
        channel.setName(channelView.getName());
        channel.setCountry(channelView.getCountry());
        channel.setAccount(new EntityLink());
        channel.getAccount().setId(channelView.getAccountId());
        channel.setVisibility(channelView.getVisibility());
        channel.setUpdated(XmlDateTimeConverter.convertEpoch(channelView.getVersion(), "GMT"));

        channel.setExpression(generateExpression(channelView));

        return channel;
    }

    private static String generateExpression(ExpressionChannelView channelView) {
        String includedChannelsStr = generateExpressionPart(channelView.getIncludedChannels());
        String excludedChannelsStr = generateExpressionPart(channelView.getExcludedChannels());

        if (excludedChannelsStr.isEmpty()) {
            return inBrackets(includedChannelsStr);
        }

        if (includedChannelsStr.isEmpty()) {
            includedChannelsStr = inBrackets(ChannelServiceImpl.getRuSystemTrueChannelId().toString());
        }

        return inBrackets(includedChannelsStr) + "^" + inBrackets(excludedChannelsStr);
    }

    private static String inBrackets(String expr) {
        if (expr == null ||
                expr.trim().isEmpty() ||
                isInBrackets(expr)) {
            return expr;
        }
        return "(" + expr + ")";
    }

    private static String generateExpressionPart(List<List<Channel>> audience) {
        return audience.stream()
                .map(channels -> inBrackets(channels.stream()
                        .map(channel -> channel.getId().toString())
                        .collect(Collectors.joining("|")))
                )
                .filter( audStr -> !audStr.isEmpty() )
                .collect(Collectors.joining("&"));
    }

    public static ExpressionChannelView buildExpressionChannelView(ExpressionChannel channel) {
        ExpressionChannelView channelView = new ExpressionChannelView();

        channelView.setId(channel.getId());
        channelView.setName(channel.getName());
        channelView.setCountry(channel.getCountry());
        channelView.setAccountId(channel.getAccount().getId());
        channelView.setVisibility(channel.getVisibility());
        channelView.setVersion(XmlDateTimeConverter.convertToEpochTime(channel.getUpdated()));

        parseExpression(channelView, channel.getExpression());

        return channelView;
    }

    // don't set anything if the expression is incorrect
    private static void parseExpression(ExpressionChannelView target, String expression) {
        List<List<Channel>> includedChannels = Collections.emptyList();
        List<List<Channel>> excludedChannels = Collections.emptyList();

        try {
            Matcher matcher = Pattern.compile("^(.*)\\^(.*)$").matcher(expression);
            if (matcher.matches()) {
                String includedChannelsStr = matcher.group(1);
                String excludedChannelsStr = matcher.group(2);

                includedChannelsStr = removeBrackets(includedChannelsStr);
                excludedChannelsStr = removeBrackets(excludedChannelsStr);

                if (!includedChannelsStr.equals(ChannelServiceImpl.getRuSystemTrueChannelId().toString())) {
                    includedChannels = parseExpressionPart(includedChannelsStr);
                }
                excludedChannels = parseExpressionPart(excludedChannelsStr);
            } else {
                includedChannels = parseExpressionPart(expression);
            }
        } catch (NumberFormatException e) {
            logger.warning("Can't parse expression: '" + expression + "' of channel id=" + target.getId());
            return;
        }

        target.setIncludedChannels(includedChannels);
        target.setExcludedChannels(excludedChannels);
    }

    private static String removeBrackets(String str) {
        if (isInBrackets(str)) {
            return removeBrackets(str.substring(1, str.length() - 1));
        }
        return str;
    }

    private static boolean isInBrackets(String value) {
        if (!value.startsWith("(") || !value.endsWith(")")) {
            return false;
        }

        int opens = 1;
        int closes = 0;
        int pos = 0;
        for (char symbol: value.substring(1).toCharArray()) {
            pos++;
            if (symbol == '(') {
                opens++;
            } else if (symbol == ')') {
                closes++;
                if (opens == closes) {
                    break;
                }
            }
        }

        return pos == value.length() - 1;
    }

    private static List<List<Channel>> parseExpressionPart(String expression) {
        List<List<Channel>> list = new ArrayList<>();
        if (expression != null && !expression.isEmpty()) {
            String strippedExpression = removeBrackets(expression);
            for (String auditoryStr : strippedExpression.split("\\&")) {
                List<Channel> auditory = new ArrayList<>();
                list.add(auditory);
                for (String channelId : removeBrackets(auditoryStr).split("\\|")) {
                    Channel channel = new Channel();
                    channel.setId(Long.valueOf(channelId));
                    auditory.add(channel);
                }
            }
        }
        return list;
    }
}
