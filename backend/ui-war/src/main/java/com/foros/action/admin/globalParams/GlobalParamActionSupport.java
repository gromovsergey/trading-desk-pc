package com.foros.action.admin.globalParams;

import com.foros.action.BaseActionSupport;
import com.foros.model.currency.Source;
import com.foros.session.admin.globalParams.GlobalParamsService;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class GlobalParamActionSupport extends BaseActionSupport implements ModelDriven<GlobalParameters> {
    @EJB
    protected GlobalParamsService paramsService;
    private GlobalParameters globalParameters = new GlobalParameters();

    private List<Source> sourceValues = new ArrayList<Source>();

    public GlobalParamActionSupport() {
        sourceValues.add(Source.MANUAL);
        sourceValues.add(Source.FEED);
    }

    public List<Source> getSourceValues() {
        return sourceValues;
    }

    @Override
    public GlobalParameters getModel() {
        return globalParameters;
    }
}
