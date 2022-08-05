<%@ tag description="UI Tab" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"    prefix="fmt" %>

<%@ attribute name="title" %>
<%@ attribute name="titleKey" %>
<%@ attribute name="hint" %>
<%@ attribute name="hintKey" %>
<%@ attribute name="id" type="java.lang.String"%>

<c:set var="active" value="${__ui_tabGroup_activeTab == id}"/>
<div class="linkedCont ${active ? '': 'hide'}" id="${id}_container">
    <jsp:doBody />
</div>

<c:set var="titleText" value="${pageScope.title}" />
<c:if test="${not empty pageScope.titleKey}">
    <fmt:message key="${pageScope.titleKey}" var="titleText"/>
</c:if>

<c:set var="hintText" value="${pageScope.hint}" />
<c:if test="${not empty pageScope.hintKey}">
    <fmt:message key="${pageScope.hintKey}" var="hintText"/>
</c:if>

<script type="text/javascript">
    (function() {
        var currTab = $('#${id}_container');
        var parentTabGroup = currTab.parents('.fieldset.withTabs:eq(0)');
        var tabZone = $('.tabs:eq(0)', parentTabGroup);
        tabZone.append('<a title="${hintText}" id="${id}" class="tab ${active ? 'active': ''}" href="#"><div>${titleText}</div></a>');
    })();
</script>

