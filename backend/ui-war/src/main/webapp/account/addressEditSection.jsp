<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!existingAccount.country.countryCode.empty">
<c:choose>
    <c:when test="${empty id or ad:isPermitted('Account.updateBillingContactDetails', existingAccount)}">
        <c:set var="addressEntity" value="billingAddress" />
        <ui:section titleKey="account.headers.billing.address" mandatory="true" id="billingAddressId">
            <%@ include file="/account/addressEdit.jsp" %>
        </ui:section>
         <script type="text/javascript">
             function copyBillingAddress() {
             <c:forEach items="${addressFields}" var="field">
                 <c:if test="${field.OFFieldName != 'Country' && field.enabled}">
                     $('#legalAddress${field.OFFieldName}').val($('#billingAddress${field.OFFieldName}').val());
                 </c:if>
             </c:forEach>
             }
         </script>
        <c:set var="addressEntity" value="legalAddress" />
        <ui:section titleKey="account.headers.legal.address" mandatory="true" id="legalAddressId">
            <ui:button id="copyBillingAddressId" message="account.copyBillingAddress" onclick="copyBillingAddress();" type="link" />
            <%@ include file="/account/addressEdit.jsp" %>
        </ui:section>
    </c:when>
    <c:otherwise>
        <s:if test="billingAddress.id != null">
            <c:set var="addressEntity" value="${billingAddress}" />
            <ui:section titleKey="account.headers.billing.address">
                <%@ include file="/account/addressView.jsp" %>
            </ui:section>
        </s:if>
        <s:if test="legalAddress.id != null">
            <c:set var="addressEntity" value="${legalAddress}" />
            <ui:section titleKey="account.headers.legal.address">
                <%@ include file="/account/addressView.jsp" %>
            </ui:section>
        </s:if>
    </c:otherwise>
</c:choose>
</s:if>
