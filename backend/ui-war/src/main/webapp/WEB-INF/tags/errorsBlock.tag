<%@ tag description="Block of Errors" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="blockOfErrors">
    <jsp:doBody />
</c:set>

<c:if test="${not empty pageScope.blockOfErrors}">
    <div class="wrapper">
        ${pageScope.blockOfErrors}
    </div>
</c:if>
