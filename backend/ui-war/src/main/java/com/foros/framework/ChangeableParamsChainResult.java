package com.foros.framework;

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import java.util.HashMap;
import java.util.regex.Pattern;
import com.opensymphony.xwork2.util.TextParseUtil;


/**
 * Expansion of com.opensymphony.xwork2.ActionChainResult: ability of clearing old query parameters
 * and ability of adding new querying parameters are added.
 * Note: if old param name is equal to new param name, then this param will contain only the new value.
 */
public class ChangeableParamsChainResult extends ActionChainResult {
    private static final Pattern PARAMETER_PAIRS_DELIM = Pattern.compile(",");
    private static final String NAME_VALUE_DELIM = "=";
    private boolean clearPreviousParameters;
    private String parameters;

    public ChangeableParamsChainResult() {
        super();
    }

    @Override
    public void execute(ActionInvocation invocation) throws Exception {
        if (clearPreviousParameters) {
            ActionContext.getContext().getParameters().clear();
        }
        refreshParametersMap(invocation);
        super.execute(invocation);
    }

    public void setClearPreviousParameters(String value) {
        clearPreviousParameters = Boolean.parseBoolean(value);
    }

    /**
     * @param value comma separated pairs "name=value"; whitespaces are not allowed.
     */
    public void setParameters(String value) {
        parameters = value;
    }

    protected String conditionalParse(String params, ActionInvocation invocation) {
        if (params == null || invocation == null) {
            return null;
        }

        return TextParseUtil.translateVariables(params, invocation.getStack());
    }

    protected void refreshParametersMap(ActionInvocation invocation) {
        String parsedParams = conditionalParse(parameters, invocation);
        if (parsedParams == null) {
            return;
        }

        HashMap newParams = new HashMap();
        String[] pairs = PARAMETER_PAIRS_DELIM.split(parsedParams);
        for (int i = 0; i < pairs.length; ++i) {
            int ind = pairs[i].indexOf(NAME_VALUE_DELIM);
            if (ind <= 0) continue;

            String name = pairs[i].substring(0, ind);
            String[] value = {pairs[i].substring(ind + NAME_VALUE_DELIM.length())};
            newParams.put(name, value);
        }

        if (!newParams.isEmpty()) {
            ActionContext.getContext().getParameters().putAll(newParams);
        }
    }
}
