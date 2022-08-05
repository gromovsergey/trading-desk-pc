<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:section>
    <ui:fieldGroup>
        <ui:simpleField labelKey="ApplicationFormat.name" value="${name}"/>
        <ui:simpleField labelKey="ApplicationFormat.mimeType" value="${mimeType}"/>
    </ui:fieldGroup>
</ui:section>