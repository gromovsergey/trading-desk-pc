<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<s:form action="%{#request.moduleName}/%{#request.entityName}/%{#attr.isCreatePage?'create':'update'}">
  <s:hidden name="id"/>
  <s:hidden name="version"/>

    <div class="wrapper">
        <s:actionerror/>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </div>

    <ui:section>
        <ui:fieldGroup>
            <ui:field labelKey="ApplicationFormat.name" labelForId="name" required="true" errors="name">
                <s:textfield name="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>
            <ui:field labelKey="ApplicationFormat.mimeType" labelForId="name" required="true" errors="mimeType">
                <s:textfield name="mimeType" cssClass="middleLengthText" maxlength="50"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <s:include value="/templates/formFooter.jsp"/>
</s:form>
