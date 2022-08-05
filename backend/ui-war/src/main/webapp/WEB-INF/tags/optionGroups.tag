<%@ tag description="UI Field" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="groupLabel" required="false" %>
<%@ attribute name="collection" required="true" type="java.util.Collection" %>
<%@ attribute name="currentGroupId" required="true" type="java.lang.Long" %>
<%@ attribute name="sortOrder" required="true" type="java.lang.Long" %>
<%@ attribute name="optgroupEnabled" required="true" type="java.lang.Boolean" %>
<%@ attribute name="type" required="true" type="java.lang.String" %>


<c:if test="${optgroupEnabled}">
    <optgroup label="${groupLabel}">
</c:if>
    <c:set var="i" value="1"/>
    <option <c:if test="${(sortOrder) == (i)}">selected="selected"</c:if> value="${type}_${i}">
        <fmt:message key="OptionGroup.position.first"/>
    </option>
    <c:forEach var="optionGroup" items="${collection}">
        <c:if test="${optionGroup.id != currentGroupId}">
            <c:set var="i" value="${i + 1}"/>
            <option <c:if test="${(sortOrder) == (i)}">selected="selected"</c:if> value="${type}_${i}">
                <fmt:message key="OptionGroup.position.after" ><fmt:param value="${optionGroup.defaultName}"/></fmt:message>
            </option>
        </c:if>
    </c:forEach>
<c:if test="${optgroupEnabled}">
    </optgroup>
</c:if>
    
