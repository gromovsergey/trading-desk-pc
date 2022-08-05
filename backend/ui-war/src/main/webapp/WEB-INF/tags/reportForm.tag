<%@ tag description="UI Field" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"      prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"       prefix="fmt" %>
<%@ taglib uri="/struts-tags"                           prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<s:form id="reportForm" action="cancellableRun">
    <c:forEach items="${paramValues}" var="parameter">
        <c:forEach items="${parameter.value}" var="value">
            <s:hidden name="%{#attr.parameter.key}" value="%{#attr.value}"/>
        </c:forEach>
    </c:forEach>

    <jsp:doBody/>
</s:form>