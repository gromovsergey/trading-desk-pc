package com.foros.framework;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;


/**
 * Copy from com.opensymphony.xwork2.interceptor.ModelDrivenInterceptor
 */
public class ModelDrivenInterceptor extends AbstractInterceptor {

    protected boolean refreshModelBeforeResult = false;

    public void setRefreshModelBeforeResult(boolean val) {
        this.refreshModelBeforeResult = val;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ModelDriven) {
            ModelDriven modelDriven = (ModelDriven) action;
            ValueStack stack = invocation.getStack();
            Object model = modelDriven.getModel();
            if (model !=  null) {
                addToStack(modelDriven, stack, model);
            }
            if (refreshModelBeforeResult) {
                invocation.addPreResultListener(new RefreshModelBeforeResult(modelDriven, model));
            }
        }
        return invocation.invoke();
    }

    private static void addToStack(ModelDriven action, ValueStack stack, Object model) {
        // place model right after action
        CompoundRoot root = stack.getRoot();
        int actionIndex = indexOf(root, action);
        root.add(actionIndex + 1, model);
    }

    private static int indexOf(CompoundRoot root, Object obj) {
        int objIndex = -1;
        for (int i = 0; i < root.size(); i++) {
            Object o = root.get(i);
            if (o == obj) {
                objIndex = i;
                break;
            }

        }
        return objIndex;
    }

    /**
     * Refreshes the model instance on the value stack, if it has changed
     */
    protected static class RefreshModelBeforeResult implements PreResultListener {
        private Object originalModel = null;
        protected ModelDriven action;


        public RefreshModelBeforeResult(ModelDriven action, Object model) {
            this.originalModel = model;
            this.action = action;
        }

        public void beforeResult(ActionInvocation invocation, String resultCode) {
            ValueStack stack = invocation.getStack();
            CompoundRoot root = stack.getRoot();

            Object newModel = action.getModel();

            if (originalModel == newModel || newModel == null) {
                return;
            }

            if (originalModel != null) {
                int modelIndex = indexOf(root, originalModel);
                if (modelIndex != -1) {
                    root.set(modelIndex, newModel);
                } else {
                    // somebody removed it?
                    addToStack(action, stack, newModel);
                }
            } else {
                addToStack(action, stack, newModel);
            }
        }
    }
}
