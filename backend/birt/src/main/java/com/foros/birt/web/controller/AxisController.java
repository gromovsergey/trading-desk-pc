package com.foros.birt.web.controller;

import com.foros.birt.services.birt.BirtReportHelperService;
import com.foros.birt.services.birt.axis.AxisServerFactory;
import com.foros.birt.services.birt.axis.MessageFactory;
import com.foros.birt.services.birt.session.SessionService;
import com.foros.birt.web.view.SoapFaultView;
import com.foros.birt.web.view.SoapView;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.session.birt.BirtReportService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;
import org.eclipse.birt.report.context.BirtContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/soap/*")
public class AxisController {

    @Autowired
    private BirtReportService reportService;

    @Autowired
    private BirtReportHelperService birtReportHelperService;

    @Autowired
    private AxisServerFactory serverFactory;

    @Autowired
    private SessionService sessionService;

    @RequestMapping(method = RequestMethod.POST)
    public View invoke(@RequestParam("__sessionId") String sessionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BirtReportSession session = getSession(sessionId);

        BirtContext context = birtReportHelperService.prepareBirtContextForSession(session, sessionService, request, response);

        MessageContext messageContext = execute(context, session);

        return new SoapView(messageContext);
    }

    @ExceptionHandler
    private View faultHandler(Exception e) throws Exception {
        return new SoapFaultView(e);
    }

    private BirtReportSession getSession(String sessionId) throws AxisFault {
        BirtReportSession session = reportService.findSession(sessionId);

        if (session == null) {
            throw new AxisFault("Session doesn't exists or expired, please rerun report.");
        }

        return session;
    }

    protected MessageContext execute(BirtContext context, BirtReportSession session) throws Exception {
        AxisServer server = serverFactory.create(session, context);

        MessageContext messageContext = new MessageFactory()
                .createContext(server, context.getRequest(), context.getResponse());

        server.invoke(messageContext);

        return messageContext;
    }

}
