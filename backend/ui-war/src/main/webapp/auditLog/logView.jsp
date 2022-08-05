<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:set var="paramName">
    <fmt:message key="labels.AuditLogFor"/>
    <c:set var="objectType" value="${objectType}"/>
    <c:choose>
        <c:when test="${objectType == 'Country' || objectType == 'PlacementsBlacklist'}">
            <c:set var="objectId" value="${countryEntity.id}"/>
        </c:when>
        <c:otherwise>
            <c:set var="objectId" value="${id}"/>
        </c:otherwise>
    </c:choose>

    <c:set var="refText"><c:out value="${name}"/></c:set>
    <c:if test="${objectType == 'CampaignCreativeGroup'}">
        <c:set var="ccgType" value="${groupForm.ccgType}"/>
    </c:if>

    <%@ include file="../makeObjectRef.jsp"%>

    <c:choose>
        <c:when test="${id != null}">
            <s:if test="version != null">
                (<fmt:message key="enums.ObjectType.${objectType.name}"/>&nbsp;ID&nbsp;${objectId};&nbsp;<fmt:message key="auditLog.version"/>:&nbsp;<fmt:formatDate value="${version}" pattern="yyyy-MM-dd HH:mm:ss" timeZone="GMT"/>&nbsp;GMT)
            </s:if>
            <s:else>
                (<fmt:message key="enums.ObjectType.${objectType.name}"/>&nbsp;ID&nbsp;${objectId})
            </s:else>
        </c:when>
        <c:otherwise>
          <fmt:message key="enums.ObjectType.${objectType.name}"/>
        </c:otherwise>
    </c:choose>
    <s:fielderror><s:param value="'errors.unableToFind'"/></s:fielderror>
</c:set>

<ui:pageHeading attributeName="${paramName}" isSimpleText="true" isEscape="false"/>

<form id="initialParams">
    <input type="hidden" name="pageSize" value="${maxRows}"/>
    <input type="hidden" name="type" value="${type}"/>
    <input type="hidden" name="action" value="${model.action}"/>
    <input type="hidden" name="id" value="${id}"/>
</form>

<div id="result" class="logicalBlock"></div>

<script type="text/javascript">
    $(function(){
        $('#initialParams').pagingAssist({
            action:     'viewLogRecords.action',
            autoSubmit: true,
            message:    '${ad:formatMessage("report.loading")}',
            result:     $('#result')
        });
    });
</script>