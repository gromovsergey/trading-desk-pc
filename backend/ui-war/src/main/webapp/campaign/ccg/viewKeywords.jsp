<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    function loadKeywordsStatsData(){
        $('#kw_preloader').show();
         var params = $('#keywordsForm').serializeArray();
         $('#keywordStatsDiv')
                 .html('<h3 class="level1">${ad:formatMessage("form.loading.resources")}</h3>')
                 .load('${_context}/campaign/group/keywordsStats.action', params, function(){
                    $('#kw_preloader').hide();
                 });
    }

    function bulkKeywordsAction(url){
        if ($('[name=selectedKeywords]:checked').length > 0) {
            $('#keywordForm').attr('action', url).submit();
        }
    }
</script>

<c:if test="${ad:isPermitted('AdvertiserEntity.update', model)}">
    <c:set var="editStatusCCG" value="true"/>
</c:if>

<ui:header styleClass="level2">
    <h2><fmt:message key="keywords"/></h2>
    <c:if test="${isCCGUpdatePermitted}">
        <ui:button message="form.edit" href="editKeywords.action?id=${id}"/>
    </c:if>
    <div id="kw_preloader" class="hide"><img src="/images/wait-animation-small.gif"></div>
</ui:header>
<s:fielderror><s:param value="'ccgKeywords'"/></s:fielderror>
<table class="dataViewSection">
    <s:if test="!keywords.empty">
        <tr class="controlsZone">
            <td>
                <table class="grouping">
                    <tr>
                        <td class="withButtons">
                            <c:choose>
                                <c:when test="${editStatusCCG && not empty keywords}">
                                    <c:set var="url" value="activateKeywords.action?id=${id}"/>
                                    <ui:button message="form.activate" onclick="bulkKeywordsAction('${url}');" />

                                    <c:set var="url" value="inactivateKeywords.action?id=${id}"/>
                                    <ui:button message="form.deactivate" onclick="bulkKeywordsAction('${url}');" />

                                    <c:set var="url" value="deleteKeywords.action?id=${id}"/>
                                    <ui:button message="form.delete" onclick="if ($('[name=selectedKeywords]:checked').length > 0 && confirm('${ad:formatMessage('confirmDelete')}')) {$('#keywordForm').attr('action', '${url}').submit();}" />

                                    <c:if test="${ad:isPermitted('AdvertiserEntity.undeleteChildren', model)}">
                                        <c:set var="url" value="undeleteKeywords.action?id=${id}"/>
                                        <ui:button message="form.undelete" onclick="bulkKeywordsAction('${url}');" />
                                    </c:if>
                                </c:when>
                                <c:otherwise>
                                    &nbsp;
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="filterZone">
                            <form id="keywordsForm">
                                <ui:daterange idSuffix="Keywords" options="TOT Y T WTD MTD QTD YTD LW LM LQ LY" fastChangeId="MTD" onChange="loadKeywordsStatsData();" timeZoneAccountId="${requestContexts.advertiserContext.accountId}"/>
                                <s:hidden name="id"/>
                            </form>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr class="bodyZone">
            <td>
                <div class="logicalBlock" id="keywordStatsDiv">
                    <%@ include file="keywordsStats.jsp" %>
                </div>
            </td>
        </tr>
    </s:if>
    <s:if test="negativeKeywordsNumber > 0">
        <tr>
            <td>
                <span class="infos">
                <fmt:message key="ccg.keywords.negative">
                    <fmt:param>${negativeKeywordsNumber}</fmt:param>
                </fmt:message>
                </span>
            </td>
        </tr>
    </s:if>
    <s:elseif test="keywords.empty">
        <tr>
            <td>
                <fmt:message key="ccg.action.noLinkedKeywordsHint"/>
            </td>
        </tr>
    </s:elseif>
</table>

