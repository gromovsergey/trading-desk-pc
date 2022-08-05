<%@ page import="com.foros.session.fileman.FileManager" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<script language="javascript">
    function showHideAuditDescription(recordId){
        var descrTr = $('#tr_descr_' + recordId);
        var toShow = descrTr.css('display') == 'none';
        if(toShow && $('#descr_'+recordId).html() == ''){
            $('#descr_'+recordId)
                .html('<h3 class="level1">${ad:formatMessage("report.loading")}</h3>')
                .load('audit/record.action?id='+recordId);
        }
        $('#btn_show_' + recordId).attr({value : toShow ? '<fmt:message key="form.hide"/>' : '<fmt:message key="form.view"/>'});
        descrTr[toShow ? 'show' : 'hide']();
    }
</script>


<ui:pages pageSize="${pageSize}"
      total="${logRecords.getTotal()}"
      selectedNumber="${page}"
      totalHasMore="${logRecords.isTotalHasMore()}"
      visiblePagesCount="10"
      handler="goToPage"
      displayHeader="true"/>

<display:table name="logRecords" id="logRecord" class="dataView">
    <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
    <display:column titleKey="report.output.field.dateAndTime">
        <c:out value="${ad:formatLocalDateTime(logRecord.logDate)}"/>
    </display:column>
    <display:column titleKey="report.output.field.accountAndLogin">
        <c:if test="${not empty logRecord.accountId}">
          <a class="rtext" href="/admin/account/view.action?id=${logRecord.accountId}"><c:out value="${logRecord.accountName}"/></a>
        </c:if>
        <c:if test="${not empty logRecord.accountId or not empty logRecord.userId}">
          /
        </c:if>
        <c:if test="${not empty logRecord.userId}">
          <a class="rtext" href="/admin/account/user/view.action?id=${logRecord.userId}">${logRecord.userLogin}</a>
        </c:if>
    </display:column>
    <display:column titleKey="report.output.field.object">
        <c:choose>
            <c:when test="${not empty logRecord.financeJob}">
              ${logRecord.financeJob.name}
            </c:when>
            <c:otherwise>
                <c:if test="${not empty logRecord.objectType and !logRecord.actionType.objectless}">
                    <c:set var="objectType" value="${logRecord.objectType}"/>
                    <c:set var="objectId" value="${logRecord.objectId}"/>
                    <c:set var="refText">
                        <c:choose>
                            <c:when test="${objectType == 'PredefinedReport'}">
                               <fmt:message key="reports.${ad:getReportName(objectId)}Report"/>
                            </c:when>
                            <c:when test="${objectType == 'FileManager'}">
                                <fmt:message key="enums.ObjectType.${objectType}"/> /
                                <fmt:message key="${ad:getFolderName(objectId)}"/>
                            </c:when>
                            <c:when test="${not empty objectType}">
                                <fmt:message key="enums.ObjectType.${objectType}"/> ${objectId}
                            </c:when>
                        </c:choose>
                    </c:set>
                    <c:if test="${not empty logRecord.objectAccountId}">
                        <a class="rtext" href="/admin/account/view.action?id=${logRecord.objectAccountId}"><c:out value="${logRecord.objectAccountName}"/></a>
                        /
                    </c:if>
                    <%@ include file="../../makeObjectRef.jsp"%>
                </c:if>
            </c:otherwise>
        </c:choose>
    </display:column>
    <display:column titleKey="report.output.field.actionType">
        <c:if test="${logRecord.actionType != null }">
            <c:set var="name" scope="request" >${fn:replace(logRecord.actionType, " ", "_")}</c:set>
               <fmt:message key="enums.ActionType.${name}"/>
        </c:if>
    </display:column>
    <display:column titleKey="report.output.field.resultType">
        <fmt:message key="enums.ResultType.${logRecord.success ? 'SUCCESS' : 'FAILURE'}"/>
    </display:column>
    <display:column titleKey="report.output.field.description">
        <span class="rtext">
            <table class="fieldAndAccessories">
                <tr>
                    <td>
                        <input type="button" id="btn_show_${logRecord.id}" value="<fmt:message key="form.view"/>" onclick="showHideAuditDescription('${logRecord.id}')"/>
                    </td>
                </tr>
                <tr class="hide" id="tr_descr_${logRecord.id}">
                    <td>
                        <div id="descr_${logRecord.id}" class="logicalBlock"></div>
                    </td>
                </tr>
            </table>
        </span>
    </display:column>
</display:table>

<ui:pages pageSize="${pageSize}"
      total="${logRecords.getTotal()}"
      selectedNumber="${page}"
      visiblePagesCount="10"
      handler="goToPage"
      displayHeader="false"/>