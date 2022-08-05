package com.foros.birt.web.listener;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.birt.report.context.BaseTaskBean;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.IEngineTask;

public class AuditTaskMap extends HashMap<String, BaseTaskBean> {

    @Override
    public BaseTaskBean remove(Object key) {
        BaseTaskBean bean = super.remove(key);

        IEngineTask task = bean.getTask();
        if (task.getTaskType() == IEngineTask.TASK_RUN && task.getStatus() == IEngineTask.STATUS_SUCCEEDED) {
            HttpServletRequest request = (HttpServletRequest) task.getAppContext().get(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST);

            HashMap taskParameterValues = task.getParameterValues();
            Map<String, String> birtAuditParams = new HashMap<String, String>(taskParameterValues.size());
            for (Object paramName : taskParameterValues.keySet()) {
                birtAuditParams.put(String.valueOf(paramName), String.valueOf(taskParameterValues.get(paramName)));
            }

            request.setAttribute("birtAuditParams", birtAuditParams);
        }

        return bean;
    }
}
