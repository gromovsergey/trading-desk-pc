<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<script type="text/javascript">
    function initiateDeletion(){
        if(confirm('<s:text name="SearchEngine.confirmDeletion"/>')){
            document.getElementById("deletionForm").submit();
        }
    }
</script>
<ui:header>

    <ui:pageHeadingByTitle/>

    <c:if test="${ad:isPermitted0('SearchEngine.update')}">
        <c:url var="url" value="/admin/SearchEngine/edit.action">
            <c:param name="id" value="${model.id}"/>
        </c:url>
        <ui:button message="form.edit" href="${url}"/>
        <ui:button message="form.delete" onclick="initiateDeletion();"/>
    </c:if>

    <c:url var="url" value="/admin/auditLog/view.action">
        <c:param name="type" value="${ad:getObjectType(model)}"/>
        <c:param name="id" value="${id}"/>
    </c:url>
    <ui:button message="form.viewLog" href="${url}" />

</ui:header>

<form action="/admin/SearchEngine/delete.action" method="POST" id="deletionForm">
    <input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/>
    <input type="hidden" name="id" value="${id}"/>
</form>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="SearchEngine.name">
            <ui:text text="${name}"/>
        </ui:field>
        <ui:field labelKey="SearchEngine.host">
            <ui:text text="${host}"/>
        </ui:field>
        <ui:field labelKey="SearchEngine.regexp">
            <ui:text text="${regexp}"/>
        </ui:field>
    <c:if test="${not empty encoding}">
        <ui:field labelKey="SearchEngine.encoding">
            <ui:text text="${encoding}"/>
        </ui:field>
    </c:if>
        <ui:field labelKey="SearchEngine.decodingDepth">
            <ui:text text="${decodingDepth}"/>
        </ui:field>
    <c:if test="${not empty postEncoding}">
        <ui:field labelKey="SearchEngine.postEncoding">
            <ui:text text="${postEncoding}"/>
        </ui:field>
    </c:if>
    </ui:fieldGroup>
</ui:section>
