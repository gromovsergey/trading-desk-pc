<%@ tag language="java" body-content="empty" description="Displays account name" %>

<%@ attribute name="account" required="false" type="com.foros.model.account.Account" %>

<%@ attribute name="id" required="false" type="java.lang.Long" %>
<%@ attribute name="name" required="false" %>
<%@ attribute name="role" required="false" %>
<%@ attribute name="displayStatus" required="false" type="com.foros.model.DisplayStatus" %>
<%@ attribute name="canView" required="false" type="java.lang.Boolean" %>
<%@ attribute name="testFlag" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:choose>
    <c:when test="${not empty pageScope.account}">
        <c:set var="id" value="${pageScope.account.id}" scope="page"/>
        <c:set var="name" value="${pageScope.account.name}" scope="page"/>
        <c:set var="role" value="${pageScope.account.role}" scope="page"/>
        <c:set var="displayStatus" value="${pageScope.account.displayStatus}" scope="page"/>
        <c:set var="testFlag" value="${pageScope.account.testFlag}" scope="page" />
        <c:if test="${ad:isInternal()}">
            <c:set var="canView" value="${ad:isPermitted('Account.view', pageScope.account)}" scope="page"/>
        </c:if>
    </c:when>

    <c:otherwise>
        <c:if test="${ad:isInternal()}">
            <c:set var="canView" value="${ad:isPermitted('Account.view', pageScope.id)}" scope="page"/>
        </c:if>
    </c:otherwise>
</c:choose>

<c:if test="${ad:isInternal()}">
    <div class="${empty pageScope.displayStatus ? '': 'withDisplayStatus'}">
        <c:if test="${not empty pageScope.displayStatus}">
            <ui:colorStatus displayStatus="${pageScope.displayStatus}" testFlag="${pageScope.testFlag}"/>
        </c:if>
        <c:choose>
            <c:when test="${pageScope.canView}">
                <c:choose>
                    <c:when test="${pageScope.role=='INTERNAL'}">
                        <c:set var="pageLink" value="/admin/internal/account/view.action"/>
                    </c:when>
                    <c:when test="${pageScope.role=='AGENCY'}">
                        <c:set var="pageLink" value="/admin/advertiser/account/advertiserView.action"/>
                    </c:when>
                    <c:when test="${pageScope.role=='ADVERTISER'}">
                        <c:set var="pageLink" value="/admin/advertiser/account/advertiserView.action"/>
                    </c:when>
                    <c:when test="${pageScope.role=='PUBLISHER'}">
                        <c:set var="pageLink" value="/admin/publisher/account/view.action"/>
                    </c:when>
                    <c:when test="${pageScope.role=='ISP'}">
                        <c:set var="pageLink" value="/admin/isp/account/view.action"/>
                    </c:when>
                    <c:when test="${pageScope.role=='CMP'}">
                        <c:set var="pageLink" value="/admin/cmp/account/view.action"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="pageLink" value="/admin/account/view.action"/>
                    </c:otherwise>
                </c:choose>

                <a href="${pageLink}?id=${pageScope.id}"><c:out value="${pageScope.name}"/></a>
            </c:when>
            <c:otherwise>
                <c:out value="${pageScope.name}"/>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>

<c:if test="${!ad:isInternal()}">
    <c:out value="${pageScope.name}"/>
</c:if>
