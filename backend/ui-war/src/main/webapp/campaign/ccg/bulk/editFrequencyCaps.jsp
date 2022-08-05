<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<s:form action="save">

<s:hidden name="campaignId"/>

<%@ include file="bulkGroupErrors.jsp"%>

<div class="b-panel">
    <ui:frequencyCapEdit fcPropertyName="frequencyCap"/>
</div>    

</s:form>