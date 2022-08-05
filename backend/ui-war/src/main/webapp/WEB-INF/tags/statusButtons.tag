<%@ tag language="java" body-content="empty" description="Displays Delete/Undelete buttons" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<%@ attribute name="entity" required="true" type="com.foros.model.StatusEntityBase" %>
<%@ attribute name="restrictionEntity" required="true" rtexprvalue="true" %>
<%@ attribute name="activatePage" rtexprvalue="true" %>
<%@ attribute name="inactivatePage" rtexprvalue="true" %>
<%@ attribute name="deletePage" rtexprvalue="true" %>
<%@ attribute name="undeletePage" rtexprvalue="true" %>

<c:if test="${empty pageScope.activatePage}">
    <c:set var="activatePage" value="activate.action"/>
</c:if>
<c:if test="${empty pageScope.inactivatePage}">
    <c:set var="inactivatePage" value="inactivate.action"/>
</c:if>
<c:if test="${empty pageScope.deletePage}">
    <c:set var="deletePage" value="delete.action"/>
</c:if>
<c:if test="${empty pageScope.undeletePage}">
    <c:set var="undeletePage" value="undelete.action"/>
</c:if>

<c:set var="workflow" value="${ad:getStatusWorkflow(pageScope.entity)}"/>

<c:set var="ACTIVATE_RESTRICTION" value="${restrictionEntity}.activate"/>
<c:set var="CHANGE_STATUS_RESTRICTON" value="${restrictionEntity}.update"/>
<c:set var="UNDELETE_RESTRICTON" value="${restrictionEntity}.undelete"/>

<table class="fieldAndAccessories">
    <tr>
        <c:if test="${not ad:isDeleted(pageScope.entity)}">
        
            <c:if test="${ad:isStatusActionAvailable(workflow, 'ACTIVATE')
                            and ad:isPermitted(ACTIVATE_RESTRICTION, pageScope.entity)}">
                <td class="withButton">
                    <ui:postButton message="form.activate" href="${pageScope.activatePage}" entityId="${entity.id}" />
                </td>
            </c:if>
        
            <c:if test="${ad:isPermitted(CHANGE_STATUS_RESTRICTON, pageScope.entity)}">
        
                <c:if test="${ad:isStatusActionAvailable(workflow, 'INACTIVATE')}">
                    <td class="withButton">
                        <ui:postButton message="form.deactivate" href="${pageScope.inactivatePage}" entityId="${pageScope.entity.id}" />
                    </td>
                </c:if>
        
                <c:if test="${ad:isStatusActionAvailable(workflow, 'DELETE')}">
                    <td class="withButton">
                        <ui:postButton message="form.delete" href="${pageScope.deletePage}" entityId="${pageScope.entity.id}"
                                onclick="if (!confirm('${ad:formatMessage('confirmDelete')}')) {return false;}" />
                    </td>
                </c:if>
        
            </c:if>
        
        </c:if>
        
        <c:if test="${not ad:isParentDeleted(pageScope.entity)
                        and ad:isStatusActionAvailable(workflow, 'UNDELETE')
                        and ad:isPermitted(UNDELETE_RESTRICTON, pageScope.entity)}">
            
            <td class="withButton">
                <ui:postButton message="form.undelete" href="${pageScope.undeletePage}" entityId="${pageScope.entity.id}" />
            </td>
        </c:if>
    </tr>
</table>



