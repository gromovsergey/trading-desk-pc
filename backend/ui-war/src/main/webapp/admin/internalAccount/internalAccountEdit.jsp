<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set value="#attr.isCreatePage?'create':'update'" var="saveActionName"/>

<s:form action="%{#request.moduleName}/%{#saveActionName}" id="actionSave">
<s:hidden name="id"/>
<s:hidden name="version"/>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<ui:section titleKey="form.main">
    <ui:fieldGroup>
        
        <%@ include file="/account/accountDetailsEdit.jsp" %>
        
        <ui:field id="advContact" labelKey="account.advertiserContact" labelForId="advContact.id">
            <s:select name="advContact.id" id="advContact.id" cssClass="middleLengthText"
                      headerValue="%{getText('form.select.none')}" headerKey=""
                      list="advContactUsers"
                      listKey="id" listValue="name" value="advContact.id" >
            </s:select>
        </ui:field>
        
        <ui:field id="pubContact" labelKey="account.publisherContact" labelForId="pubContact.id">
            <s:select name="pubContact.id" id="pubContact.id" cssClass="middleLengthText"
                      headerValue="%{getText('form.select.none')}" headerKey=""
                      list="pubContactUsers"
                      listKey="id" listValue="name" value="pubContact.id" >
            </s:select>
        </ui:field>
        
        <ui:field id="ispContact" labelKey="account.ispContact" labelForId="ispContact.id">
            <s:select name="ispContact.id" id="ispContact.id" cssClass="middleLengthText"
                      headerValue="%{getText('form.select.none')}" headerKey=""
                      list="ispContactUsers"
                      listKey="id" listValue="name" value="ispContact.id" >
            </s:select>
        </ui:field>

        <ui:field id="cmpContact" labelKey="account.cmpContact" labelForId="cmpContact.id">
            <s:select name="cmpContact.id" id="cmpContact.id" cssClass="middleLengthText"
                      headerValue="%{getText('form.select.none')}" headerKey=""
                      list="cmpContactUsers"
                      listKey="id" listValue="name" value="cmpContact.id" >
            </s:select>
        </ui:field>

        <ui:field labelKey="account.notes" labelForId="notes" errors="notes">
            <s:textarea name="notes" id="notes" cssClass="middleLengthText" />
        </ui:field>

    </ui:fieldGroup>
</ui:section>

    <div class="wrapper">
        <ui:button message="form.save" type="submit" />
        <s:if test="id == null">
            <ui:button message="form.cancel" type="button" onclick="location='list.action';" />
        </s:if>
        <s:else>
            <ui:button message="form.cancel" type="button"
                onclick="location='view.action?id=${id}';" />
        </s:else>
    </div>
</s:form>
