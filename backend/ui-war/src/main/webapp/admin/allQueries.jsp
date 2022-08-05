<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xhtml="true">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>All queries</title>
    <ui:stylesheet fileName="common.css" />
    <ui:externalLibrary libName="jquery"/>
</head>
    
<body>
<display:table name="allContexts" class="dataView" id="context">
    <display:column title="ID">
        <div style="margin-bottom: 10px"><c:out value="${context.id}"/></div>
        <c:if test="${not context.wasCancelCalled}">
            <button value="Cancel" onclick="cancelQuery('${context.id}', this)">Cancel</button>
        </c:if>
        <span>
            <c:if test="${context.wasCancelCalled}">CANCELLED</c:if>
        </span>
    </display:column>
    <display:column title="User">
        <c:out value="${context.userId}"/>/<c:out value="${context.userName}"/>
    </display:column>
    <display:column title="Started">
        <c:out value="${context.started}"/>
    </display:column>
    <display:column title="Query">
        <pre><c:out value="${context.description}"/></pre>
    </display:column>
</display:table>

<script type="text/javascript">
    function cancelQuery(id, self) {
        $(self).attr("disabled","disabled");
        $.ajax({
            type: "POST",
            url: "cancel.action",
            data: { cancellationToken: id, PWSToken: "${PWSToken}" },
            success: function() {
                $(self).hide();
                $(self).next()
                        .removeClass("errors")
                        .text("CANCELLED");
            },
            error: function(data, textStatus, jqXHR) {
                $(self).removeAttr("disabled");
                $(self).next()
                        .addClass("errors")
                        .text("ERROR: " + textStatus);
            }
        });
    }
</script>
</body>
</html>
