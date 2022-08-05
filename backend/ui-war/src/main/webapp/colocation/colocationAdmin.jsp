<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="accountBean" value="account"/>

<ui:header>
    <ui:pageHeadingByTitle/>
    <ad:requestContext var="ispContext"/>
    <c:if test="${ad:isPermitted('Colocation.create', accountBean)}">
            <ui:button message="form.createNew" href="new.action${ad:accountParam('?accountId',ispContext.accountId)}" />
    </c:if>
</ui:header>

<s:if test="colocations">
    <s:set name="colocations" value="colocations"/>

    <display:table name="colocations" class="dataView" id="colocation">
        <display:setProperty name="basic.msg.empty_list" >
            <div class="wrapper">
                <fmt:message key="nothing.found.to.display"/>
            </div>
        </display:setProperty>
        <display:column property="id" titleKey="colocation.id" style="width:50px;"/>
        <display:column titleKey="colocation">
            <ui:displayStatus displayStatus="${colocation.displayStatus}">
                <a href="view.action?id=${colocation.id}">
                    <c:out value="${colocation.name}"/>
                </a>
            </ui:displayStatus>
        </display:column>
        <display:column titleKey="colocation.version" style="text-align:right;">
            <c:out value="${colocation.softwareVersion}"/>
        </display:column>
        <display:column titleKey="colocation.lastUpdate" style="text-align:right;">
            ${ad:formatTimeInterval(colocation.lastUpdate)}
        </display:column>
        <display:column titleKey="colocation.lastStats" style="text-align:right;">
            ${ad:formatTimeInterval(colocation.lastStats)}
        </display:column>
    </display:table>
</s:if>
