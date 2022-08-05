<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<c:choose>
    <c:when test="${errorCode == '403'}">
      <span class="errors">Access is forbidden</span>

        <c:if test="${not _principal.anonymous}">
            <c:if test="${!ad:isInternal() and _method == 'GET'}">
                <div align="left">
                    <span>
                        <c:import url="${_context}/switchContextError.action">
                            <c:param name="requestedURL" value="${_url}"/>
                        </c:import>
                    </span>
                </div>
            </c:if>
        </c:if>

    </c:when>
    <c:when test="${errorCode == '404'}">
        Status ${errorCode}
        <span class="errors">Resource not found</span>
    </c:when>
    <c:otherwise>
        <c:if test="${errorCode != null && errorCode !=''}"> Status <c:out value="${errorCode}"/> </c:if>
        <span class="errors">Unexpected error</span>
    </c:otherwise>
</c:choose>
