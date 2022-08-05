<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xhtml="true">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><ui:windowTitle attributeName="page.title.fileManager"/></title>
    <ui:stylesheet fileName="common.css" />
</head>

<body>
<display:table name="infoList" class="dataView" id="info">
  <display:setProperty name="basic.msg.empty_list" >
      <div class="wrapper">
          <fmt:message key="nothing.found.to.display"/>
      </div>
  </display:setProperty>

  <display:column title="User Name" style="white-space:nowrap;">
       <c:out value="${info.userName}"/>
  </display:column>

  <display:column title="User Agent" style="white-space:nowrap;">
       <c:out value="${info.userAgent}"/>
  </display:column>

  <display:column title="User IP" style="white-space:nowrap;">
       <c:out value="${info.ip}"/>
  </display:column>

  <display:column title="Last Accessed Time (GMT)" style="white-space:nowrap;">
       <fmt:formatDate value="${info.lastAccessedTime}" type="both" timeStyle="short" dateStyle="short" timeZone="GMT"/>
  </display:column>
</display:table>
</body>
</html>