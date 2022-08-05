<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<c:if test="${ad:isInternal()}">
    <tbody id="ccgTargetingColocations">
    <tr>
        <td>
            <fmt:message key="ccg.targeting.colocations"/>
            <c:if test="${ad:isPermitted0('AdvertiserEntity.advanced') and ad:isPermitted('AdvertiserEntity.update', model)}">
                <ui:button message="form.edit" href="edit${ccgType.pageExtension}.action?id=${id}#ccgColocationTargeting"/>
            </c:if>
        </td>

        <td class="ccg_target">
            <s:if test="groupColocations.empty">
                <fmt:message key="ccg.targeting.colocations.all"/>
            </s:if>
            <s:else>
                <c:set var="canViewColocation" value="${ad:isPermitted0('Colocation.view')}"/>
                <c:set var="canViewAccount" value="${ad:isPermitted('Account.view', 'ISP')}"/>
                <s:iterator var="colocation" value="groupColocations" status="it">
                    <span class="simpleText">
                        <c:if test="${canViewAccount}"><a href="/admin/account/view.action?id=${colocation.account.id}"></c:if>
                        <c:out value="${colocation.account.name}"/>
                        <c:if test="${canViewAccount}"></a></c:if>
                         /
                        <c:if test="${canViewColocation}"><a href="/admin/colocation/view.action?id=${colocation.id}"></c:if>
                        <c:out value="${colocation.name}"/>
                        <c:if test="${canViewColocation}"></a></c:if>
                        <s:if test="!#it.last">, </s:if>
                    </span>
                </s:iterator>
            </s:else>
        </td>
        <tiles:insertTemplate template="/campaign/ccg/targeting/statsData.jsp">
            <tiles:putAttribute name="data" value="${targetingStats.colocations}"/>
        </tiles:insertTemplate>
    </tr>
    </tbody>
</c:if>


