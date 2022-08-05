<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    $().ready(function() {
        $('#accountId').change(function() {
            if ($('#accountId').val() == '') {
                $('#siteId, #tagId').prop({disabled : true});
                UI.Data.Options.replaceWith('siteId', ['form.all']);
            } else {
                $('#siteId, #tagId').prop({disabled : false});
                UI.Data.Options.get('sitesById', 'siteId', {publisherId:$(this).val()}, ['form.all']);
            }
            UI.Data.Options.replaceWith('tagId', ['form.all']);
        });

        $('#siteId').change(function() {
            UI.Data.Options.get('tagsById', 'tagId', {siteId:$(this).val()},
                    [
                        {key:'form.all', condition:UI.Data.Options.Conditions.ifNotUniqueCondition}
                    ]);
        });

    });
</script>

<ui:pageHeadingByTitle/>

<s:form id="siteChannelsForm" action="run" method="post" target="_blank">
    <%@include file="../enableDoubleSubmit.jsp"%>

    <ui:section titleKey="form.filter">
        <ui:fieldGroup>

            <ui:field labelKey="report.input.field.dateRange" labelForId="fastChangeId">
                <ui:daterange
                        fromDateFieldName="dateRange.begin"
                        toDateFieldName="dateRange.end"
                        options="Y T WTD MTD LW LM R"
                        fastChangeId="Y"
                        currentPos="1"
                        maxDate="+1d"
                        validateRange="true"/>
            </ui:field>

            <ui:field labelKey="report.input.field.publisherAccount" labelForId="accountPair">
                <c:choose>
                    <c:when test="${not empty accounts}">
                        <select id="accountId" name="accountId" class="middleLengthText">
                            <option value=""><fmt:message key="form.all"/></option>
                            <c:forEach items="${accounts}" var="account">
                                <option value="${account.id}" ${account.id == accountId ? "selected" : "" }>
                                    <c:out value="${account.name}"/></option>
                            </c:forEach>
                        </select>
                    </c:when>
                    <c:otherwise>
                        <span class="errors"><fmt:message key="report.account.error"/></span>
                    </c:otherwise>
                </c:choose>
            </ui:field>

            <ui:field labelKey="report.input.field.site" labelForId="sitePair">
                <select id="siteId" name="siteId" class="middleLengthText" disabled="true">
                    <option value=""><fmt:message key="form.all"/></option>
                </select>
            </ui:field>

            <ui:field labelKey="report.input.field.tag" labelForId="tagsPair">
                <select id="tagId" name="tagId" class="middleLengthText" disabled="true">
                    <option value=""><fmt:message key="form.all"/></option>
                </select>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button id="submitButton" message="report.button.runReport"/>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>
</s:form>
