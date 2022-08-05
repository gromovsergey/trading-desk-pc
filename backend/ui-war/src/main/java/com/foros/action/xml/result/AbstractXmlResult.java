package com.foros.action.xml.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.foros.action.xml.annotation.Model;
import com.foros.action.xml.generator.Generator;
import com.foros.util.annotation.AnnotationUtil;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractXmlResult implements Result {

    public void execute(ActionInvocation invocation) throws Exception {
        Object model = fetchModel(invocation);
        Generator generator = createGenerator(invocation);
        write(generator.generate(model));
    }

    protected Object fetchModel(ActionInvocation invocation) throws Exception {
        return AnnotationUtil.fetchAnnotatedValue(invocation.getAction(), Model.class);
    }

    protected abstract Generator createGenerator(ActionInvocation invocation) throws Exception;

    private void write(String xml) throws IOException {
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/xml");
        response.setHeader("Cache-Control", "no-cache");
        response.getWriter().write(xml);
    }

}
