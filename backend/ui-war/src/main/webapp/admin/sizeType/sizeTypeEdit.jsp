<%@ page import="com.foros.model.creative.SizeType" %>
<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<s:form action="%{#attr.isCreatePage?'create':'update'}" id="saveForm">

<div class="wrapper">
    <s:fielderror><s:param value="'version'"/></s:fielderror>
    <s:actionerror/>
</div>

<ui:section titleKey="form.main">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <ui:fieldGroup>
        <ui:field labelKey="SizeType.defaultName" required="true" errors="defaultName">
            <s:textfield name="defaultName" cssClass="middleLengthText" maxlength="50"/>
        </ui:field>
        <ui:field labelKey="SizeType.multipleSizes" errors="multipleSizes">
            <c:set var="multipleSizesValues" value="<%=SizeType.MultipleSizes.values()%>"/>
            <s:radio list="#attr.multipleSizesValues" name="multipleSizes" listValue="getText('SizeType.multipleSizes.' + name())"/>
        </ui:field>
        <ui:field labelKey="SizeType.advertiserSizeSelection" errors="advertiserSizeSelection">
            <c:set var="advertiserSizeSelectionValues" value="<%=SizeType.AdvertiserSizeSelection.values()%>"/>
            <s:radio list="#attr.advertiserSizeSelectionValues" name="advertiserSizeSelection" listValue="getText('SizeType.advertiserSizeSelection.' + name())"/>
        </ui:field>
        <ui:field labelKey="SizeType.tagHtmlTemplate" errors="tagTemplateFile">
            <textarea id="tagTemplateFile" name="tagTemplateFile" class="bigLengthText"><c:out value="${tagTemplateFile}"/></textarea>
        </ui:field>
        <ui:field labelKey="SizeType.tagHtmlTemplateIframe" errors="tagTemplateIframeFile">
            <textarea id="tagTemplateIframeFile" name="tagTemplateIframeFile" class="bigLengthText"><c:out value="${tagTemplateIframeFile}"/></textarea>
        </ui:field>
        <ui:field labelKey="SizeType.tagHtmlTemplatePassback" errors="tagTemplateBrPbFile">
            <textarea id="tagTemplateBrPbFile" name="tagTemplateBrPbFile" class="bigLengthText"><c:out value="${tagTemplateBrPbFile}"/></textarea>
        </ui:field>
        <ui:field labelKey="SizeType.inventoryEstimationTagHtmlTemplate" errors="tagTemplateIEstFile">
            <textarea id="tagTemplateIEstFile" name="tagTemplateIEstFile" class="bigLengthText"><c:out value="${tagTemplateIEstFile}"/></textarea>
        </ui:field>
        <ui:field labelKey="SizeType.previewTagHtmlTemplate" errors="tagTemplatePreviewFile">
            <textarea id="tagTemplatePreviewFile" name="tagTemplatePreviewFile" class="bigLengthText"><c:out value="${tagTemplatePreviewFile}"/></textarea>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<s:include value="/templates/formFooter.jsp"/>

</s:form>
