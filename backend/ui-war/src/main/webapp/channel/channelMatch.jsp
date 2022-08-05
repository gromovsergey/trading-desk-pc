<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<s:form action="channelMatch" method="GET">
    <ui:pageHeadingByTitle/>

    <ui:section titleKey="form.search">
        <ui:fieldGroup>

            <ui:field labelKey="channel.test.url" labelForId="url">
                <s:textfield name="url" id="url" cssClass="middleLengthText1" maxlength="300"/>
            </ui:field>

            <ui:field labelKey="channel.test.pageKeywords" labelForId="keywords">
                <s:textfield name="keywords" id="keywords" cssClass="middleLengthText1" maxlength="200"/>
            </ui:field>

            <ui:field labelKey="channel.test.uid" labelForId="uid">
                <s:textfield name="uid" id="uid" cssClass="middleLengthText1" maxlength="200"/>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button message="form.search" type="submit"/>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>
</s:form>

<ui:errorsBlock>
    <s:fielderror><s:param value="'serviceError'"/></s:fielderror>
</ui:errorsBlock>

<s:if test="matchedChannels != null">
    <h2><fmt:message key="channel.test.results"/></h2>

    <display:table name="matchedChannels" class="dataView" id="channel">
        <display:setProperty name="basic.msg.empty_list">
            <div class="wrapper">
                <fmt:message key="nothing.found.to.display"/>
            </div>
        </display:setProperty>
        <display:column property="id" titleKey="channel.test.results.id"/>
        <display:column titleKey="channel.test.results.name">
            <c:choose>
                <c:when test="${ad:isPermitted('Channel.view', channel)}">
                    <a href="/admin/channel/view.action?id=${channel.id}"><c:out value="${channel.name}"/></a>
                </c:when>
                <c:otherwise><c:out value="${channel.name}"/></c:otherwise>
            </c:choose>
        </display:column>
    </display:table>
</s:if>

<s:if test="historyChannels != null">
    <h2><fmt:message key="channel.test.results.history"/></h2>

    <display:table name="historyChannels" class="dataView" id="channel">
        <display:setProperty name="basic.msg.empty_list">
            <div class="wrapper">
                <fmt:message key="nothing.found.to.display"/>
            </div>
        </display:setProperty>
        <display:column property="id" titleKey="channel.test.results.id"/>
        <display:column titleKey="channel.test.results.name">
            <c:choose>
                <c:when test="${ad:isPermitted('Channel.view', channel)}">
                    <a href="/admin/channel/view.action?id=${channel.id}"><c:out value="${channel.name}"/></a>
                </c:when>
                <c:otherwise><c:out value="${channel.name}"/></c:otherwise>
            </c:choose>
        </display:column>
    </display:table>
</s:if>
