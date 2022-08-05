<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<s:iterator value="%{#attr.breadcrumbs}" var="element" status="iStatus">
    <s:if test="%{#element instanceof com.foros.breadcrumbs.EntityBreadcrumbsElement}">
        <s:text name="%{#element.entityNameResource}"/>:
        <s:if test="!#iStatus.last">
            <s:url var="viewUrl" value="%{#attr._context}/%{#element.path}.action">
                <s:param name="id" value="#element.id"/>
            </s:url>
            <a href="${viewUrl}"><c:out value="${element.name}"/></a>
        </s:if>
        <s:else>
            <c:out value="${element.name}"/>
        </s:else>
    </s:if>
    <s:elseif test="%{#element instanceof com.foros.breadcrumbs.CustomParametersBreadcrumbsElement}">
        <a href="${_context}/${element.path}.action?${element.parametersAsString}"><s:text name="%{#element.resource}"/></a>
    </s:elseif>
    <s:elseif test="%{#element instanceof com.foros.breadcrumbs.SimpleLinkBreadcrumbsElement}">
        <a href="${_context}/${element.path}.action"><s:text name="%{#element.resource}"/></a>
    </s:elseif>
    <s:elseif test="%{#element instanceof com.foros.breadcrumbs.SimpleTextBreadcrumbsElement}">
        <s:text name="%{#element.resource}"/>
    </s:elseif>
    <s:if test="!#iStatus.last">
        <span class="delimiter">&gt;</span>
    </s:if>
</s:iterator>