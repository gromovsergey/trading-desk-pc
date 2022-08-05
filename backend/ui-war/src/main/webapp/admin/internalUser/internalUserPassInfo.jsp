<%--
  User: nitin.afre
  Date: Nov 26, 2008
  Time: 9:13:50 PM
--%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="userBean" scope="request" class="com.foros.model.security.User"/>
<jsp:setProperty name="userBean" property="id" value="${UserForm.id}" />
<jsp:setProperty name="userBean" property="status" value="${UserForm.status}"/>

<ui:pageHeadingByTitle/>

    <table width="100%" cellpadding="0" cellspacing="0">
        <tr><td class="rtext">
            <%if (request.getParameter("sentSuccessfully") != null) {
                if (request.getParameter("sentSuccessfully").equals("true"))
                {%>
                        <fmt:message key="user.js.passwordSuccess"><fmt:param value="${UserForm.email}"/></fmt:message>
            <%  } else { %>
                        <fmt:message key="user.js.passwordSuccessGen"><fmt:param value="${UserForm.email}"/></fmt:message>
            <%  }
            } %>
            </td>
        </tr>
    </table>
