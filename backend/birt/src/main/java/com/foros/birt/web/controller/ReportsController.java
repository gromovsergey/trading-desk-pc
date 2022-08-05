package com.foros.birt.web.controller;

import com.foros.birt.services.birt.BirtReportHelperService;
import com.foros.birt.services.birt.session.SessionService;
import com.foros.birt.utils.BirtUtils;
import com.foros.birt.web.util.ExportHelper;
import com.foros.model.report.birt.BirtReportInstance;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.session.birt.BirtReportService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.birt.report.context.BirtContext;
import org.eclipse.birt.report.presentation.aggregation.IFragment;
import org.eclipse.birt.report.soapengine.api.Operation;
import org.eclipse.birt.report.utility.BirtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportsController {

    @Autowired
    private BirtReportService reportService;

    @Autowired
    private BirtReportHelperService birtReportHelperService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    @Qualifier("framesetFragment")
    private IFragment framesetFragment;

    @Autowired
    @Qualifier("engineFragment")
    private IFragment engineFragment;

    @RequestMapping("/run/{id}/*")
    public void run(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BirtReportSession session = reportService.createSession(
                reportService.get(id)
        );

        showFragmentForSession(request, response, session, framesetFragment);
    }

    @RequestMapping("/runandrender/{id}/")
    public void runAndRender(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BirtReportSession session = reportService.createSession(
                reportService.get(id)
        );

        BirtContext context = createBirtContext(session, request, response);

        Operation operation = BirtUtils.createOperatorByRequest(request);

        BirtReportHelperService.RunReportParameters runReportParameters = BirtUtils.fetchRunReportParameters(context, operation);

        BirtReportInstance instance = birtReportHelperService.runReport(session, runReportParameters);

        BirtReportHelperService.RenderReportParameters renderReportParameters = BirtUtils.fetchRenderReportParameters(context, operation);

        ExportHelper.prepareDownload(context);

        ServletOutputStream stream = context.getResponse().getOutputStream();

        birtReportHelperService.renderReport(instance, renderReportParameters, stream);
    }

    @RequestMapping("/preview/*")
    public void view(@RequestParam("__sessionId") String sessionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BirtReportSession session = reportService.findSession(sessionId);

        showFragmentForSession(request, response, session, engineFragment);
    }

    @RequestMapping("/cancel-task/")
    public String cancelTask(@RequestParam("__sessionId") String sessionId, @RequestParam("__taskid") String taskid,
                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        BirtReportSession session = reportService.findSession(sessionId);

        BirtContext context = createBirtContext(session, request, response);

        BirtUtility.cancelTask(context.getRequest(), taskid);

        return "CancelTask";
    }

    private void showFragmentForSession(HttpServletRequest request, HttpServletResponse response, BirtReportSession session, IFragment fragment) throws Exception {
        BirtContext context = createBirtContext(session, request, response);

        fragment.service(context.getRequest(), context.getResponse());
    }

    private BirtContext createBirtContext(BirtReportSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BirtContext context = birtReportHelperService
                .prepareBirtContextForSession(session, sessionService, request, response);

        setSessionPaths(context.getRequest(), session);

        return context;
    }

    private void setSessionPaths(HttpServletRequest request, BirtReportSession session) {
        String sessionId = session.getSessionId();
        request.setAttribute("SoapURL", "/birt/reports/soap/?");
        request.setAttribute("CancelTaskURL", "/birt/reports/cancel-task/?__sessionId=" + session.getSessionId());
        request.setAttribute("ExportURL", "/birt/reports/export/?__sessionId=" + sessionId);
        request.setAttribute("ExtractURL", "/birt/reports/extract/?__sessionId=" + sessionId);

        request.setAttribute("reportSession", session);
    }

}
