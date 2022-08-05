<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%pageContext.setAttribute("linefeed", "\r\n"); %>

<ui:header>
    <ui:pageHeadingByTitle/>
    <c:if test="${ad:isPermitted0('BehavioralParams.update') or ad:isPermitted0('BehavioralParams.updateStatus')}">
        <table class="grouping groupOfButtons">
            <tr>
                <c:if test="${ad:isPermitted0('BehavioralParams.update')}">
                    <td>
                        <s:set var="editHref">
                            <s:url action="admin/behavioralParameters/edit" >
                                <s:param name="id" value="%{id}"/>
                            </s:url>
                        </s:set>
                        <ui:button message="form.edit" href="${editHref}"/>
                    </td>
                </c:if>
                <c:if test="${ad:isPermitted0('BehavioralParams.updateStatus')}">
                    <td>
                        <c:if test="${usageCount == 0}">
                            <c:set var="deleteMessage"
                                   value="if (!confirm('${ad:formatMessage('confirmDelete')}')) {return false;}"/>
                        </c:if>
                        <c:if test="${usageCount != 0}">
                            <fmt:message key="channel.params.deleteInUse" var="alertMsg"><fmt:param
                                value="${usageCount}"/></fmt:message>
                            <c:set var="deleteMessage" value="alert('${alertMsg}'); return false;"/>
                        </c:if>

                        <ui:postButton message="form.delete" href="delete.action"
                                       entityId="${id}"
                                       onclick="${deleteMessage}" />
                    </td>
                </c:if>
            </tr>
        </table>
    </c:if>
</ui:header>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
    <s:fielderror><s:param value="'error'"/></s:fielderror>
</ui:errorsBlock>

<ui:section>
    <ui:fieldGroup>
        <ui:simpleField labelKey="channel.params.threshold" value="${threshold}"/>
        <fmt:message key="channel.params.channels" var="usageSuffix"/>
        <ui:simpleField labelKey="channel.params.usage" value="${usageCount} ${usageSuffix}"/>
    </ui:fieldGroup>
</ui:section>

<ui:section titleKey="channel.params">
    <ui:behavioralParamsView isChannelBehaviouralParamList="true"/>
</ui:section>


