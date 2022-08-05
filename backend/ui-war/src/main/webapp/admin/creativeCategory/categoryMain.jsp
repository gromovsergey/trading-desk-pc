<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ui:header>
    <ui:pageHeadingByTitle />
    <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=30" />
</ui:header>
    
<ui:header styleClass="level2">
    <h2>
        <fmt:message key="CreativeCategory.visual"/>
        <s:url action="%{#attr.moduleName}/%{#attr.entityName}/edit" var="url"/>
    </h2>
    <c:if test="${ad:isPermitted0('CreativeCategory.update')}">
        <ui:button message="form.edit" href="${url}?type=VISUAL" />
    </c:if>
</ui:header>

<c:choose>
    <c:when test="${not empty visuals}">
        <table id="category" class="dataView">
            <thead>
                <tr>
                    <th style="min-width:200px;"><s:text name="CreativeCategory.table.name"/></th>
                    <th><s:text name="CreativeCategory.table.id"/></th>
                    <c:forEach items="${rtbKeys}" var="rtbKey">
                        <th><c:out value="${rtbKey.name}"/></th>
                    </c:forEach>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${visuals}" var="category">
                    <tr>
                        <td>
                            <div class="markerIconContainer">
                                <ui:text subClass="entityName" text="${category.defaultName}"/>
                                <c:if test="${ad:isPermitted0('CreativeCategory.update')}">
                                    <div id="${category.id}" class="markerIcon" style="margin-top:1px;" title="<fmt:message key="dynamicResources.localize"/>"></div>
                                </c:if>
                            </div>
                        </td>
                        <td>
                            ${category.id}
                        </td>
                        <c:forEach items="${connectors}" var="connector">
                            <td>
                                <c:forEach items="${category.rtbCategories}" var="rtbCategory">
                                    <c:if test="${rtbCategory.rtbConnector.id == connector.id}">
                                        <ui:text text="${rtbCategory.name}"/>
                                    </c:if>
                                </c:forEach>
                            </td>
                        </c:forEach>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <div class="wrapper">
          <fmt:message key="nothing.found.to.display"/>
        </div>
    </c:otherwise>
</c:choose>

<ui:header styleClass="level2">
    <h2>
        <fmt:message key="CreativeCategory.content"/>
        <s:url action="%{#attr.moduleName}/%{#attr.entityName}/edit" var="url" />
    </h2>
    <c:if test="${ad:isPermitted0('CreativeCategory.update')}">
        <ui:button message="form.edit" href="${url}?type=CONTENT" />
    </c:if>
</ui:header>
<c:choose>
    <c:when test="${not empty contents}">
        <table id="contents" class="dataView">
        <thead>
            <tr>
                <th style="min-width:200px;"><s:text name="CreativeCategory.table.name"/></th>
                <th><s:text name="CreativeCategory.table.id"/></th>
                <c:forEach items="${rtbKeys}" var="rtbKey">
                    <th><c:out value="${rtbKey.name}"/></th>
                </c:forEach>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${contents}" var="category">
                <tr>
                    <td>
                        <div class="markerIconContainer">
                            <ui:text text="${category.defaultName}"/>
                            <c:if test="${ad:isPermitted0('CreativeCategory.update')}">
                                <div id="${category.id}" class="markerIcon" style="margin-top:1px;" title="<fmt:message key="dynamicResources.localize"/>"></div>
                            </c:if>
                        </div>
                    </td>
                    <td>
                        ${category.id}
                    </td>
                    <c:forEach items="${connectors}" var="connector">
                        <td>
                            <c:forEach items="${category.rtbCategories}" var="rtbCategory">
                                <c:if test="${rtbCategory.rtbConnector.id == connector.id}">
                                    <ui:text text="${rtbCategory.name}"/>
                                </c:if>
                            </c:forEach>
                        </td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <div class="wrapper">
          <fmt:message key="nothing.found.to.display"/>
        </div>
    </c:otherwise>
</c:choose>

<ui:header styleClass="level2">
    <h2>
        <fmt:message key="CreativeCategory.tag"/>
        <s:url action="%{#attr.moduleName}/%{#attr.entityName}/edit" var="url" />
    </h2>
    <c:if test="${ad:isPermitted0('CreativeCategory.update')}">
        <ui:button message="form.edit" href="${url}?type=TAG" />
    </c:if>
</ui:header>

<c:choose>
    <c:when test="${not empty tags}">
        <table id="tags" class="dataView">
        <tbody>
            <c:forEach items="${tags}" var="category">
            <tr><td style="min-width:200px;">
                <c:out value="${category.defaultName}"/>
            </td></tr>
            </c:forEach>
        </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <div class="wrapper">
          <fmt:message key="nothing.found.to.display"/>
        </div>
    </c:otherwise>
</c:choose>

<script type="text/javascript">
    $(function(){
        $('#category, #contents').on('click', '.markerIcon', function(e){
            e.preventDefault();
            UI.Localization.showPopup(e, {url:'/admin/resource/CreativeCategory/', key: this.id});
        });
    });
</script>
