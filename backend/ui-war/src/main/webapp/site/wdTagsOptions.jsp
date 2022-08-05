<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<s:hidden name="template.version"/>
<s:set var="groups" value="template.publisherOptionGroups"/>
<c:set var="optionTitleKey" value="page.title.Option"/>
<%@ include file="/admin/option/optionValuesEdit.jsp"%>
