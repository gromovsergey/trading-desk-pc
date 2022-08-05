<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    $(function(){
        $('#accountId').change(function() {
            updateByAccount($(this).val());
        });
        var accountId = $("#accountId").val();
        if (accountId != null) {
            updateByAccount(accountId);
        }
    });
    function updateByAccount(accountId) {
        UI.Data.Options.get('advertisingCmpChannelsByAccount', 'channelId', {accountId:$('#accountId').val()}, ['form.all']);
        UI.Daterange.setTimeZoneShift($('#fastChangeId').val(), {fromDateName:'dateRange.begin', toDateName:'dateRange.end'}, accountId);
    }
</script>

<ui:pageHeadingByTitle/>

<s:form id="channelUsageForm" action="run" method="post" target="_blank">
    <%@include file="../enableDoubleSubmit.jsp"%>
    <ui:section titleKey="form.filter">
    <ui:fieldGroup>
        <ui:field labelKey="report.input.field.dateRange">
            <ui:daterange
                    fromDateFieldName="dateRange.begin"
                    toDateFieldName="dateRange.end"
                    options="Y T WTD MTD LW LM R QTD LQ LY YTD"
                    fastChangeId="Y"
                    currentPos="1"
                    maxDate="+1d"
                    validateRange="true"/>
        </ui:field>

        <s:if test="%{canSelectAccount()}">
            <ui:field labelKey="report.input.field.channelMarketplaceAccount" labelForId="accountId">
                <select id="accountId" name="accountId" class="middleLengthText">
                    <c:forEach var="account" items="${accounts}">
                        <c:choose>
                            <c:when test="${account.id == accountId}">
                                <option value="${account.id}" selected="true"><c:out value="${account.name}"/></option>
                            </c:when>
                            <c:otherwise>
                                <option value="${account.id}"><c:out value="${account.name}"/></option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </ui:field>
        </s:if>
        <s:else>
            <ui:simpleField labelKey="report.input.field.account" value="${account.name}"/>
            <input type="hidden" id="accountId" name="accountId" value="${account.id}"/>
        </s:else>

        <ui:field cssClass="valignFix" labelKey="report.input.field.detailLevel">
             <s:radio list="@com.foros.session.reporting.channelUsage.DetailLevel@values()"
                      name="detailLevel"  
                      listValue="getText(getNameKey())"/>
        </ui:field>

        <s:if test="%{canSelectChannel()}">
            <ui:field labelKey="report.input.field.channel" labelForId="channelPair">
                <select id="channelId" name="channelId" class="middleLengthText"/>
            </ui:field>
        </s:if>
        <s:else>
            <input type="hidden" id="channelId" name="channelId" value="${channel.id}"/>
            <ui:simpleField labelKey="report.input.field.channel" value="${channel.name}"/>
        </s:else>

        <ui:field cssClass="withButton">
            <ui:button id="submitButton" message="report.button.runReport"/>
        </ui:field>
    </ui:fieldGroup>
    </ui:section>
</s:form>

