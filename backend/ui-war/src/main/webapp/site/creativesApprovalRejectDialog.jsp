<%@ page import="com.foros.model.channel.Channel" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<form id="rejectForm">
    <table class="formFields">
        <tr>
            <td>
                <label class="mandatory" for="rejectReason"><fmt:message key="site.creativesApproval.reject.reason"/></label>
                <c:if test="${not empty errors.rejectReason}">
                    <s:fielderror>
                        <s:param value="%{#attr.errors.rejectReason.trim()}"/>
                    </s:fielderror>
                </c:if>
            </td>
        </tr>
        <tr>
            <td class="field">
                <s:radio name="approval.rejectReason" id="rejectReason"
                         list="@com.foros.model.site.CreativeRejectReason@values()"
                         listKey="name()" listValue="getText('enums.CreativeRejectReason.' + name())"/>
            </td>
        </tr>
        <tr>
            <td>
                <label for="rejectReason"><fmt:message key="site.creativesApproval.reject.feedback"/></label>
                <c:if test="${not empty errors.feedback}">
                    <s:fielderror>
                        <s:param value="%{#attr.errors.feedback.trim()}"/>
                    </s:fielderror>
                </c:if>
            </td>
        </tr>
        <tr>
            <td class="field">
                <s:textarea name="approval.feedback" id="feedback" cols="70" rows="6" maxLength="2000"/>
                <input type="hidden" id="rejectFormCId" value="">
            </td>
        </tr>
    </table>
</form>
