<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:if test="${ad:isInternal()}">
    <tbody  id="ccgTargetingOptInStatuses">
    <tr>
        <td>
            <fmt:message key="ccg.targeting.optInStatuses"/>
            <c:if test="${ad:isPermitted('AdvertiserEntity.update', model)}">
                <ui:button message="form.edit" href="edit${ccgType.pageExtension}.action?id=${id}#ccgOptInStatusTargeting" />
            </c:if>
        </td>
        <td class="ccg_target">
            <s:if test="optInStatusTargeting.optedInUsers">
                <s:if test="minUidAge > 0">
                    <fmt:message key="ccg.optInStatusTargeting.optedInWithMinUidAge">
                        <fmt:param value="${minUidAge}"/>
                    </fmt:message>
                </s:if>
                <s:else><fmt:message key="ccg.optInStatusTargeting.optedIn"/></s:else>
                <s:if test="optInStatusTargeting.optedOutUsers || optInStatusTargeting.unknownUsers">, </s:if>
            </s:if>
            <s:if test="optInStatusTargeting.optedOutUsers">
                <fmt:message key="ccg.optInStatusTargeting.optedOut"/><s:if test="optInStatusTargeting.unknownUsers">, </s:if>
            </s:if>
            <s:if test="optInStatusTargeting.unknownUsers">
                <fmt:message key="ccg.optInStatusTargeting.unknown"/>
            </s:if>
            <s:if test="optInStatusTargeting == null">
                <fmt:message key="ccg.optInStatusTargeting.notSet"/>
            </s:if>
        </td>
        <tiles:insertTemplate template="/campaign/ccg/targeting/statsData.jsp">
            <tiles:putAttribute name="data" value="${null}"/>
        </tiles:insertTemplate>
    </tr>
    </tbody>
</c:if>
