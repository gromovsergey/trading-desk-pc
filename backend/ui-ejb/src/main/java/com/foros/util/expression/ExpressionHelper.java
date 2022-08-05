package com.foros.util.expression;

import com.phorm.oix.util.expression.CDMLLexer;
import com.phorm.oix.util.expression.CDMLParser;
import com.phorm.oix.util.expression.CDMLParsingError;
import com.phorm.oix.util.expression.HumanReadableCDMLLexer;
import com.phorm.oix.util.expression.HumanReadableCDMLParser;

import com.foros.model.Status;
import com.foros.model.channel.Channel;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.session.channel.ChannelTO;
import com.foros.session.channel.ExpressionChannelFormatter;
import com.foros.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;


public final class ExpressionHelper {
    public static final String ACCOUNT_NAME_DELIMITER = "|";
    private static final String EXTRACT_CHANNELS_REGEXP = "\\s*(\\d+)\\s*";
    private static final Pattern CHANNELS_PATTERN = Pattern.compile(EXTRACT_CHANNELS_REGEXP);

    private ExpressionHelper() {
    }

    public static Collection<Long> extractChannels(String expression) {
        Collection<Long> channels = new HashSet<Long>();
        if (StringUtil.isPropertyNotEmpty(expression)) {
            Matcher matcher = CHANNELS_PATTERN.matcher(expression);
            while (matcher.find()) {
                String id = matcher.group(1).trim();
                channels.add(Long.parseLong(id));
            }
        }
        return channels;
    }

    public static Collection<Long> parseIds(String expression) throws CDMLParsingError, IOException {
        if (expression == null || expression.length() == 0) {
            return new ArrayList<Long>();
        }

        Collection<Long> result;
        InputStream is = null;
        ANTLRInputStream stream;
        CDMLParser parser;

        try {
            is = new ByteArrayInputStream(expression.getBytes("UTF-8"));
            stream = new ANTLRInputStream(is, "UTF-8");

            CDMLLexer lexer = new CDMLLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            parser = new CDMLParser(tokens);

            CDMLParser.prog_return r = parser.prog();

            result = r.value;

            if (lexer.getError() != null) {
                throw lexer.getError();
            }

            if (parser.getError() != null) {
                throw parser.getError();
            }
        } catch (RecognitionException e) {
            throw new IllegalStateException("RecognitionException must be handled..");
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return result;
    }

    public static Collection<String> parseNames(String expression) throws CDMLParsingError, IOException {
        if (expression == null || expression.length() == 0) {
            return new ArrayList<String>();
        }

        Collection<String> result;
        InputStream is = null;
        ANTLRInputStream stream;
        HumanReadableCDMLParser parser;

        try {
            is = new ByteArrayInputStream(expression.getBytes("UTF-8"));
            stream = new ANTLRInputStream(is, "UTF-8");

            HumanReadableCDMLLexer lexer = new HumanReadableCDMLLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            parser = new HumanReadableCDMLParser(tokens);

            HumanReadableCDMLParser.prog_return r = parser.prog();

            result = r.value;

            if (lexer.getError() != null) {
                throw lexer.getError();
            }

            if (parser.getError() != null) {
                throw parser.getError();
            }
        } catch (RecognitionException e) {
            throw new IllegalStateException("RecognitionException must be handled..");
        } finally {
            if (is != null) {
                is.close();
            }
        }
        
        return result;
    }

    public static String replaceHumanOperations(String expression) {
        String result = expression;

        result = result.replace("OR", "|");
        result = result.replace("AND_NOT", "^");
        result = result.replace("AND", "&");
        result = result.replace("or", "|");
        result = result.replace("and_not ", "^");
        result = result.replace("and", "&");

        return result;
    }

    public static String replaceCDMLOperations(String expression) {
        String result = expression.replace(" ", "");

        result = result.replace("^", " AND_NOT ");
        result = result.replace("&", " AND ");
        result = result.replace("|", " OR ");

        return result;
    }

    public static String getEditableHumanName(Channel channel) {
        return "[" + formatChannelName(channel) + "]";
    }

    /**
     * returns Account name if defined
     *
     * @param channelName, formats which are allowed:
     *  1. [account name]ACCOUNT_NAME_DELIMITER[channel name]
     *  2. [channel name]
     * @return name
     */
    public static String parseAccountName(String channelName) {
        String result = null;
        String[] parts = channelName.split("[" + ACCOUNT_NAME_DELIMITER + "]");
        if (parts.length > 1) {
            result = parts[0];
        }
        return StringUtil.trimProperty(result);
    }

    public static String parseChannelName(String channelName) {
        String result = channelName;
        String[] parts = channelName.split("[" + ACCOUNT_NAME_DELIMITER + "]");
        if (parts.length > 1) {
            result = channelName.substring(parts[0].length() + 1);
        }
        return StringUtil.trimProperty(result);
    }

    private static String formatChannelName(Long accountId, String accountName, String channelName, Status status) {
        String result = "";

        // account name
        ApplicationPrincipal principal = SecurityContext.getPrincipal();
        if (principal != null) {
            Long currentAccountId = principal.getAccountId();
            if (!currentAccountId.equals(accountId)) {
                result = accountName + ACCOUNT_NAME_DELIMITER + channelName;
            } else {
                result = channelName;
            }
        }    

        // deleted
        if (status == Status.DELETED) {
            result += " " + StringUtil.getLocalizedString("suffix.deleted");
        } else {
            if (status == Status.INACTIVE) {
                result += " " + StringUtil.getLocalizedString("suffix.inactive");
            }
        }

        return result;
    }

    public static String formatChannelName(Channel channel) {
        return formatChannelName(
                channel.getAccount().getId(),
                channel.getAccount().getName(),
                channel.getName(),
                channel.getStatus());
    }

    public static String formatChannelName(ChannelTO channel) {
        return formatChannelName(
                channel.getAccountId(),
                channel.getAccountName(),
                channel.getName(),
                channel.getStatus());
    }

    public static String formatChannelNameAsLink(Channel channel, String baseUrl) {
        return "<a href='" + baseUrl + "?id=" + channel.getId() + "'>" + channel.getName() +  "</a>";
    }

    public static ExpressionChannelFormatter newChannelNameFormatter() {
        return new ExpressionChannelFormatter() {
            public String format(Channel channel) {
                return ExpressionHelper.formatChannelName(channel);
            }
        };
    }

    public static ExpressionChannelFormatter newEditableChannelNameFormatter() {
        return new ExpressionChannelFormatter() {
            public String format(Channel channel) {
                return ExpressionHelper.getEditableHumanName(channel);
            }
        };
    }
}
