<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <c:if test="${not empty foros_title_generated_heading}">
        ${foros_title_generated_heading}
    </c:if>
    <c:if test="${empty foros_title_generated_heading}">
        <h1>Warning: Page heading were not generated for this page!</h1>
    </c:if>
    <jsp:doBody />
