<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<ui:header>
    <ui:pageHeadingByTitle />
    <c:if test="${ad:isPermitted0('WDFrequencyCaps.update')}">
        <ui:button message="form.edit" href="edit.action" />
    </c:if>
    <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=39"/>
</ui:header>

<s:if test="(eventsFrequencyCap!= null && !eventsFrequencyCap.empty) or (channelsFrequencyCap!= null && !channelsFrequencyCap.empty) or (categoriesFrequencyCap!= null && !categoriesFrequencyCap.empty)">
    <ui:section>
        <ui:fieldGroup>                               
            <ui:frequencyCapView frequencyCap="${eventsFrequencyCap}" labelKey="WDFrequencyCaps.events"/>
            <ui:frequencyCapView frequencyCap="${channelsFrequencyCap}" labelKey="WDFrequencyCaps.channels"/>
            <ui:frequencyCapView frequencyCap="${categoriesFrequencyCap}" labelKey="WDFrequencyCaps.categories"/>
        </ui:fieldGroup>
    </ui:section>
</s:if> 
<s:else>
    <div class="wrapper">
        <fmt:message key="nothing.found.to.display"/>
    </div>
</s:else>