<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %><script type="text/javascript">
    $('#ccgDialogCancel').hide();
    $('#ccgDialogSubmit').hide();
    $('#ccgDialogOk').show();
</script>
<table>
    <tr>
        <td><c:if test="${not empty groupsWithError}">
            <fmt:message key="Action.linkToCampaigns.groups.error" />
            <ul>
                <c:forEach var="entry" items="${groupsByError}">
                <li>
                    <span class="errors"><c:out value="${entry.key}" /></span>
                    <ad:commaWriter var="group" items="${entry.value}">
                        <c:out value="${ad:appendStatus(group.name, group.status)}" />
                    </ad:commaWriter>
                </li>
                </c:forEach>
            </ul>
        </c:if></td>
    </tr>
</table>
