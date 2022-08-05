<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<c:set var="canUpdate" value="${ad:isPermitted0('CreativeSize.update')}"/>

<ui:section titleKey="form.main">
    <ui:fieldGroup>
        <c:if test="${canUpdate}">
            <ui:localizedField id="name" labelKey="defaultName" value="${defaultName}"
                               resourceKey="SizeType.${id}"
                               resourceUrl="/admin/resource/htmlName/"
                               entityName="SizeType"/>
        </c:if>
        <c:if test="${not canUpdate}">
            <ui:simpleField labelKey="defaultName" value="${defaultName}"/>
        </c:if>
        <ui:simpleField labelKey="SizeType.multipleSizes"
                        valueKey="SizeType.multipleSizes.${multipleSizes}"/>
        <ui:simpleField labelKey="SizeType.advertiserSizeSelection"
                        valueKey="SizeType.advertiserSizeSelection.${advertiserSizeSelection}"/>
        <ui:field labelKey="SizeType.tagHtmlTemplate">
            <textarea id="tagTemplateFile" readonly="true" class="bigLengthText"><c:out value="${tagTemplateFile}"/></textarea>
        </ui:field>
        <ui:field labelKey="SizeType.tagHtmlTemplateIframe">
            <textarea id="tagTemplateIframeFile" readonly="true" class="bigLengthText"><c:out value="${tagTemplateIframeFile}"/></textarea>
        </ui:field>
        <ui:field labelKey="SizeType.tagHtmlTemplatePassback">
            <textarea id="tagTemplateBrPbFile" readonly="true" class="bigLengthText"><c:out value="${tagTemplateBrPbFile}"/></textarea>
        </ui:field>
        <ui:field labelKey="SizeType.inventoryEstimationTagHtmlTemplate">
            <textarea id="tagTemplateIEstFile" readonly="true" class="bigLengthText"><c:out value="${tagTemplateIEstFile}"/></textarea>
        </ui:field>
        <ui:field labelKey="SizeType.previewTagHtmlTemplate">
            <textarea id="tagTemplatePreviewFile" readonly="true" class="bigLengthText"><c:out value="${tagTemplatePreviewFile}"/></textarea>
        </ui:field>

    </ui:fieldGroup>
</ui:section>
