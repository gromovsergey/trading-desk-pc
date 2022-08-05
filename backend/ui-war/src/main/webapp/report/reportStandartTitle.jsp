<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<title>
<c:if test="${not empty taskTitle}">
    <c:choose>
    <c:when test="${not empty reportEntityName}">
        <ui:windowTitle attributeName="${taskTitle}" attribute="${reportEntityName}"/>
    </c:when>
    <c:otherwise>
        <ui:windowTitle attributeName="${taskTitle}"/>
    </c:otherwise>
    </c:choose>
</c:if>
</title>
<script type="text/javascript">
function ready() {
	$("form[id$='Form'][action$='run.action']").append("<input name='taskTitle' value='${taskTitle}' type='hidden'/>"); 
}
document.addEventListener("DOMContentLoaded", ready);
</script>
