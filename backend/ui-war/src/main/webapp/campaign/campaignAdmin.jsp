<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:pageHeadingByTitle/>
<ui:section>
    <ui:fieldGroup>
        <ui:field id="" labelKey="campaign.creative.group.showing.status">
            <ui:text textKey="campaign.displaystatus.pending_user"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<s:if test="ccgTree != null">
    <s:if test="ccgTree.isEmpty()">
        <div class="wrapper">
            <fmt:message key="campaign.nothing.display"/>
        </div>
    </s:if>
    <s:else>
    <table class="dataView" id="ccg">
        <thead>
          <tr>
          <th><fmt:message key="campaign"/></th>
          <th><fmt:message key="campaign.creative.group"/></th>
          <th><fmt:message key="campaign.impressions"/></th>
          <th><fmt:message key="campaign.dates"/></th></tr>
        </thead>
        <tbody>
          <c:forEach items="${ccgTree}" var="campaignEntry">
              <c:set var="ccgList" value="${campaignEntry.value}"/>
              <c:set var="ccgCount" value="${fn:length(ccgList)}"/>
              <s:set var="campaignDisplayStatus" value="#attr.campaignEntry.key.displayStatus"/>
              <c:choose>
                <c:when test="${ccgCount > 0}">
                    <c:forEach items="${ccgList}" var="ccg" varStatus="ccgIndex">
                        <tr>
                             <c:if test="${ccgIndex.index == 0}">
                                <td rowspan="${ccgCount}">
                                    <s:set var="campaignDisplayStatus" value="#attr.campaignEntry.key.displayStatus"/>
                                    <ui:displayStatus displayStatus="${campaignDisplayStatus}">
                                        <a href="view.action?id=${campaignEntry.key.id}"><c:out value="${campaignEntry.key.name}"/></a>
                                    </ui:displayStatus>
                                </td>
                             </c:if>
                             <td>
                                <ui:displayStatus displayStatus="${ccg.displayStatus}">
                                    <a href="group/view${ccg.ccgPageExtension}.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                                </ui:displayStatus>
                             </td>
                            <td class="number"><fmt:formatNumber value="${ccg.impressionsCount}" groupingUsed="true" maxFractionDigits="0"/></td>
                            <td class="date">${ccg.dates}</td>
                        </tr>
                    </c:forEach>
              </c:when>
              <c:otherwise>
                <tr>
                    <td>
                        <ui:displayStatus displayStatus="${campaignDisplayStatus}">
                            <a href="view.action?id=${campaignEntry.key.id}"><c:out value="${campaignEntry.key.name}"/></a>
                        </ui:displayStatus>
                    </td>
                    <td>&nbsp;</td>
                    <td class="number">0</td>
                    <td>&nbsp;</td>
                </tr>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </tbody>
    </table>
    </s:else>
</s:if>
