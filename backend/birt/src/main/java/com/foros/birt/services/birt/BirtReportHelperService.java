package com.foros.birt.services.birt;

import com.foros.birt.services.birt.session.SessionService;
import com.foros.birt.utils.IOUtils;
import com.foros.birt.web.util.Hasher;
import com.foros.birt.web.util.HttpServletUtils;
import com.foros.birt.web.util.ModelBuilder;
import com.foros.model.report.birt.BirtReport;
import com.foros.model.report.birt.BirtReportInstance;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.model.report.birt.BirtReportSessionState;
import com.foros.model.security.ObjectType;
import com.foros.restriction.AccessRestrictedException;
import com.foros.restriction.RestrictionService;
import com.foros.session.birt.BirtReportService;
import com.foros.session.security.AuditService;
import com.foros.session.security.ReportLogger;
import com.foros.session.security.ReportRunTO;
import com.foros.util.NameValuePair;
import com.foros.validation.constraint.violation.ConstraintViolation;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.report.context.BirtContext;
import org.eclipse.birt.report.engine.api.impl.ParameterValidationException;
import org.eclipse.birt.report.service.BirtReportServiceFactory;
import org.eclipse.birt.report.service.api.IViewerReportDesignHandle;
import org.eclipse.birt.report.service.api.InputOptions;
import org.eclipse.birt.report.service.api.ReportServiceException;
import org.eclipse.birt.report.utility.ParameterAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BirtReportHelperService {
    private static final Logger logger = Logger.getLogger(BirtReportHelperService.class.getName());

    @Autowired
    private BirtReportService reportService;

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private SynchronizationReportSessionService synchronizationReportSessionService;

    @Autowired
    private ScriptingContext scriptingContext;

    @Autowired
    private AuditService auditService;

    public BirtReportInstance runReport(BirtReportSession session, RunReportParameters parameters) throws Exception {
        BirtReportInstance instance = runReportImpl(session, parameters);
        session.setBirtReportInstance(instance);
        reportService.setSessionInstance(session.getId(), instance.getId());
        return instance;
    }

    private BirtReportInstance runReportImpl(BirtReportSession session, RunReportParameters parameters) throws Exception {
        BirtReport report = session.getReport();

        if (!restrictionService.isPermitted("BirtReport.run", report.getId(), parameters.getParameters())) {
            throw new AccessRestrictedException("Access denied.", Collections.<ConstraintViolation>emptySet());
        }

        String hash = Hasher.hash(parameters.getParameters());

        ReportRunTO reportRunTO = new ReportRunTO(report.getId(), ObjectType.BirtReport);
        reportRunTO.setName(report.getName());
        reportRunTO.setParams(convertParams(parameters.getParameters()));

        if (report.isCachable()) {
            BirtReportInstance instance = getCachedReportInstance(report, hash);

            if (instance != null) {
                auditService.logReport(reportRunTO);
                return instance;
            }
        }

        Long sessionId = session.getId();
        ReportLogger reportLogger = new ReportLogger().logStart(reportRunTO);

        synchronizationReportSessionService.lock(sessionId);

        try {
            BirtReportInstance instance = createReportInstance(report, parameters.getParameters());

            String documentName = reportService.getReportDocumentAbsoluteFileName(instance);

            reportService.setSessionStarted(sessionId, instance.getParametersHash());

            List<Exception> exceptions = new ArrayList<Exception>();

            if (logger.isLoggable(Level.INFO)) {
                StringBuilder buf = new StringBuilder("Start Birt Report [id=")
                        .append(report.getId()).append(", name=")
                        .append(report.getName()).append("]")
                        .append(", start time:")
                        .append(reportLogger.getStart());
                buf.append('\n');

                for (NameValuePair<String, Object> param : reportRunTO.getParams()) {
                    buf.append(param.getName()).append('=').append(param.getValue()).append('\n');
                }
                logger.log(Level.INFO, buf.toString());
            }

            BirtReportServiceFactory.getReportService().runReport(parameters.getReportDesignHandle(),
                    documentName,
                    parameters.getOptions(),
                    parameters.getParameters(),
                    parameters.getDisplayTexts(),
                    exceptions);

            if (!exceptions.isEmpty()) {
                IOUtils.delete(documentName);
                reportService.setSessionAborted(sessionId);
                throw new ReportProcessingException(getProcessingFailedMessage(exceptions), exceptions);
            }

            reportService.createInstance(instance);
            reportService.setSessionDone(sessionId, instance.getId());

            reportLogger.logSuccess(null, null);

            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Stop Birt Report [id=" + report.getId() + ", name=" + report.getName() + "]"
                        + ", start time: " + reportLogger.getStart() + ", generated time (millis): " + reportLogger.getExecutionTime());
            }

            return instance;
        } catch (Exception e) {
            reportLogger.logFailure(e.getMessage());
            throw e;
        } finally {
            synchronizationReportSessionService.unlock(sessionId);
        }
    }

    private String getProcessingFailedMessage(List<Exception> exceptions) {
        for (Exception exception: exceptions) {
            if (exception instanceof ParameterValidationException) {
                return exception.getMessage();
            }
        }
        return "Report processing failed";
    }

    private Collection<NameValuePair<String, Object>> convertParams(Map<String, Object> parameters) {
        List<NameValuePair<String, Object>> result = new ArrayList<NameValuePair<String, Object>>(parameters.size());
        for (String name : parameters.keySet()) {
            Object obj = parameters.get(name);
            if (obj != null && obj.getClass().isArray()) {
                result.add(new NameValuePair<String, Object>(name, Arrays.toString((Object[]) obj)));
            } else {
                result.add(new NameValuePair<String, Object>(name, String.valueOf(obj)));
            }
        }
        return result;
    }

    public void renderReport(BirtReportInstance instance, RenderReportParameters renderReportParameters, OutputStream out) throws ReportServiceException {
        BirtReportServiceFactory.getReportService().renderReport(
                reportService.getReportDocumentAbsoluteFileName(instance),
                renderReportParameters.getReportPage(),
                renderReportParameters.getReportPageRange(),
                renderReportParameters.getOptions(),
                out);
    }

    private BirtReportInstance createReportInstance(BirtReport report, Map<String, Object> parameters) {
        BirtReportInstance instance = new BirtReportInstance();
        instance.setReport(report);
        instance.setParametersHash(Hasher.hash(parameters));
        instance.generateDefaultDocumentFileName();
        return instance;
    }

    private BirtReportInstance getCachedReportInstance(BirtReport report, String hash) {
        BirtReportSession startedSession = reportService.findStartedSessionByReportIdAndParametersHash(report.getId(), hash);

        if (startedSession != null) {
            synchronizationReportSessionService.waitFor(startedSession.getId());

            startedSession = reportService.findSession(startedSession.getSessionId());
            BirtReportInstance reportInstance = startedSession.getBirtReportInstance();

            if (startedSession.getState() == BirtReportSessionState.DONE && reportInstance != null) {
                return reportInstance;
            }
        }

        BirtReportInstance instance = reportService.findCachedInstance(report.getId(), hash);

        if (instance != null) {
            String cachedDocumentName = reportService.getReportDocumentAbsoluteFileName(instance);

            if (cachedDocumentName != null && !IOUtils.exists(cachedDocumentName)) {
                reportService.deleteInstance(instance.getId());
                return null;
            }

            return instance;
        }

        return null;
    }

    public BirtContext prepareBirtContextForSession(
            BirtReportSession session,
            SessionService sessionService,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        assert session != null;

        BirtReport report = session.getReport();
        BirtReportInstance birtReportInstance = session.getBirtReportInstance();

        assert report != null;

        ModelBuilder modelBuilder = ModelBuilder.create(ParameterAccessor.PARAM_REPORT, reportService.getReportAbsoluteFileName(report));
        if (ParameterAccessor.getParameter(request, ParameterAccessor.PARAM_PAGE) == null) {
            modelBuilder.add(ParameterAccessor.PARAM_PAGE, 1);
        }
        HttpServletRequest wrappedRequest = HttpServletUtils.addParameters(request, modelBuilder.build());

        initializeBirtScriptingContext(wrappedRequest, session);

        BirtContext context = initializeBirtContextAndSession(wrappedRequest, response, session, sessionService);

        checkContext(context);

        if (birtReportInstance != null) {
            context.getBean().setReportDocumentName(reportService.getReportDocumentAbsoluteFileName(birtReportInstance));
        }

        return context;
    }


    public static class SessionContext {

        private BirtReportSession session;
        private ScriptingContext application;

        private SessionContext(BirtReportSession session, ScriptingContext application) {
            this.session = session;
            this.application = application;
        }

        public BirtReportSession getSession() {
            return session;
        }

        public ScriptingContext getApplication() {
            return application;
        }
    }

    private void initializeBirtScriptingContext(HttpServletRequest request, BirtReportSession session) {
        request.setAttribute(ParameterAccessor.ATTR_APPCONTEXT_KEY, "context");
        request.setAttribute(ParameterAccessor.ATTR_APPCONTEXT_VALUE, new SessionContext(session, scriptingContext));
    }

    private void checkContext(BirtContext context) throws Exception {
        Exception exception = context.getBean().getException();

        if (exception != null) {
            throw exception;
        }
    }

    private BirtContext initializeBirtContextAndSession(HttpServletRequest request, HttpServletResponse response, BirtReportSession reportSession, SessionService sessionService) {
        BirtContext context = new BirtContext(request, response);
        // just to init session
        sessionService.getSession(reportSession, request);
        return context;
    }

    public static class RunReportParameters {

        private IViewerReportDesignHandle reportDesignHandle;
        private InputOptions options;
        private Map<String, Object> parameters;
        private Map displayTexts;

        public RunReportParameters(IViewerReportDesignHandle reportDesignHandle, InputOptions options, Map<String, Object> parameters, Map displayTexts) {
            this.reportDesignHandle = reportDesignHandle;
            this.options = options;
            this.parameters = parameters;
            this.displayTexts = displayTexts;
        }

        public IViewerReportDesignHandle getReportDesignHandle() {
            return reportDesignHandle;
        }

        public InputOptions getOptions() {
            return options;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public Map getDisplayTexts() {
            return displayTexts;
        }

    }

    public static class RenderReportParameters {

        private int reportPage;
        private String reportPageRange;
        private InputOptions options;

        public RenderReportParameters(int reportPage, String reportPageRange, InputOptions options) {
            this.reportPage = reportPage;
            this.reportPageRange = reportPageRange;
            this.options = options;
        }

        public int getReportPage() {
            return reportPage;
        }

        public String getReportPageRange() {
            return reportPageRange;
        }

        public InputOptions getOptions() {
            return options;
        }
    }
}
