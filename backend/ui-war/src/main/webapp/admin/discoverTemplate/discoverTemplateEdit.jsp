<%@ page import="com.foros.model.template.TemplateFileType" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">
    var targetElementId = null;

    function openFileBrowser(fileId) {
        targetElementId = 'files[' + fileId + '].templateFile';
        
        var cdStr   = '';
        if (document.getElementById(targetElementId) && document.getElementById(targetElementId).value !== ''){
            var path    = document.getElementById(targetElementId).value;
            path    = path.substring(path[0] !== undefined && path[0] === '/' ? 1 : 0, path.lastIndexOf("/")+1);
            cdStr   = (path !== '' && path !== '/') ? '&currDirStr='+encodeURIComponent(path):'';
        }
        
        window.open('/admin/fileman/fileManager.action?id=' + targetElementId + '&mode=template'+cdStr,'filebrowser','width=820,height=600,resizable=yes,scrollbars=yes');
    }
</script>

<s:form action="admin/DiscoverTemplate/%{#attr.isCreatePage?'create':'update'}">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:actionerror/>
    </div>
    <s:hidden name="status"/>

<ui:section titleKey="form.main" >
    <ui:fieldGroup id="" cssClass="">

        <ui:field labelKey="defaultName" labelForId="name" required="true" errors="defaultName,name">
            <s:textfield name="defaultName" id="name" cssClass="middleLengthText" maxlength="100"/>
        </ui:field>

    </ui:fieldGroup>
</ui:section>

<c:set var="templateTypes" value="<%=TemplateFileType.values()%>"/>
<ui:section titleKey="DiscoverTemplate.files">
    <ui:fieldGroup>
        
        <ui:field 
            labelKey="DiscoverTemplate.tagTemplateFile" 
            labelForId="files[0].templateFile" 
            required="true" errors="files[0].templateFile">
            <div class="cell">
                <s:radio cssClass="withInput narrowSet" name="files[0].type" list="#attr.templateTypes"
                        listValue="getText('enums.TemplateFileType.' + name())"/>
            </div>
            <div class="cell withInput">
                <s:textfield name="files[0].templateFile" id="files[0].templateFile" cssClass="middleLengthText"/>
                <s:hidden name="files[0].id"/>
                <s:hidden name="files[0].version"/>
            </div>
            <div class="cell withInput">
                <ui:button message="form.browse" onclick="openFileBrowser(0);" />
            </div>
        </ui:field>
        
        <ui:field labelKey="DiscoverTemplate.customizationTemplateFile" 
                labelForId="files[1].templateFile" required="true" errors="files[1].templateFile">
            <div class="cell">
                <s:radio cssClass="withInput narrowSet" name="files[1].type" list="#attr.templateTypes"
                        listValue="getText('enums.TemplateFileType.' + name())"/>
            </div>
            <div class="cell withInput">
                <s:textfield name="files[1].templateFile" id="files[1].templateFile" cssClass="middleLengthText"/>
                <s:hidden name="files[1].id"/>
                <s:hidden name="files[1].version"/>
            </div>
            <div class="cell withInput">
                <ui:button message="form.browse" onclick="openFileBrowser(1);" />
            </div>
        </ui:field>
        
        <ui:field labelKey="DiscoverTemplate.previewTemplateFile" labelForId="files[2].templateFile" 
                required="true" errors="files[2].templateFile">
            <div class="cell">
                <s:radio cssClass="withInput narrowSet" name="files[2].type" list="#attr.templateTypes"
                        listValue="getText('enums.TemplateFileType.' + name())"/>
            </div>
            <div class="cell withInput">
                <s:textfield name="files[2].templateFile" id="files[2].templateFile" cssClass="middleLengthText"/>
                <s:hidden name="files[2].id"/>
                <s:hidden name="files[2].version"/>
            </div>
            <div class="cell withInput">
                <ui:button message="form.browse" onclick="openFileBrowser(2);" />
            </div>
        </ui:field>
        
    </ui:fieldGroup>
</ui:section>

  <s:include value="/templates/formFooter.jsp"/>
</s:form>
