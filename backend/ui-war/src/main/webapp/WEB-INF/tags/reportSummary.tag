<%@ tag description="UI Field" body-content="empty"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"      prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"       prefix="fmt" %>
<%@ taglib uri="/struts-tags"                           prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<%@ attribute name="data" required="true" rtexprvalue="true" type="com.foros.reporting.serializer.SimpleReportData" %>
<%@ attribute name="includeCaption" required="false" type="java.lang.Boolean"  %>

<c:set var="summary" value="${pageScope.data.summary}"/>

<c:if test="${empty includeCaption}">
    <c:set var="includeCaption" value="true"/>
</c:if>

<c:if test="${includeCaption}">
    <h2><fmt:message key="report.summary"/></h2>
</c:if>

<div class="wrapper">

<c:choose>
    <c:when test="${not empty summary}">
        <table class="summary">
            <c:forEach items="${summary.headers}" var="column" varStatus="columnStatus">
                <c:if test="${not empty summary.values[columnStatus.index].html}">
                    <tr>
                        <td class="paramName">${column.html}</td>
                        <td>${summary.values[columnStatus.index].html}</td>
                    </tr>
                </c:if>
                <c:if test="${empty summary.values[columnStatus.index].html}">
                    <tr>
                        <td class="paramName">${column.html}</td>
                        <td><fmt:message key="notAvailable"/></td>
                    </tr>
                </c:if>
            </c:forEach>
        </table>
    </c:when>
    <c:otherwise>
        <fmt:message key="nothing.found.to.display"/>
    </c:otherwise>
</c:choose>

</div>
