<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<c:set var="pageExt" value="${ccgType.pageExtension}" scope="request"/>
<ui:header>
    <ui:pageHeadingByTitle />
    <c:if test="${isCCGUpdatePermitted}">
         <ui:button message="form.edit" href="edit${pageExt}.action?id=${id}" />
    </c:if>
    <c:if test="${ad:isPermitted('AdvertiserEntity.createCopy', model)}">
        <ui:postButton message="form.createCopy" href="createCopy${pageExt}.action"
                onclick="return UI.Util.confirmCopy(this);"
                entityId="${id}" />
    </c:if>
    <c:if test="${ad:isPermitted('Entity.viewLog', model)}">
        <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=5&id=${id}" />
    </c:if>
</ui:header>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
    <s:fielderror><s:param value="'name'"/></s:fielderror>
</ui:errorsBlock>
