<%@ tag language="java" body-content="empty" description="Displays Approve/Decline buttons" %>
<%@ attribute name="entity" required="true" type="com.foros.model.OwnedApprovable" %>
<%@ attribute name="restrictionEntity" required="true" rtexprvalue="true" %>
<%@ attribute name="approvePage" rtexprvalue="true" %>
<%@ attribute name="declinePage" rtexprvalue="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="fieldAndAccessories">
    <tr>
        <c:set var="workflow" value="${ad:getApprovalWorkflow(pageScope.entity)}"/>
        
        <c:set var="APPROVE_RESTRICTON" value="${restrictionEntity}.approve"/>
        <c:set var="DECLINE_RESTRICTON" value="${restrictionEntity}.decline"/>
        
        <c:if test="${ad:isPermitted(APPROVE_RESTRICTON, pageScope.entity)}">
            <td class="withButton">
                <ui:postButton message="form.approve" href="${pageScope.approvePage}" entityId="${pageScope.entity.id}" />
            </td>
        </c:if>
        
        <c:if test="${ad:isPermitted(DECLINE_RESTRICTON, pageScope.entity)}">
            <script type="text/javascript">
                function decline(jqButton) {
                    do {
                        var reason = prompt("<fmt:message key="decline.reason"/>", "");
                        if (!reason || reason.length == 0) return false;
                        if (reason.length > 500) {
                            alert("<fmt:message key="decline.too.long"/>");
                        }
                    } while (reason.length > 500);
        
                    var currForm = jqButton.data('currForm');
                    $('<input>')
                        .attr({type: 'hidden', name: 'declinationReason', value: reason})
                        .appendTo(currForm);

                    return true;
                }
            </script>
        
            <td class="withButton">
                <ui:postButton message="form.decline" href="${pageScope.declinePage}" entityId="${pageScope.entity.id}"
                               onclick="if (!decline($(this))) {return false;}" />
            </td>
        </c:if>
        <c:set var="ai" value="${ad:qaApprovalInfo(pageScope.entity)}"/>
        <c:if test="${not empty ai}">
            <c:set var="statusMessage" value="${ai.statusMessage}"/>
            <c:set var="reason" value="${ai.reason}"/>

            <c:if test="${not empty reason}">
                <c:set var="reasonType" value="${reason.class.simpleName}"/>
                <c:choose>
                    <c:when test="${reasonType == 'QADescriptionText'}">
                        <c:set var="reasonText"><c:out value="${reason}"/></c:set>
                    </c:when>

                    <c:when test="${reasonType == 'QADescriptionChannelTriggerQA'}">
                        <%@include file="qaStatusButton/channelTriggerQA.jsp"%>
                    </c:when>

                    <c:when test="${reasonType == 'QADescriptionChannelMinUrlTriggerThreshold'}">
                        <%@include file="qaStatusButton/channelMinUrlTriggerThreshold.jsp"%>
                    </c:when>

                    <c:when test="${reasonType == 'QADescriptionChannelMaxUrlTriggerShare'}">
                        <%@include file="qaStatusButton/channelMaxUrlTriggerShare.jsp"%>
                    </c:when>

                    <c:when test="${reasonType == 'QADescriptionError'}">
                        <fmt:message var="reasonText" key="birtReports.error.unknown"/>
                    </c:when>
                </c:choose>
                <c:set var="reasonText"><fmt:message key="approval.reason"/>: ${reasonText}</c:set>
            </c:if>
            <td class="withField">
                <c:set var="textVal">(<c:out value="${statusMessage}"/><c:if test="${not (empty statusMessage or empty reasonText)}"> </c:if>${reasonText})</c:set>
                <span class="simpleText">${pageScope.textVal}</span>
                ${extra}
            </td>
        </c:if>
    </tr>
</table>
