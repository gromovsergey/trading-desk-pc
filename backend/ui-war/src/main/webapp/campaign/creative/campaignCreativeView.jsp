<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="showNewMessage && creativeGroup.status.letter != 'D'">
    <ui:section>
        <ui:text textKey="creative.link.sucessfully.added"/>
            <fmt:message key="creative.link.nextAction">
                <fmt:param>
                    <ui:button message="creative.link.another" href="new.action?ccgId=${creativeGroup.id}" />
                </fmt:param>
                <fmt:param>
                    <ui:button message="creative.group.overview" href="${_context}/campaign/group/view${creativeGroup.ccgType.pageExtension}.action?id=${creativeGroup.id}" />
                </fmt:param>
            </fmt:message>
    </ui:section>
</s:if>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted('AdvertiserEntity.update', model)}">
        <ui:button message="form.edit" href="edit.action?id=${id}" />
    </c:if>
    <c:if test="${ad:isPermitted('AdvertiserEntity.view', creative)}">
            <ui:button message="creative.view.creative" href="${_context}/creative/view.action?id=${creative.id}" />
    </c:if>
</ui:header>

<c:if test="${not ad:isPermitted('AdvertiserEntity.view', creative)}">
    <div class="wrapper">
        <span class="errors">
            <fmt:message key="creative.is.deleted"/>
        </span>
    </div>
</c:if>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>
    
<ui:section titleKey="form.main" >
    <ui:fieldGroup>
        <ui:field labelKey="creative.status">
            <ui:statusButtonGroup
                descriptionKey="${displayStatus.description}"
                entity="${model}" restrictionEntity="AdvertiserEntity"
                activatePage="activate.action"
                inactivatePage="inactivate.action"
                deletePage="delete.action"
                undeletePage="undelete.action"
                 />
        </ui:field>
        <s:if test="canUpdateWeight() and weight != null">
            <ui:field labelKey="weight">
                <fmt:formatNumber value="${weight}" groupingUsed="true" maxFractionDigits="0"/>
            </ui:field>
        </s:if>
        <ui:frequencyCapView/>
    </ui:fieldGroup>
</ui:section>
  
<s:if test="creative.id != null">
    <ui:section titleKey="creative.preview" >
        <div>
            <ui:creativePreview creativeId="${creative.id}"/>
        </div>
    </ui:section>
</s:if>
