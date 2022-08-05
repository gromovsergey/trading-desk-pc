<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
<s:if test="searchParams.page > 1">
    (function(){
        window.location.href    = '#${searchParams.page}';
    })();
</s:if>

    $(function () {
        $('#hiddenCreativeIds').remove();
        $('#initialParams').pagingAssist({
            action: 'search.action',
            message: '${ad:formatMessage("creative.loading")}',
            autoSubmit: true,
            result: $('#creativesDiv')
        });
        $('#submitButtonId').on('click', function() {
            $('#errorsDiv').remove();
        })
    });

    var targetElementId = null;

    function openFileBrowser(accountId) {
        targetElementId = 'creatives';
        
        var cdStr   = '';
        if (document.getElementById(targetElementId) && document.getElementById(targetElementId).value !== ''){
            var path    = document.getElementById(targetElementId).value;
            path    = path.substring(path[0] !== undefined && path[0] === '/' ? 1 : 0, path.lastIndexOf("/")+1);
            cdStr   = (path !== '' && path !== '/') ? '&currDirStr='+encodeURIComponent(path):'';
        }
        
        window.open('${_context}/fileman/fileManager.action?id=' + targetElementId +
                '&mode=creative&accountId=' + accountId+cdStr,'filebrowser','width=820,height=600,resizable=yes,scrollbars=yes');
    }
</script>

<ui:header>
    <ui:pageHeadingByTitle />
    <ad:requestContext var="advertiserContext"/>
    <c:set var="accountBean" value="${advertiserContext.advertiser}"/>
    <c:if test="${ad:isPermitted('AdvertiserEntity.createDisplayCreative', accountBean)}">
        <ui:button message="creative.new.display" href="newDisplay.action?advertiserId=${advertiserContext.advertiserId}" />
    </c:if>
    <c:if test="${ad:isPermitted('AdvertiserEntity.createTextCreative', accountBean)}">
        <ui:button message="creative.new.text" href="newText.action?advertiserId=${advertiserContext.advertiserId}" />
    </c:if>
    <c:if test="${ad:isPermitted('AdvertiserEntity.createOrUpdate', accountBean)}">
        <ui:button message="creative.file.manager" onclick="openFileBrowser('${advertiserContext.advertiserId}');"/>
    </c:if>
</ui:header>

<form id="initialParams">
    <input type="hidden" name="pageSize" value="${searchParams.pageSize}"/>
    <input type="hidden" name="advertiserId" value="${advertiserContext.advertiserId}"/>
    <ui:section titleKey="form.search">
        <ui:fieldGroup>
            <ui:field labelKey="creative.campaignCreative.campaign" labelForId="campaignId">
                <s:select name="searchParams.campaignId" id="campaignId" cssClass="middleLengthText"
                    headerValue="%{getText('form.all')}" headerKey=""
                    list="campaignList" listKey="id" listValue="localizedName" value="searchParams.campaignId"/>
            </ui:field>

            <ui:field labelKey="creative.size" labelForId="sizeId">
                <s:select name="searchParams.sizeId" id="sizeId" cssClass="middleLengthText"
                    headerValue="%{getText('form.all')}" headerKey=""
                    list="sizes" listKey="id" listValue="name" value="searchParams.sizeId"/>
            </ui:field>

            <ui:field labelKey="searchParams.status" labelForId="status" id="_status">
                <s:select name="searchParams.displayStatusId" id="status"
                    list="creativeStatuses" listKey="key" listValue="value"
                    cssClass="middleLengthText" value="searchParams.displayStatusId"/>
            </ui:field>

            <ui:field labelKey="form.orderBy" labelForId="orderBy" id="_orderBy">
                <s:select name="searchParams.orderBy" id="orderBy"
                          list="orderBy" listKey="key" listValue="value" cssClass="middleLengthText" value="searchParams.orderBy"/>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button message="form.search" id="submitButtonId"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</form>
<div class="logicalBlock" id="errorsDiv"><s:actionerror escape="false"/></div>
<div class="logicalBlock" id="creativesDiv"></div>
<div id="ccgDialog" class="hide"></div>
