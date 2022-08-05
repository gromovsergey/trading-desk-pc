package com.foros.session.workflow;

import com.foros.util.workflow.WorkflowScheme;

import java.util.Arrays;
import java.util.List;

public abstract class BaseWorkflowMatcher<S, A> implements WorkflowMatcher<S, A> {
    private WorkflowScheme<S, A> scheme;
    private List<WorkflowMatcher<S, A>> nested = Arrays.asList();

    public BaseWorkflowMatcher(WorkflowScheme<S, A> scheme) {
        this.scheme = scheme;
    }

    public WorkflowMatcher<S, A> nested(WorkflowMatcher<S, A>... nested) {
        this.nested = Arrays.asList(nested);
        return this;
    }

    @Override
    public WorkflowScheme<S, A> findMatched(Object entity) {
        WorkflowScheme<S, A> found;

        // no matches
        if (!isThisMatched(entity)) {
            return null;
        }

        // this match
        for (WorkflowMatcher<S, A> matcher : nested) {
            found = matcher.findMatched(entity);
            if (found != null) {
                return found;
            }
        }

        return scheme;
    }

    protected abstract boolean isThisMatched(Object entity);

}
