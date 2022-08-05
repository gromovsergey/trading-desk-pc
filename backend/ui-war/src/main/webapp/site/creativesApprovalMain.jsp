<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<script type="text/javascript">
    $(function() {
        var fResize = function(){
            var iMaxWidth   = $('#result').width(),
            iPaddings       = 38;
            
            $('.b-creative__approval-fix', '#result').remove();
            $('.b-creative__approval-list', '#result').each(function(){
                var iSum            = 0;
                $(this).children('.b-creative__approval').each(function(){
                    var iFrameW = $(this).find('iframe').eq(0).width(),
                    jqPanel     = $(this).children('.b-creative__approval-panel:eq(0)'),
                    jqButtons   = jqPanel.children('.b-creative__approval-buttons:eq(0)');

                    if (jqPanel.width() > iFrameW) {
                        if (iFrameW > jqButtons.width()) {
                            jqPanel.width(iFrameW);
                        } else {
                            jqPanel.width(jqButtons.width()+10);
                        }
                    }
                    $(this).children('.b-creative__approval-rejecttext').width(jqPanel.width());
                    iSum        += $(this).width()+iPaddings;
                    if (iSum    > iMaxWidth) {
                        iSum    = $(this).width()+iPaddings;
                        $(this).before('<div class="b-creative__approval-fix" />');
                    }
                });
            });
        }
        $('#searchForm').pagingAssist({
            action: 'search.action',
            message: '${ad:formatMessage("channel.search.loading")}',
            result: $('#result'),
            onLoad: fResize,
            onBeforeSubmit: function(callback){
                new UI.AjaxLoader().switchOn();
                callback();
            }
        });
        
        $(window).on('resize', fResize);
        
        var img = new Image();
        img.src = '/images/logo.png';
        img.onload = function() {
            $('head > link[rel*="icon"]').remove();
            var link = document.createElement('link');
            link.type = 'image/x-icon';
            link.rel = 'shortcut icon';
            link.href = img.src;
            document.getElementsByTagName('head')[0].appendChild(link);
        }
    });
</script>

<ui:pageHeadingByTitle/>

<form id="searchForm">
    <s:hidden name="site.id"/>
    <s:hidden name="site.account.id"/>

    <input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/>

    <ui:section>
        <ui:fieldGroup>

            <ui:field labelKey="site.creativesApproval.tagSize" labelForId="sizeId" errors="sizeId">
                <c:set var="localizedSizes" value="${ad:localizeEntities(sizes)}"/>
                <s:select name="searchParams.sizeId" id="sizeId" cssClass="middleLengthText"
                          headerValue="%{getText('form.all')}" headerKey=""
                          list="%{#attr.localizedSizes}" listKey="id" listValue="name"/>
            </ui:field>

            <ui:field labelKey="site.creativesApproval.destinationUrl" labelForId="destinationUrl" errors="destinationUrl" tipKey="site.creativesApproval.destinationUrl.tip">
                <s:textfield  name="searchParams.destinationUrl" id="destinationUrl" cssClass="middleLengthText" maxlength="2000"/>
            </ui:field>

            <ui:field labelKey="site.creativesApproval.approvalStatus" labelForId="approvals" errors="approvals">
                <label class="withInput narrowSet">
                    <input type="checkbox" name="searchParams.approvalStatuses" value="APPROVED"/>
                    <fmt:message key="enums.ApprovalStatus.APPROVED"/>
                </label>
                <label class="withInput narrowSet">
                    <input type="checkbox" name="searchParams.approvalStatuses" value="PENDING" ${showPending ? 'checked="checked"' : ''}/>
                    <fmt:message key="enums.ApprovalStatus.PENDING"/>
                </label>
                <label class="withInput narrowSet">
                    <input type="checkbox" name="searchParams.approvalStatuses" value="REJECTED"/>
                    <fmt:message key="enums.ApprovalStatus.REJECTED"/>
                </label>
                <label class="withInput narrowSet">
                    <input type="checkbox" name="searchParams.approvalStatuses" value="CREATIVE_CATEGORY_APPROVED"/>
                    <fmt:message key="enums.ApprovalStatus.CREATIVE_CATEGORY_APPROVED"/>
                </label>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button message="form.filter"/>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>
</form>

<s:if test="showPending">
    <script type="text/javascript">
        $().ready(function () {
            $('#searchForm').submit();
        });
    </script>
</s:if>

<div id="result" class="logicalBlock"/>
