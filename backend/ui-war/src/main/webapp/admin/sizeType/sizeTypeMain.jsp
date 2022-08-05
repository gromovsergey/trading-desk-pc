<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags"  prefix="ui"%>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<display:table name="types" class="dataView" id="type">
    <display:setProperty name="basic.msg.empty_list" >
        <div class="wrapper">
            <fmt:message key="nothing.found.to.display"/>
        </div>
    </display:setProperty>
    <display:column titleKey="SizeType.defaultName">
        <a class="preText" href="view.action?id=${type.id}"><c:out value="${ad:localizeName(type.name)}"/></a>
    </display:column>
    <display:column titleKey="SizeType.multipleSizes">
        <fmt:message key="SizeType.multipleSizes.${type.multipleSizes}"/>
    </display:column>
    <display:column titleKey="SizeType.advertiserSizeSelection">
        <fmt:message key="SizeType.advertiserSizeSelection.${type.advertiserSizeSelection}"/>
    </display:column>
</display:table>
