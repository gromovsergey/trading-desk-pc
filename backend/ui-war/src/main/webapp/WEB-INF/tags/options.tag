<%@ tag description="UI Field" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<%@ attribute name="label" required="true" %>
<%@ attribute name="collection" required="true" type="java.util.Collection" %>
<%@ attribute name="sortOrder" required="true" type="java.lang.Long" %>
<%@ attribute name="currentGroupId" required="true" type="java.lang.Long" %>
<%@ attribute name="currentOptionId" required="true" type="java.lang.Long" %>
<%@ attribute name="optgroupEnabled" required="true" type="java.lang.Boolean" %>

<c:if test="${not empty collection}">

    <c:if test="${optgroupEnabled}">
        <optgroup label="${label}">
    </c:if>

        <c:forEach var="optionGroup" items="${collection}">
            <c:set var="order" value="0"/>
            <option <c:if test="${currentGroupId == optionGroup.id}">selected="selected"</c:if>
                    value="${optionGroup.id}_${order}">
                <c:out value="${ad:localizeName(optionGroup.name)}"/>
            </option>

            <c:set var="delta" value="0"/>
            <c:forEach var="option" items="${optionGroup.options}">
                <c:if test="${option.id == currentOptionId}">
                    <c:set var="delta" value="1"/>
                </c:if>
                <c:if test="${option.id != currentOptionId}">
                    <c:set var="order" value="${order + 1}"/>
                    <option <c:if test="${(optionGroup.id == currentGroupId) && ((sortOrder - delta) == order)}">selected="selected"</c:if>
                            value="${optionGroup.id}_${order}">
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${ad:localizeName(option.name)}"/>
                    </option>
                </c:if>
            </c:forEach>
        </c:forEach>

    <c:if test="${optgroupEnabled}">
    </optgroup>
    </c:if>

</c:if>
