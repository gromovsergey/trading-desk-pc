<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<display:table name="entities" class="dataView" id="rowEntity">
    <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
    <display:column titleKey="AccountType.name" style="width:30%;">
        <a class="preText" href="<s:url action="%{#attr.moduleName}/%{#attr.entityName}/view"/>?id=${rowEntity.id}"><c:out value="${rowEntity.name}"/></a>
    </display:column>
    <display:column titleKey="AccountType.accountRole">
        <fmt:message key="enum.accountRole.${rowEntity.accountRole}"/>
    </display:column>
</display:table>
