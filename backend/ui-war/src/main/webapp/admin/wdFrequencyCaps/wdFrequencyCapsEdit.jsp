<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:pageHeadingByTitle/>

<s:form action="admin/WDFrequencyCaps/save">

<div class="wrapper">
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</div>

<ui:frequencyCapEdit fcPropertyName="eventsFrequencyCap" legendKey="WDFrequencyCaps.events"/>

<ui:frequencyCapEdit fcPropertyName="channelsFrequencyCap" legendKey="WDFrequencyCaps.channels" showLifeLimit="false"/>

<ui:frequencyCapEdit fcPropertyName="categoriesFrequencyCap" legendKey="WDFrequencyCaps.categories" showLifeLimit="false"/>

<div class="wrapper">
    <ui:button message="form.save" type="submit" />
    <ui:button message="form.cancel" onclick="location='view.action';" type="button" />
</div>

</s:form>