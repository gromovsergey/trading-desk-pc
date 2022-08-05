<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<ui:header>
    <ui:pageHeadingByTitle/>

    <c:if test="${ad:isPermitted('AuditLog.view', model)}">
        <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=58&id=${model.id}&contextName=${contextName}"/>
    </c:if>
</ui:header>

<c:set var="stateChannel" value="${model.stateChannel}"/>
<c:set var="countryChannel" value="${model.countryChannel}"/>
<ui:section>
    <ui:fieldGroup>

        <ui:field labelKey="channel.status">
            <s:url var="deleteUrl" action="admin/GeoChannel/delete.action"/>
            <s:url var="undeleteUrl" action="admin/GeoChannel/undelete.action"/>

            <ui:statusButtonGroup
                customClass="statusFieldGroup"
                descriptionKey="${model.displayStatus.description}"
                entity="${model}" restrictionEntity="GeoChannel"
                deletePage="${deleteUrl}" undeletePage="${undeleteUrl}">
                <c:if test="${model.qaStatus.letter == 'D'}">
                    <ui:qaStatusButons entity="${model}" restrictionEntity="GeoChannel"/>
                </c:if>
            </ui:statusButtonGroup>
        </ui:field>

        <c:choose>
            <c:when test="${model.geoType == 'CNTRY'}">
                <ui:field labelKey="channel.country">
                    <c:out value="${model.name}"/>
                </ui:field>

                <s:set var="children" value="%{model.childChannels.{? (#this.geoType.name() == 'STATE')}}"/>
                <c:if test="${not empty children}">
                    <ui:field label="${stateLabel}">
                        <c:forEach var="child" items="${children}">
                            <a href="view.action?id=${child.id}"><c:out value="${child.name}"/></a><br/>
                        </c:forEach>
                    </ui:field>
                </c:if>

                <s:set var="children" value="%{model.childChannels.{? (#this.geoType.name() == 'CITY')}}"/>
                <c:if test="${not empty children}">
                    <ui:field label="${cityLabel}">
                        <c:forEach var="child" items="${children}">
                            <a href="view.action?id=${child.id}"><c:out value="${child.name}"/></a><br/>
                        </c:forEach>
                    </ui:field>
                </c:if>
            </c:when>
            <c:when test="${model.geoType == 'STATE'}">
                <ui:field labelKey="channel.country">
                    <a href="view.action?id=${countryChannel.id}">
                        <c:out value="${countryChannel.name}"/>
                    </a>
                </ui:field>

                <ui:field label="${stateLabel}">
                    <c:out value="${model.name}"/>
                </ui:field>

                <c:if test="${not empty model.childChannels}">
                    <ui:field label="${cityLabel}">
                        <c:forEach var="child" items="${model.childChannels}">
                            <a href="view.action?id=${child.id}"><c:out value="${child.name}"/></a><br/>
                        </c:forEach>
                    </ui:field>
                </c:if>
            </c:when>
            <c:when test="${model.geoType == 'CITY'}">
                <ui:field labelKey="channel.country">
                    <a href="view.action?id=${countryChannel.id}">
                        <c:out value="${countryChannel.name}"/>
                    </a>
                </ui:field>

                <c:if test="${not empty stateChannel}">
                    <ui:field label="${stateLabel}">
                        <a href="view.action?id=${stateChannel.id}">
                            <c:out value="${stateChannel.name}"/>
                        </a>
                    </ui:field>
                </c:if>

                <ui:field label="${cityLabel}">
                    <c:out value="${model.name}"/>
                </ui:field>
            </c:when>
        </c:choose>
    </ui:fieldGroup>
</ui:section>

<%@ include file="/channel/channelStatsWrapper.jsp" %>
<%@ include file="/channel/campaignAssociationsView.jsp" %>
