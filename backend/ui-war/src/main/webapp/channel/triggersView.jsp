<%@ page import="com.foros.model.channel.BehavioralParametersUnits" %>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<script type="text/javascript">
    function addScrollbars(jqFrame){
        var maxHeight   = 264,
        jqTable         = jqFrame.find('table:visible');
        if (jqTable.height() > maxHeight) {
            jqFrame.addClass('scrollbarsY');
        } else {
            jqFrame.removeClass('scrollbarsY');
        }
    }

    function loadTriggers(containerId) {
        var repDiv  = $(containerId),
        form        = repDiv.closest('form'),
        qaStatus    = form.find('input[name="triggersFilter.qaStatus"]:checked').val();
        
        if(!qaStatus) {
            qaStatus = form.find('input[name="triggersFilter.qaStatus"]').val();
        }
        
        var page = form.find('input[name="' + qaStatus + '_page"]').val();
        form.find('input[name="triggersFilter.page"]').val(page);

        var total = form.find('input[name="' + qaStatus + '_total"]').val();
        form.find('input[name="triggersTotal"]').val(total);

        var sortKey = form.find('input[name="' + qaStatus + '_sortKey"]').val();
        form.find('input[name="triggersFilter.sortKey"]').val(sortKey);

        var sortOrder = form.find('input[name="' + qaStatus + '_sortOrder"]').val();
        form.find('input[name="triggersFilter.sortOrder"]').val(sortOrder);

        repDiv.siblings('.ajax_loader').show().end().ajaxPanel({
            url: 'loadTriggersPage.action',
            form: form
        }).replace();
    }


    $(document).ajaxSuccess(function(){
        $('#reportDataPageKeywords, #reportDataSearchKeywords, #reportDataUrls, #reportDataUrlKeywords').each(function(){
            addScrollbars($(this));
        });
    });

    $(function() {
        new UI.AjaxLoader().switchOff();
    
        <c:if test="${!triggersTotals.pageKeywords.fullyNegative}">
            loadTriggers('#reportDataPageKeywords');
        </c:if>
        <c:if test="${!triggersTotals.searchKeywords.fullyNegative}">
            loadTriggers('#reportDataSearchKeywords');
        </c:if>
        <c:if test="${!triggersTotals.urls.fullyNegative}">
            loadTriggers('#reportDataUrls');
        </c:if>
        <c:if test="${!triggersTotals.urlKeywords.fullyNegative}">
            loadTriggers('#reportDataUrlKeywords');
        </c:if>
    
        $("input[name='triggersFilter.qaStatus']", $('#pageKeywords')).click(function(){
            loadTriggers('#reportDataPageKeywords');
        });

        $("input[name='triggersFilter.qaStatus']", $('#searchKeywords')).click(function(){
            loadTriggers('#reportDataSearchKeywords');
        });

        $("input[name='triggersFilter.qaStatus']", $('#urls')).click(function(){
            loadTriggers('#reportDataUrls');
        });

        $("input[name='triggersFilter.qaStatus']", $('#urlKeywords')).click(function(){
            loadTriggers('#reportDataUrlKeywords');
        });

        $('.statusFieldGroup.fieldAndAccessories').find('span.simpleText').each(function(){
            var aParents    = $(this).parents('table.fieldAndAccessories');
            if (aParents.length > 1 && $(aParents[0]).width() > 500) {
                var sHtml   = $(this).html();
                $(this).parent('td').remove();
                $(aParents[1]).after('<div id="newline">'+sHtml+'</div>');
                $('#newline').css({'margin-top':'2px'});
            }
        });

        $('#removedPageKeywordsSW').on('click', function(e){
            e.preventDefault();
            $(this).children('span').toggle();
            $('#removedPageKeywordsTA').toggle();
        });

        $('#removedSearchKeywordsSW').on('click', function(e){
            e.preventDefault();
            $(this).children('span').toggle();
            $('#removedSearchKeywordsTA').toggle();
        });

        $('#removedUrlsSW').on('click', function(e){
            e.preventDefault();
            $(this).children('span').toggle();
            $('#removedUrlsTA').toggle();
        });

        $('#removedUrlKeywordsSW').on('click', function(e){
            e.preventDefault();
            $(this).children('span').toggle();
            $('#removedUrlKeywordsTA').toggle();
        });
    });
</script>

<s:form id="pageKeywords">
<s:hidden name="id"/>
<c:set var="pageTotals" value="${triggersTotals.pageKeywords}"/>
<c:if test="${pageTotals.total > 0}">
    <c:set var="pageKeywordsTotal">
        <fmt:message key="channel.pageKeywords.total">
            <fmt:param value="${pageTotals.total}"/>
        </fmt:message>
    </c:set>
    <ui:section title="${pageKeywordsTotal}" id="pageKeywordsSection" cssClass="ajax_loader_container">
        <c:choose>
            <c:when test="${!pageTotals.fullyNegative}">
                <div class="ajax_loader">
                </div>
                <ui:channelTriggersStatusControls totals="${pageTotals}" triggerType="PAGE_KEYWORD"/>
                <div id="reportDataPageKeywords" class="logicalBlock ajax_loader_container" style="min-height:100px;"></div>
                <table>
                    <c:if test="${not empty sessionScope.removedPageKeywords}">
                        <tr>
                            <td>
                                <p><fmt:message key="channel.removedKeywords">
                                    <fmt:param>${sessionScope.removedPageKeywordsNumber}</fmt:param>
                                </fmt:message> <a href="#" id="removedPageKeywordsSW" class="dashed"><span><fmt:message key="form.show" /></span><span class="hide"><fmt:message key="form.hide" /></span></a></p>
                                <p><textarea id="removedPageKeywordsTA" readonly="readonly" class="middleLengthText1 hide">${sessionScope.removedPageKeywords}</textarea></p>
                            </td>
                        </tr>
                        <s:set name="removedPageKeywords" value="" scope="session"/>
                    </c:if>
                    <c:if test="${pageTotals.negative > 0}">
                        <tr><td><span class="infos">
                                    <fmt:message key="channel.pageKeywords.negativeNumber">
                                        <fmt:param>${pageTotals.negative}</fmt:param>
                                    </fmt:message>
                                </span></td></tr>
                    </c:if>
                    <s:if test="(#behavioralParameters == null || #behavioralParameters.{^ #this.triggerType == 'P'}.isEmpty()) && not empty pageKeywordsData">
                        <tr><td><span class="infos"><fmt:message key="channel.keywordsAreSwitchedOff"/></span></td></tr>
                    </s:if>
                    <s:iterator value="behavioralParameters.{? #this.triggerType == 'P'}" var="bparam">
                        <tr>
                            <td>
                                <s:if test="#bparam.minimumVisits > 1">
                                    <s:if test="#bparam.timeFrom > 0 || #bparam.timeTo > 0">
                                        <c:set value="channel.params.matchPage.plural" var="matchMessage"/>
                                    </s:if>
                                    <s:else>
                                        <c:set value="channel.params.matchPageNow.plural" var="matchMessage"/>
                                    </s:else>
                                </s:if>
                                <s:else>
                                    <s:if test="#bparam.timeFrom > 0 || #bparam.timeTo > 0">
                                        <c:set value="channel.params.matchPage" var="matchMessage"/>
                                    </s:if>
                                    <s:else>
                                        <c:set value="channel.params.matchPageNow" var="matchMessage"/>
                                    </s:else>
                                </s:else>
                                <c:set var="timeUnit" value="<%=BehavioralParametersUnits.DAYS.getName()%>"/>
                                <c:set var="multiplier" value="<%=BehavioralParametersUnits.DAYS.getMultiplier()%>"/>
                                <c:if test="${bparam.timeFrom mod multiplier != 0 or bparam.timeTo mod multiplier != 0}">
                                    <c:set var="timeUnit" value="<%=BehavioralParametersUnits.HOURS.getName()%>"/>
                                    <c:set var="multiplier" value="<%=BehavioralParametersUnits.HOURS.getMultiplier()%>"/>
                                    <c:if test="${bparam.timeFrom mod multiplier != 0 or bparam.timeTo mod multiplier != 0}">
                                        <c:set var="timeUnit" value="<%=BehavioralParametersUnits.MINUTES.getName()%>"/>
                                        <c:set var="multiplier" value="<%=BehavioralParametersUnits.MINUTES.getMultiplier()%>"/>
                                    </c:if>
                                </c:if>
                                <s:if test="#bparam.timeFrom > 0">
                                    <fmt:formatNumber value="${bparam.timeFrom div multiplier}" maxFractionDigits="0" var="timeFrom"/>
                                </s:if>
                                <s:else>
                                    <fmt:message key="channel.params.now" var="timeFrom"/>
                                </s:else>
                                <s:if test="#bparam.timeTo > 0">
                                    <fmt:formatNumber value="${bparam.timeTo div multiplier}" maxFractionDigits="0" var="timeTo"/>
                                </s:if>
                                <s:else>
                                    <fmt:message key="channel.params.now" var="timeTo"/>
                                </s:else>
                                <fmt:message key="${matchMessage}">
                                    <fmt:param value="${bparam.minimumVisits}"/>
                                    <fmt:param value="${timeFrom}"/>
                                    <fmt:param value="${timeTo}"/>
                                    <fmt:param>
                                        <s:if test="#bparam.timeFrom > 0 || #bparam.timeTo > 0">
                                            <s:if test="#bparam.timeTo > #attr.multiplier">
                                                <fmt:message key="channel.params.${timeUnit}.plural"/>
                                            </s:if>
                                            <s:else>
                                                <fmt:message key="channel.params.${timeUnit}"/>
                                            </s:else>
                                        </s:if>
                                    </fmt:param>
                                </fmt:message>
                            </td>
                        </tr>
                    </s:iterator>
                    <tr>
                        <td>
                            <fmt:message key="channel.statsDescr">
                                <fmt:param value="${formattedYesterdayDate}"/>
                            </fmt:message>
                        </td>
                    </tr>
                </table>
            </c:when>
            <c:otherwise>
                <span class="infos"><fmt:message key="channel.pageKeywords.negativeNumber">
                    <fmt:param>${pageTotals.negative}</fmt:param>
                </fmt:message></span>
            </c:otherwise>
        </c:choose>
    </ui:section>
</c:if>
</s:form>

<s:form id="searchKeywords">
<s:hidden name="id"/>
<c:set var="searchTotals" value="${triggersTotals.searchKeywords}"/>
<c:if test="${searchTotals.total > 0}">
    <c:set var="searchKeywordsTotal">
        <fmt:message key="channel.searchKeywords.total">
            <fmt:param value="${searchTotals.total}"/>
        </fmt:message>
    </c:set>
    <ui:section title="${searchKeywordsTotal}" id="searchKeywordsSection" cssClass="ajax_loader_container">
        <c:choose>
            <c:when test="${!searchTotals.fullyNegative}">
                <div class="ajax_loader">
                </div>
                <ui:channelTriggersStatusControls totals="${searchTotals}" triggerType="SEARCH_KEYWORD"/>
                <div id="reportDataSearchKeywords" class="logicalBlock ajax_loader_container" style="min-height:100px;"></div>
                <table>
                    <c:if test="${not empty sessionScope.removedSearchKeywords}">
                        <tr>
                            <td>
                                <p><fmt:message key="channel.removedKeywords">
                                    <fmt:param>${sessionScope.removedSearchKeywordsNumber}</fmt:param>
                                </fmt:message> <a href="#" id="removedSearchKeywordsSW" class="dashed"><span><fmt:message key="form.show" /></span><span class="hide"><fmt:message key="form.hide" /></span></a></p>
                                <p><textarea id="removedSearchKeywordsTA" readonly="readonly" class="middleLengthText1 hide">${sessionScope.removedSearchKeywords}</textarea></p>
                            </td>
                        </tr>
                        <s:set name="removedSearchKeywords" value="" scope="session"/>
                    </c:if>
                    <c:if test="${searchTotals.negative > 0}">
                        <tr><td><span class="infos">
                                    <fmt:message key="channel.searchKeywords.negativeNumber">
                                        <fmt:param>${searchTotals.negative}</fmt:param>
                                    </fmt:message>
                                </span></td></tr>
                    </c:if>
                    <s:if test="(#behavioralParameters == null || #behavioralParameters.{^ #this.triggerType == 'S'}.isEmpty()) && not empty searchKeywordsData">
                        <tr><td><span class="infos"><fmt:message key="channel.keywordsAreSwitchedOff"/></span></td></tr>
                    </s:if>
                    <s:iterator value="behavioralParameters.{? #this.triggerType == 'S'}" var="bparam">
                        <tr>
                            <td>
                                <s:if test="#bparam.minimumVisits > 1">
                                    <s:if test="#bparam.timeFrom > 0 || #bparam.timeTo > 0">
                                        <c:set value="channel.params.matchSearch.plural" var="matchMessage"/>
                                    </s:if>
                                    <s:else>
                                        <c:set value="channel.params.matchSearchNow.plural" var="matchMessage"/>
                                    </s:else>
                                </s:if>
                                <s:else>
                                    <s:if test="#bparam.timeFrom > 0 || #bparam.timeTo > 0">
                                        <c:set value="channel.params.matchSearch" var="matchMessage"/>
                                    </s:if>
                                    <s:else>
                                        <c:set value="channel.params.matchSearchNow" var="matchMessage"/>
                                    </s:else>
                                </s:else>
                                <c:set var="timeUnit" value="<%=BehavioralParametersUnits.DAYS.getName()%>"/>
                                <c:set var="multiplier" value="<%=BehavioralParametersUnits.DAYS.getMultiplier()%>"/>
                                <c:if test="${bparam.timeFrom mod multiplier != 0 or bparam.timeTo mod multiplier != 0}">
                                    <c:set var="timeUnit" value="<%=BehavioralParametersUnits.HOURS.getName()%>"/>
                                    <c:set var="multiplier" value="<%=BehavioralParametersUnits.HOURS.getMultiplier()%>"/>
                                    <c:if test="${bparam.timeFrom mod multiplier != 0 or bparam.timeTo mod multiplier != 0}">
                                        <c:set var="timeUnit" value="<%=BehavioralParametersUnits.MINUTES.getName()%>"/>
                                        <c:set var="multiplier" value="<%=BehavioralParametersUnits.MINUTES.getMultiplier()%>"/>
                                    </c:if>
                                </c:if>
                                <s:if test="#bparam.timeFrom > 0">
                                    <fmt:formatNumber value="${bparam.timeFrom div multiplier}" maxFractionDigits="0" var="timeFrom"/>
                                </s:if>
                                <s:else>
                                    <fmt:message key="channel.params.now" var="timeFrom"/>
                                </s:else>
                                <s:if test="#bparam.timeTo > 0">
                                    <fmt:formatNumber value="${bparam.timeTo div multiplier}" maxFractionDigits="0" var="timeTo"/>
                                </s:if>
                                <s:else>
                                    <fmt:message key="channel.params.now" var="timeTo"/>
                                </s:else>
                                <fmt:message key="${matchMessage}">
                                    <fmt:param value="${bparam.minimumVisits}"/>
                                    <fmt:param value="${timeFrom}"/>
                                    <fmt:param value="${timeTo}"/>
                                    <fmt:param>
                                        <s:if test="#bparam.timeFrom > 0 || #bparam.timeTo > 0">
                                            <s:if test="#bparam.timeTo > #attr.multiplier">
                                                <fmt:message key="channel.params.${timeUnit}.plural"/>
                                            </s:if>
                                            <s:else>
                                                <fmt:message key="channel.params.${timeUnit}"/>
                                            </s:else>
                                        </s:if>
                                    </fmt:param>
                                </fmt:message>
                            </td>
                        </tr>
                    </s:iterator>
                    <tr>
                        <td>
                            <fmt:message key="channel.statsDescr">
                                <fmt:param value="${formattedYesterdayDate}"/>
                            </fmt:message>
                        </td>
                    </tr>
                </table>
            </c:when>
            <c:otherwise>
                <span class="infos"><fmt:message key="channel.searchKeywords.negativeNumber">
                    <fmt:param>${searchTotals.negative}</fmt:param>
                </fmt:message></span>
            </c:otherwise>
        </c:choose>
    </ui:section>
</c:if>
</s:form>

<s:form id="urls">
    <s:hidden name="id"/>
    <c:set var="urlTotals" value="${triggersTotals.urls}"/>
    <c:if test="${urlTotals.total > 0}">
        <c:set var="urlsTotal">
            <fmt:message key="channel.urls.total">
                <fmt:param value="${urlTotals.total}"/>
            </fmt:message>
        </c:set>
        <ui:section title="${urlsTotal}" id="urlsSection" cssClass="ajax_loader_container">
            <c:choose>
                <c:when test="${!urlTotals.fullyNegative}">
                    <div class="ajax_loader">
                    </div>
                    <ui:channelTriggersStatusControls totals="${urlTotals}" triggerType="URL"/>
                    <div id="reportDataUrls" class="logicalBlock ajax_loader_container" style="min-height:100px;"></div>
                    <table>
                        <c:if test="${not empty sessionScope.removedUrls}">
                            <tr>
                                <td>
                                    <p><fmt:message key="channel.removedUrls">
                                        <fmt:param>${sessionScope.removedUrlsNumber}</fmt:param>
                                    </fmt:message> <a href="#" id="removedUrlsSW" class="dashed"><span><fmt:message key="form.show" /></span><span class="hide"><fmt:message key="form.hide" /></span></a></p>
                                    <p><textarea id="removedUrlsTA" readonly="readonly" class="middleLengthText1 hide">${sessionScope.removedUrls}</textarea></p>
                                </td>
                            </tr>
                            <s:set name="removedUrls" value="" scope="session"/>
                        </c:if>
                        <c:if test="${urlTotals.negative > 0}">
                            <tr><td><span class="infos">
                                        <fmt:message key="channel.urls.negativeNumber">
                                            <fmt:param>${urlTotals.negative}</fmt:param>
                                        </fmt:message>
                                    </span></td></tr>
                        </c:if>
                        <s:if test="(#behavioralParameters == null || #behavioralParameters.{^ #this.triggerType == 'U'}.isEmpty()) && not empty urlsData">
                            <tr><td><span class="infos"><fmt:message key="channel.urlsAreSwitchedOff"/></span></td></tr>
                        </s:if>
                        <s:iterator value="behavioralParameters.{? #this.triggerType == 'U'}" var="bparam">
                            <tr>
                                <td>
                                    <s:if test="#bparam.minimumVisits > 1">
                                        <s:if test="#bparam.timeFrom > 0 || #bparam.timeTo > 0">
                                            <c:set value="channel.params.match.plural" var="matchMessage"/>
                                        </s:if>
                                        <s:else>
                                            <c:set value="channel.params.matchNow.plural" var="matchMessage"/>
                                        </s:else>
                                    </s:if>
                                    <s:else>
                                        <s:if test="#bparam.timeFrom > 0 || #bparam.timeTo > 0">
                                            <c:set value="channel.params.match" var="matchMessage"/>
                                        </s:if>
                                        <s:else>
                                            <c:set value="channel.params.matchNow" var="matchMessage"/>
                                        </s:else>
                                    </s:else>
                                    <c:set var="timeUnit" value="<%=BehavioralParametersUnits.DAYS.getName()%>"/>
                                    <c:set var="multiplier" value="<%=BehavioralParametersUnits.DAYS.getMultiplier()%>"/>
                                    <c:if test="${bparam.timeFrom mod multiplier != 0 or bparam.timeTo mod multiplier != 0}">
                                        <c:set var="timeUnit" value="<%=BehavioralParametersUnits.HOURS.getName()%>"/>
                                        <c:set var="multiplier" value="<%=BehavioralParametersUnits.HOURS.getMultiplier()%>"/>
                                        <c:if test="${bparam.timeFrom mod multiplier != 0 or bparam.timeTo mod multiplier != 0}">
                                            <c:set var="timeUnit" value="<%=BehavioralParametersUnits.MINUTES.getName()%>"/>
                                            <c:set var="multiplier" value="<%=BehavioralParametersUnits.MINUTES.getMultiplier()%>"/>
                                        </c:if>
                                    </c:if>
                                    <s:if test="#bparam.timeFrom > 0">
                                        <fmt:formatNumber value="${bparam.timeFrom div multiplier}" maxFractionDigits="0" var="timeFrom"/>
                                    </s:if>
                                    <s:else>
                                        <fmt:message key="channel.params.now" var="timeFrom"/>
                                    </s:else>
                                    <s:if test="#bparam.timeTo > 0">
                                        <fmt:formatNumber value="${bparam.timeTo div multiplier}" maxFractionDigits="0" var="timeTo"/>
                                    </s:if>
                                    <s:else>
                                        <fmt:message key="channel.params.now" var="timeTo"/>
                                    </s:else>
                                    <fmt:message key="${matchMessage}">
                                        <fmt:param value="${bparam.minimumVisits}"/>
                                        <fmt:param value="${timeFrom}"/>
                                        <fmt:param value="${timeTo}"/>
                                        <fmt:param>
                                            <s:if test="#bparam.timeFrom > 0 || #bparam.timeTo > 0">
                                                <s:if test="#bparam.timeTo > #attr.multiplier">
                                                    <fmt:message key="channel.params.${timeUnit}.plural"/>
                                                </s:if>
                                                <s:else>
                                                    <fmt:message key="channel.params.${timeUnit}"/>
                                                </s:else>
                                            </s:if>
                                        </fmt:param>
                                    </fmt:message>
                                </td>
                            </tr>
                        </s:iterator>
                        <tr>
                            <td>
                                <fmt:message key="channel.statsDescr">
                                    <fmt:param value="${formattedYesterdayDate}"/>
                                </fmt:message>
                            </td>
                        </tr>
                    </table>
                </c:when>
                <c:otherwise>
                <span class="infos"><fmt:message key="channel.urls.negativeNumber">
                            <fmt:param>${urlTotals.negative}</fmt:param>
                        </fmt:message></span>
                </c:otherwise>
            </c:choose>
        </ui:section>
    </c:if>
</s:form>

<s:form id="urlKeywords">
    <s:hidden name="id"/>
    <c:set var="urlKeywordTotals" value="${triggersTotals.urlKeywords}"/>
    <c:if test="${urlKeywordTotals.total > 0}">
        <c:set var="urlKeywordsTotal">
            <fmt:message key="channel.urlKeywords.total">
                <fmt:param value="${urlKeywordTotals.total}"/>
            </fmt:message>
        </c:set>
        <ui:section title="${urlKeywordsTotal}" id="urlKeywordsSection" cssClass="ajax_loader_container">
            <c:choose>
                <c:when test="${!urlKeywordTotals.fullyNegative}">
                    <div class="ajax_loader">
                    </div>
                    <ui:channelTriggersStatusControls totals="${urlKeywordTotals}" triggerType="URL_KEYWORD"/>
                    <div id="reportDataUrlKeywords" class="logicalBlock ajax_loader_container" style="min-height:100px;"></div>
                    <table>
                        <c:if test="${not empty sessionScope.removedUrlKeywords}">
                            <tr>
                                <td>
                                    <p><fmt:message key="channel.removedKeywords">
                                        <fmt:param>${sessionScope.removedUrlKeywordsNumber}</fmt:param>
                                    </fmt:message> <a href="#" id="removedUrlKeywordsSW" class="dashed"><span><fmt:message key="form.show" /></span><span class="hide"><fmt:message key="form.hide" /></span></a></p>
                                    <p><textarea id="removedUrlKeywordsTA" readonly="readonly" class="middleLengthText1 hide">${sessionScope.removedUrlKeywords}</textarea></p>
                                </td>
                            </tr>
                            <s:set name="removedUrlKeywords" value="" scope="session"/>
                        </c:if>
                        <c:if test="${urlKeywordTotals.negative > 0}">
                            <tr><td><span class="infos">
                                    <fmt:message key="channel.urlKeywords.negativeNumber">
                                        <fmt:param>${urlKeywordTotals.negative}</fmt:param>
                                    </fmt:message>
                                </span></td></tr>
                        </c:if>
                        <s:if test="(#behavioralParameters == null || #behavioralParameters.{^ #this.triggerType == 'R'}.isEmpty()) && not empty urlKeywordsData">
                            <tr><td><span class="infos"><fmt:message key="channel.keywordsAreSwitchedOff"/></span></td></tr>
                        </s:if>
                        <s:iterator value="behavioralParameters.{? #this.triggerType == 'R'}" var="bparam">
                            <tr>
                                <td>
                                    <s:if test="#bparam.minimumVisits > 1">
                                        <s:if test="#bparam.timeFrom > 0 || #bparam.timeTo > 0">
                                            <c:set value="channel.params.matchUrl.plural" var="matchMessage"/>
                                        </s:if>
                                        <s:else>
                                            <c:set value="channel.params.matchUrlNow.plural" var="matchMessage"/>
                                        </s:else>
                                    </s:if>
                                    <s:else>
                                        <s:if test="#bparam.timeFrom > 0 || #bparam.timeTo > 0">
                                            <c:set value="channel.params.matchUrl" var="matchMessage"/>
                                        </s:if>
                                        <s:else>
                                            <c:set value="channel.params.matchUrlNow" var="matchMessage"/>
                                        </s:else>
                                    </s:else>
                                    <c:set var="timeUnit" value="<%=BehavioralParametersUnits.DAYS.getName()%>"/>
                                    <c:set var="multiplier" value="<%=BehavioralParametersUnits.DAYS.getMultiplier()%>"/>
                                    <c:if test="${bparam.timeFrom mod multiplier != 0 or bparam.timeTo mod multiplier != 0}">
                                        <c:set var="timeUnit" value="<%=BehavioralParametersUnits.HOURS.getName()%>"/>
                                        <c:set var="multiplier" value="<%=BehavioralParametersUnits.HOURS.getMultiplier()%>"/>
                                        <c:if test="${bparam.timeFrom mod multiplier != 0 or bparam.timeTo mod multiplier != 0}">
                                            <c:set var="timeUnit" value="<%=BehavioralParametersUnits.MINUTES.getName()%>"/>
                                            <c:set var="multiplier" value="<%=BehavioralParametersUnits.MINUTES.getMultiplier()%>"/>
                                        </c:if>
                                    </c:if>
                                    <s:if test="#bparam.timeFrom > 0">
                                        <fmt:formatNumber value="${bparam.timeFrom div multiplier}" maxFractionDigits="0" var="timeFrom"/>
                                    </s:if>
                                    <s:else>
                                        <fmt:message key="channel.params.now" var="timeFrom"/>
                                    </s:else>
                                    <s:if test="#bparam.timeTo > 0">
                                        <fmt:formatNumber value="${bparam.timeTo div multiplier}" maxFractionDigits="0" var="timeTo"/>
                                    </s:if>
                                    <s:else>
                                        <fmt:message key="channel.params.now" var="timeTo"/>
                                    </s:else>
                                    <fmt:message key="${matchMessage}">
                                        <fmt:param value="${bparam.minimumVisits}"/>
                                        <fmt:param value="${timeFrom}"/>
                                        <fmt:param value="${timeTo}"/>
                                        <fmt:param>
                                            <s:if test="#bparam.timeFrom > 0 || #bparam.timeTo > 0">
                                                <s:if test="#bparam.timeTo > #attr.multiplier">
                                                    <fmt:message key="channel.params.${timeUnit}.plural"/>
                                                </s:if>
                                                <s:else>
                                                    <fmt:message key="channel.params.${timeUnit}"/>
                                                </s:else>
                                            </s:if>
                                        </fmt:param>
                                    </fmt:message>
                                </td>
                            </tr>
                        </s:iterator>
                        <tr>
                            <td>
                                <fmt:message key="channel.statsDescr">
                                    <fmt:param value="${formattedYesterdayDate}"/>
                                </fmt:message>
                            </td>
                        </tr>
                    </table>
                </c:when>
                <c:otherwise>
                <span class="infos"><fmt:message key="channel.urlKeywords.negativeNumber">
                    <fmt:param>${urlKeywordTotals.negative}</fmt:param>
                </fmt:message></span>
                </c:otherwise>
            </c:choose>
        </ui:section>
    </c:if>
</s:form>
