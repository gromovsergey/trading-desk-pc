<%@ page import="com.foros.model.channel.Channel" %>
<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<div class="ajax_loader hide"></div>
<c:choose>
    <c:when test="${not empty approval}">
        <ui:errorsBlock>
            <s:fielderror><s:param value="'version'"/></s:fielderror>
        </ui:errorsBlock>

        <form id="creativeForm_${approval.creative.id}">
            <s:hidden name="approval.creative.id" value="%{#attr.approval.creative.id}"/>
            <s:hidden name="approval.creative.size.id" value="%{#attr.approval.creative.size.id}"/>
            <s:hidden name="approval.creative.templateId" value="%{#attr.approval.creative.templateId}"/>
            <s:hidden name="approval.creative.destinationUrl" value="%{#attr.approval.creative.destinationUrl}"/>
            <s:hidden name="approval.version" value="%{#attr.approval.version}"/>
            <s:hidden name="approval.approvalStatus" value="%{#attr.approval.approvalStatus}"/>
        </form>

        <ui:creativePreview creativeId="${approval.creative.id}"
                            sizeId="${approval.creative.sizeId}"
                            templateId="${approval.creative.templateId}"
                            noload="false"/>

        <div class="b-creative__approval-panel">
            <s:if test="isInternal()">
                <c:choose>
                    <c:when test="${ad:isPermitted('AdvertiserEntity.viewCreative', approval.creative.id)}">
                        <a href="${_context}/creative/view.action?id=${approval.creative.id}">
                            <c:out value="${approval.creative.id}"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <span><c:out value="${approval.creative.id}"/></span>
                    </c:otherwise>
                </c:choose>
            </s:if>

            <c:if test="${not empty approval.creative.destinationUrl}">
                <a href="${approval.creative.destinationUrl}" target="_blank"><c:out value="${ad:truncateUrl(approval.creative.destinationUrl)}"/></a>
            </c:if>

            <c:set var="canReview" value="${ad:isPermitted('PublisherEntity.reviewCreatives', site.id)}"/>
            <div class="b-creative__approval-buttons">
                <c:choose>
                    <c:when test="${approval.approvalStatus.letter != 'A' && approval.approvalStatus.letter != 'C'}">
                        <c:choose>
                            <c:when test="${canReview}">
                                <ui:button message="site.creativesApproval.button.approve" type="button" onclick="approveCreative(${approval.creative.id});"/>
                            </c:when>
                            <c:otherwise>
                                <ui:button message="site.creativesApproval.button.approve" disabled="true"/>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <ui:button message="site.creativesApproval.button.approved" disabled="true"/>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${approval.approvalStatus.letter != 'R'}">
                        <c:choose>
                            <c:when test="${canReview}">
                                <ui:button message="site.creativesApproval.button.reject" type="button" onclick="showRejectDialog(${approval.creative.id});"/>
                            </c:when>
                            <c:otherwise>
                                <ui:button message="site.creativesApproval.button.reject" disabled="true"/>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <c:set var="rejectReasonTooltip">
                            <fmt:message key="enums.CreativeRejectReason.${approval.rejectReason}"/>
                        </c:set>
                        <ui:button message="site.creativesApproval.button.rejected" title="${rejectReasonTooltip}" disabled="true"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <c:if test="${approval.approvalStatus.letter == 'R'}">
            <div class="b-creative__approval-rejecttext">
                <fmt:message key="site.creativesApproval.rejectReason">
                    <fmt:param><fmt:message key="enums.CreativeRejectReason.${approval.rejectReason}"/></fmt:param>
                </fmt:message>
                <c:if test="${not empty approval.feedback}">
                    <span style="margin-left: 3px;">
                        <ui:hint>
                            <c:out value="${approval.feedback}"/>
                        </ui:hint>
                    </span>
                </c:if>
            </div>
        </c:if>
    </c:when>
    <c:otherwise>
        <ui:errorsBlock>
            <span class="errors"><fmt:message key="site.creativesApproval.error.noApproveOrReject"/></span>
        </ui:errorsBlock>
    </c:otherwise>
</c:choose>
