<%@ page import="com.foros.model.channel.Channel" %>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="channelType" value="<%=Channel.CHANNEL_TYPE_CATEGORY%>"/>
<c:set var="entityType" value="8"/>
<c:set var="entityBean" value="${model}"/>

<ui:header>
    <ui:pageHeadingByTitle />
    <c:if test="${ad:isPermittedAny('CategoryChannel.update,CategoryChannel.view')}">
    <table class="grouping groupOfButtons">
        <tr>
            <c:if test="${ad:isPermitted('CategoryChannel.update', model)}">
            <td>
                <ui:button message="form.edit" href="/admin/CategoryChannel/edit.action?id=${model.id}" />
            </td>
            </c:if>
            <c:if test="${ad:isPermitted0('CategoryChannel.view')}">
            <td>
                <%@ include file="../../auditLog/viewLogButton.jspf" %>
            </td>
            </c:if>
        </tr>
    </table>
    </c:if>
</ui:header>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
    <s:fielderror fieldName="name"/>
</ui:errorsBlock>

<ui:section>
    <ui:fieldGroup>
        <c:if test="${ad:isPermitted0('CategoryChannel.update')}">
            <ui:localizedField id="name" labelKey="channel.defaultName" value="${model.name}"
                  resourceKey="${model.id}"
                  resourceUrl="/admin/resource/CategoryChannel/"
                  entityName="CategoryChannel"/>
        </c:if>
        <c:if test="${not ad:isPermitted0('CategoryChannel.update')}">
            <ui:simpleField labelKey="channel.defaultName" value="${model.name}"/>
        </c:if>

        <ui:field labelKey="channel.account">
          <ui:accountLink id="${model.account.id}" role="${model.account.role}" name="${accountName}"
                          displayStatus="${model.account.displayStatus}"/>
        </ui:field>

        <c:if test="${not empty parentLocations}">
          <ui:field labelKey="channel.categoryChannel.location">
              <div class="path">
                  <c:forEach items="${parentLocations}" var="parentLocation" varStatus="indexId">
                      <c:if test="${indexId.count > 0}">
                          <span class='delimiter'>/</span>
                      </c:if>
                      <a href="/admin/CategoryChannel/view.action?id=${parentLocation.id}"><c:out value="${parentLocation.name}"/></a>
                  </c:forEach>
              </div>
          </ui:field>
        </c:if>

        <c:choose>
            <c:when test="${model.isHiddenChannel}">
                <fmt:message key="yes" var="isHiddenMessage"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="no" var="isHiddenMessage"/>
            </c:otherwise>
        </c:choose>

        <ui:simpleField labelKey="channel.hidden" value="${isHiddenMessage}"/>

        <c:if test="${not empty model.newsgateCategoryName}">
            <ui:simpleField labelKey="channel.newsgateCategoryName" value="${model.newsgateCategoryName}"/>
        </c:if>

        <ui:field labelKey="channel.status">
          <ui:statusButtonGroup
              descriptionKey="${model.displayStatus.description}"
              entity="${model}" restrictionEntity="CategoryChannel"
              activatePage="activate.action" inactivatePage="inactivate.action"
              deletePage="delete.action" undeletePage="undelete.action"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<ui:header styleClass="level2">
    <h2><fmt:message key="channel.categoryChannel.children"/></h2>
    <c:if test="${ad:isPermitted('CategoryChannel.create', model)}">
          <a class="button" href="/admin/CategoryChannel/edit.action?parentChannelId=${model.id}"><fmt:message key="form.createNew"/></a>
    </c:if>
</ui:header>

<table class="dataViewSection">
        <tr class="bodyZone">
            <td>
                <display:table name="childrenChannels" class="dataView" id="channel">
                    <display:setProperty name="basic.msg.empty_list" >
                      <div class="wrapper">
                          <fmt:message key="nothing.found.to.display"/>
                      </div>
                    </display:setProperty>
                
                    <display:column titleKey="channel.search.account">
                        <ui:accountLink id="${channel.accountId}" name="${channel.accountName}"
                                        displayStatus="${channel.accountStatus}"/>
                    </display:column>
                
                    <display:column titleKey="CategoryChannel.entityName">
                        <ui:displayStatus cssClass="indentLevel_${channel.level - 1}" displayStatus="${channel.displayStatus}">
                            <a href="/admin/CategoryChannel/view.action?id=${channel.id}"><c:out value="${channel.name}"/></a>
                        </ui:displayStatus>
                    </display:column>
                </display:table>
            </td>
        </tr>
</table>
