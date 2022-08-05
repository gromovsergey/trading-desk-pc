<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<ui:section>
    <ui:fieldGroup>
        <c:if test="${ad:isPermitted('OptionGroup.update', model)}">
            <ui:localizedField id="name" labelKey="defaultName" value="${defaultName}"
                resourceKey="OptionGroup-name.${id}"
                resourceUrl="/admin/resource/htmlName/"
                entityName="OptionGroup"/>
        </c:if>
        <c:if test="${not ad:isPermitted('OptionGroup.update', model)}">
            <ui:simpleField labelKey="defaultName" value="${defaultName}"/>
        </c:if>

        <c:if test="${ad:isPermitted('OptionGroup.update', model) && not empty defaultLabel}">
            <ui:localizedField id="label" labelKey="Option.defaultTooltip" value="${defaultLabel}"
                resourceKey="OptionGroup-label.${id}"
                resourceUrl="/admin/resource/htmlName/"
                entityName="OptionGroup"/>
        </c:if>
        <c:if test="${not ad:isPermitted('OptionGroup.update', model)}">
            <ui:simpleField labelKey="Option.defaultTooltip" value="${defaultLabel}"/>
        </c:if>

        <ui:simpleField labelKey="OptionGroup.availability" value="${availabilityKey}"/>

        <ui:simpleField labelKey="OptionGroup.collapsibility" value="${collapsabilityKey}"/>

    </ui:fieldGroup>
</ui:section>

<c:if test="${ad:isPermitted('OptionGroup.update', model)}">
    <ui:header styleClass="level2">
        <ui:button message="Option.create" href="/admin/Option/new.action?optionGroupId=${id}" />
    </ui:header>
</c:if>

<c:choose>
    <c:when test="${not empty model.options}">
        <table class="dataView">
            <thead>
            <tr>
                <th><fmt:message key="CreativeSize.options.name"/></th>
                <th><fmt:message key="CreativeSize.options.group.token"/></th>
                <th><fmt:message key="CreativeSize.options.group.defaultValue"/></th>
            </tr>
            </thead>
            <tbody>
                <c:set var="label"><fmt:message key="CreativeSize.options.group.type.${model.type.toString()}"/></c:set>
                <ui:optionsView label="${label}" optionGroup="${model}" groupInfo="false"/>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </c:otherwise>
</c:choose>
