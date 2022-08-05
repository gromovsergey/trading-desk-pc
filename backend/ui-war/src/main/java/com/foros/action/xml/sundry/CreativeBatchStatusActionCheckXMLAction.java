package com.foros.action.xml.sundry;

import com.foros.session.creative.CreativeService;

import javax.ejb.EJB;

public class CreativeBatchStatusActionCheckXMLAction extends BatchActionCheckXMLActionBase {
    @EJB
    private CreativeService creativeService;

    @Override
    protected boolean isBatchActionPossible(String action) {
        return creativeService.isBatchActionPossible(getIdsAsList(), action);
    }
}
