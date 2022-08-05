<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<script type="text/javascript">

function restoreOptTransf(arrValues){
    var restored = false;
    if(!arrValues) return restored;
    
    UI.Optiontransfer.moveAllOptions(
        document.getElementById('selected_objectTypeIds_id'), 
        document.getElementById('available_objectTypeIds_id'), 
        document.getElementById('alloptions_objectTypeIds_id'), 
        false, 
        true, 
        function(){}, 
        []
    );
    
    var options = $('#available_objectTypeIds_id')[0].options;
    
    $.each($('#available_objectTypeIds_id')[0].options, function(){
        restored = true;
        
        for(var idx in arrValues){
            if(arrValues[idx] == this.value){
                this.selected = true;
                return true;
            }
        }
    })
    
    UI.Optiontransfer.moveSelectedOptions(
        document.getElementById('available_objectTypeIds_id'), 
        document.getElementById('selected_objectTypeIds_id'), 
        document.getElementById('alloptions_objectTypeIds_id'), 
        false, 
        false, 
        function(){}, 
        []
    );
    
    return restored;
}

$(function() {
    setTimeout(function(){
        $('#logViewAuditReport').pagingAssist({
            action:     'audit/run.action',
            message:    '${ad:formatMessage("report.loading")}',
            result:     $('#result')
        });
    },500);
});

</script>

<ui:pageHeadingByTitle/>

<form id="logViewAuditReport" action="audit.action" method="post">
    <c:set var="accountId" value="${_principal.accountId}" />
    <ui:section titleKey="form.search">
        <ui:fieldGroup>
            
            <ui:field labelKey="report.input.field.dateRange" labelForId="fastChangeId" errors="date">
                <ui:daterange options="Y T WTD MTD QTD YTD LW LM LQ LY R"
                        fastChangeId="${fastChangeId}"
                        toDate="${toDate.datePart}"
                        toTime="${toDate.timePart}"
                        fromDate="${fromDate.datePart}"
                        fromTime="${fromDate.timePart}"
                        validateRange="true"
                        currentPos="1"
                        maxDate="+1d"
                        timeZoneAccountId="${accountId}"
                        fromDateFieldId="dateRange_begin" toDateFieldId="dateRange_end"
                        showTime="true"/>
            </ui:field>
            
            <ui:field labelKey="enum.accountRole" labelForId="accountType" errors="accountType">
                <select class="smallLengthText1" id="accountType" name="accountType">
                  <option value=""><fmt:message key="form.all"/></option>
                  <option value="0" ><fmt:message key="enum.accountRole.INTERNAL"/></option>
                  <option value="1,4"><fmt:message key="enum.accountRole.ADV_AGN"/></option>
                  <option value="2"><fmt:message key="enum.accountRole.PUBLISHER"/></option>
                  <option value="3"><fmt:message key="enum.accountRole.ISP"/></option>
                  <option value="5"><fmt:message key="enum.accountRole.CMP"/></option>
                </select>
            </ui:field>
            
            <ui:field labelKey="report.input.field.accountName" labelForId="accountName" errors="accountName">
                <s:textfield name="accountName" id="accountName" cssClass="middleLengthText" maxLength="100"/>
            </ui:field>

            <ui:field labelKey="report.input.field.login" labelForId="login" errors="login">
                <s:textfield name="email" id="login" cssClass="middleLengthText" maxLength="320"/>
            </ui:field>

            <ui:field labelKey="report.input.field.objectType" labelForId="type" errors="type">
                <ui:optiontransfer
                        name="objectTypeIds"
                        size="9"
                        cssClass="middleLengthText"
                        listKey="id"
                        listValue="name"
                        list="${objectTypeIndex}"
                        selList="${null}"
                        selListKey="id"
                        selListValue="name"
                        sort="true"
                    />
            </ui:field>
            
            <ui:field labelKey="report.input.field.actionType" labelForId="action" errors="action">
                <select name="actionType" id="action" class="middleLengthText">
                    <option value=""><fmt:message key="form.all"/></option>
                    <option value="CREATE"><fmt:message key="enums.ActionType.CREATE"/></option>
                    <option value="UPDATE"><fmt:message key="enums.ActionType.UPDATE"/></option>
                    <option value="LOGIN"><fmt:message key="enums.ActionType.LOGIN"/></option>
                    <option value="START_REPORT"><fmt:message key="enums.ActionType.START_REPORT"/></option>
                    <option value="COMPLETE_REPORT"><fmt:message key="enums.ActionType.COMPLETE_REPORT"/></option>
                </select>
            </ui:field>

            <ui:field labelKey="report.input.field.resultType" labelForId="resultType" errors="resultType">
                <select name="resultType" id="resultType" class="middleLengthText">
                    <option value=""><fmt:message key="form.all"/></option>
                    <option value="SUCCESS"><fmt:message key="enums.ResultType.SUCCESS"/></option>
                    <option value="FAILURE"><fmt:message key="enums.ResultType.FAILURE"/></option>
                </select>
            </ui:field>
            
            <ui:field cssClass="withButton">
                <ui:button message="report.button.runReport" type="submit"/>
            </ui:field>
            
        </ui:fieldGroup>
    </ui:section>

    <input type="hidden" name="formSubmitted" value="true"/>
</form>
  
<div class="wrapper">
    <s:fielderror><s:param value="'report'"/></s:fielderror>
</div>

<div id="result" class="logicalBlock"></div>

<tiles:insertTemplate template="/auditLog/logDetails.jsp">
    <tiles:putAttribute name="container" value="#result"/>
</tiles:insertTemplate>