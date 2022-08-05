<%@ tag language="java" body-content="empty" description="Displays color status" %>
<%@ attribute name="entityName" required="true" type="java.lang.String" %>
<%@ attribute name="entityStatus" required="true" type="com.foros.model.Status" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<c:out value="${pageScope.entityName}"/><c:if test="${pageScope.entityStatus=='DELETED'}"> <fmt:message key="suffix.deleted"/></c:if><c:if test="${pageScope.entityStatus=='INACTIVE'}"> <fmt:message key="suffix.inactive"/></c:if>
