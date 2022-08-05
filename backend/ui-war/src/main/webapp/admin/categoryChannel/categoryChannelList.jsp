<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:header>
    <ui:pageHeadingByTitle />
    <c:if test="${ad:isPermitted0('CategoryChannel.create')}">
    <ui:button message="form.createNew" href="/admin/CategoryChannel/edit.action" />
    </c:if>
</ui:header>

<c:if test="${ad:isPermitted0('CategoryChannel.view')}">
    <c:set var="canViewAccount" value="${ad:isPermitted('Account.view', 'Internal')}"/>
    <display:table name="channels" class="dataView" id="channel">
        <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
      </display:setProperty>

      <display:column titleKey="channel.search.account">
            <ui:displayStatus displayStatus="${channel.accountStatus}">
              <c:choose>
                  <c:when test="${canViewAccount}">
                      <a href="/admin/internal/account/view.action?id=${channel.accountId}">
                          <c:out value="${channel.accountName}"/>
                      </a>
                  </c:when>
                  <c:otherwise>
                      <c:out value="${channel.accountName}"/>
                  </c:otherwise>
              </c:choose>
            </ui:displayStatus>
      </display:column>

      <display:column titleKey="channel.search.channel">
            <ui:displayStatus displayStatus="${channel.displayStatus}" cssClass="indentLevel_${channel.level - 1}">
                <a href="/admin/CategoryChannel/view.action?id=${channel.id}"><c:out value="${channel.name}"/></a>
            </ui:displayStatus>
      </display:column>
    </display:table>
</c:if>
