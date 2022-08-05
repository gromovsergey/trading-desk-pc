<%@ tag language="java" body-content="empty" description="Displays creative preview"%>

<%@ tag import="com.foros.config.ConfigParameters"%>
<%@ tag import="com.foros.config.ConfigService"%>
<%@ tag import="com.foros.session.ServiceLocator"%>
<%@ tag import="com.foros.session.creative.CreativePreviewService"%>
<%@ tag import="com.foros.session.creative.PreviewInfoTO" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ attribute name="creativeId" required="true" type="java.lang.Long"%>
<%@ attribute name="sizeId" required="false" type="java.lang.Long"%>
<%@ attribute name="templateId" required="false" type="java.lang.Long"%>
<%@ attribute name="style" required="false" type="java.lang.String"%>
<%@ attribute name="noload" required="false" type="java.lang.Boolean"%>
<%@ attribute name="postfix" required="false" type="java.lang.String"%>

<% Boolean noLoad = (Boolean) jspContext.getAttribute("noload");
if (noLoad != null && noLoad) { %>
    <div id="preview_box${postfix}_${creativeId}" class="unloaded" data-id="${creativeId}" data-info="<fmt:message key="creative.previewIsNotAvailable"/>" data-style="${style}"></div>
<% } else { 
    CreativePreviewService cps = ServiceLocator.getInstance().lookup(CreativePreviewService.class);
    PreviewInfoTO previewInfo;
    if (sizeId != null && templateId != null) {
        previewInfo = cps.generateCreativePreviewInfo(creativeId, sizeId, templateId);
    } else {
        previewInfo = cps.generateCreativePreviewInfo(creativeId);
    }

    if (previewInfo != null) {
        if (previewInfo.hasErrors()) {
            for (String error : previewInfo.getErrors()) {
                %>
                <p><span class="errors"><fmt:message key="<%=error%>"/></span></p>
                <%
            }
        } else {%>
            <iframe width="<%=previewInfo.getWidth()%>" height="<%=previewInfo.getHeight()%>" style="${style}" frameborder="0" src="<%=previewInfo.getPath()%>"></iframe>
            <%
        }
        %>
    <% } else { %>
        <span class="infos">
            <fmt:message key="creative.previewIsNotAvailable"/>
        </span>
    <% } %>
<% } %>
