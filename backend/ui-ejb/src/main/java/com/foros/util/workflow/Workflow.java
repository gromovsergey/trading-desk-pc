package com.foros.util.workflow;

import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.util.Set;

public class Workflow<S, A> {
    private WorkflowScheme<S, A> scheme;

    private S currentStep;
    
    public Workflow(WorkflowScheme<S, A> scheme, S step) {
        this.scheme = scheme;
        if (!scheme.isStepAvailable(step)) {
            throw new IllegalStateException("Step " + step + " is not available in the workflow");
        }

        this.currentStep = step;
    }
    
    public S getCurrentStep() {
        return currentStep;
    }
    
    public Set<A> getAvailableActions() {
    	return scheme.getAvailableActions(currentStep);
    }
    
    public boolean isActionAvailable(A action) {
    	return scheme.isActionAvailable(currentStep, action);
    }
    
    public S doAction(A action) {
        if (!isActionAvailable(action)) {
            throw ConstraintViolationException.newBuilder("error.statusCollision." + action).build();
        }

    	currentStep = scheme.getNextStep(currentStep, action);
    	return currentStep;
    }
}
