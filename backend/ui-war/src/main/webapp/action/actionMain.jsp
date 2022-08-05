<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">
function loadConversionsData() {
    $('#conversionsForm').submit();
}

function toggleAllConversions(header) {
    $('[name=setNumberIds]').prop({checked : header.checked});
}

$(function() {
    $('#bulk_btn').menubutton();
});

function getSelectedConversions() {
    var conversions = [];
    $('[name=setNumberIds]').each(function() {
        if (this.checked) {
            conversions.push(this.value);
        }
    });
    return conversions;
}

function linkCampaigns() {
    var conversions = getSelectedConversions();
    if (conversions.length == 0) {
        return;
    }

    $.get('createLinks.action', {advertiserId:'${advertiserId}'}, function(data){
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
                            $.each(conversions, function(i, id) {
                                params.push({name: "conversionIds", value: id});
                            });
                            params.push({name: "PWSToken", value: "${sessionScope.PWSToken}"});
                            params.push({name: "advertiserId", value: ${advertiserId}});
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

<c:set var="reporting" value="${_context}/reporting"/>
<ui:header>
    <ui:pageHeadingByTitle/>
    <ad:requestContext var="advertiserContext"/>
    <c:set var="accountBean" value="${advertiserContext.advertiser}"/>
    <c:if test="${ad:isPermitted('AdvertiserEntity.create', accountBean)}">
        <c:set var="buttHref">
            <s:url action="%{#attr.moduleName}/Action/new" includeParams="get"/>
        </c:set>
        <ui:button message="form.createNew" href="${buttHref}" />
    </c:if>
    <c:if test="${ad:isPermitted('Report.run', 'conversions')}">
        <c:set var="conversionsParameters" value="${ad:accountParam('?accountId', advertiserContext.advertiserSet ? advertiserContext.advertiserId : advertiserContext.accountId)}"/>
        <ui:button message="reports.conversionsReport" href="${reporting}/conversions/options.action${conversionsParameters}"/>
    </c:if>
    <c:if test="${ad:isPermitted('Report.run', 'conversionPixels')}">
        <c:set var="conversionPixelsParameters" value="${ad:accountParam('?accountId', advertiserContext.advertiserSet ? advertiserContext.advertiserId : advertiserContext.accountId)}"/>
        <ui:button message="reports.conversionPixelsReport" href="${reporting}/conversionPixels/options.action${conversionPixelsParameters}"/>
    </c:if>
</ui:header>
<table class="dataViewSection">
    <tr class="controlsZone">
        <td>
            <table class="grouping">
                <tr>
                    <c:if test="${ad:isPermitted('AdvertiserEntity.update', accountBean)}">
                    <td class="withButtons">
                        <a class="button" id="bulk_btn" href="#"><fmt:message key="ccg.bulk.menu"/></a>
                        <ul id="bulk_menu" class="hide b-menu__bulk">
                            <li>
                                <ui:button message="Action.linkToCampaigns" onclick="linkCampaigns()" />
                            </li>
                        </ul>
                    </td>
                    </c:if>
                    <td class="filterZone">
                        <form id="conversionsForm" action="${_context}/Action/list.action">
                            <ui:daterange options="TOT Y T WTD MTD QTD YTD LW LM LQ LY" fastChangeId="${fastChangeId}"
                                          onChange="loadConversionsData();" timeZoneAccountId="${advertiserId}" fromDateFieldName="dateRange.begin" toDateFieldName="dateRange.end"/>
                            <s:hidden name="advertiserId"/>
                        </form>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr class="bodyZone">
        <td>
            <table class="dataView" id="conversionList">
                <thead>
                    <tr><th width="1">
                            <input type="checkbox" onclick="toggleAllConversions(this)"/>
                        </th><th>
                            <s:text name="Action.name"/>
                        </th><th>
                            <s:text name="Action.conversionCategory"/>
                        </th><th>
                            <s:text name="Action.url"/>
                        </th><th>
                            <s:text name="Action.plural"/>
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <s:iterator value="actions" var="action" status="row">
                    <tr class="conversionRow"><td>
                            <input type="checkbox" name="setNumberIds" value="${action.id}"/>
                        </td><td>
                            <ui:displayStatus displayStatus="${action.displayStatus}">
                                <a href="<s:url action="%{#attr.moduleName}/Action/view"/>?id=${action.id}"><c:out value="${action.name}"/></a>
                            </ui:displayStatus>
                        </td><td>
                            <fmt:message key="${action.conversionCategory.nameKey}"/>
                        </td><td>
                            <c:choose>
                                <c:when test="${ad:isUrl(action.url)}">
                                    <a href="<c:out value="${action.url}"/>" target="_blank"><c:out value="${action.url}"/></a>
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${action.url}"/>
                                </c:otherwise>
                            </c:choose>
                        </td><td>
                            <fmt:formatNumber value="${action.conversions}" groupingUsed="true"/>
                        </td>
                    </tr>
                    </s:iterator>
                </tbody>
            </table>
        </td>
    </tr>
</table>
<div id="ccgDialog" class="hide"></div>
