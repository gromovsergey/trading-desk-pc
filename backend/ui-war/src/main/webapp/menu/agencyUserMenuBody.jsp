<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<ad:sessionContext var="contexts"/>
<c:set var="advertiserContext" value="${contexts.advertiserContext}"/>
<c:if test="${advertiserContext.agencyContext and advertiserContext.agencyAdvertiserSet}">
    <c:set var="accountId" value="${advertiserContext.accountId}"/>
    <tr id="accountContextMenu">
        <td class="rootCell">
            <div class="contextZone">
                <ad:accountDisplayStatus accountId="${advertiserContext.agencyAdvertiserId}"/>
                <fmt:message key="advertiserAccount.account"/>:
                <strong><ad:accountName accountId="${advertiserContext.agencyAdvertiserId}"/></strong>

                <a href="/advertiser/campaign/advertisers.action?${ad:accountParam('advertiserId',advertiserContext.accountId)}" class="operation"><fmt:message key="switchContext.change"/></a>
            </div>
        </td>
    </tr>
</c:if>
