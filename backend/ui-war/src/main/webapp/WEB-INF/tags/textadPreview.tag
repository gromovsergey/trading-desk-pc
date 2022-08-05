<%@ tag language="java" body-content="empty" description="Displays Text Ad preview"%>

<%@ tag import="com.foros.config.ConfigParameters"%>
<%@ tag import="com.foros.config.ConfigService"%>
<%@ tag import="com.foros.session.ServiceLocator"%>
<%@ tag import="com.foros.session.creative.CreativePreviewService"%>
<%@ tag import="com.foros.session.creative.PreviewInfoTO" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ attribute name="creativeId" required="true" type="java.lang.Long"%>
<%@ attribute name="sizeId" required="true" type="java.lang.Long"%>
<%@ attribute name="templateId" required="true" type="java.lang.Long"%>
<%@ attribute name="width" required="true" type="java.lang.Long"%>
<%@ attribute name="height" required="true" type="java.lang.Long"%>
<%@ attribute name="style" required="false" type="java.lang.String"%>
<%@ attribute name="noload" required="false" type="java.lang.Boolean"%>
<%@ attribute name="postfix" required="false" type="java.lang.String"%>

<% Boolean noLoad = (Boolean) jspContext.getAttribute("noload");
if (noLoad != null && noLoad) { %>
    <div id="preview_box${postfix}_${creativeId}" class="unloaded" data-id="${creativeId}" data-info="<fmt:message key="creative.previewIsNotAvailable"/>" data-style="${style}"></div>
<% } else { 
    Long cid = (Long) jspContext.getAttribute("creativeId");
    Long sid = (Long) jspContext.getAttribute("sizeId");
    Long tid = (Long) jspContext.getAttribute("templateId");
    Long h = (Long) jspContext.getAttribute("height");
    Long w = (Long) jspContext.getAttribute("width");

    CreativePreviewService cps = ServiceLocator.getInstance().lookup(CreativePreviewService.class);
    String path = cps.generateTextAdPreviewPath(cid, sid, tid);

    if (path != null) {
        %>
        <iframe width="<%=w%>" height="<%=h%>" style="${style}" frameborder="0" src="<%=path%>"></iframe>
    <% } else { %>
        <span class="infos">
            <fmt:message key="creative.previewIsNotAvailable"/>
        </span>
    <% } %>
<% } %>