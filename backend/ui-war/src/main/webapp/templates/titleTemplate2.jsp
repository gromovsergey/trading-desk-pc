<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<title>
    <s:if test="model">
        <c:set var="entity" value="${model}"/>
    </s:if>
    <c:if test="${empty titleProperty}">
        <s:if test="%{#attr.entity.localizableName != null}">
            <c:set var="localizedName" value="${entity.localizableName.defaultName}"/>
        </s:if>
        <s:elseif test="%{#attr.entity.name != null}">
            <c:set var="localizedName" value="${entity.name}"/>
            <s:if test="%{#attr.entity.name instanceof com.foros.model.LocalizableName}">
                <c:set var="localizedName" value="${entity.name.defaultName}"/>
            </s:if>
        </s:elseif>
        <s:if test="%{#attr.entity instanceof com.foros.model.DisplayStatusEntity}">
            <s:if test="%{#attr.entity instanceof com.foros.model.account.Account}">
                <ui:windowTitleEntity entityName="${entityName}" isViewPage="${isViewPage}" id="${entity.id}" name="${localizedName}" colorStatus="${entity.displayStatus}" testFlag="${entity.testFlag}"/>
            </s:if>
            <s:else>
                <ui:windowTitleEntity entityName="${entityName}" isViewPage="${isViewPage}" id="${entity.id}" name="${localizedName}" colorStatus="${entity.displayStatus}"/>
            </s:else>
        </s:if>
        <s:else>
            <ui:windowTitleEntity entityName="${entityName}" isViewPage="${isViewPage}" id="${entity.id}" name="${localizedName}"/>
        </s:else>
    </c:if>
    <c:if test="${not empty titleProperty}">
        <ui:windowTitle attributeName="${titleProperty}"/>
    </c:if>
</title>
