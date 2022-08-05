<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
    function loadCreativesStatsData() {
        $('#creativeForm').submit();
    }

    function checkCampaignCreativeBatchStatusActionAndProceed(action, url, doConfirmation) {
        var creativeIds = [],
        serializedData  = $('#creativesForm').serialize();
        $('[name=creativesIds]').each(function() {
            if( $(this).prop('checked')) {
                creativeIds.push($(this).val());
            }
        });

        if(creativeIds.length == 0 ) {
            return;
        }
        
        UI.Data.get('CampaignCreativeBatchStatusActionCheck', {'action': action, 'ids': creativeIds}, function(data) {
            var result = $('result', data).text();
            var confirmed;
            if (result == 'true'){
                confirmed = !doConfirmation || (action == 'DECLINE') || confirm('<fmt:message key="confirmDelete"/>');
            } else {
                confirmed = confirm('<fmt:message key="creative.batchAction.incomplete.confirm"/>');
            }
            if (confirmed){
                $('#creativesStatsDiv').html('<img src="/images/wait-animation-small.gif">');
                $.post(url, serializedData, function(data){
                    $('#creativesStatsDiv').html(data);
                }, 'html');
            }
        });
    }

    $(function() {
        $('#creativeForm').pagingAssist({
            action:     "${_context}/campaign/group/creativesStats.action",
            autoSubmit: false,
            onLoad:     function(){
                textCWLoadCallback();
                $('#preloader').hide();
                $('#creativeForm').pagingAssist('options', {autoSubmit: true});
            },
            onBeforeSubmit: function(callback) {
                $('#creativesDiv .dataViewSection .controlsZone > td:eq(0)').children('.paginationHeader').remove(); 
                $('#preloader').show();
                callback();
            },
            message:    '${ad:formatMessage("channel.search.loading")}',
            result:     $('#creativesStatsDiv')
        });
    });
</script>


<c:set var="isCCGUpdatePermitted" value="${ad:isPermitted('AdvertiserEntity.update', model)}"/>
<c:set var="isCCGEntityCreatePermitted" value="${ad:isPermitted('AdvertiserEntity.create', model)}"/>

<ui:header styleClass="level2">
    <h2>
        <c:choose>
            <c:when test="${ccgType.letter == 'T'}">
                <fmt:message key="textcreatives"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="creatives"/>
            </c:otherwise>
        </c:choose>
    </h2>
    <c:if test="${isCCGEntityCreatePermitted}">
        <ui:button message="ccg.link.exiting.creative" href="creative/new.action?ccgId=${id}" />
        <ui:button message="ccg.create.new.creative" href="${_context}/creative/new.action?ccgId=${id}" />
    </c:if>
    <div id="preloader" style="float: right" class="hide"><img src="/images/wait-animation-small.gif"></div>
</ui:header>
<table class="dataViewSection">
    <tr class="controlsZone">
        <td>
            <s:if test="!creativeSets[0].linkedTOs.empty">
                <c:set var="canUpdateLinkedCreatives" value="${ad:isPermitted('AdvertiserEntity.update', model)}"/>
                <table class="grouping">
                    <tr>
                        <td class="withButtons">
                            <c:if test="${canUpdateLinkedCreatives}">
                                <c:set var="url">
                                    ${_context}/campaign/group/creative/bulk/activateAll.action
                                </c:set>
                                <ui:button message="form.activate" onclick="checkCampaignCreativeBatchStatusActionAndProceed('ACTIVATE', '${url}')" />

                                <c:set var="url">
                                    ${_context}/campaign/group/creative/bulk/inactivateAll.action
                                </c:set>
                                <ui:button message="form.deactivate" onclick="checkCampaignCreativeBatchStatusActionAndProceed('INACTIVATE', '${url}')" />

                                <c:set var="url">
                                    ${_context}/campaign/group/creative/bulk/deleteAll.action
                                </c:set>
                                <ui:button message="form.delete" onclick="checkCampaignCreativeBatchStatusActionAndProceed('DELETE', '${url}', true)" />

                                <c:if test="${ad:isPermitted('AdvertiserEntity.undeleteChildren', model)}">
                                    <c:set var="url">
                                        ${_context}/campaign/group/creative/bulk/undeleteAll.action
                                    </c:set>
                                    <ui:button message="form.undelete" onclick="checkCampaignCreativeBatchStatusActionAndProceed('UNDELETE', '${url}')" />
                                </c:if>
                            </c:if>
                            &nbsp;
                        </td>
                        <td class="filterZone">
                            <form id="creativeForm">
                                <ui:daterange idSuffix="Creatives" options="TOT Y T WTD MTD QTD YTD LW LM LQ LY" fastChangeId="MTD" onChange="loadCreativesStatsData();" timeZoneAccountId="${requestContexts.advertiserContext.accountId}"/>
                                <s:hidden name="id"/>
                            </form>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <c:if test="${sequentialAdservingFlag && canUpdateLinkedCreatives}">
                                <%@include file="moveToCreativeSet.jsp" %>
                            </c:if>
                        </td>
                    </tr>
                </table>
            </s:if>
        </td>
    </tr>
    <tr class="bodyZone">
        <td>
            <div class="logicalBlock" id="creativesStatsDiv">
                <%@ include file="displayCreativeStats.jsp"%>
            </div>
        </td>
    </tr>
</table>
