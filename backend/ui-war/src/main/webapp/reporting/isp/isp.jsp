<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    $(function () {
        $('#accountId').change(function () {
            updateByAccount($(this).val())
        });
        updateByAccount($("#accountId").val());
    });
    function updateByAccount(accountId) {
        UI.Data.Options.get('colocationsById', 'colocationId', {ispId:accountId}, ['form.all'], toggleFilters);
        UI.Daterange.setTimeZoneShift($('#fastChangeId').val(), {fromDateName:'dateRange.begin', toDateName:'dateRange.end'}, accountId);
    }
    function toggleFilters() {
        if ($('#colocationId option').size() > 2) {
            // more than one colocations associated with account
            $('#colocationFilter,#rptTypeFilter').show();
        } else {
            $('#colocationFilter,#rptTypeFilter').hide();
            $('input[type=radio][value=BY_DATE]').attr('checked', true);
        }
    }
</script>

<ui:pageHeadingByTitle/>

<s:form id="referrerForm" action="run" method="post" target="_blank">
    <ui:section titleKey="form.filter">
        <ui:fieldGroup>

            <c:choose>
                <c:when test="${not empty accounts or fn:length(colocations) > 1 }">
                    <ui:field id="rptTypeFilter" labelKey="report.input.field.reportType">
                        <label class="withInput">
                            <input type="radio" name="reportType" value="BY_DATE" checked>
                            <fmt:message key="report.input.field.reportType.byDate"/>
                        </label>
                        <label class="withInput">
                            <input type="radio" name="reportType" value="BY_COLOCATION">
                            <fmt:message key="report.input.field.reportType.byColocation"/>
                        </label>
                    </ui:field>
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="reportType" value="BY_DATE">
                </c:otherwise>
            </c:choose>

            <ui:field labelKey="report.input.field.dateRange">
                <ui:daterange
                        fromDateFieldName="dateRange.begin"
                        toDateFieldName="dateRange.end"
                        options="Y T WTD MTD QTD YTD LW LM LQ LY R"
                        fastChangeId="Y"
                        currentPos="1"
                        maxDate="+1d"
                        validateRange="true"/>
            </ui:field>

            <c:choose>
                <c:when test="${not empty accounts}">
                    <ui:field labelKey="report.input.field.objectType.Account" labelForId="accountId">
                        <select id="accountId" name="accountId" class="middleLengthText">
                            <c:forEach var="account" items="${accounts}">
                                <c:choose>
                                    <c:when test="${account.id == accountId}">
                                        <option value="${account.id}" selected="true">
                                            <c:out value="${account.name}"/></option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${account.id}"><c:out value="${account.name}"/></option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </ui:field>
                </c:when>
                <c:otherwise>
                    <input type="hidden" id="accountId" name="accountId" value=${accountId}>
                </c:otherwise>
            </c:choose>

            <c:if test="${not empty accounts or fn:length(colocations) > 1}">
                <ui:field id="colocationFilter" labelKey="report.input.field.colocation" labelForId="colocationId">
                    <select id="colocationId" name="colocationId" class="middleLengthText">
                        <option value=""><fmt:message key="form.all"/></option>
                        <c:forEach var="colocation" items="${colocations}">
                            <option value="${colocation.id}"><c:out value="${colocation.name}"/></option>
                        </c:forEach>
                    </select>
                </ui:field>
            </c:if>

            <ui:field cssClass="withButton">
                <ui:button message="report.button.runReport"/>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>
</s:form>
