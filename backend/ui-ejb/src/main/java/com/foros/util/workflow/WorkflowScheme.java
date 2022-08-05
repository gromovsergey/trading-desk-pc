package com.foros.util.workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class WorkflowScheme<S, A> {
    private Map<S, Map<A, S>> scheme = new HashMap<S, Map<A,S>>();
    
    public WorkflowScheme(S[] steps, Object[][] transitions) {
        for (S s : steps) {
            scheme.put(s, new HashMap<A, S>());
        }
		
        for (Object[] transition : transitions) {
            @SuppressWarnings("unchecked")
            S stepFrom = (S)transition[0];

            @SuppressWarnings("unchecked")
            A action = (A)transition[1];

            @SuppressWarnings("unchecked")
            S stepTo = (S)transition[2];
			
            Map<A, S> actionMap = getActionMap(stepFrom);
            if (actionMap == null) {
                throw new IllegalArgumentException("Step " + stepFrom + " does not exist in workflow");
            }

            if (!isStepAvailable(stepTo)) {
                throw new IllegalArgumentException("Step " + stepTo + " does not exist in workflow");
            }
			
            actionMap.put(action, stepTo);
        }
    }
    
    public boolean isStepAvailable(S step) {
    	return scheme.containsKey(step);
    }
    
    public boolean isActionAvailable(S step, A action) {
    	return getActionMap(step).containsKey(action);
    }
    
    public Set<A> getAvailableActions(S step) {
    	return getActionMap(step).keySet();
    }
    
    public S getNextStep(S step, A action) {
    	return getActionMap(step).get(action);
    }

    private Map<A, S> getActionMap(S step) {
        Map<A, S> actionMap = scheme.get(step);
        if (actionMap == null) {
            throw new IllegalArgumentException("Step " + step + " does not exist in workflow");
        }
        
        return actionMap;
    }
}
