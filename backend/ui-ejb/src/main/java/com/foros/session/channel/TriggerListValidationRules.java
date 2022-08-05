package com.foros.session.channel;

import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.security.AccountType;
import com.foros.security.AccountRole;
import com.foros.util.CollectionUtils;
import com.foros.util.TriggerUtil;
import com.foros.util.bean.Filter;

import java.util.Collection;

public class TriggerListValidationRules {
    
    public static TriggerListValidationRules DEFAULT_RULES = new TriggerListValidationRules(); 
    
    private Long maxKeywordLength = (long) TriggerUtil.MAX_KEYWORD_LENGTH;
    private Long maxUrlLength = (long) TriggerUtil.MAX_URL_LENGTH;
    private Long maxKeywordsPerGroup = null;
    private Long maxKeywordsPerChannel = null;
    private Long maxUrlsPerChannel = null;
    
    private TriggerListValidationRules() {}
    
    public TriggerListValidationRules (Account account) {
        AccountRole role = account.getRole();
        AccountType accountType = account.getAccountType(); 
        if (role == AccountRole.ADVERTISER || role == AccountRole.AGENCY 
                || role == AccountRole.CMP) {
            maxKeywordLength = accountType.getMaxKeywordLength();
            maxUrlLength = accountType.getMaxUrlLength();
            maxKeywordsPerChannel = accountType.getMaxKeywordsPerChannel();
            maxUrlsPerChannel = accountType.getMaxUrlsPerChannel();
        }
        
        if (role == AccountRole.ADVERTISER || role == AccountRole.AGENCY) {
            maxKeywordsPerGroup = accountType.getMaxKeywordsPerGroup();
        }
    }
    

    public Long getMaxKeywordLength() {
        return maxKeywordLength;
    }

    public Long getMaxUrlLength() {
        return maxUrlLength;
    }

    public Long getMaxKeywordsPerGroup() {
        return maxKeywordsPerGroup;
    }

    public Long getMaxKeywordsPerChannel() {
        return maxKeywordsPerChannel;
    }

    public Long getMaxUrlsPerChannel() {
        return maxUrlsPerChannel;
    }

    public boolean isValidMaxKeywordsPerChannel(int size) {
        return maxKeywordsPerChannel == null ||  maxKeywordsPerChannel >= size;
    }
    
    public boolean isValidMaxUrlsPerChannel(int size) {
        return maxUrlsPerChannel == null ||  maxUrlsPerChannel >= size;
    }

    public boolean isValidMaxKeywordsPerGroup(int size) { return maxKeywordsPerGroup == null ||  maxKeywordsPerGroup >= size;
    }

    public boolean isValidMaxKeywordsPerGroup(Collection<CCGKeyword> ccgKeywords) {
        
        CollectionUtils.filter(ccgKeywords, new Filter<CCGKeyword>() {
            @Override
            public boolean accept(CCGKeyword element) {
                return element.getStatus() != Status.DELETED;
            }
        });
        return isValidMaxKeywordsPerGroup(ccgKeywords.size());
    }
}
