<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">
    var maxAdsId = "";
    var adFooterId = "";
    
    function openFileBrowser(id, fileTypes) {
        targetElementId = id;
        
        var cdStr   = '';
        if (document.getElementById(targetElementId) && document.getElementById(targetElementId).value !== ''){
            var path    = document.getElementById(targetElementId).value;
            path    = path.substring(path[0] !== undefined && path[0] === '/' ? 1 : 0, path.lastIndexOf("/")+1);
            cdStr   = (path !== '' && path !== '/') ? '&currDirStr='+encodeURIComponent(path):'';
        }
        
        var account = '&accountId=' + '${tag.account.id}';
        window.open('${_context}/fileman/fileManager.action?id='
                + targetElementId + '&mode=publisher' + account + '&fileTypes=' + fileTypes+cdStr,'filebrowser','width=820,height=600,resizable=yes,scrollbars=yes');
    }
</script>

<ui:header>
    <ui:pageHeadingByTitle />
</ui:header>

<ui:errorsBlock>
    <s:fielderror><s:param value="'options'"/></s:fielderror>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<s:form action="%{#request.moduleName}/updateCreativeCustomization" id="saveForm">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <table id="siteCustomisation">
        <tbody>
            <s:iterator value="%{sortedSizes}" var="size">
                <tr>
                    <td>
                        <div class="logicalBlock" id="optionsDiv">
                            <c:set var="optionTitleKey" value="" />
                            <c:set var="optionTitle" >
                                <c:choose>
                                    <c:when test="${ad:isPermitted0('CreativeSize.view')}">
                                        <a href="/admin/CreativeSize/view.action?id=${size.id}"><c:out value="${ad:localizeName(size.name)}"/></a>
                                    </c:when>
                                    <c:otherwise>
                                        ${ad:localizeName(size.name)}
                                    </c:otherwise>
                                </c:choose>
                                <fmt:message key="site.tag.creativeSize"/>
                            </c:set>
                            <s:set var="groups" value="#size.publisherOptionGroups"/>
                            <%@ include file="/admin/option/optionValuesEdit.jsp"%>
                        </div>
                    </td>
                </tr>
            </s:iterator>
            <s:iterator value="templates" var="creativeTemplate">
                <tr>
                    <td>
                        <c:choose>
                            <c:when  test="${creativeTemplate.defaultName == 'Text'}">
                                <div class="logicalBlock" id="optionsTextDiv">
                            </c:when>
                            <c:otherwise>
                                <div class="logicalBlock" id="optionsDiv">
                            </c:otherwise>
                        </c:choose>
                            <s:set var="groups" value="#creativeTemplate.publisherOptionGroups"/>
                            <c:set var="optionTitleKey" value="" />
                            <c:set var="optionTitle" >
                                <c:choose>
                                    <c:when  test="${ad:isPermitted0('Template.view')}">
                                        <a href="/admin/CreativeTemplate/view.action?id=${creativeTemplate.id}" target="_blank">${creativeTemplate.defaultName}</a>
                                    </c:when>
                                    <c:otherwise>
                                        ${creativeTemplate.defaultName}
                                    </c:otherwise>
                                </c:choose>
                                <fmt:message key="site.tag.template"/>
                            </c:set>
                            <%@ include file="/admin/option/optionValuesEdit.jsp"%>
                        </div>
                    <s:set var="sizesSize" value="%{getSizes().size()}"/>
                    <c:if test="${creativeTemplate.defaultName == 'Text' && sizesSize == 1}">

    <script type="text/javascript">

    var targetElementId = null;

    var maxAdsOptValue = "";
    if (maxAdsId != "" ) {
        maxAdsOptValue = ", [name*='" + maxAdsId + "']";
    }

    var footerOptValue = "";
    if (adFooterId != "") {
        footerOptValue = " , [name*='" + adFooterId + "']";
    }
    var updateLivePreview = true;
    function showSectionPreview(){
        if (updateLivePreview){
            updatePreview();
            updateLivePreview = false;
        }

    }
    $().ready(function() {
        $('#optionsTextDiv [type=text], #optionsTextDiv select, #optionsTextDiv [type=radio] ' + maxAdsOptValue + footerOptValue).liveChange(updatePreview);
        showSectionPreview();
    });

    function updatePreview() {
        $.ajax(
            {
                type: 'POST',
                url: '/xml/tagPreview.action',
                dataType: 'xml',
                data: prepareData(),
                success : previewSuccess,
                error: previewError
            }
        );
    }

    function prepareData() {
        return $("#saveForm_id, #MAX_ADS_PER_TAG " +
                "#optionsTextDiv input, #optionsTextDiv select," +
                " #optionsTextDiv .formFields :input " + maxAdsOptValue + footerOptValue).not("[name^=groupStateValues]").serialize();
    }

    function prepareCreativeSizeData() {
        return $("#saveForm").serializeArray();
        }

    function previewError(jqXHR, textStatus, errorThrown) {
        $('#tagPreview').show();
        var frame = $('#previewFrame');
        var width = 300;
        var height = 150;
        var perview = '<fmt:message key="site.tag.previewIsNotAvailable"/>';
        frame.width(width);
        frame.height(height);
        frame.show();

        frame[0].contentWindow.document.write(perview);
        frame[0].contentWindow.document.close();
    }

    function previewSuccess(data, textStatus) {
        $('#previewFrame').remove();
        $('#tagPreview .content:eq(0)').append('<iframe src="about:blank" frameborder="0" marginwidth="0" marginheight="0" scrolling="auto" id="previewFrame" />');

        var frame   = $('#previewFrame'),
        frameDoc    = frame[0].contentWindow.document;

        if ($('tagPreview', data).size() != 0){
            var width = $('tagPreview width', data).text(),
            height = $('tagPreview height', data).text(),
            preview = $('tagPreview preview', data).text();
        } else  {
            preview = '<span class="validation_error" style="display:block;color:red;">' + $('error field', data).text() + '</span>';
        }
        frameDoc.write(preview);
        frameDoc.close();

        frame.width(width || 300);
        frame.height(height || 150);
        $('#tagPreview').show();
    }
</script>
                    </td>
                    <td>
                        <ui:section id="tagPreview" titleKey="site.tag.preview">
                            <s:set name="frameWidth" value="300"/>
                            <s:set name="frameHeight" value="150"/>
                            <c:if test="${not empty size.id}">
                                <s:set name="frameWidth" value="%{size.width}"/>
                                <s:set name="frameHeight" value="%{size.height}"/>
                            </c:if>
                            <iframe name="previewFrame" id="previewFrame" width="${frameWidth}" height="${frameHeight}"
                                frameborder="0" marginwidth="0" marginheight="0" scrolling="auto"> </iframe>
                        </ui:section>
                    </c:if>
                    </td>
                </tr>
            </s:iterator>
        </tbody>
    </table>

    <div class="wrapper">
        <ui:button message="form.save" type="submit" />
        <ui:button message="form.cancel" onclick="location='view.action?id=${id}';" type="button" />
    </div>
</s:form>
