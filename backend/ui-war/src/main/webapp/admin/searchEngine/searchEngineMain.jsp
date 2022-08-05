<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<display:table name="entities" class="dataView" defaultsort="1" id="rowEntity">
    <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
    <display:column titleKey="SearchEngine.name" style="width:30%;" sortProperty="name">
        <a href="<s:url action="%{#attr.moduleName}/%{#attr.entityName}/view"/>?id=${rowEntity.id}"><c:out value="${rowEntity.name}"/></a>
    </display:column>
    <display:column titleKey="SearchEngine.host"><c:out value="${rowEntity.host}"/></display:column>
    <display:column titleKey="SearchEngine.regexp"><c:out value="${rowEntity.regexp}"/></display:column>
</display:table>
