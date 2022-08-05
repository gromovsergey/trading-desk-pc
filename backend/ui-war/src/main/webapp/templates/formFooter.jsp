<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="wrapper">
    <ui:button message="form.save" type="submit" />
    <s:if test="id == null">
        <ui:button message="form.cancel" onclick="location='${empty param.createUrl ? 'main.action' : param.createUrl}';" type="button" />
    </s:if>
    <s:else>
        <ui:button message="form.cancel" onclick="location='view.action?id=${id}';" type="button" />
    </s:else>
</div>