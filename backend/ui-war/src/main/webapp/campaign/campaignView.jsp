<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    function openWindow(id) {
        var url = "${_context}/campaign/viewSchedule.action?id=" + id;
        url = url + "&runningKey=deliverySchedule.campaign.running&availableKey=deliverySchedule.campaign.not.running";
        var options = "resizable=no,menubar=no,status=no,toolbar=no,scrollbars=no,location=0,dialog=no,height=350,width=950,left=100,top=100,titlebar=no";
        window.open(url, "printpop", options);
    }

    function loadCampaignGroupDashboard() {
        $('#campaignGroupDashboardDiv')
                .html('<h3 class="level1">${ad:formatMessage("form.loading.resources")}</h3>')
                .load("${_context}/campaign/groupStats.action?id=${id}", $('#mainForm').serializeArray());
    }

    function getDeclineReason() {
        var declReason = '';
        do {
            declReason = prompt("<fmt:message key="decline.reason"/>", "");
            if(declReason == null || declReason.length == 0){
                return;
            }
            if (declReason.length > 500) {
                alert("<fmt:message key="decline.too.long"/>");
            }
        } while (declReason.length > 500);
        return declReason;
    }

    function checkCCGBatchStatusActionAndProceed(action, url){
        var ccgIds = getSelectedIds();
        if(ccgIds.length == 0 ){
            return;
        }

        UI.Data.get('CCGBatchStatusActionCheck', {action: action, ids: ccgIds}, function(data) {
                var result = $('result', data).text();
                var confirmed;
                if(result == 'true'){
                    confirmed = (action == 'DECLINE') || (ccgIds.length == 1) || confirm('${ad:formatMessage('confirmBulkChange')}');
                } else {
                    confirmed = confirm('${ad:formatMessage('campaign.creative.group.batchAction.incomplete.confirm')}');
                }
                if(confirmed){
                    if (action == 'DECLINE') {
                        var declReason = getDeclineReason();
                        if(declReason == null){
                            return;
                        }
                        $('#declReason').val(declReason);
                    }
                    $('#mainForm').attr('action', url).submit();
                }
        });
    }

    function proceedUrl(url) {
        var ccgIds = getSelectedIds();
        if(ccgIds.length == 0) {
            return;
        }
        var params = '';
        $.each(ccgIds, function(i, id) {
            params += '<input type="hidden" name="ids" value="'+id+'">';
        });
        $('<form action="'+url+'" method="POST">'+params+'</form>').appendTo('body').submit();
    }

    $(function(){
        $('.row_selectable').on('click', 'tr', function(e){
            var jqChbx  = $(this).children('td:eq(0)').children('input:eq(0)');
            if(jqChbx.prop('disabled')) return;
            jqChbx.prop('checked', !jqChbx.prop('checked')).change();
            jqChbx.prop('checked') ? $(this).addClass('on') : $(this).removeClass('on');
        }).on('click', 'a', function(e){
            e.stopPropagation();
        }).on('click change', '.row_select_box', function(e){
            e.stopPropagation();
            if($(this).prop('disabled')) return;
            $(this).prop('checked') ? $(this).closest('tr').addClass('on') : $(this).closest('tr').removeClass('on');
        });

        $('.row_select_box', '#ccgStatsTable').trigger('change');
        $('#bulk_btn').menubutton();
        $('#bulk_group_creation_menu').menubutton();

        $('#bulk_menu').on('click', 'li.bulk', function(e){
            e.preventDefault();
            var sTitle = $(this).text();
            var sDialogWidth = $(this).data('dialog-width') || '670';

            if ($('.row_select_box:checked').length === 0) return;

            var ccgIds = getSelectedIds();
            var url = $(this).data('url');
            if ($(this).data('withids')) {
                url = url + "&" + $.param({ids:ccgIds}, true);
            }

            $.get(url, {}, function(data){
                $('#ccgDialog').html(data).dialog({
                    'title': sTitle,
                    'buttons': [
                        {
                            id: 'ccgDialogSubmit',
                            text: '${ad:formatMessage("form.submit")}',
                            click: function(){
                                if (confirm('${ad:formatMessage("ccg.bulk.confirm")}')) {
                                    var self = $(this);
                                    var form = self.find('form');
                                    // submit data from device bulk form
                                    if (window.deviceFormSubmit !== undefined && (typeof window.deviceFormSubmit == 'function')) {
                                        deviceFormSubmit();
                                    }
                                    var params = form.find(':input').serializeArray();
                                    $.each(ccgIds, function(i, id) {
                                        params.push({name: "ids", value: id});
                                    });
                                    $('#ccgDialogSubmit, #ccgDialogCancel').button("disable");
                                    $.post(form.attr('action'), $.param(params, true), function(data){
                                        self.html(data);
                                        $('#ccgDialogSubmit, #ccgDialogCancel').button("enable");
                                    }, 'html');
                                }
                            }
                        },
                        {
                            id: 'ccgDialogCancel',
                            text: '${ad:formatMessage("form.cancel")}',
                            click: function(){ $(this).dialog('close'); }
                        }
                    ],
                    'width': sDialogWidth,
                    'resizable': false,
                    'modal': true
                }).on('keypress', ':input', function(e){
                    e.stopPropagation();
                    if (e.keyCode === 13) {
                        e.preventDefault();
                        $('#ccgDialogSubmit:enabled').trigger('click');
                    }
                });
            }, 'html');
        });
    });

</script>

<c:set var="isCampaignUpdatePermitted" value="${ad:isPermitted('AdvertiserEntity.update', model)}"/>

<ui:header>
    <ui:pageHeadingByTitle />
            <c:if test="${isCampaignUpdatePermitted}">
                <ui:button message="form.edit" href="edit.action?id=${id}"/>
            </c:if>

            <c:if test="${ad:isPermitted('AdvertiserEntity.createCopy', model)}">
                   <ui:postButton message="form.createCopy" href="createCopy.action"
                                  onclick="return UI.Util.confirmCopy(this);"
                                  entityId="${id}" />
            </c:if>

            <c:if test="${ad:isPermitted('Entity.viewLog', model)}">
                <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=4&id=${id}" />
            </c:if>

</ui:header>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
    <s:fielderror><s:param value="'name'"/></s:fielderror>
</ui:errorsBlock>

<s:if test="isNoFreqCapsWarning()">
    <ui:section cssClass="message warning">
        <fmt:message key="creative.nofrequencycaps"/>
    </ui:section>
</s:if>
<ui:section titleKey="form.general.properties">
    <table class="grouping fieldGroups">
        <tr>
            <td>
                <ui:fieldGroup>
                    <ui:field labelKey="campaign.status">
                        <ui:statusButtonGroup
                            descriptionKey="${displayStatus.description}"
                            entity="${model}" restrictionEntity="AdvertiserEntity"
                            activatePage="activate.action" inactivatePage="inactivate.action"
                            deletePage="delete.action" undeletePage="undelete.action"
                            />
                    </ui:field>

                    <ui:frequencyCapView/>

                    <c:if test="${ad:isPermitted('Campaign.viewCommission', model)}">
                        <ui:field labelKey="campaign.commission">
                            <c:set var="textVal"><fmt:formatNumber value="${commission * 100}" maxFractionDigits="3"/>%</c:set>
                            <ui:text text="${pageScope.textVal}"/>
                        </ui:field>
                    </c:if>

                    <s:if test="dateStart != null">
                        <ui:field labelKey="creative.campaignCreative.dates">
                            <fmt:formatDate value="${dateStart}" type="both"
                                            timeStyle="short" dateStyle="short"
                                            timeZone="${account.timezone.key}"
                                            var="formattedDateStart"/>
                            <c:out value="${formattedDateStart}"/>
                            &ndash;
                            <c:set var="textVal">
                                <s:if test="dateEnd != null">
                                    <fmt:formatDate value="${dateEnd}" type="both"
                                                    timeStyle="short" dateStyle="short"
                                                    timeZone="${account.timezone.key}"
                                                    var="formattedDateEnd"/>
                                    <c:out value="${formattedDateEnd}"/>
                                </s:if>
                                <s:else>
                                    <fmt:message key="campaign.notSet" />
                                </s:else>
                            </c:set>
                            <ui:text subClass="date" text="${pageScope.textVal}"/>
                        </ui:field>
                    </s:if>

                    <s:if test="deliverySchedule">
                        <ui:field labelKey="deliverySchedule.label">
                            <ui:button message="view.deliverySchedule" onclick="openWindow('${id}');"/>
                        </ui:field>
                    </s:if>

                    <c:if test="${not empty excludedChannels}">
                        <ui:field labelKey="campaign.excludedChannels">
                            <ad:commaWriter var="channel" items="${excludedChannels}">
                                <c:set var="channelName" value="${ad:appendStatus(channel.name, channel.status)}"/>
                                <c:choose>
                                    <c:when test="${ad:isPermitted('AdvertisingChannel.view', channel)}">
                                        <a href="${_context}/channel/view.action?id=${channel.id}"><c:out value="${channelName}"/></a>
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value="${channelName}"/>
                                    </c:otherwise>
                                </c:choose>
                            </ad:commaWriter>
                        </ui:field>
                    </c:if>

                    <s:if test="isInternal()">
                        <ui:simpleField labelKey="ccg.bidStrategy" valueKey="campaign.bidStrategy.${model.bidStrategy}" />
                    </s:if>

                    <s:if test="marketplaceType != null && marketplaceType.name() != 'NOT_SET'">
                        <ui:simpleField labelKey="campaign.walledGarden" valueKey="WalledGarden.agency.marketplace.${marketplaceType}"/>
                    </s:if>

                </ui:fieldGroup>
            </td>
            <td>
                <ui:fieldGroup>
                    <s:if test="totalBudget != null">
                        <c:set var="budgetMessage" value="${ad:formatCurrency(totalBudget, account.currency.currencyCode)}"/>
                    </s:if>
                    <s:else>
                        <fmt:message key="campaign.budget.unlimited" var="budgetMessage"/>
                    </s:else>

                    <ui:simpleField labelKey="campaign.total.budget" value="${budgetMessage}"/>

                    <c:if test="${ad:isInternal()}">
                        <ui:simpleField labelKey="campaign.spent.budget"
                                value="${ad:formatCurrency(campaignStats.spentBudget, account.currency.currencyCode)}"/>

                        <ui:simpleField labelKey="campaign.available.budget" value="${ad:formatCurrency(availableBudget, account.currency.currencyCode)}"/>

                        <%--ToDo: uncomment when needed (OUI-28825)--%>
                        <%--<c:if test="${ad:isPermitted('Campaign.viewAvailableAccountCredit', model)}">--%>
                            <%--<ui:simpleField labelKey="campaign.available.credit"--%>
                                    <%--value="${ad:formatCurrency(availableCredit, account.currency.currencyCode)}"/>--%>
                        <%--</c:if>--%>
                    </c:if>

                    <s:set var="formattedDailyBudget">${ad:formatCurrency(calculatedDailyBudget, account.currency.currencyCode)}</s:set>
                    <s:include value="deliveryPacingView.jsp">
                        <s:param name="deliveryPacingLetter" value="deliveryPacing.letter"/>
                        <s:param name="formattedDailyBudget" value="formattedDailyBudget"/>
                        <s:param name="type" value="campaignType.letter"/>
                        <s:param name="includedInCampaign" value="%{true}"/>
                    </s:include>

                </ui:fieldGroup>
            </td>
            <td>
                <ui:fieldGroup>
                    <fmt:formatNumber var="totalImpsString" value="${campaignStats.imps}" groupingUsed="true"/>
                    <ui:simpleField labelKey="campaign.impressions.total" value="${totalImpsString}"/>

                    <ui:field labelKey="campaign.clicks.total">
                        <c:set var="textVal"><fmt:formatNumber value="${campaignStats.clicks}" groupingUsed="true"/>
                            <c:if test="${campaignStats.ctr > 0}">
                                (<fmt:formatNumber value="${campaignStats.ctr}" groupingUsed="false" maxFractionDigits="2"/>%)
                            </c:if>
                        </c:set>
                        <ui:text text="${pageScope.textVal}"/>
                    </ui:field>

                    <c:if test="${campaignStats.showPostImpConv}">
                        <ui:field labelKey="campaign.conversions.postImpConv">
                            <c:set var="textVal"><fmt:formatNumber value="${campaignStats.postImpConv}" groupingUsed="true"/>
                                <c:if test="${campaignStats.postImpConvCr > 0}">
                                    (<fmt:formatNumber value="${campaignStats.postImpConvCr}" groupingUsed="false" maxFractionDigits="2"/>%)
                                </c:if>
                            </c:set>
                            <c:choose>
                                <c:when test="${ad:isPermitted('Report.run', 'conversions')}">
                                    <c:set var="reporting" value="${_context}/reporting"/>
                                    <ui:button messageText="${pageScope.textVal}" href="${reporting}/conversions/options.action?campaignIds=${id}" />
                                </c:when>
                                <c:otherwise>
                                    <ui:text text="${pageScope.textVal}"/>
                                </c:otherwise>
                            </c:choose>
                        </ui:field>
                    </c:if>

                    <c:if test="${campaignStats.showPostClickConv}">
                        <ui:field labelKey="campaign.conversions.postClickConv">
                            <c:set var="textVal"><fmt:formatNumber value="${campaignStats.postClickConv}" groupingUsed="true"/>
                                <c:if test="${campaignStats.postClickConvCr > 0}">
                                    (<fmt:formatNumber value="${campaignStats.postClickConvCr}" groupingUsed="false" maxFractionDigits="2"/>%)
                                </c:if>
                            </c:set>
                            <c:choose>
                                <c:when test="${ad:isPermitted('Report.run', 'conversions')}">
                                    <c:set var="reporting" value="${_context}/reporting"/>
                                    <ui:button messageText="${pageScope.textVal}" href="${reporting}/conversions/options.action?campaignIds=${id}" />
                                </c:when>
                                <c:otherwise>
                                    <ui:text text="${pageScope.textVal}"/>
                                </c:otherwise>
                            </c:choose>
                        </ui:field>
                    </c:if>

                    <fmt:formatNumber var="totalUniqueUsersString" value="${campaignStats.totalUniqueUsers}" groupingUsed="true"/>
                    <ui:simpleField labelKey="campaign.unique.users.total" value="${totalUniqueUsersString}"/>

                    <s:if test="isInternal()">
                        <fmt:formatNumber var="formattedPubShare" value="${maxPubShare * 100}" groupingUsed="false"/>
                        <ui:simpleField labelKey="campaign.maxPubShare" value="${formattedPubShare}%"/>
                    </s:if>

                </ui:fieldGroup>
            </td>
        </tr>
    </table>
</ui:section>

<c:set var="reportPathPrefix" value="${_context}"/>
<c:choose>
    <c:when test="${campaignType == 'DISPLAY'}">
        <s:url var="reportUrl" value="%{#attr.reportPathPrefix}/reporting/displayAdvertising/options.action" >
            <s:param name="campaignIds" value="id"/>
        </s:url>
    </c:when>
    <c:when test="${campaignType == 'TEXT'}">
        <s:url var="textReportUrl" value="%{#attr.reportPathPrefix}/reporting/textAdvertising/options.action">
            <s:param name="campaignIds" value="id"/>
        </s:url>
    </c:when>
</c:choose>

<form action="view.action?id=${id}" id="mainForm" method="post">
    <input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/>
    <input type="hidden" name="declinationReason" value="" id="declReason"/>
    <input type="hidden" name="campaignType" value="${campaignType}" id="campaignType" />
    <ui:chart type="campaignChart" id="${id}" total="${campaignStats.imps}" msgKey="chart.message.nodata.campaign"
          reportLink="${reportUrl}" textReportLink="${textReportUrl}" selected="${xSelect},${y1Select},${y2Select}" />

    <s:if test="isInternal()">
        <ad:requestContext var="advertiserContext"/>
        <c:set var="accountId" value="${advertiserContext.accountId}"/>
    </s:if>
    <s:else>
        <c:set var="accountId" value="${_principal.accountId}" />
    </s:else>

    <c:if test="${campaignType == 'DISPLAY'}">
        <ui:header styleClass="level2">
            <h2><fmt:message key="campaign.display.creative.groups"/></h2>
            <c:if test="${ad:isPermitted('AdvertiserEntity.createDisplayGroup', model)}">
                <a class="button" id="bulk_group_creation_menu" href="#"><fmt:message key="campaign.new.group"/></a>
                <ul class="hide b-menu__bulk">
                    <li><ui:button message="campaign.new.display.group" href="group/newDisplay.action?campaignId=${id}" /></li>
                    <li><ui:button message="campaign.new.display.group.wizard" href="group/newDisplayWizardTarget.action?campaignId=${id}" /></li>
                </ul>
            </c:if>
        </ui:header>
    </c:if>
    <c:if test="${campaignType == 'TEXT'}">
        <c:set var="canCreateKeywordTargetedTextGroup" value="${ad:isPermitted('AdvertiserEntity.createKeywordTargetedTextGroup', model)}"/>
        <c:set var="canCreateChannelTargetedTextGroup" value="${ad:isPermitted('AdvertiserEntity.createChannelTargetedTextGroup', model)}"/>
        <ui:header styleClass="level2">
            <h2><fmt:message key="campaign.text.creative.groups"/></h2>
            <c:if test="${canCreateChannelTargetedTextGroup or canCreateKeywordTargetedTextGroup}">
                <a class="button" id="bulk_group_creation_menu" href="#"><fmt:message key="campaign.new.group"/></a>
                <ul class="hide b-menu__bulk">
                    <c:if test="${canCreateChannelTargetedTextGroup}">
                        <li><ui:button message="campaign.newChannelTargetedTextGroup" href="group/newChannelTargetedText.action?campaignId=${id}"  /></li>
                        <li><ui:button message="campaign.newChannelTargetedTextGroupWizard" href="group/newChannelTargetedTextWizardTarget.action?campaignId=${id}"  /></li>
                    </c:if>
                    <c:if test="${canCreateKeywordTargetedTextGroup}">
                        <li><ui:button message="campaign.new.text.group" href="group/newText.action?campaignId=${id}" /></li>
                    </c:if>
                </ul>
            </c:if>
        </ui:header>
    </c:if>

    <s:if test="!groups.empty">
        <table class="dataViewSection" id="groupsSection">
            <tr class="controlsZone">
                <td>
                    <table class="grouping">
                        <tr>
                            <td class="withButtons">
                                <c:if test="${isCampaignUpdatePermitted}">
                                    <c:set var="showbatchOperations" value="true"/>
                                </c:if>
                                <c:if test="${ad:isPermitted('AdvertiserEntity.approveChildren', model)}">
                                    <c:set var="showbatchOperations" value="true"/>
                                    <c:set var="isApprovePermitted" value="true"/>
                                </c:if>
                                <c:if test="${not empty showbatchOperations}">
                                    <a class="button" id="bulk_btn" href="#"><fmt:message key="ccg.bulk.menu"/></a>
                                </c:if>
                                <ul id="bulk_menu" class="hide b-menu__bulk">
                                    <c:if test="${isCampaignUpdatePermitted}">
                                        <c:set var="url" value="activateGroups.action?id=${id}"/>
                                        <li><ui:button message="form.activate" onclick="checkCCGBatchStatusActionAndProceed('ACTIVATE', '${url}')" /></li>
                                        <c:set var="url" value="inactivateGroups.action?id=${id}"/>
                                        <li><ui:button message="form.deactivate" onclick="checkCCGBatchStatusActionAndProceed('INACTIVATE', '${url}')" /></li>
                                    </c:if>
                                    <c:if test="${isApprovePermitted}">
                                        <c:set var="url" value="approveGroups.action?id=${id}"/>
                                        <li><ui:button message="form.approve" onclick="checkCCGBatchStatusActionAndProceed('APPROVE', '${url}')" /></li>
                                        <c:set var="url" value="declineGroups.action?id=${id}"/>
                                        <li><ui:button message="form.decline" onclick="checkCCGBatchStatusActionAndProceed('DECLINE', '${url}')" /></li>
                                    </c:if>
                                    <c:if test="${isCampaignUpdatePermitted}">
                                        <li class="ui-menu-divider">&nbsp;</li>
                                        <li class="bulk" data-url="${_context}/group/bulk/frequencyCaps/edit.action?campaignId=${id}">
                                            <ui:button message="ccg.bulk.menu.frequencyCaps" href="#" />
                                        </li>
                                        <li class="bulk" data-withids="true" data-url="${_context}/group/bulk/geotarget/edit.action?campaignId=${id}">
                                            <ui:button message="ccg.bulk.menu.geotarget" href="#"/>
                                        </li>
                                        <li class="bulk" data-dialog-width="440" data-url="${_context}/group/bulk/ccgRate/edit.action?campaignId=${id}">
                                            <ui:button message="ccg.bulk.menu.ccgRates" href="#" />
                                        </li>
                                        <li class="bulk" data-url="${_context}/group/bulk/bidStrategy/edit.action?campaignId=${id}">
                                            <ui:button message="ccg.bulk.menu.bidStrategy" href="#" />
                                        </li>
                                        <c:if test="${ad:isPermitted('AdvertiserEntity.editSiteTargeting', account)}">
                                            <li class="bulk" data-url="${_context}/group/bulk/siteTargeting/edit.action?campaignId=${id}">
                                                <ui:button message="ccg.bulk.menu.siteTargeting" href="#" />
                                            </li>
                                        </c:if>
                                        <li class="bulk" data-url="${_context}/group/bulk/deviceTargeting/edit.action?campaignId=${id}">
                                            <ui:button message="ccg.bulk.menu.deviceTargeting" href="#" />
                                        </li>
                                        <li class="ui-menu-divider">&nbsp;</li>
                                        <c:choose>
                                            <c:when test="${campaignType == 'TEXT'}">
                                                <li><ui:button message="ccg.bulk.menu.textAd" href="#" onclick="proceedUrl('${_context}/creative/new.action?campaignId=${id}')"/></li>
                                            </c:when>
                                            <c:otherwise>
                                                <li><ui:button message="ccg.bulk.menu.creative" href="#" onclick="proceedUrl('${_context}/creative/new.action?campaignId=${id}')"/></li>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${isCampaignUpdatePermitted}">
                                            <li class="bulk" data-url="${_context}/group/bulk/clickUrls/edit.action?campaignId=${id}">
                                                <ui:button message="ccg.bulk.menu.clickUrls" href="#" />
                                            </li>
                                        </c:if>
                                    </c:if>
                                </ul>
                            </td>
                            <td class="filterZone">
                                <ui:daterange idSuffix="CCGS" options="TOT Y T WTD MTD QTD YTD LW LM LQ LY"
                                              fastChangeId="${fastChangeIdCCGS}" onChange="loadCampaignGroupDashboard();"
                                              timeZoneAccountId="${accountId}"/>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr class="bodyZone">
                <td>
                    <div class="logicalBlock" id="campaignGroupDashboardDiv">
                        <%@ include file="/campaign/groupStats.jsp" %>
                    </div>
                </td>
            </tr>
        </table>
    </s:if>
    <s:else>
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </s:else>
</form>

<c:set var="isAllocationViewPermitted" value="${ad:isInternal() and ad:isPermitted('CampaignAllocation.view', model)}"/>
<c:if test="${isAllocationViewPermitted}">
    <ui:header styleClass="level2">
        <a name="campaignAllocation"></a>
        <h2><fmt:message key="campaignAllocation.title"/></h2>
        <c:set var="isAllocationEditPermitted" value="${ad:isPermitted('CampaignAllocation.createUpdate', model)}"/>
        <c:if test="${isAllocationEditPermitted}">
            <ui:button message="form.edit" href="allocation/edit.action?id=${id}"/>
        </c:if>
        <ui:button message="campaignAllocation.history" href="/admin/reporting/campaignAllocationHistory/run.action?campaignId=${id}" target="_blank" />
    </ui:header>

    <s:if test="campaignCreditAllocation == null && campaignAllocations.empty">
        <div class="wrapper">
            <fmt:message key="campaignAllocation.emptyList"/>
        </div>
    </s:if>
    <s:else>
        <table class="dataView">
            <thead>
            <tr id="tableHeader">
                <th><s:text name="campaignAllocation.order"/></th>
                <th><s:text name="campaignAllocation.type"/></th>
                <th><s:text name="campaignAllocation.id"/></th>
                <th><s:text name="campaignAllocation.allocationAmount"/></th>
                <th><s:text name="campaignAllocation.utilizedAmount"/></th>
                <th><s:text name="campaignAllocation.availableAmount"/></th>
            </tr>
            </thead>

            <tbody>
            <s:if test="campaignCreditAllocation != null">
                <tr>
                    <td class="number">1</td>
                    <td><fmt:message key="campaignAllocation.status.campaignCredit"/></td>
                    <td>
                        <a href="${_context}/campaignCredit/view.action?id=${campaignCreditAllocation.campaignCredit.id}">
                            <fmt:message key="CampaignCredit.id">
                                <fmt:param value="${campaignCreditAllocation.campaignCredit.id}"/>
                            </fmt:message>
                        </a>
                    </td>
                    <td class="number">${ad:formatCurrency(campaignCreditAllocation.allocatedAmount, account.currency.currencyCode)}</td>
                    <td class="number">${ad:formatCurrency(campaignCreditAllocation.usedAmount, account.currency.currencyCode)}</td>
                    <td class="number">${ad:formatCurrency(campaignCreditAllocation.availableAmount, account.currency.currencyCode)}</td>
                </tr>
            </s:if>

            <c:forEach items="${campaignAllocations}" var="campaignAllocation">
                <tr>
                    <s:if test="campaignCreditAllocation != null">
                        <td class="number"><c:out value="${campaignAllocation.order + 1}"/></td>
                    </s:if>
                    <s:else>
                        <td class="number"><c:out value="${campaignAllocation.order}"/></td>
                    </s:else>
                    <td><fmt:message key="campaignAllocation.status.insertionOrder"/></td>
                    <td>
                        <a href="${_context}/insertionOrder/view.action?id=${campaignAllocation.opportunity.id}&campaignId=${id}">
                            <fmt:message key="campaignAllocation.ioNumber">
                                <fmt:param value="${campaignAllocation.opportunity.ioNumber}"/>
                            </fmt:message>
                        </a>
                    </td>
                    <td class="number">${ad:formatCurrency(campaignAllocation.amount, account.currency.currencyCode)}</td>
                    <td class="number">${ad:formatCurrency(campaignAllocation.utilizedAmount, account.currency.currencyCode)}</td>
                    <td class="number">${ad:formatCurrency(campaignAllocation.availableAmount, account.currency.currencyCode)}</td>
                </tr>
            </c:forEach>

            <tr class="total">
                <td>&nbsp;</td>
                <td class="totalText">
                    <fmt:message key="campaignAllocation.totals"/>
                </td>
                <td>&nbsp;</td>
                <td class="number">${ad:formatCurrency(campaignAllocationsTotal.amount, account.currency.currencyCode)}</td>
                <td class="number">${ad:formatCurrency(campaignAllocationsTotal.utilisedAmount, account.currency.currencyCode)}</td>
                <td class="number">${ad:formatCurrency(campaignAllocationsTotal.availableAmount, account.currency.currencyCode)}</td>
            </tr>
            </tbody>
        </table>
    </s:else>
</c:if>

<%--ToDo: uncomment when needed (OUI-28825)--%>
<%--<c:if test="${ad:isInternal() and account.accountType.perCampaignInvoicingFlag and ad:isPermitted('Account.view', account)}">--%>
    <%--<h2><fmt:message key="invoices"/></h2>--%>

    <%--<display:table name="campaignInvoices" class="dataView" id="invoice">--%>
        <%--<display:setProperty name="basic.msg.empty_list">--%>
            <%--<div class="wrapper">--%>
                <%--<fmt:message key="no.invoices"/>--%>
            <%--<div class="wrapper">--%>
        <%--</display:setProperty>--%>
        <%--<display:column titleKey="invoice.date" class="date">--%>
            <%--<a href="invoiceView.action?id=${invoice.id}">--%>
                <%--<fmt:formatDate value="${invoice.invoiceDate}" type="date" dateStyle="short"/>--%>
            <%--</a>--%>
        <%--</display:column>--%>
        <%--<display:column titleKey="invoice.status">--%>
            <%--<fmt:message key="enums.FinanceStatus.${invoice.status}"/>--%>
        <%--</display:column>--%>
        <%--<display:column titleKey="invoice.dueDate" class="date">--%>
            <%--<fmt:formatDate value="${invoice.dueDate}" type="date" dateStyle="short"/>--%>
        <%--</display:column>--%>
        <%--<display:column titleKey="account.invoice.totalAmountPayable" class="number">--%>
            <%--${ad:formatCurrency(invoice.totalAmountDue, invoice.account.currency.currencyCode)}--%>
        <%--</display:column>--%>
    <%--</display:table>--%>
<%--</c:if>--%>

<div id="ccgDialog" class="hide"></div>
