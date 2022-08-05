package com.foros.action.xml.sundry;

import static com.foros.util.StringUtil.getLocalizedString;
import static com.foros.util.StringUtil.isPropertyEmpty;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.model.TriggerQACriteriaResult;
import com.foros.session.channel.triggerQA.TriggerQAType;
import com.foros.util.StringUtil;
import com.foros.util.TriggerUtil;

public class TriggerQACriteriaCheckXMLAction extends AbstractXmlAction<TriggerQACriteriaResult> {
    private String searchCriteria;
    private TriggerQAType triggerType;

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(String searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public TriggerQAType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerQAType triggerType) {
        this.triggerType = triggerType;
    }

    @Override
    protected TriggerQACriteriaResult generateModel() throws ProcessException {
        TriggerQACriteriaResult result = new TriggerQACriteriaResult();

        if (isPropertyEmpty(getSearchCriteria())) {
            result.setValid(true);
        } else {
            result.setValid(StringUtil.getBytesCount(getSearchCriteria()) <= TriggerUtil.MAX_URL_LENGTH);
            if (!result.isValid()) {
                result.setMessage(getLocalizedString("errors.field.invalidMaxLengthExc"));
            }
        }

        return result;
    }
}
