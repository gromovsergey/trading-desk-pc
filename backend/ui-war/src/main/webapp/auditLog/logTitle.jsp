<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<title>
    <c:set var="paramName">
        <c:set var="objectType" value="${objectType}"/>
        <c:set var="refText" value="${name}"/>
        
        <c:choose>
            <c:when test="${objectType.name == 'Country'}">
                <c:set var="objectId" value="${countryEntity.id}"/>
            </c:when>
            <c:otherwise>
                <c:set var="objectId" value="${id}"/>
            </c:otherwise>
        </c:choose>
        
        <fmt:message key="labels.AuditLogFor"/>&nbsp;${name}
        <c:choose>
            <c:when test="${id != null}">
                (<fmt:message key="enums.ObjectType.${objectType.name}"/>&nbsp;ID&nbsp;${objectId})
            </c:when>
            <c:otherwise>
                <fmt:message key="enums.ObjectType.${objectType.name}"/>
            </c:otherwise>
        </c:choose>
        <s:fielderror><s:param value="'errors.unableToFind'"/></s:fielderror>
    </c:set>

    <ui:windowTitle attributeName="${paramName}" isSimpleText="true" isEscape="false"/>
</title>
