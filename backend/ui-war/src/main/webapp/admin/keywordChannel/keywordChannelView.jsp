<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <div class="groupOfButtons">
        <c:if test="${ad:isPermitted('KeywordChannel.update',model)}">
            <ui:button message="form.edit" href="/admin/KeywordChannel/edit.action?id=${id}"/>
        </c:if>
        <c:if test="${ad:isPermitted('Entity.viewLog',model)}">
            <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=28&id=${id}"/>
        </c:if>
    </div>
</ui:header>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="account.internalAccount">
            <ui:accountLink account="${account}"/>
        </ui:field>
        <ui:simpleField labelKey="channel.keywordChannel.keyword" value="${name}"/>
        <ui:field labelKey="channel.keywordChannel.type">
            <fmt:message key="enums.KeywordTriggerType.${triggerType.name}"/>
        </ui:field>
        <ui:field labelKey="channel.country" labelForId="countryCode" id="countryElem" errors="country.countryCode">
            <ui:countryLink countryCode="${country.countryCode}"/>
        </ui:field>
        <ui:field labelKey="channel.params">
            <c:choose>
                <c:when test="${empty behavioralParameters}">
                    <p class="rtext"><fmt:message key="channel.parametersAreNotDefined"/></p>
                </c:when>
                <c:otherwise>
                    <c:forEach var="bparam" items="${behavioralParameters}" end="0">
                        <ui:behavioralParamView bparam="${bparam}"/>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </ui:field>
        <ui:frequencyCapView/>

        <s:if test="isInternal()">
            <%@include file="/channel/channelCategoriesView.jsp" %>
        </s:if>
    </ui:fieldGroup>
</ui:section>

<%@ include file="/channel/channelStatsWrapper.jsp" %>

<%@ include file="/channel/campaignAssociationsView.jsp" %>
