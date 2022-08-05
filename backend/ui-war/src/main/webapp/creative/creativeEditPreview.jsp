<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:choose>
    <c:when test="${previewUrl != null}">
        <iframe name="previewFrame"
                id="previewFrameId"
                frameborder="0"
                marginwidth="0"
                marginheight="0"
                scrolling="auto"
                width="${previewWidth}"
                height="${previewHeight}"
                src="${previewUrl}">
        </iframe>
    </c:when>
    <c:otherwise>
        <fmt:message key="creative.previewIsNotAvailable"/>
    </c:otherwise>
</c:choose>