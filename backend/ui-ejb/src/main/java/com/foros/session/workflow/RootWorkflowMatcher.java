package com.foros.session.workflow;

import com.foros.util.workflow.WorkflowScheme;

public class RootWorkflowMatcher<S, A> extends BaseWorkflowMatcher<S, A> {
    public RootWorkflowMatcher(WorkflowScheme<S, A> scheme) {
        super(scheme);
    }

    @Override
    protected boolean isThisMatched(Object entity) {
        return true;
    }
}
