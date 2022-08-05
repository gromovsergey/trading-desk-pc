<%@ tag description="Constraint Violations" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"      prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"       prefix="fmt" %>


<c:set var="violations" value="${constraintViolations}"/>
<c:if test="${not empty violations}">
    <span class="hide">
        <c:forEach var="violation" items="${violations}">
            <span class="errorKeys" messageTemplate="${violation.messageTemplate}" propertyPath="${violation.propertyPath}">
                <c:out value="${violation.message}"/>
            </span>
        </c:forEach>
    </span>
</c:if>
