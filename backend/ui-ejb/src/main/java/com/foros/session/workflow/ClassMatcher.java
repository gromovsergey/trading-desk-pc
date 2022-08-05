package com.foros.session.workflow;

import com.foros.util.workflow.WorkflowScheme;

public class ClassMatcher<S, A> extends BaseWorkflowMatcher<S, A> {
    private Class[] classes;

    public ClassMatcher(WorkflowScheme<S, A> scheme, Class... classes) {
        super(scheme);
        this.classes = classes;
    }

    @Override
    public boolean isThisMatched(Object entity) {
        Class<?> entityClazz = entity.getClass();

        for (Class clazz : classes) {
            if( clazz.isAssignableFrom(entityClazz)) {
                return true;
            }
        }
        return false;
    }
}
