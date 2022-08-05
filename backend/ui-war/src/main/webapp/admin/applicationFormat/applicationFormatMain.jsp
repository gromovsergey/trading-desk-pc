<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<display:table name="entities" class="dataView" defaultsort="1" id="format">
    <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
    <display:column titleKey="ApplicationFormat.name" style="width:30%;" sortProperty="name">
        <a href="<s:url action="%{#attr.moduleName}/%{#attr.entityName}/view"/>?id=${format.id}"><c:out value="${format.name}"/></a>
    </display:column>
    <display:column property="mimeType" titleKey="ApplicationFormat.mimeType"/>
</display:table>
