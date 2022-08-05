<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ page import="com.foros.model.channel.trigger.TriggerType" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="SEARCH_KW_TRIGGER_TYPE" value="<%=TriggerType.SEARCH_KEYWORD.getLetter()%>"/>
<c:set var="PAGE_KW_TRIGGER_TYPE" value="<%=TriggerType.PAGE_KEYWORD.getLetter()%>"/>

<s:form action="admin/KeywordChannel/saveDefaultSettings" styleId="channelSave">

    <s:hidden name="versions['%{#attr.SEARCH_KW_TRIGGER_TYPE}']"/>
    <s:hidden name="versions['%{#attr.PAGE_KW_TRIGGER_TYPE}']"/>

    <ui:pageHeadingByTitle/>

    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </ui:errorsBlock>

    <ui:keywordBehavioralParamsEdit bpTypes="S,P" bpTitleKey="channel.defaultSettings" />

    <div class="wrapper">
        <ui:button message="form.save" type="submit"/>
        <ui:button message="form.cancel" href="/admin/KeywordChannel/main.action" type="button"/>
    </div>

</s:form>
