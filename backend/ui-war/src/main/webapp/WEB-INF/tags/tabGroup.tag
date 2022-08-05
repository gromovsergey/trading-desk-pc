<%@ tag description="UI Tab Group" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>


<%@ attribute name="id" required="true" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="additionalInfo" %>
<%@ attribute name="additionalInfoHint" %>
<%@ attribute name="additionalInfoHintKey" %>
<%@ attribute name="activeTab" type="java.lang.String"%>
<%@ attribute name="activeTabProperty" type="java.lang.String"%>


<c:set var="additionalHint">
    <c:choose>
        <c:when test="${not empty pageScope.additionalInfoHint}">${pageScope.additionalInfoHint}</c:when>
        <c:when test="${not empty pageScope.additionalInfoHintKey}">
            <fmt:message key="${pageScope.additionalInfoHintKey}"/>
        </c:when>
        <c:otherwise></c:otherwise>
    </c:choose>
</c:set>

<ui:section id="${pageScope.id}" cssClass="withTabs ${pageScope.cssClass}">
    <div class="tabsZone">
        <c:set var="__ui_tabGroup_activeTab" value="${pageScope.activeTab}" scope="request"/>
        <div class="tabs"></div>
        <c:if test="${not empty pageScope.additionalInfo}">
            <div class="additionalInfo">
                <span>${pageScope.additionalInfo}</span>
                <c:if test="${not empty pageScope.additionalHint}">
                    <ui:hint><c:out value="${pageScope.additionalHint}"/></ui:hint>
                </c:if>
            </div>
        </c:if>
    </div>
    <jsp:doBody />
</ui:section>

<script type="text/javascript">
    (function() {
        var currTabGroup = $('#${pageScope.id}');
        var tabs = $('.tabs:eq(0) > .tab', currTabGroup)
        var containersLinkedToTabs = $('.linkedCont', currTabGroup);

        if(!tabs.filter('.active').length){
            tabs.eq(0).addClass('active');
            containersLinkedToTabs.eq(0).removeClass('hide');
        }

        tabs.click(function(){
            var curr = $(this);
            containersLinkedToTabs.addClass('hide');
            containersLinkedToTabs.eq(tabs.index(this)).removeClass('hide');
                tabs.removeClass('active');
            curr.addClass('active')
                .blur();
                return false;
            });

        var tabsInTabContainer = $('#${pageScope.id} .tabs:eq(0) > .tab');

        <c:if test="${not empty pageScope.activeTabProperty}">
            tabsInTabContainer.click(function(){
                $('#${id}_activeTab').val(this.id);
            });
        </c:if>
    })();
</script>

