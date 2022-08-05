<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<s:set var="listType" scope="request">${fn:replace(entityName, ".", "")}</s:set>
<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted0('BannedChannel.update')}">
        <s:url action="%{#attr.moduleName}/%{#request.listType}/edit" var="url"/>
        <ui:button message="form.edit" href="${url}"/>
    </c:if>
    <s:url action="admin/auditLog/view.action?type=%{#attr.entityName == 'TriggerList.NoTrackChannel' ? '36' : '37'}" var="logUrl"/>
    <ui:button message="form.viewLog" href="${logUrl}"/>
</ui:header>

<div class="wrapper">
    <table class="grouping fieldsets">
        <tr>
            <td class="singleFieldset">
                <ui:section titleKey="TriggerList.keywords">
                    <s:textarea name="keywordsText" readonly="true" cssClass="middleLengthText1"/>
                </ui:section>
            </td>
            <td class="singleFieldset">
                <ui:section titleKey="TriggerList.urls">
                    <s:textarea name="urlsText" readonly="true" cssClass="middleLengthText1"/>
                </ui:section>
            </td>
        </tr>
        <tr><td colspan="2">&nbsp<td/></tr>
        <tr>
            <td class="singleFieldset">
                <ui:section titleKey="TriggerList.urlKeywords">
                    <s:textarea name="urlKeywordsText" readonly="true" cssClass="middleLengthText1"/>
                </ui:section>
            </td>
            <td/>
        </tr>
    </table>
</div>