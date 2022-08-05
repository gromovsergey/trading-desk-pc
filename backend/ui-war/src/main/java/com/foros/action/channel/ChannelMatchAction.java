package com.foros.action.channel;

import static com.foros.config.ConfigParameters.CHANNEL_MATCH_SERVER_URL;
import com.foros.action.BaseActionSupport;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.config.ConfigService;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.channel.TriggersChannel;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.StringUtil;
import com.foros.util.UrlUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.ejb.EJB;

public class ChannelMatchAction extends BaseActionSupport implements BreadcrumbsSupport {
    private static final Logger logger = Logger.getLogger(ChannelMatchAction.class.getName());
    private static final Pattern REFERER_KWW_PATTERN = Pattern.compile("\\S+\\s+\\d+(\\s*,\\s*\\S+\\s+\\d+)*");

    @EJB
    private ConfigService configService;

    @EJB
    private SearchChannelService searchChannelService;

    private String url;
    private String keywords;
    private String uid;
    private List<TriggersChannel> matchedChannels;
    private List<TriggersChannel> historyChannels;

    @ReadOnly
    @Restrict(restriction = "ChannelMatchTest.run")
    public String main() {
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "ChannelMatchTest.run")
    public String run() throws Exception {
        String channelMatchServerUrl = configService.get(CHANNEL_MATCH_SERVER_URL);
        String requestStr = !StringUtil.isPropertyNotEmpty(getUid()) ? channelMatchServerUrl :
            UrlUtil.replaceUidParamValue(channelMatchServerUrl, encodeParameter(getUid()));

        ChannelMatchParser channelMatchParser = new ChannelMatchParser();
        try {
            channelMatchParser.processChannelMatch(requestStr, generateHeaders());

            matchedChannels = searchChannelService.findMatchedChannelsByIds(channelMatchParser.getMatchedChannels());
            historyChannels = searchChannelService.findMatchedChannelsByIds(channelMatchParser.getHistory());
        } catch (ChannelMatchException e) {
            logger.log(Level.SEVERE, "Error occurred in processing Channel Match Server", e);
            addFieldError("serviceError", e.getMessage());
        }
        return SUCCESS;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<TriggersChannel> getMatchedChannels() {
        return matchedChannels;
    }

    public List<TriggersChannel> getHistoryChannels() {
        return historyChannels;
    }

    private String encodeParameter(String param) throws UnsupportedEncodingException {
        return URLEncoder.encode(param, "UTF-8");
    }

    private Map<String, String> generateHeaders() throws Exception {
        Map<String, String> headers = new HashMap<String, String>();

        if (StringUtil.isPropertyNotEmpty(getUrl())) {
            headers.put("referer", getUrl());
        }

        if (StringUtil.isPropertyNotEmpty(getKeywords())) {
            String headerName = REFERER_KWW_PATTERN.matcher(getKeywords()).matches() ? "referer-kww" : "referer-kw";
            headers.put(headerName, getKeywords());
        }

        return headers;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new ChannelsBreadcrumbsElement()).add(new ChannelMatchBreadcrumbsElement());
    }
}
