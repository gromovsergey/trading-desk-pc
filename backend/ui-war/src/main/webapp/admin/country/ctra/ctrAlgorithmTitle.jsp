<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<title>
    <s:if test="#attr.isViewPage">
        <s:text name="country.CTRAlgorithmData" var="titleText"/>
    </s:if>
    <s:else>
        <s:text name="country.CTRAlgorithmData.edit" var="titleText"/>
    </s:else>
    <ui:windowTitle attributeName="${titleText}" isSimpleText="true"/>
</title>
