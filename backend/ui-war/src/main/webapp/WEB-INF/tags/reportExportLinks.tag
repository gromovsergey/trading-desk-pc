<%@ tag description="UI Tab" body-content="empty" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<%@ attribute name="formats" type="java.lang.String"%>

<c:if test="${empty pageScope.formats}">
    <c:set var="formats" value="CSV, Excel"/>
</c:if>

<div id="exportButtons" class="wrapper">
    <script type="text/javascript">
    
        UI.Reporting = $.extend(UI.Reporting, {
            exportReport: function(format) {
                var form = $('#exportButtons').closest('form');
                var action = form.attr('action');
                form.attr('action', 'cancellableRun.action?format=' + format);
                form.submit();
                form.attr('action', action);
            }
        });
    </script>

    <table>
        <tr>
            <td>
                <c:forEach var="format" items="${formats}" varStatus="formatStatus">
                    <c:set var="format" value="${fn:trim(format)}"/>
                    <ui:button message="report.export.to${format}" onclick="UI.Reporting.exportReport('${fn:toUpperCase(format)}');" type="button" />
                </c:forEach>
            </td>
            <s:if test="mayExceedExportLimit">
            <td>
                <ui:hint><fmt:message key="error.report.tooManyRows.exportLimited"><fmt:param>${exportMaxRows}</fmt:param></fmt:message></ui:hint>
             </td>
             </s:if>
        </tr>
    </table>
</div>
