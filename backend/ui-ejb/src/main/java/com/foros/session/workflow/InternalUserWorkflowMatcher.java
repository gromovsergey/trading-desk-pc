package com.foros.session.workflow;

import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.util.workflow.WorkflowScheme;

public class InternalUserWorkflowMatcher<S,A> extends BaseWorkflowMatcher<S,A> {

    public InternalUserWorkflowMatcher(WorkflowScheme<S, A> saWorkflowScheme) {
        super(saWorkflowScheme);
    }

    @Override
    protected boolean isThisMatched(Object entity) {
        return SecurityContext.isInternal();
    }

}
