<%@ tag description="UI Tab" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%@attribute name="totals" required="true" rtexprvalue="true" type="com.foros.session.channel.service.TotalByTriggerTypeTO"%>
<%@attribute name="triggerType" required="true" rtexprvalue="true" type="com.foros.model.channel.trigger.TriggerType"%>

<ui:fieldGroup>
    <c:choose>
        <c:when test="${totals.severalStatuses}">
            <ui:field labelKey="triggers.triggerQaStatus" cssClass="tableTriggers valignFix">
                <s:set var="qaStatusesMap"
                       value="#{'A' : #attr.totals.approved,
                                'H' : #attr.totals.pending,
                                'D' : #attr.totals.declined}.entrySet().{? value > 0}"/>

                <s:radio list="#qaStatusesMap"
                         name="triggersFilter.qaStatus"
                         listValue="getText('triggers.qaStatusWithCount.' + key, {value})"
                         listKey="key"
                         value="#qaStatusesMap[0].key"/>
            </ui:field>
        </c:when>
        <c:otherwise>
            <s:hidden name="pageKeywordsQaStatus"/>
            <ui:field labelKey="triggers.triggerQaStatus">
                <fmt:message key="triggers.qastatus.${totals.singleStatus}"/>
                <input type="hidden" name="triggersFilter.qaStatus" value="${attr.totals.singleStatus}" />
            </ui:field>
        </c:otherwise>
    </c:choose>
</ui:fieldGroup>
<input type="hidden" name="A_page" value="1" />
<input type="hidden" name="H_page" value="1" />
<input type="hidden" name="D_page" value="1" />

<input type="hidden" name="A_total" value="${totals.approved}" />
<input type="hidden" name="H_total" value="${totals.pending}" />
<input type="hidden" name="D_total" value="${totals.declined}" />

<input type="hidden" name="A_sortKey" value="hits"/>
<input type="hidden" name="H_sortKey" value="original_trigger"/>
<input type="hidden" name="D_sortKey" value="original_trigger"/>

<input type="hidden" name="A_sortOrder" value="DESC"/>
<input type="hidden" name="H_sortOrder" value="ASC"/>
<input type="hidden" name="D_sortOrder" value="ASC"/>

<s:hidden name="triggersTotal"/>
<s:hidden name="triggersFilter.page"/>
<s:hidden name="triggersFilter.triggerType" value="%{#attr.triggerType}"/>
<s:hidden name="triggersFilter.sortKey"/>
<s:hidden name="triggersFilter.sortOrder"/>
