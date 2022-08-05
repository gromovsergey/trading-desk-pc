package com.foros.action.channel;

import com.foros.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

public class ChannelMatchParser {
    private static Logger logger = Logger.getLogger(ChannelMatchAction.class.getName());
    
    private static final String matchKey = "trigger_channels";
    private static final String historyKey = "history_channels";
    private Set<Long> matchedChannels = new HashSet<Long>();
    private Set<Long> history = new HashSet<Long>();

    private Set<Long> parseId(String input, String key, boolean endsWithLetter) {
        Set<Long> idSet = new HashSet<Long>();
        StringTokenizer tokenizer = new StringTokenizer(input.substring((key + " = ").length(), input.length() - 1), ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.length() > 0) {
                if (endsWithLetter) {
                    token = token.substring(0, token.length() - 1);
                }
                idSet.add(Long.valueOf(token));
            }
        }

        return idSet;
    }

    private void parse(InputStream in) throws ChannelMatchException {
        try {
            logger.log(Level.INFO, "Before Starting to parse :");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                if (inputLine.startsWith(matchKey)) {
                    matchedChannels.addAll(parseId(inputLine, matchKey, true));
                } else if (inputLine.startsWith(historyKey)) {
                    history.addAll(parseId(inputLine, historyKey, false));
                    break;
                }
            }
            logger.log(Level.INFO, "Received Matched Channel ID List :" + StringUtils.join(matchedChannels, ' '));
            logger.log(Level.INFO, "Received History Channel ID List :" + StringUtils.join(history, ' '));
        } catch (IOException ioe) {
            throw new ChannelMatchException(StringUtil.getLocalizedString("error.unableToReadOutput"), ioe);
        } catch (RuntimeException re) {
            throw new ChannelMatchException(StringUtil.getLocalizedString("error.runtimeError"), re);
        }

    }

    public Set<Long> getMatchedChannels() {
        return matchedChannels;
    }

    public Set<Long> getHistory() {
        return history;
    }


    public void processChannelMatch(String requestStr, Map<String, String> headers) throws ChannelMatchException {
        try {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, getRequestInfo(requestStr, headers));
            }

            URLConnection connection = new URL(requestStr).openConnection();
            for (Map.Entry<String, String> header: headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            Date timeBefore = new Date();
            InputStream inpuStream = connection.getInputStream();
            Date timeAfter = new Date();
            Long timeElapsedInSecs = (timeAfter.getTime() - timeBefore.getTime()) / 1000;

            logger.log(Level.INFO, "Time taken to respond by Channel Match Server: {0} seconds", new Object[] { timeElapsedInSecs } );

            this.parse(inpuStream);
        } catch (MalformedURLException mfue) {
            throw new ChannelMatchException(StringUtil.getLocalizedString("error.urlIsIncorrect"), mfue);
        } catch (UnknownHostException uhe) {
            throw new ChannelMatchException(StringUtil.getLocalizedString("error.unknownHost"), uhe);
        } catch (IOException io) {
            throw new ChannelMatchException(StringUtil.getLocalizedString("errors.serviceIsNotAvailable", StringUtil.getLocalizedString("channel.channelMatchServer")), io);
        }

    }

    private String getRequestInfo(String requestStr, Map<String, String> headers) {
        StringBuilder msg = new StringBuilder();
        msg.append("URL for channel match service: ");
        msg.append(requestStr);
        msg.append(". Headers: ");

        for (Map.Entry<String, String> header: headers.entrySet()) {
            msg.append(header.getKey());
            msg.append(": ");
            msg.append(header.getValue());
            msg.append(", ");
        }

        return msg.toString();
    }
}
