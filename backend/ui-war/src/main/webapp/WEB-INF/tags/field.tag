<%@ tag description="UI Field" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"      prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"       prefix="fmt" %>
<%@ taglib uri="/struts-tags"                           prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<%@ attribute name="id" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="labelKey" %>
<%@ attribute name="label" %>
<%@ attribute name="labelForId" %>
<%@ attribute name="required" type="java.lang.Boolean" %>
<%@ attribute name="errors" %>
<%@ attribute name="tipKey" %>
<%@ attribute name="tipText" %>
<%@ attribute name="escapeXml" type="java.lang.Boolean" %>

<c:if test="${escapeXml == null}"><c:set var="escapeXml" value="${true}" /></c:if>

<c:choose>
    <c:when test="${pageScope.cssClass == 'subsectionRow'}">
        <c:set var="isSubsectionRow" value="true"/>
        <c:set var="colspan" value="2"/>
    </c:when>
    <c:otherwise>
        <c:set var="labelAttachment" value=":"/>
    </c:otherwise>
</c:choose>

  <tr id="${pageScope.id}" class="${pageScope.cssClass}">
    <c:choose>
      <c:when test="${not empty pageScope.labelKey or not empty pageScope.label}">
        <td class="fieldName" colspan="${colspan}">
          <label for="${pageScope.labelForId}" class="${pageScope.required ? 'mandatory' : ''}" >
            <c:choose>
              <c:when test="${not empty pageScope.labelKey}">
                <fmt:message key="${pageScope.labelKey}" />${labelAttachment}
              </c:when>
              <c:when test="${not empty pageScope.label}">
                <c:out value="${pageScope.label}" />${labelAttachment}
              </c:when>
            </c:choose>
          </label>
        </td>
      </c:when>
      <c:otherwise>
        <td style="padding:0;"></td>
      </c:otherwise>
    </c:choose>

    <c:if test="${not isSubsectionRow}">
      <td class="field">
        <c:choose>
          <c:when test="${not empty pageScope.errors}">
            <table class="fieldAndAccessories">
              <tr>
                  <td class="withField">
                    <jsp:doBody />
                  </td>
                  <c:if test="${not empty pageScope.tipText or not empty pageScope.tipKey}">
                    <td class="withTip">
                        <c:choose>
                            <c:when test="${not empty pageScope.tipText}">
                                <ui:hint>
                                    <c:out value="${pageScope.tipText}" escapeXml="${escapeXml}" />
                                </ui:hint>
                            </c:when>
                            <c:otherwise>
                                <ui:hint>
                                    <fmt:message key="${pageScope.tipKey}"/>
                                </ui:hint>
                            </c:otherwise>
                        </c:choose>
                    </td>
                  </c:if>
                  <td class="withError">
                    <c:set var="errList" value="${fn:split(pageScope.errors, ', ')}" />
                    <%--Errors for struts 2--%>  
                    <s:fielderror>
                      <c:forEach items="${errList}" var="err">
                        <s:param value="%{#attr.err.trim()}"/>
                      </c:forEach>
                    </s:fielderror>
                  </td>
              </tr>
            </table>
          </c:when>
          <c:otherwise>
            <c:choose>
              <c:when test="${not empty pageScope.tipText or not empty pageScope.tipKey}">
                <table class="fieldAndAccessories">
                  <tr>
                    <td class="withField">
                      <jsp:doBody />
                    </td>
                    <td class="withTip">
                      <c:choose>
                        <c:when test="${not empty pageScope.tipText}">
                          <ui:hint>
                            <c:out value="${pageScope.tipText}" escapeXml="${escapeXml}" />
                          </ui:hint>
                        </c:when>
                        <c:otherwise>
                          <ui:hint>
                            <fmt:message key="${pageScope.tipKey}"/>
                          </ui:hint>
                        </c:otherwise>
                      </c:choose>
                    </td>
                  </tr>
                </table>
              </c:when>
              <c:otherwise>
                <jsp:doBody />
              </c:otherwise>
            </c:choose>
          </c:otherwise>
        </c:choose>
      </td>
    </c:if>
  </tr>
