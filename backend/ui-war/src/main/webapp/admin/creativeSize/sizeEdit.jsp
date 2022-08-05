<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript">
function prepareSizeData() {
	   return $("#saveForm").serializeArray();
}

function creativeSizeChange() {
    var height = UI.Localization.parseInt($("#height").val());
    var width = UI.Localization.parseInt($("#width").val());
    var maxHeight = UI.Localization.parseInt($("#maxHeight").val());
    var maxWidht = UI.Localization.parseInt($("#maxWidth").val());
    
    var condition0 = Boolean(height > 0 && width > 0 && maxHeight > 0 && maxWidht > 0 && height <= maxHeight && width <= maxWidht);
    var condition1 = Boolean(height < maxHeight && width < maxWidht);
    var condition2 = Boolean(height == maxHeight && width < maxWidht);
    var condition3 = Boolean(height < maxHeight && width == maxWidht);


    if(condition0&&(condition1||condition2||condition3)) {
        var params = prepareSizeData();
        $("#creativeSizeExpansions").load('${_context}/CreativeSize/changeSize.action',
                params, 
                function() {
                    $("#creativeSizeExpansionsField").show();
                    $("#expansionsErrorId").hide();
                }
         );
    } else {
        $("#creativeSizeExpansionsField").hide();
        $("#creativeSizeExpansions").html("");
        $("#expansionsErrorId").show();
        $("#saveForm").unbind("submit");
    }
}

function linkMaxSize(jSize, jMaxSize){
    jSize.change(function(){
        var sizeVal = jSize.val();
        var maxSizeVal = +jMaxSize.val();
        if(!maxSizeVal || isNaN(maxSizeVal) || isNaN(+sizeVal) || maxSizeVal < +sizeVal){
            jMaxSize.val(sizeVal);
        }
        creativeSizeChange();
    });
};

function linkMinSize(jSize, jMinSize){
    jSize.change(function(){
        var sizeVal = +jSize.val();
        var minSizeVal = +jMinSize.val();
        if(!isNaN(minSizeVal)){
            if(isNaN(sizeVal) || sizeVal < minSizeVal){
                jSize.val(minSizeVal);
            }
        }
        creativeSizeChange();
    });
};

$().ready(function() {
    
    linkMaxSize($('#width'), $('#maxWidth'));
    linkMaxSize($('#height'), $('#maxHeight'));
    linkMinSize($('#maxWidth'), $('#width'));
    linkMinSize($('#maxHeight'), $('#height'));

    <s:if test="availableExpansions.empty">
    creativeSizeChange();
    </s:if>
});

</script>

<s:form action="admin/CreativeSize/%{#attr.isCreatePage?'create':'update'}" id="saveForm">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <div id="expansionsErrorId"><s:if test="availableExpansions.empty"><s:fielderror><s:param value="'expansions'"/></s:fielderror></s:if></div>
        <s:if test="hasActionErrors()">
            <s:actionerror/>
        </s:if>
    </div>

    <ui:section titleKey="form.main" >
        <ui:fieldGroup>

            <ui:field labelKey="defaultName" labelForId="name" required="true" errors="defaultName,name">
                <s:textfield name="defaultName" id="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>

            <ui:field labelKey="CreativeSize.type" required="true" errors="sizeType">
                <s:select list="sizeTypes" name="sizeType.id"
                          headerValue="%{getText('form.select.pleaseSelect')}" headerKey=""
                          listKey="id" listValue="getText(name)" cssClass="middleLengthText"
                />
            </ui:field>

            <ui:field labelKey="CreativeSize.protocolName" labelForId="protocolName" required="true" errors="protocolName,prtclname">
                <s:textfield name="protocolName" id="protocolName" cssClass="middleLengthText" maxlength="50"/>
            </ui:field>

            <ui:field labelKey="CreativeSize.width" labelForId="width" errors="width,maxWidth">
                <table class="formFields">
                    <tr>
                        <td class="fieldName">
                            <label for="width"><ui:text textKey="CreativeSize.expansions.sizeCollapsed"/></label>
                        </td>
                        <td class="fieldName">
                            <label for="maxWidth"><ui:text textKey="CreativeSize.expansions.sizeExpanded" /></label>
                        </td>
                    </tr>
                    <tr>
                        <td class="field">
                            <s:textfield cssStyle="width: 100px;" size="4" name="width" cssClass="middleLengthText" maxlength="4" labelposition="top" label="Collapsed" id="width"/>
                       </td>
                       <td class="field">
                            <s:textfield cssStyle="width: 100px;" size="4" name="maxWidth" cssClass="middleLengthText" maxlength="4" labelposition="top" label="Max. Expanded" id="maxWidth"/>  
                       </td>
                   </tr>
                </table>
            </ui:field>

            <ui:field labelKey="CreativeSize.height" labelForId="height" errors="height,maxHeight" id="heightId">
            
                <table class="formFields">
                    <tr>
                        <td class="fieldName">
                            <label for="height"><ui:text textKey="CreativeSize.expansions.sizeCollapsed"/></label>
                        </td>
                        <td class="fieldName">
                            <label for="maxHeight"><ui:text textKey="CreativeSize.expansions.sizeExpanded" /></label>
                        </td>
                    </tr>
                    <tr>
                        <td class="field">
                            <s:textfield cssStyle="width: 100px;" size="4" name="height" cssClass="middleLengthText" maxlength="4" labelposition="top" label="Collapsed" id="height"/>
                        </td>
                       <td class="field">
                            <s:textfield cssStyle="width: 100px;" size="4" name="maxHeight" cssClass="middleLengthText" maxlength="4" labelposition="top" label="Max.Expanded" id="maxHeight"/>
                       </td>
                   </tr>
                </table>   
                
            </ui:field>

            <ui:field labelKey="CreativeSize.expansions.label" labelForId="expansions" id="creativeSizeExpansionsField" required="true">
                <div id="creativeSizeExpansions">
                    <s:fielderror fieldName="expansions"/>
                    <%@include file="creativeSizeExpansions.jsp"%>
                </div>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>

  <s:include value="/templates/formFooter.jsp"/>
</s:form>
