<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<s:form action="save">
    <s:hidden name="campaignId"/>
    <%@ include file="bulkGroupErrors.jsp"%>
    <s:include value="../editBidStrategy.jsp"/>
</s:form>
