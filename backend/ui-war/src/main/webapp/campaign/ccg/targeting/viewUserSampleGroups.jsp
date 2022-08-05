<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<c:if test="${ad:isPermitted('CreativeGroup.viewUserSampleGroups', model)}">
<tbody id="ccgTargetingUserSampleGroups">
<tr>
    <td>
        <ad:wrap>
            <span>
                <fmt:message key="ccg.userSampleGroups"/>
                <c:if test="${ad:isPermitted('CreativeGroup.updateUserSampleGroups', model)}">
                    <ui:button message="form.edit" href="userSampleGroups/edit.action?id=${id}" />
                </c:if>
            </span>
            <ui:hint>
                <fmt:message key="ccg.userSampleGroups.toolTip"/>
            </ui:hint>
        </ad:wrap>
    </td>
    <td class="ccg_target">
        <s:if test="userSampleGroupStart != null && userSampleGroupEnd != null">
            <s:property value="userSampleGroupStart"/> - <s:property value="userSampleGroupEnd"/>
        </s:if>
        <s:else>
            <ui:text textKey="form.range.all"/>
        </s:else>
    </td>
    <tiles:insertTemplate template="/campaign/ccg/targeting/statsData.jsp">
        <tiles:putAttribute name="data" value="${targetingStats.userSampleGroups}"/>
    </tiles:insertTemplate>
</tr>
</tbody>
</c:if>
