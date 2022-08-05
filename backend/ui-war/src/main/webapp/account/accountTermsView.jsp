<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<c:if test="${ad:isPermitted('Account.updateTerms', model)}">
    <c:set var="allowUpdateTerms" value="true"/>
</c:if>

<script type="text/javascript">
function doDelete(id, fileName, control) {
    if (!confirm('<fmt:message key="account.terms.confirmDelete"/>'.replace('{0}', fileName))) return;
    $.ajax({
        url: '${_context}/account/terms/delete.action',
        type: 'GET',
        data: {id: id, file: fileName},
        success: function(data) {
            control.remove();
        }
    });
}
</script>

<c:if test="${not empty terms or allowUpdateTerms}">
<ui:section titleKey="account.terms">
    <ui:fieldGroup>
        <c:forEach items="${terms}" var="term">
            <tr>
                <td><ui:button messageText="${term.name}" href="${_context}/account/terms/download.action?id=${id}&file=${term.name}" /></td>
                <td>${ad:formatDateTime(term.time)}</td>
                <td><c:if test="${allowUpdateTerms}"><ui:button message="form.delete" onclick="doDelete(${id}, '${term.name}', $(this).parent().parent())" /></c:if></td>
            </tr>
        </c:forEach>
    </ui:fieldGroup>
    <c:if test="${allowUpdateTerms}">
        <ui:button message="form.add"  href="terms/add.action?id=${id}"/>
    </c:if>
</ui:section>
</c:if>

