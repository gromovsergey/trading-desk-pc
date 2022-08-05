<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>


<ui:pageHeadingByTitle/>

<s:form id="channelTriggersForm" action="run" method="post" target="_blank">
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

        <s:if test="%{canSelectAccount()}">
            <ui:field labelKey="report.input.field.channelCreatorAccount" labelForId="accountId">
                <c:choose>
                    <c:when test="${not empty accounts}">
                        <select id="accountId" name="accountId" class="middleLengthText">
                            <c:forEach var="account" items="${accounts}">
                                <option value="${account.id}"><c:out value="${account.name}"/></option>
                            </c:forEach>
                        </select>
                    </c:when>
                    <c:otherwise>
                        <span class="errors"><fmt:message key="report.account.error"/></span>
                    </c:otherwise>
                </c:choose>
            </ui:field>
        </s:if>
        <s:else>
            <s:if test="%{isInternal()}">
                <ui:simpleField labelKey="report.input.field.account" value="${account.name}"/>
            </s:if>
            <input type="hidden" id="accountId" name="accountId" value="${account.id}"/>
        </s:else>

        <s:if test="%{canSelectChannel()}">
            <ui:field cssClass="valignFix" labelKey="report.input.field.channels" required="true">
                <ui:autocomplete
                    id="channelIds"
                    cssClass="middleLengthText"
                    isMultiSelect="true"
                    minLength="1"
                >
                </ui:autocomplete>
            </ui:field>
        </s:if>
        <s:else>
            <ui:field labelKey="report.input.field.channel">
                <input type="hidden" name="channelIds" value="${channel.id}"/>
                ${channel.name}
            </ui:field>
        </s:else>

        <ui:field cssClass="withButton">
            <ui:button id="submitButton" message="report.button.runReport"/>
        </ui:field>

    </ui:fieldGroup>
    </ui:section>
</s:form>

<script type="text/javascript">
    var channelUrl = '';
    
    $().ready(function(){
        
        <s:if test="%{canSelectChannel()}">

        channelUrl = '/xml/behavioralDiscoverChannels.action';
        $('#channelIds').tokenizer('option', 'source', function(request, response){
            $.ajax({
                url: channelUrl,
                data: {'accountPair':$('#accountId').val(), query: $('#channelIds').val()},
                type: 'GET',
                success: function(data) {
                    var opts = $.map($('option', $('options', data)), function(elem){
                        var curr = $(elem);
                        return {label:curr.text(), value:curr.attr('id')};
                    });
                    
                    response(opts);
                }
            });
        });
        </s:if>

        $('#accountId').change(function () {
            $('#channelIds').tokenizer('clear');
        });
    });

</script>