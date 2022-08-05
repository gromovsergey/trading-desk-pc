<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<ui:pageHeadingByTitle/>

<s:form action="admin/TermsOfUse/save">
    <s:hidden name="type"/>
    <s:hidden name="version"/>
    <s:hidden name="accountId"/>
    <div class="wrapper">
        <s:actionerror/>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </div>
  
    <ui:section>
        <ui:fieldGroup>
            
            <ui:field labelKey="TermsOfUse.value" labelForId="termsOfUseId">
                <s:textarea name="value" cssClass="middleLengthText" id="termsOfUseId"/>
            </ui:field>
            
        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
        <ui:button message="form.save" type="submit"/>
        <ui:button message="form.cancel" onclick="location='main.action?accountId=${accountId}';" type="button"/>
    </div>
</s:form>
