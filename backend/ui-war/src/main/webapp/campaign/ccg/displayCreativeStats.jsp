<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<script type="text/javascript">
    function toggleAllCreatives(header) {
        $('[name=creativesIds]').prop({checked : header.checked});
        $('[name=setNumberIds]').prop({checked : header.checked});
    }

    function toggleCreativesInSet(setId) {
        $('#data_setNumberId_' + setId.value).find("[name=creativesIds]").attr({"checked" : setId.checked})
    }

    function showHideCreatives(j, currBtn){
        var dataBlock = $('#data_setNumberId_' + j);
        var currBtn = $(currBtn);
        var isHidden = dataBlock.data('isHidden');
        isHidden = isHidden != undefined ? isHidden : true;

        dataBlock.toggle();
        currBtn.html(isHidden ?
            '<fmt:message key="admin.dashboard.show"/>' :
            '<fmt:message key="admin.dashboard.hide"/>');
        dataBlock.data('isHidden', !isHidden);
    }
    
    $(function(){
        $('#creative').on('click', '.b-cpreview', function(e){
            e.preventDefault(); 
            UI.Util.togglePreview($(this).data('cid'), $(this).data('pstfx'));
        });
    });
</script>

<ui:errorsBlock>
    <s:fielderror><s:param value="%{'version'}"/></s:fielderror>
</ui:errorsBlock>

<s:if test="!creativeSets[0].linkedTOs.empty">
    <form action="" id="creativesForm" method="post">
        <input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/>
        <input type="hidden" name="ccgId" value="${id}"/>
        <c:if test="${sequentialAdservingFlag}">
            <input type="hidden" id="setNumber" name="numberOfSet" value="1"/>
            <s:hidden  name="creativesMaxVersion"/>
        </c:if>
        
        <c:set var="canUpdateLinkedCreatives" value="${ad:isPermitted('AdvertiserEntity.update', model) &&  not empty creativeSets[0].linkedTOs}"/>
        <c:set var="columnCount" value="10"/>
        
        <table class="dataView" id="creative" style="border-bottom:1px solid #ccc;">
            <thead>
            <tr>
                <c:if test="${canUpdateLinkedCreatives}">
                    <c:set var="columnCount" value="${columnCount+1}"/>
                    <th width="1">
                        <input type="checkbox" onclick="toggleAllCreatives(this)"/>
                    </th>
                </c:if>
                <th><fmt:message key="creative.name"/></th>
                <th><fmt:message key="creative.size"/></th>
                <th><fmt:message key="creative.impressions"/></th>
                <th><fmt:message key="creative.clicks"/></th>
                <th><fmt:message key="creative.ctr"/></th>
                <c:if test="${showPostImpConv}">
                    <c:set var="columnCount" value="${columnCount+2}"/>
                    <th><fmt:message key="creative.postImpConv"/></th>
                    <th><fmt:message key="creative.postImpConvCr"/></th>
                </c:if>
                <c:if test="${showPostClickConv}">
                    <c:set var="columnCount" value="${columnCount+2}"/>
                    <th><fmt:message key="creative.postClickConv"/></th>
                    <th><fmt:message key="creative.postClickConvCr"/></th>
                </c:if>
                <c:if test="${showUniqueUsers}">
                    <c:set var="columnCount" value="${columnCount+1}"/>
                    <th><fmt:message key="creative.uniqueUsers"/></th>
                </c:if>
                <c:if test="${ad:isInternal()}">
                    <c:if test="${availableCreditUsed}">
                        <c:set var="columnCount" value="${columnCount+1}"/>
                        <th><fmt:message key="creative.creditUsed"/></th>
                    </c:if>
                    <th><fmt:message key="creative.totalCost"/></th>
                    <c:if test="${availableCreditUsed}">
                        <c:set var="columnCount" value="${columnCount+1}"/>
                        <th><fmt:message key="creative.totalValue"/></th>
                    </c:if>
                    <th><fmt:message key="creative.ecpm"/></th>
                    <c:if test="${ccgType.letter == 'T'}">
                        <th><fmt:message key="creative.averageActualCPC"/></th>
                    </c:if>
                </c:if>
            </tr>
            </thead>
            <c:forEach items="${creativeSets}"  var="creativeSet" varStatus="setIndexID">
                <c:if test="${sequentialAdservingFlag}">
                    <tbody class="parent">
                        <tr>
                            <c:if test="${canUpdateLinkedCreatives}">
                                <td>
                                    <input type="checkbox" name="setNumberIds" onclick="toggleCreativesInSet(this)" value="${creativeSet.setNumber}"/>
                                </td>
                            </c:if>
                            <td>
                                <a href="#" onclick="showHideCreatives(${creativeSet.setNumber}, this); return false;"><fmt:message key="admin.dashboard.hide"/></a>
                                <fmt:message key="creative.set">
                                    <fmt:param value="${creativeSet.setNumber}"/>
                                </fmt:message>
                            </td>
                            <td/>
                            <c:set var="to" value="${creativeSet}"/>
                            
                            <%@include file="textMetricColumns.jsp" %>
                            <c:if test="${ad:isInternal() and ccgType.letter == 'T'}">
                                <td class="currency">
                                    ${ad:formatCurrency(to.averageActualCPC, account.currency.currencyCode)}
                                </td>
                            </c:if>
                        </tr>
                    </tbody>
                </c:if>

                <tbody id="data_setNumberId_${creativeSet.setNumber}" class="child">
                    <c:forEach items="${creativeSet.linkedTOs}" var="creative" varStatus="creativeIndexID">
                        <tr>
                            <c:if test="${canUpdateLinkedCreatives}">
                                <td>
                                    <input type="checkbox" name="creativesIds" value="${creative.id}"/>
                                </td>
                            </c:if>
                            <td> 
                                <table class="grouping">
                                    <tr>
                                        <td>
                                            <ui:displayStatus displayStatus="${creative.displayStatus}">
                                                <a class="preText" href="creative/view.action?id=${creative.id}"><c:out value="${creative.creativeName}"/></a>
                                            </ui:displayStatus>
                                        </td>
                                        <td style="padding-left:.5em;">
                                            <c:set var="sPostfixVal" value="${creativeSet.setNumber}_${creativeIndexID.count}"/>
                                            <a class="preText b-cpreview" href="#" data-cid="${creative.creativeId}" data-pstfx="${sPostfixVal}"><fmt:message key="creative.previewCreative"/></a>
                                        </td>
                                        <td style="padding-left:.5em;">
                                            <a class="preText" href="${_context}/creative/view.action?id=${creative.creativeId}"><fmt:message key="creative"/></a>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                            <td>
                                <c:out value="${ad:localizeName(creative.sizeName)}"/>
                            </td>
                            <c:set var="to" value="${creative}"/>
                            <%@include file="textMetricColumns.jsp" %>
                            <c:if test="${ad:isInternal() and ccgType.letter == 'T'}">
                                <td class="currency">
                                    ${ad:formatCurrency(to.averageActualCPC, account.currency.currencyCode)}
                                </td>
                            </c:if>
                        </tr>
                        <tr class="hide" id="preview_tr_${creative.creativeId}_${sPostfixVal}">
                            <td colspan="${columnCount}">
                                <ui:creativePreview postfix="${sPostfixVal}" creativeId="${creative.creativeId}" noload="true"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </c:forEach>
        </table>
    
        <ui:pages pageSize="${pageSize}"
                  total="${total}"
                  selectedNumber="${page}"
                  visiblePagesCount="10"
                  handler="goToPage"
                  displayHeader="true"/>
    
    </form>
</s:if>
<s:else>
    <div class="wrapper">
        <fmt:message key="ccg.action.noLinkedCreativesHint"/>
    </div>
</s:else>
