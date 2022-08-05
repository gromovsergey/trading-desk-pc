package com.foros.action.campaign.campaignGroup;

import com.foros.action.BaseActionSupport;
import com.foros.session.campaign.CCGKeywordService;

import java.util.Set;
import javax.ejb.EJB;

public class BulkStatusKeywordsAction extends BaseActionSupport {
    @EJB
    private CCGKeywordService ccgKeywordService;

    private Long id;
    private Set<Long> selectedKeywords;

    public String activate() {
        ccgKeywordService.activate(selectedKeywords, id);

        return SUCCESS;
    }

    public String inactivate() {
        ccgKeywordService.inactivate(selectedKeywords, id);

        return SUCCESS;
    }

    public String delete() {
        ccgKeywordService.delete(selectedKeywords, id);

        return SUCCESS;
    }

    public String undelete() {
        ccgKeywordService.undelete(selectedKeywords, id);

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Long> getSelectedKeywords() {
        return selectedKeywords;
    }

    public void setSelectedKeywords(Set<Long> selectedKeywords) {
        this.selectedKeywords = selectedKeywords;
    }
}
