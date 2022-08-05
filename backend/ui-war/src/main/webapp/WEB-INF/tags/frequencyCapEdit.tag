<%@tag language="java" body-content="empty" description="Renders frequency cap edit section" %>
<%-- TODO Replace the old version of this tag when all action will be migrated to Struts2 --%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<%@ attribute name="fcPropertyName" %>
<%@ attribute name="showLifeLimit" %>
<%@ attribute name="legendKey" %>
<%@ attribute name="id" %>

<c:if test="${empty fcPropertyName}">
    <c:set var="fcPropertyName" value="frequencyCap"/>
</c:if>
<c:if test="${empty showLifeLimit}">
    <c:set var="showLifeLimit" value="true"/>
</c:if>

<c:if test="${empty legendKey}">
    <c:set var="legendKey" value="frequency.caps"/>
</c:if>

<ui:section titleKey="${legendKey}" id="${id}">
  <s:hidden name="%{#attr.fcPropertyName}.id"/>
  <s:hidden name="%{#attr.fcPropertyName}.version"/>

  <ui:fieldGroup>

    <s:set var="unitList" value="#{'SECOND':getText('form.select.second'),
                                    'MINUTE':getText('form.select.minute'),
                                    'HOUR':getText('form.select.hour'),
                                    'DAY':getText('form.select.day'),
                                    'WEEK':getText('form.select.week')}"/>

    <ui:field labelKey="frequency.period" errors="${fcPropertyName},${fcPropertyName}.period,${fcPropertyName}.periodSpan,${fcPropertyName}.periodSpan.value">
        <table class="fieldAndAccessories">
            <tr>
                <td class="withField">
                    <ui:text textKey="frequency.one.impression.per"/>
                </td>
                <td class="withField">
                    <s:textfield name="%{#attr.fcPropertyName}.periodSpan.value" cssClass="smallLengthText" maxlength="13"/>
                </td>
                <td class="withField">
                    <s:select name="%{#attr.fcPropertyName}.periodSpan.unit" cssClass="smallLengthText1" list="unitList"/>
                </td>
            </tr>
        </table>
    </ui:field>

    <ui:field labelKey="frequency.window.limit" errors="${fcPropertyName}.windowCount,${fcPropertyName}.windowLength,${fcPropertyName}.windowLengthSpan,${fcPropertyName}.windowLengthSpan.value">
        <table class="fieldAndAccessories">
            <tr>
                <td class="withField">
                    <ui:text textKey="frequency.maximum"/>
                </td>
                <td class="withField">
                    <s:textfield name="%{#attr.fcPropertyName}.windowCount" cssClass="smallLengthText" maxlength="8"/>
                </td>
                <td class="withField">
                    <ui:text textKey="frequency.impressions.per"/>
                </td>
                <td class="withField">
                    <s:textfield name="%{#attr.fcPropertyName}.windowLengthSpan.value" cssClass="smallLengthText" maxlength="13"/>
                </td>
                <td class="withField">
                    <s:select name="%{#attr.fcPropertyName}.windowLengthSpan.unit" cssClass="smallLengthText1" list="unitList"/>
                </td>
            </tr>
        </table>
    </ui:field>

    <c:if test="${showLifeLimit}">
        <ui:field labelKey="frequency.life.limit" errors="${fcPropertyName}.lifeCount">
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField">
                        <ui:text textKey="frequency.maximum"/>
                    </td>
                    <td class="withField">
                        <s:textfield name="%{#attr.fcPropertyName}.lifeCount" cssClass="smallLengthText" maxlength="8"/>
                    </td>
                    <td class="withField">
                        <ui:text textKey="frequency.imression.per.user"/>
                    </td>
                </tr>
            </table>
        </ui:field>
    </c:if>

  </ui:fieldGroup>

</ui:section>
