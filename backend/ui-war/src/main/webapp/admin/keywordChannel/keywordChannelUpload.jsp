<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<ui:pageHeadingByTitle/>

<s:form action="admin/KeywordChannel/doUpload" styleId="channelUpload" enctype="multipart/form-data" method="POST">
    <ui:section titleKey="KeywordChannel.upload.title">
        <s:actionerror/>
        <s:actionmessage/>
        <ui:fieldGroup>
            <ui:field labelKey="KeywordChannel.upload.format" labelForId="format" required="true" errors="format">
                <s:radio list="'CSV'" listValue="getText('form.csv')" name="format"/>
                <s:radio list="'TAB'" listValue="getText('form.tab')" name="format"/>
            </ui:field>

            <ui:field labelKey="KeywordChannel.upload.file" labelForId="fileId" required="true" errors="csvFile">
                <s:file name="csvFile"/>
            </ui:field>
            <ui:field cssClass="withButton">
                <ui:button message="KeywordChannel.upload.button" type="submit"/>
                <ui:button message="form.cancel" href="main.action" type="button"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

</s:form>