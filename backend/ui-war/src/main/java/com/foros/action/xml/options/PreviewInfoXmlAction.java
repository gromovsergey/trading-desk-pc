package com.foros.action.xml.options;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.session.creative.CreativePreviewService;
import com.foros.session.creative.PreviewInfoTO;
import com.foros.util.StringUtil;

import javax.ejb.EJB;
import java.util.List;

public class PreviewInfoXmlAction extends AbstractXmlAction<PreviewInfoTO> {
    @EJB
    private CreativePreviewService creativePreviewService;

    private Long creativeId;

    @Override
    protected PreviewInfoTO generateModel() throws ProcessException {
        PreviewInfoTO to = creativePreviewService.generateCreativePreviewInfo(creativeId);
        if (to.hasErrors()) {
            localizeErrors(to);
        }
        return to;
    }

    public Long getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(Long creativeId) {
        this.creativeId = creativeId;
    }

    private void localizeErrors(PreviewInfoTO to) {
        List<String> errors = to.getErrors();
        for (int i = 0; i < errors.size(); i++) {
            errors.set(i, StringUtil.getLocalizedString(errors.get(i)));
        }
    }
}
