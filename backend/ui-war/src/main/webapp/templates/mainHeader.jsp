<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<h1><s:text name="%{#attr.entityName}.plural"/></h1>
    
<s:url var="createNewUrl" action="%{#attr.moduleName}/%{#attr.entityName}/edit"/>
<ui:button message="form.createNew" href="${createNewUrl}" />
