package com.foros.birt.web.view;

import com.foros.birt.web.util.ExceptionUtils;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.axis.AxisFault;
import org.apache.axis.encoding.SerializationContext;
import org.eclipse.birt.report.utility.BirtUtility;
import org.springframework.web.servlet.view.AbstractView;

public class SoapFaultView extends AbstractView {

    private AxisFault fault;
    private int status;

    public SoapFaultView(Exception e) {
        this.fault = BirtUtility.makeAxisFault(e);
        this.status = ExceptionUtils.getResponseStatusByException(e);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/xml; charset=utf-8");
        response.setStatus(status);
        fault.output(new SerializationContext(response.getWriter()));
    }

}
