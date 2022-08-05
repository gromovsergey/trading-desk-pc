package com.foros.action.admin.kwmTool;

import com.foros.action.BaseActionSupport;
import com.foros.action.IdNameBean;
import com.foros.framework.ReadOnly;
import com.foros.model.fileman.FileInfo;
import com.foros.service.RemoteServiceException;
import com.foros.session.NamedTO;
import com.foros.session.admin.kwmTool.KWMOutputMode;
import com.foros.session.admin.kwmTool.KWMToolException;
import com.foros.session.admin.kwmTool.KWMToolResult;
import com.foros.session.admin.kwmTool.KWMToolService;
import com.foros.session.channel.PopulatedBehavioralChannelMatchInfo;
import com.foros.session.channel.PopulatedDiscoverChannelMatchInfo;
import com.foros.session.channel.PopulatedMatchInfo;
import com.foros.session.channel.PopulatedNewsItemInfo;
import com.foros.session.channel.PopulatedTriggerInfo;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.fileman.FileManager;
import com.foros.session.fileman.FileManagerException;
import com.foros.session.fileman.FileManagerUIService;
import com.foros.util.CollectionUtils;
import com.foros.util.ExceptionUtil;
import com.foros.util.NameValuePair;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

public class KWMToolAction extends BaseActionSupport implements ServletRequestAware {
    private static final Logger logger = Logger.getLogger(KWMToolAction.class.getName());

    @EJB
    private KWMToolService service;
    @EJB
    private SearchChannelService searchChannelService;
    @EJB
    private FileManagerUIService fileManagerUIService;

    private int source;
    private String maxSize;
    private String sourceUrl;
    private String text;
    private String settingsSchema;
    private boolean adserverDebug;
    private String loops;
    private KWMToolResult result;
    private PopulatedMatchInfo matchResults;
    private String serializedResults;

    private int toolExitCode;
    private String errorMessage;

    private static final int MAX_SIZE_LIMIT = 1048576;
    private static final int MAX_LOOPS_COUNT = 500;

    private static final String KEYWORD_RESULT_STRING = "{0}: ({1}: {2})";
    private static final String CHANNEL_MATCH_RESULT_STRING = "{0}: {1} (id={2})\n{3}: {4}\n{5}: {6}\n";

    private HttpServletRequest request;

    private long parsedMaxSize;

    private int parsedLoops;

    @ReadOnly
    public String main() throws Exception {
        if (!hasErrors()) {
            if (StringUtil.isPropertyEmpty(sourceUrl)) {
                sourceUrl = "http://";
            }

            if (StringUtil.isPropertyEmpty(maxSize)) {
                maxSize = "2048";
            }

            if (StringUtil.isPropertyEmpty(loops)) {
                loops = "1";
            }

            List<FileInfo> kwmToolFiles = fileManagerUIService.getKwmToolFileManager().getFileList("");

            if (!CollectionUtils.isNullOrEmpty(kwmToolFiles)) {
                Iterator<FileInfo> filesIterator = kwmToolFiles.iterator();

                for (FileInfo elem = filesIterator.next(); filesIterator.hasNext(); elem = filesIterator.next()) {
                    if (elem.isDirectory()) {
                        filesIterator.remove();
                    }
                }

                if (!kwmToolFiles.isEmpty()) {
                    Collections.sort(kwmToolFiles, new Comparator<FileInfo>() {
                        @Override
                        public int compare(FileInfo o1, FileInfo o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });

                    settingsSchema = kwmToolFiles.get(0).getName();
                }
            }

            adserverDebug = true;
        }

        return SUCCESS;
    }

    public String results() throws Exception {
        prepare();

        if (hasFieldErrors()) {
            return INPUT;
        }

        try {
            result = service.runKWMTool(source, sourceUrl, (int) parsedMaxSize, parsedLoops, settingsSchema, text, request.getHeader("user-agent"));
        } catch (KWMToolException e) {
            if (e.getToolExitCode() != 0) {
                toolExitCode = e.getToolExitCode();
                errorMessage = e.getMessage();
            } else {
                errorMessage = getText(e.getMessage());
            }
            return INPUT;
        }

        if (adserverDebug) {
            try {
                matchResults = searchChannelService.match(sourceUrl,
                    KWMOutputMode.FULL_TEXT == result.getMode() ? result.getOriginalFullText() : serializeKeywords(result.getKeywords()));
            } catch (RemoteServiceException e) {
                if (ExceptionUtil.getRootMessage(e).contains("ORBMaximumReadByteBufferSize")) {
                    logger.warning(ExceptionUtil.getRootMessage(e));
                    errorMessage = getText("error.responseIsTooLarge");
                } else {
                    logger.warning("Failed to execute channel match service: " + e.getMessage());
                    errorMessage = getText("error.channelMatch");
                }
                return INPUT;
            }
        }

        prepareSerializedResults();

        if (matchResults != null) {
            if (matchResults.isNumberOfChannelsExceeded()) {
                addFieldError("channels", StringUtil.getLocalizedString("error.kwmCountMaxChannel", matchResults.getCountMaxChannels()));
            }

            if (matchResults.isNumberOfDiscoverChannelsExceeded()) {
                addFieldError("discoverChannels", StringUtil.getLocalizedString("error.kwmCountMaxChannel", matchResults.getCountMaxChannels()));
            }
        }

        return SUCCESS;
    }

    private void prepare() throws UnsupportedEncodingException, IOException {
        parsedMaxSize = 2048;
        parsedLoops = 1;
        if (StringUtil.isPropertyEmpty(maxSize)) {
            parsedMaxSize = 0;
        } else if (!NumberUtil.isLong(maxSize)) {
            String msg = getText("errors.integer", new String[]{StringUtil.getLocalizedString("KWMTool.source.maxSize")});
            addFieldError("maxSize", msg);
        } else {
            parsedMaxSize = NumberUtil.parseLong(maxSize);

            if (parsedMaxSize <= 0) {
                String msg = getText("errors.positiveNumber", new String[]{StringUtil.getLocalizedString("KWMTool.source.maxSize")});
                addFieldError("maxSize", msg);
            } else if (parsedMaxSize > MAX_SIZE_LIMIT) {
                addFieldError("maxSize", getText("errors.tooLarge", new String[]{getText("KWMTool.source.maxSize")}));
            }
        }

        if (source == 0) {
            if (StringUtil.isPropertyEmpty(sourceUrl)) {
                String msg = getText("errors.required", new String[]{StringUtil.getLocalizedString("KWMTool.source.url")});
                addFieldError("sourceUrl", msg);
            } else  if (!sourceUrl.startsWith("http://")) {
                String msg = getText("errors.httpurl", new String[]{StringUtil.getLocalizedString("KWMTool.source.url")});
                addFieldError("sourceUrl", msg);
            }
        } else if (StringUtil.isPropertyEmpty(text)) {
            String msg;

            if (source == 1) {
                msg = getText("errors.required", new String[]{StringUtil.getLocalizedString("KWMTool.source.text")});
            } else {
                msg = getText("errors.required", new String[]{StringUtil.getLocalizedString("KWMTool.source.html")});
            }

            addFieldError("text", msg);
        } else {
            parsedMaxSize = text.getBytes("UTF-8").length;
        }

        if (!adserverDebug) {
            if (StringUtil.isPropertyEmpty(loops)) {
                String msg = getText("errors.required", new String[]{StringUtil.getLocalizedString("KWMTool.loops")});
                addFieldError("loops", msg);
            } else if (!NumberUtil.isBigInteger(loops)) {
                String msg = getText("errors.integer", new String[]{StringUtil.getLocalizedString("KWMTool.loops")});
                addFieldError("loops", msg);
            } else {
                BigInteger bigIntegerLoops = NumberUtil.parseBigInteger(loops);

                if (bigIntegerLoops.compareTo(BigInteger.ZERO) < 0) {
                    String msg = getText("errors.positiveNumber", new String[]{StringUtil.getLocalizedString("KWMTool.loops")});
                    addFieldError("loops", msg);
                } else if (bigIntegerLoops.compareTo(BigInteger.valueOf(MAX_LOOPS_COUNT)) > 0) {
                    String msg = getText("errors.notgreater",
                            new String[]{StringUtil.getLocalizedString("KWMTool.loops"), String.valueOf(MAX_LOOPS_COUNT)});
                    addFieldError("loops", msg);
                } else {
                    parsedLoops = bigIntegerLoops.intValue();
                }
            }
        }

        if (StringUtil.isPropertyEmpty(settingsSchema)) {
            String msg = getText("errors.required", new String[]{StringUtil.getLocalizedString("KWMTool.settingsSchema")});
            addFieldError("settingsSchema", msg);
        } else {
            boolean fileExists = false;

            try {
                FileManager kwmToolFileManager = fileManagerUIService.getKwmToolFileManager();

                fileExists = kwmToolFileManager.checkExist("", settingsSchema);

                if (fileExists && kwmToolFileManager.getFileInfo(settingsSchema).isDirectory()) {
                    fileExists = false;
                }
            } catch (FileManagerException ignored) {
            }

            if (!fileExists) {
                String msg = getText("errors.fileexist", new String[]{settingsSchema});
                addFieldError("settingsSchema", msg);
            }
        }
    }

    private void prepareSerializedResults() {
        StringBuilder res = new StringBuilder();

        if (source == 0) {
            res.append(formatTextField("KWMTool.source.url", sourceUrl));
            res.append(formatTextField("KWMTool.source.maxSize", maxSize + " " + StringUtil.getLocalizedString("KWMTool.source.bytes")));
        }

        res.append(formatTextField("KWMTool.settingsSchema", settingsSchema));
        res.append('\n');
        res.append(formatTextField("KWMTool.plainText", '\n' + result.getPlainText()));
        res.append('\n');
        res.append(formatTextField("KWMTool.segmentedText", '\n' + result.getSeparatedWords()));
        res.append('\n');

        switch (result.getMode()) {
            case KWM:
                res.append(StringUtil.getLocalizedString("KWMTool.keywordsMiningResults")).append(":\n");
                for (NameValuePair<String, Long> keyword : result.getKeywords()) {
                res.append(getKeywordResultString(keyword)).append('\n');

            }
            break;
        case FULL_TEXT:
            res.append(StringUtil.getLocalizedString("KWMTool.fullTextResults")).append(":\n");
            for (String text : result.getFullText()) {
                res.append(text).append('\n');
            }
            break;
        }


        if (adserverDebug) {
            if (matchResults != null) {
                res.append('\n');
                res.append(StringUtil.getLocalizedString("KWMTool.advertisingMatchingResults")).append(":\n");


                if (!CollectionUtils.isNullOrEmpty(matchResults.getChannels())) {
                    for (PopulatedBehavioralChannelMatchInfo channelInfo : matchResults.getChannels()) {
                        res.append(getBehavioralChannelMatchResultString(channelInfo)).append('\n');
                    }
                } else {
                    res.append(StringUtil.getLocalizedString("nothing.found.to.display")).append('\n');
                }


                res.append('\n');
                res.append(StringUtil.getLocalizedString("KWMTool.webwiseDiscoverMatchingResults")).append(":\n");

                if (!CollectionUtils.isNullOrEmpty(matchResults.getDiscoverChannels())) {
                    for (PopulatedDiscoverChannelMatchInfo channelInfo : matchResults.getDiscoverChannels()) {
                        res.append(getDiscoverChannelMatchResultString(channelInfo)).append('\n');
                    }
                } else {
                    res.append(StringUtil.getLocalizedString("nothing.found.to.display")).append('\n');
                }
            }
        } else {
            res.append('\n');
            res.append(formatTextField("KWMTool.averageTime", result.getAverageTime() + " " + StringUtil.getLocalizedString("KWMTool.ms")));
        }

        serializedResults = res.toString();
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(String maxSize) {
        this.maxSize = maxSize;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSettingsSchema() {
        return settingsSchema;
    }

    public void setSettingsSchema(String settingsSchema) {
        this.settingsSchema = settingsSchema;
    }

    public boolean isAdserverDebug() {
        return adserverDebug;
    }

    public void setAdserverDebug(boolean adserverDebug) {
        this.adserverDebug = adserverDebug;
    }

    public String getLoops() {
        return loops;
    }

    public void setLoops(String loops) {
        this.loops = loops;
    }

    public KWMToolResult getResult() {
        return result;
    }

    private List<String> getIdList(List<IdNameBean> beans) {
        if (beans == null || beans.isEmpty()) {
            return null;
        }

        List<String> res = new LinkedList<String>();

        for (IdNameBean bean : beans) {
            res.add(bean.getId());
        }

        return res;
    }

    private String getIdListAsString(List<IdNameBean> beans) {
        if (beans == null || beans.isEmpty()) {
            return null;
        }

        StringBuilder res = new StringBuilder();

        for (IdNameBean bean : beans) {
            res.append(bean.getId()).append(", ");
        }

        res.deleteCharAt(res.length() - 1);
        res.deleteCharAt(res.length() - 1);

        return res.toString();
    }

    public PopulatedMatchInfo getMatchResults() {
        return matchResults;
    }

    public int getToolExitCode() {
        return toolExitCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSerializedResults() {
        return serializedResults;
    }

    private String serializeKeywords(List<NameValuePair<String, Long>> keywords) {
        StringBuilder result = new StringBuilder();

        if (!CollectionUtils.isNullOrEmpty(keywords)) {
            for (NameValuePair<String, Long> keyword : keywords) {
                result.append(keyword.getName()).append(", ");
            }

            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    private String formatTextField(String fieldNameKey, Object value) {
        return StringUtil.getLocalizedString(fieldNameKey) + ": " + value + "\n";
    }

    private String getKeywordResultString(NameValuePair<String, Long> keyword) {
        return MessageFormat.format(KEYWORD_RESULT_STRING, keyword.getName(),
                StringUtil.getLocalizedString("KWMTool.weight"), keyword.getValue());
    }

    private String getBehavioralChannelMatchResultString(PopulatedBehavioralChannelMatchInfo channelInfo) {
        StringBuilder ccgs = new StringBuilder();

        if (!CollectionUtils.isNullOrEmpty(channelInfo.getCcgs())) {
            for (NamedTO ccg: channelInfo.getCcgs()) {
                ccgs.append(ccg.getName()).append(" (id=").append(ccg.getId()).append("), ");
            }

            ccgs.deleteCharAt(ccgs.length() - 1);
            ccgs.deleteCharAt(ccgs.length() - 1);
        }

        StringBuilder matchedTriggers = new StringBuilder();

        if (!channelInfo.getSearchTriggers().isEmpty()) {
            matchedTriggers.append(getTriggersList("KWMTool.trigger.search", channelInfo.getSearchTriggers())).append("; ");
        }

        if (!channelInfo.getPageTriggers().isEmpty()) {
            matchedTriggers.append(getTriggersList("KWMTool.trigger.page", channelInfo.getPageTriggers())).append("; ");
        }

        if (!channelInfo.getUrlTriggers().isEmpty()) {
            matchedTriggers.append(getTriggersList("KWMTool.trigger.url", channelInfo.getUrlTriggers())).append("; ");
        }

        if (!channelInfo.getUrlKeywordTriggers().isEmpty()) {
            matchedTriggers.append(getTriggersList("KWMTool.trigger.urlKeyword", channelInfo.getUrlKeywordTriggers())).append("; ");
        }

        matchedTriggers.deleteCharAt(matchedTriggers.length() - 1);
        matchedTriggers.deleteCharAt(matchedTriggers.length() - 1);

        return MessageFormat.format(CHANNEL_MATCH_RESULT_STRING, StringUtil.getLocalizedString("KWMTool.channel"),
                channelInfo.getChannel().getName(), channelInfo.getChannel().getId().toString(),
                StringUtil.getLocalizedString("KWMTool.matchedTriggers"), matchedTriggers.toString(),
                StringUtil.getLocalizedString("KWMTool.matchedCcgs"), ccgs.toString());
    }

    private String getDiscoverChannelMatchResultString(PopulatedDiscoverChannelMatchInfo channelInfo) {
        StringBuilder news = new StringBuilder();

        if (!CollectionUtils.isNullOrEmpty(channelInfo.getNewsItems())) {
            for (PopulatedNewsItemInfo newsItem : channelInfo.getNewsItems()) {
                news.append(newsItem.getTitle()).append(", ");
            }

            news.deleteCharAt(news.length() - 1);
            news.deleteCharAt(news.length() - 1);
        }

        StringBuilder matchedTriggers = new StringBuilder();

        if (!channelInfo.getSearchTriggers().isEmpty()) {
            matchedTriggers.append(getTriggersList("KWMTool.trigger.search", channelInfo.getSearchTriggers())).append("; ");
        }

        if (!channelInfo.getPageTriggers().isEmpty()) {
            matchedTriggers.append(getTriggersList("KWMTool.trigger.page", channelInfo.getPageTriggers())).append("; ");
        }

        if (!channelInfo.getUrlTriggers().isEmpty()) {
            matchedTriggers.append(getTriggersList("KWMTool.trigger.url", channelInfo.getUrlTriggers())).append("; ");
        }

        if (!channelInfo.getUrlKeywordTriggers().isEmpty()) {
            matchedTriggers.append(getTriggersList("KWMTool.trigger.urlKeyword", channelInfo.getUrlKeywordTriggers())).append("; ");
        }

        matchedTriggers.deleteCharAt(matchedTriggers.length() - 1);
        matchedTriggers.deleteCharAt(matchedTriggers.length() - 1);

        return MessageFormat.format(CHANNEL_MATCH_RESULT_STRING, StringUtil.getLocalizedString("KWMTool.channel"),
                channelInfo.getChannel().getName(), channelInfo.getChannel().getId().toString(),
                StringUtil.getLocalizedString("KWMTool.matchedTriggers"), matchedTriggers.toString(),
                StringUtil.getLocalizedString("KWMTool.matchedNews"), news.toString());

    }

    private String getTriggersList(String triggerTypeKey, List<PopulatedTriggerInfo> triggersInfo) {
        StringBuilder res = new StringBuilder();

        if (!CollectionUtils.isNullOrEmpty(triggersInfo)) {
            for (PopulatedTriggerInfo triggerInfo : triggersInfo) {
                res.append(triggerInfo.getTrigger()).append(", ");
            }

            res.deleteCharAt(res.length() - 1);
            res.deleteCharAt(res.length() - 1);
        }

        return StringUtil.getLocalizedString(triggerTypeKey) + ": " + res.toString();
    }

    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.request = httpServletRequest;
    }
}
