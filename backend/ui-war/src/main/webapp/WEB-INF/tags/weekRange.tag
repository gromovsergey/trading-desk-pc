<%@ tag description="UI weekRange" body-content="empty" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"    prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>


<%@ attribute name="id" required="true" %>
<%@ attribute name="name" %>
<%@ attribute name="occupiedRanges" %>
<%@ attribute name="occupiedRangesName" %>
<%@ attribute name="editableRanges" %>
<%@ attribute name="editableRangesName" %>
<%@ attribute name="readonly" type="java.lang.Boolean" %>
<%@ attribute name="iconRunningKey" %>
<%@ attribute name="iconNotRunningKey" %>
<%@ attribute name="iconAvailableKey" %>
<%@ attribute name="iconConflictedKey" %>
<%@ attribute name="iconOccupiedKey" %>

<c:if test="${not empty name && empty occupiedRangesName}">
    <c:set var="occupiedRanges" scope="page"><s:property value="[#attr.name]"/></c:set>
</c:if>

<c:if test="${not empty pageScope.occupiedRangesName}">
    <c:set var="occupiedRanges" scope="page"><s:property value="[#attr.occupiedRangesName]"/></c:set>
</c:if>

<c:if test="${not empty pageScope.editableRangesName}">
    <c:set var="editableRanges" scope="page"><s:property value="[#attr.editableRangesName]"/></c:set>
</c:if>

<c:if test="${empty pageScope.editableRanges and pageScope.readonly}">
    <c:set var="editableRanges" value="0:0" />
</c:if>

<script type="text/javascript">
    $().ready(function(){
        var weekRangeDiv = $('#${pageScope.id}');
        weekRangeDiv.WeekRange('${pageScope.name}', '${pageScope.occupiedRanges}', '${pageScope.editableRanges}', ${pageScope.readonly ? true : false});
    })

    $.localize('dayOfWeek.short.1', '<fmt:message key="dayOfWeek.short.1"/>');
    $.localize('dayOfWeek.short.2', '<fmt:message key="dayOfWeek.short.2"/>');
    $.localize('dayOfWeek.short.3', '<fmt:message key="dayOfWeek.short.3"/>');
    $.localize('dayOfWeek.short.4', '<fmt:message key="dayOfWeek.short.4"/>');
    $.localize('dayOfWeek.short.5', '<fmt:message key="dayOfWeek.short.5"/>');
    $.localize('dayOfWeek.short.6', '<fmt:message key="dayOfWeek.short.6"/>');
    $.localize('dayOfWeek.short.7', '<fmt:message key="dayOfWeek.short.7"/>');

</script>
<c:if test="${!pageScope.readonly}">
    <div>
        <span class="infos">
            <fmt:message key="deliverySchedule.info.message1"/><br/>
            <fmt:message key="deliverySchedule.info.message2"/>
        </span>
    </div>
</c:if>

<div id="${pageScope.id}_outer" class="timeRange_outer">
    <div id="${pageScope.id}"></div>
    <div class="fixing"></div>
    <c:if test="${not empty pageScope.iconRunningKey or not empty pageScope.iconNotRunningKey 
                or not empty pageScope.iconAvailableKey or not empty pageScope.iconConflictedKey}">
        <div class="typesContainer">
            <c:if test="${not empty pageScope.iconRunningKey}">
                <div class="type running">
                    <div class="icon"></div>
                    <span class="simpleText"><fmt:message key="${pageScope.iconRunningKey}" /></span>
                </div>
            </c:if>
            <c:if test="${not empty pageScope.iconAvailableKey}">
                <div class="type available">
                    <div class="icon"></div>
                    <span class="simpleText"><fmt:message key="${pageScope.iconAvailableKey}" /></span>
                </div>
            </c:if>
            <c:if test="${not empty pageScope.iconNotRunningKey}">
                <div class="type notRunning">
                    <div class="icon"></div>
                    <span class="simpleText"><fmt:message key="${pageScope.iconNotRunningKey}" /></span>
                </div>
            </c:if>
            <c:if test="${not empty pageScope.iconOccupiedKey}">
                <div class="type occupied">
                    <div class="icon"></div>
                    <span class="simpleText"><fmt:message key="${pageScope.iconOccupiedKey}" /></span>
                </div>
            </c:if>
            <c:if test="${not empty pageScope.iconConflictedKey}">
                <div class="type conflicted">
                    <div class="icon"></div>
                    <span class="simpleText"><fmt:message key="${pageScope.iconConflictedKey}" /></span>
                </div>
            </c:if>
        </div>
    </c:if>
    <div class="fixing"></div>
</div>
