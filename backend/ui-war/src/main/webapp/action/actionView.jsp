<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<s:set name="entity" value="model"/>
<c:set var="reporting" value="${_context}/reporting"/>

<ui:externalLibrary libName="codemirror" />
<script type="text/javascript">
function linkCampaigns() {
    $.get('createLinks.action', {advertiserId:'${entity.account.id}'}, function(data){
        $('#ccgDialog').html(data).dialog({
            'title': "${ad:formatMessage("Action.linkToCampaigns")}",
            'buttons': [
                {
                    id: 'ccgDialogSubmit',
                    text: '${ad:formatMessage("Action.linkToCampaigns.btn")}',
                    click: function(){
                        if ($('#treeFilterByCampaign').find('input[type="checkbox"]').length > 0
                                && $('#treeFilterByCampaign').find('input[type="checkbox"]:checked').length == 0) {
                            showRequiredAlertTreeFilterByCampaign();
                            return false;
                        }

                        if (confirm('${ad:formatMessage("ccg.bulk.confirm")}')) {
                            $('#waiter').show();
                            var self = $(this);
                            var params = self.find(':input').serializeArray();
                            params.push({name: "conversionIds", value: ${id}});
                            params.push({name: "PWSToken", value: "${sessionScope.PWSToken}"});
                            params.push({name: "advertiserId", value: ${entity.account.id}});
                            $('#ccgDialogSubmit, #ccgDialogCancel').button("disable");
                            $.post("saveLinks.action", $.param(params, true), function(data){
                                $('#ccgDialogCancel').hide();
                                $('#ccgDialogSubmit').hide();
                                $('#ccgDialogOk').show();
                                self.html('<p style="text-align:center;color:#3c3;">'+data+'</p>');
                            }, 'json');
                        }
                    }
                },
                {
                    id: 'ccgDialogCancel',
                    text: '${ad:formatMessage("form.cancel")}',
                    click: function(){ $(this).dialog('close');  }
                },
                {
                    id: 'ccgDialogOk',
                    text: '${ad:formatMessage("form.ok")}',
                    click: function(){ $(this).dialog('close'); window.location.href = window.location.pathname + window.location.search;}
                }
            ],
            'width': 670,
            'resizable': false,
            'modal': true,
            'open': function() { 
                $('#ccgDialogOk').hide();
            }
        }).on('keypress', ':input', function(e){
            e.stopPropagation();
            if (e.keyCode === 13) {
                e.preventDefault();
                $('#ccgDialogSubmit:enabled').trigger('click');
            }
        });
    });
}
</script>

<ui:header>
    <ui:pageHeadingByTitle />
    <ad:requestContext var="advertiserContext"/>
    <c:set var="accountBean" value="${advertiserContext.advertiser}"/>
    <c:if test="${ad:isPermitted('AdvertiserEntity.update', entity)}">
        <s:url var="url" action="%{#attr.moduleName}/Action/edit" includeParams="get"/>
        <ui:button message="form.edit" href="${url}" />
    </c:if>
    <c:if test="${entity.status.letter != 'D' and ad:isPermitted('AdvertiserEntity.create', accountBean)}">
        <ui:button message="Action.linkToCampaigns" onclick="linkCampaigns()" />
    </c:if>
    <c:if test="${ad:isPermitted('Report.run', 'conversions')}">
        <ui:button message="reports.conversionsReport" href="${reporting}/conversions/options.action?conversionIds=${id}" />
    </c:if>
    <c:if test="${ad:isPermitted('Report.run', 'conversionPixels')}">
        <ui:button message="reports.conversionPixelsReport" href="${reporting}/conversionPixels/options.action?conversionIds=${id}" />
    </c:if>
    <c:if test="${ad:isPermitted('Entity.viewLog', entity)}">
        <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=13&id=${id}" />
    </c:if>
</ui:header>

<ui:section>
    <ui:fieldGroup>
        
        <s:if test="agency">
            <ui:simpleField labelKey="Action.advertiser" value="${entity.account.name}"/>
        </s:if>

        <ui:field labelKey="Action.status">
            <ui:statusButtonGroup
                descriptionKey="${displayStatus.description}"
                entity="${entity}" restrictionEntity="AdvertiserEntity"
                tipText="${statusHint}"
                deletePage="delete.action"
                undeletePage="undelete.action"/>
        </ui:field>

        <ui:simpleField labelKey="Action.conversionCategory" valueKey="${entity.conversionCategory.nameKey}"/>

        <c:set var="valueMessage" value="${ad:formatCurrency(value, account.currency.currencyCode)}"/>
        <ui:field labelKey="Action.value" tipKey="Action.value.hint">
            <c:out value="${valueMessage}"/>
        </ui:field>

        <s:if test="url != null && url.length() > 0">
            <c:choose>
                <c:when test="${ad:isUrl(entity.url)}">
                    <ui:field labelKey="Action.url">
                        <a href="<c:out value="${entity.url}"/>" target="_blank"><c:out value="${entity.url}"/></a>
                    </ui:field>
                </c:when>
                <c:otherwise>
                    <ui:simpleField labelKey="Action.url" value="${entity.url}"/>
                </c:otherwise>
            </c:choose>
        </s:if>

        <ui:field labelKey="Action.conversionTrackingPixelCode">
            <textarea data-readonly="true" class="html_highlight">${conversionTrackingPixelCode}</textarea>
        </ui:field>

        <ui:field labelKey="Action.conversionTrackingNoAudiencePixelCode">
            <textarea data-readonly="true" class="html_highlight">${conversionTrackingNoAudiencePixelCode}</textarea>
        </ui:field>

        <ui:field labelKey="Action.imagePixel">
            <textarea data-readonly="true" class="html_highlight">${imagePixel}</textarea>
        </ui:field>

        <ui:field labelKey="Action.impWindow" labelForId="impWindow" tipKey="Action.impWindow.hint">
            <c:out value="${entity.impWindow} days"/>
        </ui:field>

        <ui:field labelKey="Action.clickWindow" labelForId="clickWindow" tipKey="Action.clickWindow.hint">
            <c:out value="${entity.clickWindow} days"/>
        </ui:field>

    </ui:fieldGroup>
</ui:section>
<div id="ccgDialog" class="hide"></div>
