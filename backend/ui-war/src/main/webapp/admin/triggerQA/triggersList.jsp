<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<script type="text/javascript">

    function confirmUpdate() {
        var ans = confirm('${ad:formatMessage("triggersApproval.confirmationOnUpdate")}');
        if (!ans) return;
        var params = $('#update').serializeArray();
        $('#result')
            .html('<h3 class="level1">${ad:formatMessage("triggersApproval.loading")}</h3>')
            .load('/admin/Triggers/update.action', params, function(responseText, textStatus, XMLHttpRequest){
                if (textStatus == 'success') {
                    $('#result').find('.pagingButton').each(function(){
                        $(this).attr({'href':'#'+$(this).data('page')+'s'+(sessionStorage.getItem('paLastSession') || 1)});
                    });
                }
            });
    }
    
</script>


<%--<ui:pageHeadingByTitle/>--%>

<s:if test="hasActionErrors()">
    <div style="margin-top: 5px; margin-bottom: 5px">
        <s:actionerror/>
    </div>
</s:if>

<s:if test="isSaved()">
    <h3><s:text name="triggersApproval.saved"/></h3>
</s:if>

<form id="searchParams">
    <s:hidden name="searchParams.triggerType"/>
    <s:hidden name="searchParams.criteria"/>
    <s:hidden name="searchParams.approval"/>
    <s:hidden name="searchParams.roleName"/>
    <s:hidden name="searchParams.ccgAccountId"/>
    <s:hidden name="searchParams.channelAccountId"/>
    <s:hidden name="searchParams.visibility"/>
    <s:hidden name="searchParams.displayStatusId"/>
    <s:hidden name="searchParams.channelId"/>
    <s:hidden name="searchParams.countryCode"/>
    <s:hidden name="searchParams.type"/>
    <s:hidden name="searchParams.advertiserId"/>
    <s:hidden name="searchParams.campaignId"/>
    <s:hidden name="searchParams.ccgId"/>
    <s:hidden name="searchParams.filterBy"/>
    <s:hidden name="searchParams.discoverAccountId"/>
    <s:hidden name="searchParams.discoverDisplayStatusId"/>
    <s:hidden name="searchParams.discoverChannelListId"/>
    <s:hidden name="searchParams.discoverChannelId"/>
    <s:hidden name="searchParams.orderBy"/>
</form>
<s:if test="triggers != null && triggers.size > 0">
<form id="update">
    
        <script type="text/javascript">
            <c:if test="${ad:isPermitted0('TriggerQA.update')}">
            function checkAllRadios(value) {
                var radios = $("input[type=radio][value=" + value + "]");
                for (var i = 0; i < radios.length; i++) {
                    //do not change value of type radio
                    if (radios[i].name != 'searchParams.type') {
                        radios[i].checked = true;
                    } 
                }
            }
            </c:if>
            
            $(function(){
                <c:if test="${searchParams.total > searchParams.pageSize}">
                    $('#triggerList .triggerRow').each(function(i){
                        if (i >= ${searchParams.pageSize}) {
                            $(this).remove();
                        }
                    });
                </c:if>
            });
        </script>
    

    <%-- search params --%>
    <s:hidden name="searchParams.triggerType"/>
    <s:hidden name="searchParams.criteria"/>
    <s:hidden name="searchParams.approval"/>
    <s:hidden name="searchParams.roleName"/>
    <s:hidden name="searchParams.ccgAccountId"/>
    <s:hidden name="searchParams.channelAccountId"/>
    <s:hidden name="searchParams.visibility"/>
    <s:hidden name="searchParams.displayStatusId"/>
    <s:hidden name="searchParams.channelId"/>
    <s:hidden name="searchParams.page"/>
    <s:hidden name="searchParams.pageSize"/>
    <s:hidden name="searchParams.total"/>
    <s:hidden name="searchParams.countryCode"/>
    <s:hidden name="searchParams.type"/>
    <s:hidden name="searchParams.advertiserId"/>
    <s:hidden name="searchParams.campaignId"/>
    <s:hidden name="searchParams.ccgId"/>
    <s:hidden name="searchParams.filterBy"/>
    <s:hidden name="searchParams.discoverAccountId"/>
    <s:hidden name="searchParams.discoverDisplayStatusId"/>
    <s:hidden name="searchParams.discoverChannelListId"/>
    <s:hidden name="searchParams.discoverChannelId"/>
    <s:hidden name="searchParams.orderBy"/>
    <input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/>
    
    <ui:pages pageSize="${searchParams.pageSize}"
              total="${searchParams.total}"
              selectedNumber="${searchParams.page}"
              handler="goToPage"
              withoutNumbers="true"
              displayHeader="true"/>

    <c:set var="disabledRadio" value="${ad:isPermitted0('TriggerQA.update') ? '' : 'disabled=\"disabled\"'}"/>
    <table class="dataView" id="triggerList">
        <thead>
        <tr>
            <th rowspan="2"><s:text name="TriggersApproval.trigger"/></th>
            <th rowspan="2"><s:text name="TriggersApproval.channels"/></th>
            <th colspan="3">
                <s:text name="TriggersApproval"/>:
                <s:text name="global.country.%{searchParams.countryCode}.name"/>,
                <s:text name="TriggersApproval.channel.type.%{searchParams.type}"/>
            </th>
        </tr>
        <tr>
            <th>
                <s:text name="TriggersApproval.approve"/>
                <c:if test="${ad:isPermitted0('TriggerQA.update')}">
                    <ui:button message="form.all" type="button" onclick="checkAllRadios('APPROVED')"/>
                </c:if>
            </th>
            <th>
                <s:text name="TriggersApproval.decline"/>
                <c:if test="${ad:isPermitted0('TriggerQA.update')}">
                    <ui:button message="form.all" type="button" onclick="checkAllRadios('DECLINED')"/>
                </c:if>
            </th>
            <th>
                <s:text name="TriggersApproval.pending"/>
                <c:if test="${ad:isPermitted0('TriggerQA.update')}">
                    <ui:button message="form.all" type="button" onclick="checkAllRadios('HOLD')"/>
                </c:if>
            </th>
        </tr>
        </thead>
        <tbody>
        <s:iterator value="triggers" var="trigger" status="row">
            <tr class="triggerRow"><td>
                    <s:hidden name="triggers[%{#row.index}].id"/>
                    <s:if test="%{#trigger.triggerType.letter == 'U'}">
                        <a href="http://${ad:extractUrlFromTrigger(originalTrigger)}" target="_blank" id="_urlTrigger_${row.index}">
                            <s:property value="originalTrigger" escape="true"/>
                        </a>
                    </s:if>
                    <s:else>
                        <s:property value="originalTrigger" escape="true"/>
                    </s:else>
                    <s:fielderror><s:param value="'triggers[' + #row.index + '].originalTrigger'"/></s:fielderror>
                </td><td>
                    <s:iterator value="channels" var="channel" status="channelStatus">
                        <s:if test="not #channelStatus.first">, </s:if>
                        <a href="/admin/channel/view.action?standalone=true&id=${channel.id}" target="_blank"><s:property value="name"/></a>
                    </s:iterator>
                </td><td>
                    <input type="radio" name="triggers[${row.index}].qaStatus" value="APPROVED" ${trigger.qaStatus.letter =='A' ? 'checked="checked"' : 'false'} ${disabledRadio}/>
                </td><td>
                    <input type="radio" name="triggers[${row.index}].qaStatus" value="DECLINED" ${trigger.qaStatus.letter =='D' ? 'checked="checked"' : 'false'} ${disabledRadio}/>
                </td><td>
                    <input type="radio" name="triggers[${row.index}].qaStatus" value="HOLD" ${trigger.qaStatus.letter == 'H' ? 'checked="checked"' : 'false'} ${disabledRadio}/>
                </td>
            </tr>
        </s:iterator>
        </tbody>
    </table>

    <ui:pages pageSize="${searchParams.pageSize}"
              total="${searchParams.total}"
              selectedNumber="${searchParams.page}"
              handler="goToPage"
              withoutNumbers="true"
              displayHeader="true"/>

    <div class="wrapper">
        <c:if test="${ad:isPermitted0('TriggerQA.update')}">
            <ui:button message="form.save" onclick="confirmUpdate();" type="button"/>
            <ui:button message="form.cancel" onclick="location='main.action';" type="button" />
        </c:if>
    </div>
</form>
</s:if>
<s:else>
    <div class="wrapper">
        <s:text name="nothing.found.to.display"/>
    </div>
</s:else>
