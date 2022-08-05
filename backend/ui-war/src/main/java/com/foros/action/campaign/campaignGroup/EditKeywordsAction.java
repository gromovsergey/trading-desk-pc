package com.foros.action.campaign.campaignGroup;

import com.foros.framework.ReadOnly;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.session.campaign.EditCCGKeywordTO;
import com.foros.util.MultiKey;
import com.foros.web.taglib.NumberFormatter;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class EditKeywordsAction extends EditSaveKeywordsActionBase {
    @ReadOnly
    public String edit() {
        ccgVersion = getExistingGroup().getVersion();

        List<EditCCGKeywordTO> keywords = ccgKeywordService.findCCGKeywords(id);

        LinkedHashMap<MultiKey, List<EditCCGKeywordTO>> keywordsMap = new LinkedHashMap<MultiKey, List<EditCCGKeywordTO>>();
        for (EditCCGKeywordTO keyword : keywords) {
            String originalKeyword = keyword.getOriginalKeyword();
            BigDecimal maxCpcBid = keyword.getMaxCpcBid();
            String clickUrl = keyword.getClickURL();
            MultiKey key = new MultiKey(originalKeyword, maxCpcBid, clickUrl);

            List<EditCCGKeywordTO> keywordList = keywordsMap.get(key);
            if (keywordList == null) {
                keywordList = new LinkedList<EditCCGKeywordTO>();
                keywordsMap.put(key, keywordList);
            }
            keywordList.add(keyword);
        }

        StringBuilder text = new StringBuilder();
        for (Iterator<List<EditCCGKeywordTO>> iterator = keywordsMap.values().iterator(); iterator.hasNext(); ) {
            List<EditCCGKeywordTO> keywordList = iterator.next();

            if (keywordList.size() == 1) {
                text.append(getKeywordString(keywordList.get(0), keywordList.get(0).getTriggerType()));
            } else {
                text.append(getKeywordString(keywordList.get(0), null));
            }

            if (iterator.hasNext()) {
                text.append("\r\n");
            }
        }

        keywordsText = text.toString();

        return SUCCESS;
    }

    private String getKeywordString(EditCCGKeywordTO keyword, KeywordTriggerType triggerType) {
        StringBuilder sb = new StringBuilder();

        BigDecimal maxCpcBid = keyword.getMaxCpcBid();
        String clickUrl = keyword.getClickURL();

        sb.append(keyword.getOriginalKeyword());
        if (maxCpcBid != null) {
            sb.append(" ** ").append(
                NumberFormatter.formatNumber(maxCpcBid, getExistingGroup().getAccount().getCurrency().getFractionDigits()));
        }
        if (triggerType != null) {
            if (maxCpcBid == null) {
                sb.append(" **");
            }
            sb.append(" ** ").append(triggerType.getName());
        }
        if (clickUrl != null) {
            if (triggerType == null) {
                sb.append(" **");
                if (maxCpcBid == null) {
                    sb.append(" **");
                }
            }
            sb.append(" ** ").append(clickUrl);
        }

        return sb.toString();
    }
}