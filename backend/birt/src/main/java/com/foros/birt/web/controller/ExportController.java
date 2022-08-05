package com.foros.birt.web.controller;

import com.foros.birt.services.birt.ActionHandlerFactory;
import com.foros.birt.services.birt.BirtReportHelperService;
import com.foros.birt.services.birt.session.SessionService;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.session.birt.BirtReportService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.birt.report.context.BirtContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ExportController {

    @Autowired
    private BirtReportService reportService;

    @Autowired
    private BirtReportHelperService birtReportHelperService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ActionHandlerFactory actionHandlerFactory;

    @RequestMapping("/export/*")
    public void export(@RequestParam("__sessionId") String sessionId,
                       HttpServletRequest request,
                       HttpServletResponse response) throws Exception {

        BirtReportSession session = reportService.findSession(sessionId);

        BirtContext context = birtReportHelperService.prepareBirtContextForSession(session, sessionService, request, response);

        actionHandlerFactory.createExportActionHandler(session, context).execute();
    }

    @RequestMapping("/extract/*")
    public void extract(@RequestParam("__sessionId") String sessionId,
                        HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        BirtReportSession session = reportService.findSession(sessionId);

        BirtContext context = birtReportHelperService.prepareBirtContextForSession(session, sessionService, request, response);

        actionHandlerFactory.createExtractDataActionHandler(session, context).execute();
    }


}
