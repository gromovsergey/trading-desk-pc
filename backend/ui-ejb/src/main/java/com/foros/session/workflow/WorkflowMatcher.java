package com.foros.session.workflow;

import com.foros.util.workflow.WorkflowScheme;

public interface WorkflowMatcher<S, A> {
    WorkflowScheme<S, A> findMatched(Object entity);
}
