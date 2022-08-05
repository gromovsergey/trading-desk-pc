<%@ tag language="java" body-content="empty" %>

<%@ attribute name="fromDateFieldName" %>
<%@ attribute name="toDateFieldName" %>
<%@ attribute name="fromDateFieldId" %>
<%@ attribute name="toDateFieldId" %>
<%@ attribute name="idSuffix" %>
<%@ attribute name="options" %>
<%@ attribute name="fromDate" %>
<%@ attribute name="fromTime" %>
<%@ attribute name="toDate" %>
<%@ attribute name="toTime" %>
<%@ attribute name="fastChangeId" %>
<%@ attribute name="validateRange" %>
<%@ attribute name="onChange" %>
<%@ attribute name="currentPos" type="java.lang.Long" %>
<%@ attribute name="maxDate" %>
<%@ attribute name="timeZoneAccountId" %>
<%@ attribute name="showTime" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:if test="${empty pageScope.options}">
    <c:set var="options" value="Y T WTD MTD QTD YTD LW LM LQ LY R"/>
</c:if>

<c:if test="${empty pageScope.timeZoneAccountId}">
    <c:set var="timeZoneAccountId" value="0"/>
</c:if>

<c:choose>
    <c:when test="${pageScope.showTime}">
        <c:set var="fromDateName" value="${not empty fromDateFieldName ? fromDateFieldName : 'fromDate.datePart'}"/>
        <c:set var="toDateName" value="${not empty toDateFieldName ? toDateFieldName : 'toDate.datePart'}"/>
        <c:set var="fromTimeName" value="fromDate.timePart"/>
        <c:set var="toTimeName" value="toDate.timePart"/>
    </c:when>
    <c:otherwise>
        <c:set var="suffixedFromName" value="fromDate${pageScope.idSuffix}"/>
        <c:set var="suffixedToName" value="toDate${pageScope.idSuffix}"/>
        <c:set var="fromDateName" value="${not empty fromDateFieldName ? fromDateFieldName : suffixedFromName}"/>
        <c:set var="toDateName" value="${not empty toDateFieldName ? toDateFieldName : suffixedToName}"/>
    </c:otherwise>
</c:choose>

<c:set var="selectName" value="fastChangeId${pageScope.idSuffix}"/>
<c:set var="selectId" value="${fn:replace(pageScope.selectName, '.', '_')}"/>
<c:set var="fromDateId" value="${not empty fromDateFieldId ? fromDateFieldId : fn:replace(pageScope.fromDateName, '.', '_')}"/>
<c:set var="toDateId" value="${not empty toDateFieldId ? toDateFieldId : fn:replace(pageScope.toDateName, '.', '_')}"/>
<c:set var="fromTimeId" value="${fn:replace(pageScope.fromTimeName, '.', '_')}"/>
<c:set var="toTimeId" value="${fn:replace(pageScope.toTimeName, '.', '_')}"/>
<c:set var="timeZoneShiftId" value="timeZoneShift${pageScope.idSuffix}"/>
<c:set var="timePattern" value="${ad:timeFormatPattern()}"/>
<c:set var="ampms" value="${ad:ampms()}"/>

<%
java.util.Map optionsMap = new java.util.LinkedHashMap();
jspContext.setAttribute("optionsMap", optionsMap);
for(String s : ((String) jspContext.getAttribute("options")).split(" ")) {
    optionsMap.put(s, s);
}
%>


<script type="text/javascript">
    
    <c:if test="${not empty optionsMap['R']}">
        $.datepicker.setDefaults({
            changeMonth: true,
            changeYear: true,
            showOn: 'both',
            numberOfMonths: 2,
            <c:if test="${not empty currentPos && currentPos > 0}">beforeShow: function(){
                var rDatep   = setInterval(function(){
                    if ( $( "#ui-datepicker-div:visible").length ) { 
                        clearInterval(rDatep);
                        if ( $( "#ui-datepicker-div" ).children('.ui-datepicker-group').index($('.ui-datepicker-current-day').closest('.ui-datepicker-group')) === 0 ) {
                            $('.ui-datepicker-prev:visible').click();
                        }
                    }
                }, 20);
            },</c:if>
            <c:if test="${not empty maxDate}">maxDate: '${maxDate}',</c:if>
            buttonImageOnly: true,
            buttonImage: '/images/calendar.gif'
        });
    </c:if>
    
    $(function(){
        
        var settings = {
            'fastChangeId':     '${selectId}',
            'timeZoneShiftId':  '${timeZoneShiftId}',
            'fromDateId':       '${fromDateId}',
            'fromDateName':     '${fromDateName}',
            'toDateId':         '${toDateId}',
            'toDateName':       '${toDateName}',
            'fromTimeId':       '${fromTimeId}',
            'toTimeId':         '${toTimeId}'
        }
        
        <c:if test="${not empty optionsMap['R']}">
            $('#${fromDateId}, #${toDateId}').datepicker().on('change', function(){
                $('#${selectId}').val('R');
            });
            <c:if test="${pageScope.showTime}">
                $('#${fromTimeId}, #${toTimeId}').on('change', function(){
                    $('#${selectId}').val('R');
                });
            </c:if>
        </c:if>

        <c:if test="${pageScope.validateRange == 'true'}">
        $('#${selectId}').parents('form:first').submit(function(){
            <fmt:message var="invalidRange" key="report.invalid.range"/>
            <fmt:message var="invalidTime" key="report.invalid.time"/>
            <c:choose>
                <c:when test="${pageScope.showTime}">
            var t = UI.Daterange.checkTime('${fromTimeId}', '${timePattern}', ${ampms}, '${ad:escapeJavaScript(invalidTime)}') && UI.Daterange.checkTime('${toTimeId}', '${timePattern}', ${ampms}, '${ad:escapeJavaScript(invalidTime)}');
            return t && UI.Daterange.checkDateTimeRange('${fromDateId}', '${toDateId}', '${fromTimeId}', '${toTimeId}', '${timePattern}', '${ad:escapeJavaScript(invalidRange)}');
                </c:when>
                <c:otherwise>
            return UI.Daterange.checkDateRange('${fromDateId}', '${toDateId}', '${ad:escapeJavaScript(invalidRange)}');
                </c:otherwise>
            </c:choose>
        });
        </c:if>
    
        $('#${selectId}').change(function(){
            UI.Daterange.setDateRange(this.value, settings, '${pageScope.onChange}');
            UI.Daterange.setTimeRange(settings, '${ad:formatTimeString("00:00")}', '${ad:formatTimeString("23:59")}');
        });
        
        if ('${pageScope.fastChangeId}' == 'R'){
            $('#${fromDateId}').val('${pageScope.fromDate}');
            $('#${toDateId}').val('${pageScope.toDate}');
        }else{
            UI.Daterange.setTimeZoneShift('${pageScope.fastChangeId}', settings, ${timeZoneAccountId});
            UI.Daterange.setTimeRange(settings, '${ad:formatTimeString("00:00")}', '${ad:formatTimeString("23:59")}');
        }
        UI.Daterange.setInitialDateRange('${pageScope.fastChangeId}', settings);
    });
</script>

<table class="fieldAndAccessories">
    <tr>
        <td class="withField">
            <input type="hidden" id="${timeZoneShiftId}"/>
            <select id="${selectId}" name="${selectId}" class="halfwideCtl">
                <c:if test="${not empty optionsMap['TOT']}">
                    <option value='TOT'><fmt:message key="report.input.field.dateRange.totals"/></option>
                </c:if>
                <c:if test="${not empty optionsMap['Y']}">
                    <option value='Y'><fmt:message key="report.input.field.dateRange.yesterday"/></option>
                </c:if>
                <c:if test="${not empty optionsMap['T']}">
                    <option value='T'><fmt:message key="report.input.field.dateRange.today"/></option>
                </c:if>
                <c:if test="${not empty optionsMap['WTD']}">
                    <option value='WTD'><fmt:message key="report.input.field.dateRange.weekToDate"/></option>
                </c:if>
                <c:if test="${not empty optionsMap['MTD']}">
                    <option value='MTD'><fmt:message key="report.input.field.dateRange.monthToDate"/></option>
                </c:if>
                <c:if test="${not empty optionsMap['QTD']}">
                    <option value='QTD'><fmt:message key="report.input.field.dateRange.quarterToDate"/></option>
                </c:if>
                <c:if test="${not empty optionsMap['YTD']}">
                    <option value='YTD'><fmt:message key="report.input.field.dateRange.yearToDate"/></option>
                </c:if>
                <c:if test="${not empty optionsMap['LW']}">
                    <option value='LW'><fmt:message key="report.input.field.dateRange.lastWeek"/></option>
                </c:if>
                <c:if test="${not empty optionsMap['LM']}">
                    <option value='LM'><fmt:message key="report.input.field.dateRange.lastMonth"/></option>
                </c:if>
                <c:if test="${not empty optionsMap['LQ']}">
                    <option value='LQ'><fmt:message key="report.input.field.dateRange.lastQuarter"/></option>
                </c:if>
                <c:if test="${not empty optionsMap['LY']}">
                    <option value='LY'><fmt:message key="report.input.field.dateRange.lastYear"/></option>
                </c:if>
                <c:if test="${not empty optionsMap['R']}">
                    <option value='R'><fmt:message key="report.input.field.dateRange.range"/></option>
                </c:if>
            </select>
            <c:if test="${empty optionsMap['R']}">
                <input type="hidden" name="${fromDateName}" id="${fromDateId}"/>
                <input type="hidden" name="${toDateName}" id="${toDateId}"/>
            </c:if>
        </td>
        <c:if test="${not empty optionsMap['R']}">
            <td class="withField">
                <fmt:message key="report.input.field.dateRange.from"/>:
            </td>
            <td class="withField">
                <input type="text" size="10" name="${fromDateName}" id="${fromDateId}" readonly/>
            </td>
            <c:if test="${pageScope.showTime}">
                <td class="withField">
                    <input type="text" size="7" name="${pageScope.fromTimeName}" id="${pageScope.fromTimeId}" value="${pageScope.fromTime}"/>
                </td>
            </c:if>
            <td class="withField">
                <fmt:message key="report.input.field.dateRange.to"/>:
            </td>
            <td class="withField">
                <input type="text" size="10" name="${toDateName}" id="${toDateId}" readonly/>
            </td>
            <c:if test="${pageScope.showTime}">
                <td class="withField">
                    <input type="text" size="7" name="${pageScope.toTimeName}" id="${pageScope.toTimeId}" value="${pageScope.toTime}"/>
                </td>
            </c:if>
        </c:if>
    </tr>
</table>


