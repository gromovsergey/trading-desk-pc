<%@ tag description="UI Field Group" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"    prefix="fmt" %>

<%@ attribute name="id" %>
<%@ attribute name="cssClass" %>

  <table id="${pageScope.id}" class="formFields ${pageScope.cssClass}">
    <jsp:doBody />
  </table>