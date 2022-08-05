<%@ tag language="java" body-content="empty" description="Displays color status" %>
<%@ attribute name="displayStatus" required="true" type="com.foros.model.DisplayStatus" %>
<%@ attribute name="testFlag" required="false" type="java.lang.Boolean" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ tag import="com.foros.model.DisplayStatus"%>

<c:set var="LiveEnumValue" value='<%=DisplayStatus.Major.LIVE%>'/>
<c:set var="LiveNeedAttEnumValue" value='<%=DisplayStatus.Major.LIVE_NEED_ATT%>'/>
<c:set var="NotLiveEnumValue" value='<%=DisplayStatus.Major.NOT_LIVE%>'/>
<c:set var="InactiveEnumValue" value='<%=DisplayStatus.Major.INACTIVE%>'/>
<c:set var="DeletedEnumValue" value='<%=DisplayStatus.Major.DELETED%>'/>

<c:if test="${pageScope.testFlag==null}">
    <c:set var="testFlag" value="false" />
</c:if>

<c:choose>
    <c:when test="${pageScope.displayStatus.major == LiveEnumValue}">
        <c:set var="color" value="Green"/>
    </c:when>
    <c:when test="${pageScope.displayStatus.major == LiveNeedAttEnumValue}">
        <c:set var="color" value="Amber"/>
    </c:when>
    <c:when test="${pageScope.displayStatus.major == NotLiveEnumValue}">
        <c:set var="color" value="Red"/>
    </c:when>
    <c:when test="${pageScope.displayStatus.major == InactiveEnumValue}">
        <c:set var="color" value="Gray"/>
    </c:when>
    <c:when test="${pageScope.displayStatus.major == DeletedEnumValue}">
        <c:set var="color" value="Gray"/>
    </c:when>
    <c:otherwise>
        <c:set var="color" value=""/>
    </c:otherwise>
</c:choose>

<c:if test="${!testFlag}">
    <c:set var="testSuffix" value=""/>
</c:if>
<c:if test="${testFlag}">
    <c:set var="testSuffix" value="Test"/>
</c:if>

<div class="displayStatus ${color}${testSuffix}" title="<fmt:message key='${pageScope.displayStatus.description}'/>"></div>
