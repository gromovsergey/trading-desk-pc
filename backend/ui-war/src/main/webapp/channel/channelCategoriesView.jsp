<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<s:if test="channelType.charAt(0) == 'L'">
    <s:set var="pageCategories" value="populatedCategories"/>
</s:if>
<s:else>
    <s:set var="pageCategories" value="categories"/>
</s:else>
<ui:field labelKey="channel.channelCategories">
    <s:if test="!#pageCategories.empty">
        <s:iterator value="#pageCategories" var="categoryChannel" status="indexID">
            <s:iterator value="categoryChannel" var="ancestorsChainElement" status="childIndexID">
                <s:if test="!#childIndexID.first">
                    >
                </s:if>
                <c:choose>
                    <c:when test="${ad:isPermitted0('CategoryChannel.view')}">
                        <a href="/admin/CategoryChannel/view.action?id=${ancestorsChainElement.id}"
                            ><c:out value="${ad:localizeEntityName(ancestorsChainElement)}"
                        /></a>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${ad:localizeEntityName(ancestorsChainElement)}"/>
                    </c:otherwise>
                </c:choose>
            </s:iterator>
            <s:if test="!#indexID.last">
                <c:out value=", "/>
            </s:if>
        </s:iterator>
    </s:if>
    <s:else>
        <fmt:message key="channel.categoriesNotAttached"/>
    </s:else>

    <s:if test="channelType == 'L'">
        <c:set var="dclParam" value="&discoverList=true"/>
    </s:if>
    <s:else>
        <c:set var="dclParam" value=""/>
    </s:else>
    <c:if test="${ad:isPermitted('Channel.editCategories', model)}">
        <ui:button message="form.edit" href="/admin/channel/Categories/edit.action?id=${model.id}${dclParam}"/>
    </c:if>
</ui:field>
