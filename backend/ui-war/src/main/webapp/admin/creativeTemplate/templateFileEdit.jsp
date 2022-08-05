<%@ page import="com.foros.model.template.TemplateFileType" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<script type="text/javascript">

<s:set name="creativeTemplateFile" value="%{getText('CreativeTemplate.file')}"/>
<s:set name="creativeTemplateXsl" value="%{getText('CreativeTemplate.xsl')}"/>

var fileLabel = '${creativeTemplateFile}: ';
var xslLabel = '${creativeTemplateXsl}: ';
var targetElementId = null;

function openFileBrowser(browseButton){
    targetElementId = 'templateFile';
    
    var cdStr   = '';
    if (document.getElementById(targetElementId) && document.getElementById(targetElementId).value !== ''){
        var path    = document.getElementById(targetElementId).value;
        path    = path.substring(path[0] !== undefined && path[0] === '/' ? 1 : 0, path.lastIndexOf("/")+1);
        cdStr   = (path !== '' && path !== '/') ? '&currDirStr='+encodeURIComponent(path):'';
    }
    
    window.open('/admin/fileman/fileManager.action?id=' + targetElementId+'&mode=template'+cdStr,'filebrowser','width=820,height=600,resizable=yes,scrollbars=yes');
}

function typeChanged(){
    var labelForFile = $('label:eq(0)', '#trWithTemplateFileName');
    labelForFile.text($('input[name=type]:checked').val() == 'TEXT' ? fileLabel : xslLabel);
}

$(function() {
    typeChanged();
});

function fnShowCreatives(jqBtnTextHolder, jqLinkedElem, textShow, textHide) {
    var toShow = jqLinkedElem.is(':hidden');
    
    jqBtnTextHolder.text(toShow ? textHide : textShow)
    jqLinkedElem[toShow ? 'show' : 'hide']();
}

</script>

<ui:pageHeadingByTitle/>
<s:form action="admin/CreativeTemplateFile/%{id != null?'update':'create'}">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <s:hidden name="template.id"/>
    <s:hidden name="template.defaultName"/>
    <s:hidden name="template.version"/>
    
    <div class="wrapper">
        <s:actionerror/>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </div>
    
    <ui:section errors="template.id">
        <ui:fieldGroup>
            
            <ui:field labelKey="CreativeTemplate.crSize" labelForId="statusId" required="true">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <s:select id="statusId" name="creativeSize.id"
                                headerValue="%{getText('form.select.pleaseSelect')}" headerKey=""
                                list="availableSizes" listKey="id" listValue="%{getText(name)}"
                                cssClass="smallLengthText1"/>
                        </td>
                        <td class="withError">
                            <s:fielderror><s:param value="'creativeSize'"/></s:fielderror>
                            <s:if test="sizeLinkedCreatives != null && !sizeLinkedCreatives.isEmpty()">
                                <div>
                                    <c:set var="btnOnClick">
                                        fnShowCreatives($('div', this), $('#creativeSizesCreativesDiv'), '<s:text name="CreativeTemplate.showCreatives"/>', '<s:text name="CreativeTemplate.hideCreatives" />'); return false;
                                    </c:set>
                                    <ui:button message="CreativeTemplate.showCreatives" onclick="${btnOnClick}" />
                                    <div class="hide" id="creativeSizesCreativesDiv" name="creativeSizesCreativesDiv" style="margin-top:10px;">
                                        <display:table name="sizeLinkedCreatives" class="dataView" id="creative">
                                            <display:column titleKey="CreativeTemplate.creative">
                                                <a href="/admin/creative/view.action?id=${creative.id}"><c:out value="${creative.name}"/></a>
                                            </display:column>
                                        </display:table>
                                    </div>
                                </div>
                            </s:if>
                        </td>
                    </tr>
                </table>
            </ui:field>
            
            <ui:field labelKey="CreativeTemplate.appFmt" labelForId="applicationFormatId" required="true">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <s:select id="applicationFormatId" name="applicationFormat.id"
                                headerValue="%{getText('form.select.pleaseSelect')}" headerKey=""
                                list="availableAppFormats"
                                listKey="id" listValue="name" cssClass="smallLengthText1"/>
                        </td>
                        <td class="withError">
                            <s:fielderror><s:param value="'applicationFormat'"/></s:fielderror>
                            <s:if test="appFormatLinkedCreatives != null && !appFormatLinkedCreatives.isEmpty()">
                                <div>
                                    <c:set var="btnOnClick">
                                        fnShowCreatives($('div', this), $('#appFormatLinkedCreativesDiv'), '<s:text name="CreativeTemplate.showCreatives"/>', '<s:text name="CreativeTemplate.hideCreatives" />'); return false;
                                    </c:set>
                                    
                                    <ui:button message="CreativeTemplate.showCreatives" onclick="${btnOnClick}" />
                                    <div class="hide" id="appFormatLinkedCreativesDiv" name="appFormatLinkedCreativesDiv" style="margin-top:10px;">
                                        <display:table name="appFormatLinkedCreatives" class="dataView" id="creative">
                                            <display:column titleKey="CreativeTemplate.creative">
                                                <a href="/admin/creative/view.action?id=${creative.id}"><c:out value="${creative.name}"/></a>
                                            </display:column>
                                        </display:table>
                                    </div>
                                </div>
                            </s:if>
                        </td>
                    </tr>
                </table>
            </ui:field>
            
            <ui:field labelKey="CreativeTemplate.trImpr">
                <s:checkbox name="impressionsTrackFlag"/>
            </ui:field>

            <c:set var="templateTypes" value="<%=TemplateFileType.values()%>"/>
            <ui:field labelKey="CreativeTemplate.type" cssClass="valignFix">
                <s:radio cssClass="withInput"  id="templateType" name="type" list="#attr.templateTypes" listValue="getText('enums.TemplateFileType.' + name())" onclick="typeChanged();"/>
            </ui:field>
            
            <s:set var="creativeTemplateFile" value="%{getText('CreativeTemplate.file')}"/>

            <ui:field label="${creativeTemplateFile}" id="trWithTemplateFileName" labelForId="templateFileName" required="true" errors="templateFile">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <s:textfield id="templateFile" name="templateFile" cssClass="smallLengthText2" maxLength="1024"/>
                        </td>
                        <td class="withButton">
                            <ui:button message="form.browse" onclick="openFileBrowser();" />
                        </td>
                    </tr>
                </table>
            </ui:field>
            
        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
        <ui:button message="form.save" type="submit"/>
        <ui:button message="form.cancel" onclick="location='/${moduleName}/CreativeTemplate/view.action?id=${template.id}';" type="button" />
    </div>
</s:form>
