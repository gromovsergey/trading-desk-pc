<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${!template.expandable}" > 
    <ui:section titleKey="creative.expandableTemplate">
        <ui:field labelKey="creative.expandableCreative" required="true">
            <s:select list="creative.size.expansions" ></s:select>
        </ui:field>
    </ui:section>
</c:if>