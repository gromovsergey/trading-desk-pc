<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.logging.Logger" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@page import="com.foros.session.reporting.ReportType"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad"%>

<table width="100%" cellpadding="0" cellspacing="0">
  <tbody>
    <tr>
      <td>
        <b>
          <fmt:formatDate var="logDate" value="${logRecord.logDate}" type="both" timeStyle="short" dateStyle="short" timeZone="${_userSettings.timeZone}"/>
          ${logDate}
          <c:if test="${not empty logRecord.user}">,&nbsp;
            <c:set var="user" value="${logRecord.user}"/>
            <c:set var="account" value="${logRecord.user.account}"/>
              <c:if test="${ad:isPermitted('User.view',user)}">
                  <a class="rtext" href="/admin/account/user/view.action?id=${user.id}">${user.email}</a>
              </c:if>
              <c:if test="${not ad:isPermitted('User.view', user)}">
                ${user.email}
              </c:if>,&nbsp;
              <c:if test="${ad:isPermitted('Account.view', account)}">
                  <a href="/admin/account/view.action?id=${account.id}">
                    <c:out value="${account.name}"/> (<fmt:message key="enum.accountRole.${account.role}"/>)</a>
              </c:if>
              <c:if test="${not ad:isPermitted('Account.view', account)}">
                <c:out value="${account.name}"/> (<fmt:message key="enum.accountRole.${account.role}"/>)
              </c:if>
          </c:if>
          <c:if test="${not empty logRecord.IP}">
            ,&nbsp;<c:out value="${logRecord.IP }"/>
          </c:if>
        </b>
      </td>
    </tr>
    <tr>
      <c:set var="actionType"  >${fn:replace(logRecord.actionType, " ", "_")}</c:set>
      <td><h2>
        <fmt:message key="enums.ActionType.${actionType}"/>&nbsp;
        <c:if test="${not empty logRecord.objectType && logRecord.objectType != 'PredefinedReport' 
                && logRecord.objectType != 'BirtReport' 
                || (logRecord.objectType == 'BirtReport' && logRecord.actionType != 'START_REPORT' && logRecord.actionType != 'COMPLETE_REPORT')}">
            <fmt:message key="enums.ObjectType.${logRecord.objectType}"/>
        </c:if>
      </h2></td>
    </tr>
    <tr>
      <td>
        <c:if test="${not empty logRecord.actionDescription}">
          <%@ include file="logRecordDescription.jsp"%>
        </c:if>
      </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr><td>&nbsp;</td></tr>
  </tbody>
</table>
