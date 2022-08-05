<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:form action="%{#attr.moduleName}/update">
    <ui:pageHeadingByTitle/>

    <s:hidden name="id"/>
    <s:hidden name="version"/>

    <s:hidden name="creative.id"/>
    <s:hidden name="creative.name"/>

	<div class="wrapper">
	    <s:fielderror><s:param value="'version'"/></s:fielderror>
	</div>

    <ui:section>
        <ui:fieldGroup>
            <ui:simpleField labelKey="creative" value="${creative.name}"/>

            <s:if test="%{canUpdateWeight()}">
                <ui:field labelKey="weight" labelForId="weight" tipKey="creative.weight.note" errors="weight" required="true">
                    <s:textfield name="weight" cssClass="smallLengthText" maxlength="10"/>
                </ui:field>
            </s:if>
        </ui:fieldGroup>
    </ui:section>

    <ui:section titleKey="creative.preview">
        <div>
            <ui:creativePreview creativeId="${creative.id}"/>
        </div>
    </ui:section>

    <ui:frequencyCapEdit fcPropertyName="frequencyCap"/>

    <div class="wrapper">
        <ui:button message="form.save" type="submit"/>
        <ui:button message="form.cancel" onclick="location='view.action?id=${id}';" type="button"/>
    </div>
</s:form>
