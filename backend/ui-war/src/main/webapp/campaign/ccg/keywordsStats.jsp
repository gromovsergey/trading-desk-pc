<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<script type="text/javascript">
    function toggleAllKeywords(header) {
        var list = document.getElementById('keywords').getElementsByTagName('input');
        for (i = 0; i < list.length; i++) {
            list[i].checked = header.checked;
        }
    }

    function toggleChildKeywords(j, header) {
        var list = document.getElementById('data_' + j).getElementsByTagName('input');
        for (i = 0; i < list.length; i++) {
            list[i].checked = header.checked;
        }
    }

    function toggleParentKeyword(j) {
        var children = document.getElementById('data_' + j).getElementsByTagName('input');
        var parent = document.getElementById('root_' + j).getElementsByTagName('input')[0];

        var checked = true;
        for (i = 0; i < children.length; i++) {
            if (!children[i].checked) {
                checked = false;
            }
        }

        parent.checked = checked;
    }

    function showHide(j, currBtn){
        var dataBlock = $('#data_' + j);
        var currBtn = $(currBtn);
        var isHidden = dataBlock.data('isHidden');
        isHidden = isHidden != undefined ? isHidden : true;

        dataBlock.toggle();
        currBtn.html(isHidden ?
                '<fmt:message key="admin.dashboard.hide"/>' :
                '<fmt:message key="admin.dashboard.show"/>');
        dataBlock.data('isHidden', !isHidden);
    }
</script>

<c:if test="${ad:isPermitted('AdvertiserEntity.update', model)}">
    <c:set var="editStatusCCG" value="true"/>
</c:if>

<form action="" id="keywordForm" method="post">
    <input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/>
    <table class="dataView collapsing" id="keywords">
        <thead>
        <tr>
            <c:if test="${editStatusCCG}">
                <th width="1">
                    <input type="checkbox" onclick="toggleAllKeywords(this)"/>
                </th>
            </c:if>
            <th class="withCollapsingButton">&nbsp;</th>
            <th><fmt:message key="keyword"/></th>
            <th><fmt:message key="keywords.status"/></th>
            <th><fmt:message key="keywords.bidCPC"/></th>
            <th><fmt:message key="keywords.impressions"/></th>
            <th><fmt:message key="keywords.clicks"/></th>
            <th><fmt:message key="keywords.ctr"/></th>
            <c:if test="${ad:isInternal()}">
                <th><fmt:message key="keywords.ecpm"/></th>
                <th><fmt:message key="keywords.cost"/></th>
                <th><fmt:message key="keywords.averageActualCPC"/></th>
            </c:if>
            <th>
                <span class="textWithHint">
                    <fmt:message key="keywords.audience"/>
                    <ui:hint>
                        <fmt:message key="keywords.audience.tip"/>
                    </ui:hint>
                </span>
            </th>
        </tr>
        </thead>
        <c:forEach items="${keywords}" var="rootKeyword" varStatus="row">
            <tbody id="root_${row.index}" class="parent">
                <fmt:message var="notAvailable" key="notAvailable"/>
                <tr>
                    <c:if test="${editStatusCCG}">
                        <td>
                            <input type="checkbox" onclick="toggleChildKeywords(${row.index}, this)"/>
                        </td>
                    </c:if>
                    <td class="withCollapsingButton">
                        <a href="#" onclick="showHide(${row.index}, this); return false;"><fmt:message key="admin.dashboard.show"/></a>
                    </td>
                    <td>
                        <ui:displayStatus displayStatus="${rootKeyword.displayStatus}">
                            <c:out value="${rootKeyword.keyword}"/>
                        </ui:displayStatus>
                    </td>
                    <td>
                        <fmt:message key="${rootKeyword.displayStatus.description}"/>
                    </td>
                    <td class="currency">
                        <c:choose>
                            <c:when test="${rootKeyword.sameCPC}">
                                <c:set var="keywordCpc" value="${empty rootKeyword.cpc ? ccgRate.cpc : rootKeyword.cpc}"/>
                                ${ad:formatCurrency(keywordCpc, account.currency.currencyCode)}
                            </c:when>
                            <c:otherwise>
                                ${notAvailable}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="number">
                            ${rootKeyword.impressions}
                    </td>
                    <td class="number">
                            ${rootKeyword.clicks}
                    </td>
                    <td class="number">
                            <fmt:formatNumber value="${rootKeyword.ctr}" groupingUsed="false" maxFractionDigits="2"/>%
                    </td>
                    <c:if test="${ad:isInternal()}">
                        <td class="currency">
                                ${ad:formatCurrency(rootKeyword.ecpm, account.currency.currencyCode)}
                        </td>
                        <td class="currency">
                                ${ad:formatCurrency(rootKeyword.cost, account.currency.currencyCode)}
                        </td>
                        <td class="currency">
                                ${ad:formatCurrency(rootKeyword.averageActualCPC, account.currency.currencyCode)}
                        </td>
                    </c:if>
                    <td class="number">
                        <c:choose>
                            <c:when test="${not empty rootKeyword.audience}">
                                > ${ad:formatNumber(rootKeyword.audience)}
                            </c:when>
                            <c:otherwise>
                                ${notAvailable}
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </tbody>
            <tbody id="data_${row.index}" style="display:none" class="child">
            <c:forEach items="${rootKeyword.keywords}" var="keyword">
                <tr>
                    <c:if test="${editStatusCCG}">
                        <td>
                            <input type="checkbox" name="selectedKeywords" value="${keyword.id}" onclick="toggleParentKeyword(${row.index})"/>
                        </td>
                    </c:if>
                    <td class="withCollapsingButton"></td>
                    <td>
                        <ui:displayStatus displayStatus="${keyword.displayStatus}">
                            <c:choose>
                                <c:when test="${ad:isPermitted0('KeywordChannel.view')}">
                                    <a href="/admin/KeywordChannel/view.action?id=${keyword.channelId}">
                                        <fmt:message key="enums.KeywordTriggerType.${keyword.triggerType.name}"/>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <fmt:message key="enums.KeywordTriggerType.${keyword.triggerType.name}"/>
                                </c:otherwise>
                            </c:choose>
                        </ui:displayStatus>
                    </td>
                    <td>
                        <fmt:message key="${keyword.displayStatus.description}"/>
                    </td>
                    <td class="currency">
                        <c:set var="keywordCpc" value="${empty keyword.cpc ? ccgRate.cpc : keyword.cpc}"/>
                            ${ad:formatCurrency(keywordCpc, account.currency.currencyCode)}
                    </td>
                    <td class="number">
                            ${keyword.impressions}
                    </td>
                    <td class="number">
                            ${keyword.clicks}
                    </td>
                    <td class="number">
                            <fmt:formatNumber value="${keyword.ctr}" groupingUsed="false" maxFractionDigits="2"/>%
                    </td>
                    <c:if test="${ad:isInternal()}">
                        <td class="currency">
                                ${ad:formatCurrency(keyword.ecpm, account.currency.currencyCode)}
                        </td>
                        <td class="currency">
                                ${ad:formatCurrency(keyword.cost, account.currency.currencyCode)}
                        </td>
                        <td class="currency">
                                ${ad:formatCurrency(keyword.averageActualCPC, account.currency.currencyCode)}
                        </td>
                    </c:if>
                    <td class="number">
                            ${empty keyword.audience ? notAvailable : ad:formatNumber(keyword.audience)}
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </c:forEach>
    </table>
</form>
