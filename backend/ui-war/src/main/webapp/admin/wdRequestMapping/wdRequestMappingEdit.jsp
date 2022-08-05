<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<s:form action="admin/WDRequestMapping/%{#attr.isCreatePage?'create':'update'}">
    <s:hidden name="id"/>
    <s:hidden name="version"/>

    <div class="wrapper">
        <s:actionerror/>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </div>

    <ui:section>
        <ui:fieldGroup>
            <ui:field labelKey="WDRequestMapping.name" required="true" errors="name">
                <s:textfield name="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>
            <ui:field labelKey="WDRequestMapping.description" errors="description">
                <s:textfield name="description" cssClass="middleLengthText" maxlength="2000"/>
            </ui:field>
            <ui:field labelKey="WDRequestMapping.request" required="true" errors="protocolRequest">
                <s:textarea name="protocolRequest" cssClass="bigLengthText" rows="20"/>
            </ui:field>
            <ui:field label="" labelKey="">
                <s:text name="WDRequestMapping.requestTip"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
        <ui:button message="form.save" />
        <ui:button message="form.cancel" onclick="location='main.action'" type="button" />
    </div>
</s:form>
