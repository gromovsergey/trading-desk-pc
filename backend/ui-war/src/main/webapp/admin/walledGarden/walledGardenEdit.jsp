<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<ui:pageHeadingByTitle/>

<s:form action="admin/WalledGarden/save">
    <s:hidden name="id" />
    
    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </ui:errorsBlock>
    
    <ui:section titleKey="WalledGarden.edit">
        <ui:fieldGroup>
            <ui:field labelKey="WalledGarden.select.publisherAccount" errors="publisher">
                <c:out value="${entity.publisher.name}"/>
            </ui:field>
            
            <s:set var="marketplace" value="publisherMarketplace"/>
            <s:include value="walledGardenMarketplace.jsp">
                <s:param name="prefix" value="'publisher'" />
            </s:include>
        
            <ui:field labelKey="WalledGarden.select.agencyAccount" errors="agency">
                <c:out value="${entity.agency.name}"/>
            </ui:field>
            
            <s:set var="marketplace" value="agencyMarketplace"/>
            <s:include value="walledGardenMarketplace.jsp">
                <s:param name="prefix" value="'agency'" />
            </s:include>
            <s:hidden name="version"/>
        
        </ui:fieldGroup>
    </ui:section>
    
    <div class="wrapper">
        <ui:button message="form.save" type="submit" />
        <ui:button message="form.cancel" onclick="location='main.action';" type="button" />
    </div>

</s:form>
