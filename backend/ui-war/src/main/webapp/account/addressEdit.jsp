<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:fieldGroup>
    <s:hidden name="%{#attr.addressEntity}.id"/>
    <s:hidden name="%{#attr.addressEntity}.version"/>
    <c:forEach items="${addressFields}" var="field">
        <s:if test="#attr.field.OFFieldName != 'Country' && #attr.field.enabled">
            <s:if test="#attr.field.mandatory">
                <c:set var="fieldLabelClass" value="mandatory"/>
            </s:if>
            <s:else>
                <c:set var="fieldLabelClass" value=""/>
            </s:else>

            <c:set var="addressFieldName" value="${ad:localizeName(field.name)}"/>
            <ui:field label="${addressFieldName}" labelForId="${field.OFFieldName}" required="${field.mandatory}" errors="${addressEntity}.${fn:toLowerCase(field.OFFieldName)}">
                <c:choose>
                    <c:when test="${field.OFFieldName=='Line1' || field.OFFieldName=='Line2' || field.OFFieldName=='Line3' || field.OFFieldName=='City'}">
                        <c:set var="maxFieldLength" value="240" />
                    </c:when>
                    <c:when test="${field.OFFieldName=='State' || field.OFFieldName=='Province'}">
                        <c:set var="maxFieldLength" value="150" />
                    </c:when>
                    <c:when test="${field.OFFieldName=='Zip'}">
                        <c:set var="maxFieldLength" value="20" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="maxFieldLength" value="50" />
                    </c:otherwise>
                </c:choose>
                <c:set value="${addressEntity}.${fn:toLowerCase(field.OFFieldName)}" var="propertyName"/>
                <s:textfield name="%{#attr.propertyName}" id="%{#attr.addressEntity}%{#attr.field.OFFieldName}" cssClass="middleLengthText" maxLength="%{#attr.maxFieldLength}"/>
            </ui:field>
        </s:if>
    </c:forEach>
</ui:fieldGroup>
