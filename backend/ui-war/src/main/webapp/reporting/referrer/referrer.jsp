<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    $(function() {
        $('#accountId').change(function() {
            UI.Data.Options.get('sitesById', 'siteId', {publisherId:$(this).val()}, null, onAccountLoaded);
        });

        $('#siteId').change(function() {
            updateTags();
        });
    });

    function updateTags(){
        UI.Data.Options.get('tagsById', 'tagId', {siteId:$('#siteId').val(), hideDeleted:true}, ['form.all']);
    }

    function onAccountLoaded() {
        updateTags();
        $('#submitButton').prop({disabled : false});
    }
</script>
<ui:pageHeadingByTitle/>

<s:form id="referrerForm" action="run" method="post" target="_blank">
    <s:hidden name="accountId"/>
    <s:hidden name="useImpala"/>
    <%@include file="../enableDoubleSubmit.jsp"%>

    <ui:section titleKey="form.filter">
        <ui:fieldGroup>

            <ui:field labelKey="report.input.field.dateRange">
                <ui:daterange
                        fromDateFieldName="dateRange.begin"
                        toDateFieldName="dateRange.end"
                        options="Y T WTD MTD LW LM R"
                        fastChangeId="Y"
                        currentPos="1"
                        maxDate="+1d"
                        validateRange="true"/>
            </ui:field>

            <ui:field labelKey="report.input.field.site">
                <s:if test="siteId == null && tagId == null">
                    <select id="siteId" name="siteId" class="middleLengthText">
                        <c:forEach var="site" items="${sites}">
                            <option value="${site.id}"><c:out value="${site.name}"/></option>
                        </c:forEach>
                    </select>
                </s:if>
                <s:else>
                    <s:property value="siteName"/>
                    <s:hidden name="siteId"/>
                </s:else>
            </ui:field>

            <ui:field labelKey="report.input.field.tag">
                <s:if test="tagId == null">
                    <s:select name="tagId" list="tags" cssClass="middleLengthText" id="tagId"
                              listKey="id" listValue="name"
                              headerKey="" headerValue="%{getText('form.all')}"/>
                </s:if>
                <s:else>
                    <s:property value="tagName"/>
                    <s:hidden name="tagId"/>
                </s:else>
            </ui:field>

            <ui:field cssClass="withButton">
                <c:choose>
                    <c:when test="${not empty sites}">
                        <ui:button id="submitButton" message="report.button.runReport"/>
                    </c:when>
                    <c:otherwise>
                        <ui:button id="submitButton" message="report.button.runReport" disabled="true"/>
                    </c:otherwise>
                </c:choose>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</s:form>
