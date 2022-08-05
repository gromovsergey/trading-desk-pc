<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
    function getAccountId() {
        return UI.Util.Pair.fetchId($('#accountId').val());
    }
    $().ready(function() {
        UI.Daterange.setTimeZoneShift($('#fastChangeId').val(), '', getAccountId());

        $('#siteId').change(function() {
            $('#tagId').prop({disabled : (this.selectedIndex == 0)});
            UI.Data.Options.get('tagsById', 'tagId', {siteId:this.value}, ['form.all']);
        });


    <c:if test="${internal}">
        $('#accountId').change(function() {
            UI.Data.Options.get('sitesById', 'siteId', {publisherId:this.value}, ['form.all']);
            UI.Data.Options.replaceWith('tagId', ['form.all']);

            var fastChangeId = $('#fastChangeId').val();
            UI.Daterange.setTimeZoneShift(fastChangeId, '', getAccountId());
        });
    </c:if>

    });
</script>

<ui:pageHeadingByTitle/>

<c:set var="publisherContext" value="${requestContexts.publisherContext}"/>
<s:form id="inventoryEstimationForm" action="run" method="post" target="_blank">
	<%@include file="../enableDoubleSubmit.jsp"%>
    <input type="hidden" name="reportName" value="inventoryEstimation"/>
    <ui:section titleKey="form.filter">
        <ui:fieldGroup>
            <ui:field labelKey="report.input.field.dateRange" labelForId="fastChangeId">
                <ui:daterange options="Y T WTD MTD QTD YTD LW LM LQ LY R" fastChangeId="Y" currentPos="1" maxDate="+1d" validateRange="true" fromDateFieldName="dateRange.begin" toDateFieldName="dateRange.end"/>
            </ui:field>

            <c:choose>
                <c:when test="${not publisherContext.set}">
                    <c:choose>
                        <c:when test="${not empty accounts}">
                            <ui:field labelKey="report.input.field.publisherAccount" labelForId="accountPair">
                                <select id="accountId" name="accountId" class="middleLengthText">
                                   <c:forEach items="${accounts}" var="account">
                                       <option value="${account.id}" ${account.id == accountId ? "selected" : "" }><c:out value="${account.name}"/></option>
                                   </c:forEach>
                                </select>
                            </ui:field>
                        </c:when>
                        <c:otherwise>
                            <ui:field labelKey="report.input.field.publisherAccount">
                                <span class="errors"><fmt:message key="report.account.error"/></span>
                            </ui:field>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <ad:requestContext var="publisherContext"/>
                    <input type="hidden" id="accountId" name="accountId" value="${publisherContext.accountId}"/>
                </c:otherwise>
            </c:choose>

            <ui:field labelKey="report.input.field.site">
                <select id="siteId" name="siteId" class="middleLengthText">
                    <option value=""><fmt:message key="form.all"/></option>
                    <c:forEach var="site" items="${sites}">
                        <option value="${site.id}" ${site.id == siteId ? "selected" : "" }><c:out value="${site.name}"/></option>
                    </c:forEach>
                </select>
            </ui:field>

            <ui:field labelKey="report.input.field.tag">
                <select id="tagId" name="tagId" class="middleLengthText">
                    <option value=""><fmt:message key="form.all"/></option>
                    <c:forEach var="row" items="${tags}">
                        <option value="${row.id}" ${row.id == tagId ? "selected" : "" }><c:out value="${row.name}"/></option>
                    </c:forEach>
                </select>
            </ui:field>

            <ui:field labelKey="report.input.field.reservedPremium">
                <input type="text" name="reservedPremium" value="0" id="reservedPremium" maxlength="5"/>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button id="submitButton" message="report.button.runReport"/>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>
</s:form>
