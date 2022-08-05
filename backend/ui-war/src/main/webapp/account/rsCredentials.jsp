<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<c:set var="isUserEditPermitted" value="${ad:isPermitted('User.changeRsCredentials', model)}"/>
<c:if test="${isUserEditPermitted}">
    <form action="changeRsCredentials.action?id=${id}" method="POST" id="change-credentials-form">
        <input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/>
    </form>
    <ui:section titleKey="user.rsCredentials.title">
        <ui:fieldGroup>
            <c:choose>
                <c:when test="${empty userCredential.rsToken}">
                    <ui:field>
                        <ui:button message="user.rsCredentials.create" onclick="$('#change-credentials-form').submit();"/>
                    </ui:field>
                </c:when>
                <c:otherwise>
                    <ui:simpleField labelKey="user.rsCredentials.token" value="${userCredential.rsToken}"/>
                    <ui:simpleField labelKey="user.rsCredentials.key" value="${userCredential.rsKeyBase64}"/>
                    <c:if test="${isUserEditPermitted}">
                        <ui:field>
                            <ui:button message="user.rsCredentials.change" onclick="$('#change-credentials-form').submit();"/>
                        </ui:field>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </ui:fieldGroup>
        <c:if test="${param['changeWSCredentialsFailure'] == 'true'}">
            <span class="errors"><fmt:message key="errors.genericVersionCollision"/></span>
        </c:if>
    </ui:section>
</c:if>
