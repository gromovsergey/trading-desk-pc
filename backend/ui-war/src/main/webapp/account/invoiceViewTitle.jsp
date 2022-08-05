<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<c:set var="paramName">
    <fmt:message key="account.invoice.pageHeading">
        <fmt:param>${id}</fmt:param>
        <fmt:param>
            <c:if test="${not empty invoiceLegalNumber}">
                <fmt:message key="account.invoice.pageHeadingOF">
                    <fmt:param>${invoiceLegalNumber}</fmt:param>
                </fmt:message>
            </c:if>
        </fmt:param>
        <fmt:param>
            <fmt:formatDate value="${invoiceDate}" type="date" dateStyle="short"/>
        </fmt:param>
    </fmt:message>
</c:set>
<title>
    <ui:windowTitle attributeName="${paramName}" isSimpleText="true"/>
</title>
