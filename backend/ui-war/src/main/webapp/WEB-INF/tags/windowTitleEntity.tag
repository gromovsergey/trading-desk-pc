<%@ tag language="java" body-content="empty" description="Renders page title for entity" %>
<%@ attribute name="name" required="false" %>
<%@ attribute name="entityName" required="true"%>
<%@ attribute name="id" required="false"%>
<%@ attribute name="colorStatus" required="false" type="com.foros.model.DisplayStatus" %>
<%@ attribute name="testFlag" required="false" type="java.lang.Boolean" %>
<%@ attribute name="isViewPage" required="false"%>

<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<c:choose>
    <c:when test="${empty pageScope.isViewPage || pageScope.isViewPage == 'false'}">
        <c:choose>
            <c:when test="${empty pageScope.id}">
                <fmt:message key="${pageScope.entityName}.entityName.new"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="${pageScope.entityName}.entityName.edit"/>
                <c:if test="${not empty pageScope.name}">: <c:out value="${pageScope.name}"/></c:if>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
            <fmt:message key="${pageScope.entityName}.entityName"/>: <c:out value="${pageScope.name}"/>
    </c:otherwise>
</c:choose>

<c:set scope="request" var="foros_title_generated_heading">
    <ui:pageHeadingEntity entityName="${pageScope.entityName}" id="${pageScope.id}" isViewPage="${pageScope.isViewPage}" name="${pageScope.name}" colorStatus="${pageScope.colorStatus}" testFlag="${pageScope.testFlag}"/>
</c:set>

 - <fmt:message key="systemTitle"/>