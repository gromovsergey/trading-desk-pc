<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<center>
    <c:choose>
        <c:when test="${tagPreviewAvailable}">
                <p><fmt:message key="site.tagsPreview.preMessage"/></p>
                ${previewText}
                <p><fmt:message key="site.tagsPreview.postMessage"/></p>
        </c:when>
        <c:otherwise>
            <fmt:message key="site.tagsPreview.notAvailable"/>
        </c:otherwise>
    </c:choose>

    <table style="margin-top:10px; margin-bottom:10px;">
      <tr>
          <td>
              <ui:button message="form.close" onclick="window.close();"/>
          </td>
      </tr>
    </table>
</center>
