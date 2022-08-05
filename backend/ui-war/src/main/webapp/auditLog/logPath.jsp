<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@page import="com.foros.model.security.ObjectType"%>
<%@page import="com.foros.model.account.Account"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>

<c:set var="isTypeAssignableFromAccount" value='<%=Account.class.isAssignableFrom(((ObjectType)request.getAttribute("objectType")).getObjectClass())%>'/>

<c:choose>
    <c:when test="${breadcrumbs==null}">
        <c:if test="${(!isTypeAssignableFromAccount && objectType != 'CampaignCredit') || contextName == 'global.menu.admin'}">
            <c:if test="${objectType != 'BirtReport'}">
                <span class='delimiter'>&gt;</span> <fmt:message key="form.viewLog"/>
            </c:if>
        </c:if>
    </c:when>
    <c:otherwise>
        <jsp:include page="/breadcrumbs/breadcrumbs.jsp"/>
    </c:otherwise>
</c:choose>