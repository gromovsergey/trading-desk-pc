<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script language="JavaScript">
    function changeCreative(form) {
        if($('#creativeId').val() != ''){
            form.action = '<s:url action="%{#attr.moduleName}/changeCreative"/>';
            form.submit();
        }
        else {
            location.href = '<s:url action="%{#attr.moduleName}/new.action?creativeGroupId=%{creativeGroupId}"/>';
        }

    }
</script>

<s:form action="%{#attr.moduleName}/create">
    <ui:pageHeadingByTitle/>

    <s:hidden name="ccgId"/>

	<div class="wrapper">
	    <s:fielderror><s:param value="'version'"/></s:fielderror>
	</div>

    <ui:section>
        <ui:fieldGroup>
            <ui:field labelKey="creative" labelForId="creativeId" required="true" errors="creative">
                <s:select list="creatives" listKey="id" listValue="name" value="creative.id"
                             name="creative.id" id="creativeId" cssClass="middleLengthText"
                             headerKey="" headerValue="%{getText('form.select.pleaseSelect')}"
                             onchange="changeCreative(this.form);"/>
            </ui:field>
            <s:if test="%{canUpdateWeight()}">
                <ui:field labelKey="weight" labelForId="weight" tipKey="creative.weight.note" errors="weight"
                          required="true">
                    <s:textfield name="weight" cssClass="smallLengthText" maxlength="10"/>
                </ui:field>
            </s:if>

        </ui:fieldGroup>
    </ui:section>

    <s:if test="creative.id != null">
        <ui:section titleKey="creative.preview">
            <div>
                <ui:creativePreview creativeId="${creative.id}"/>
            </div>
        </ui:section>
    </s:if>

    <ui:frequencyCapEdit fcPropertyName="frequencyCap"/>

    <div class="wrapper">
        <ui:button message="form.save" type="submit"/>
        <ui:button message="form.cancel"
                   onclick="location='${_context}/campaign/group/view.action?id=${ccgId}';"
                   type="button"/>
    </div>
</s:form>
