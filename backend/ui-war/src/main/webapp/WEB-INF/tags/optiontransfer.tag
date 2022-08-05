<%@ tag import="java.util.Collection" %>
<%@ tag language="java" body-content="empty" %>
<%@ attribute name="id" %>
<%@ attribute name="selId" %>
<%@ attribute name="name" required="true" rtexprvalue="true" %>
<%@ attribute name="size" rtexprvalue="true" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="listKey" rtexprvalue="true" %>
<%@ attribute name="listValue" rtexprvalue="true" %>
<%@ attribute name="list" required="true" rtexprvalue="true" type="java.util.Collection" %>
<%@ attribute name="selList" required="true" rtexprvalue="true" type="java.util.Collection" %>
<%@ attribute name="selListKey" rtexprvalue="true" %>
<%@ attribute name="selListValue" rtexprvalue="true" %>
<%@ attribute name="saveSorting" type="java.lang.Boolean"%>
<%@ attribute name="sort" type="java.lang.Boolean"%>
<%@ attribute name="selSort" type="java.lang.Boolean"%>
<%@ attribute name="title" rtexprvalue="true" %>
<%@ attribute name="titleKey" rtexprvalue="true" %>
<%@ attribute name="selTitle" rtexprvalue="true" %>
<%@ attribute name="selTitleKey" rtexprvalue="true" %>
<%@ attribute name="mandatory" type="java.lang.Boolean"%>
<%@ attribute name="escape" type="java.lang.Boolean"%>
<%@ attribute name="onchange" type="java.lang.String"%>
<%@ attribute name="immovableOptions" rtexprvalue="true" type="java.util.Collection" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<c:if test="${empty pageScope.id}">
  <c:set var="id" value="available_${ad:escapeId(pageScope.name)}_id"/>
</c:if>
<c:if test="${empty pageScope.selId}">
  <c:set var="selId" value="selected_${ad:escapeId(pageScope.name)}_id"/>
</c:if>
<c:set var="allOptions" value="alloptions_${ad:escapeId(pageScope.name)}_id"/>
<c:if test="${empty pageScope.saveSorting}">
  <c:set var="saveSorting" value="false"/>
</c:if>
<c:if test="${empty pageScope.sort}">
  <c:set var="sort" value="false"/>
</c:if>
<c:if test="${empty pageScope.selSort}">
  <c:set var="selSort" value="false"/>
</c:if>
<c:if test="${empty pageScope.listKey}">
  <c:set var="listKey" value="id"/>
</c:if>
<c:if test="${empty pageScope.listValue}">
  <c:set var="listValue" value="name"/>
</c:if>
<c:if test="${empty pageScope.selListKey}">
  <c:set var="selListKey" value="${pageScope.listKey}"/>
</c:if>
<c:if test="${empty pageScope.selListValue}">
  <c:set var="selListValue" value="${pageScope.listValue}"/>
</c:if>
<c:if test="${pageScope.list == null}">
  <jsp:useBean id="list" class="java.util.ArrayList"/>
</c:if>
<c:if test="${pageScope.selList == null}">
  <jsp:useBean id="selList" class="java.util.ArrayList"/>
</c:if>
<c:if test="${pageScope.immovableOptions == null}">
  <jsp:useBean id="immovableOptions" class="java.util.ArrayList"/>
</c:if>
<c:set var="onchangeFunction" value="function() {${pageScope.onchange};}"/>
<c:set var="name" value="${not empty pageScope.name ? pageScope.name : ''}"/>
<c:set var="size" value="${not empty pageScope.size ? pageScope.size : ''}"/>
<c:set var="cssClass" value="${not empty pageScope.cssClass ? pageScope.cssClass : ''}"/>

<c:if test="${empty optiontransfer_js_included || optiontransfer_js_included != 'true'}">
  <c:set var="optiontransfer_js_included" value="true" scope="request"/>
</c:if>

<table class="grouping optionTransfer">
<tr>
<td>
    <c:set var="leftLabelText">
        <c:if test="${not empty pageScope.titleKey}"><fmt:message key="${pageScope.titleKey}"/></c:if>
        <c:if test="${not empty pageScope.title}">${pageScope.title}</c:if>
    </c:set>

  <c:if test="${pageScope.saveSorting == true}">
    <select id="${allOptions}" disabled="true" style="display:none;">
      <c:forEach items="${pageScope.list}" var="opt">
        <option value="${opt[pageScope.listKey]}"><c:out value="${opt[pageScope.listValue]}"/></option>
      </c:forEach>
    </select>
  </c:if>
    
    <ui:select id="${pageScope.id}" size="${pageScope.size}" cssClass="${pageScope.cssClass}" labelText="${pageScope.leftLabelText}">
        <c:forEach items="${pageScope.list}" var="opt">
            <c:if test="${not ad:contains(pageScope.selList, opt)}">
                <option value="${opt[pageScope.listKey]}">
                    <c:choose>
                        <c:when test="${not empty pageScope.escape}"><c:out value="${opt[pageScope.listValue]}"/></c:when>
                        <c:otherwise>${opt[pageScope.listValue]}</c:otherwise>
                    </c:choose>
                </option>
            </c:if>
        </c:forEach>
    </ui:select>


</td>
<td class="optionTransferControls">
    <table class="buttonsContainer">
        <tr>
            <td>
                <ui:button messageText="&gt;" type="button" onclick="UI.Optiontransfer.moveSelectedOptions(document.getElementById('${pageScope.id}'), document.getElementById('${pageScope.selId}'), document.getElementById('${allOptions}'), ${pageScope.saveSorting}, ${pageScope.selSort}, ${onchangeFunction}, ${immovableOptions});" />
                <ui:button messageText="&gt;&gt;" type="button" onclick="UI.Optiontransfer.moveAllOptions(document.getElementById('${pageScope.id}'), document.getElementById('${pageScope.selId}'), document.getElementById('${allOptions}'), ${pageScope.saveSorting}, ${pageScope.selSort}, ${onchangeFunction}, ${immovableOptions});" />
                <ui:button messageText="&lt;" type="button" onclick="UI.Optiontransfer.moveSelectedOptions(document.getElementById('${pageScope.selId}'), document.getElementById('${pageScope.id}'), document.getElementById('${allOptions}'), ${pageScope.saveSorting}, ${pageScope.sort}, ${onchangeFunction}, ${immovableOptions});" />
                <ui:button messageText="&lt;&lt;" type="button" onclick="UI.Optiontransfer.moveAllOptions(document.getElementById('${pageScope.selId}'), document.getElementById('${pageScope.id}'), document.getElementById('${allOptions}'), ${pageScope.saveSorting}, ${pageScope.sort}, ${onchangeFunction}, ${immovableOptions});" />
            </td>
        </tr>
    </table>
</td>
<td>
    
    <c:set var="rightLabelText">
        <c:if test="${not empty pageScope.selTitleKey}">
           <fmt:message key="${pageScope.selTitleKey}"/>
        </c:if>
        <c:if test="${not empty pageScope.selTitle}">
            ${pageScope.selTitle}
        </c:if>
    </c:set>
  
    <ui:select id="${pageScope.selId}" name="${pageScope.name}" 
                size="${pageScope.size}" cssClass="${pageScope.cssClass} optiontransferSelected" 
                labelText="${pageScope.rightLabelText}" required="${pageScope.mandatory}">
        <c:if test="${pageScope.saveSorting == true}">
          <c:forEach items="${pageScope.list}" var="opt">
              <c:if test="${ad:contains(pageScope.selList, opt)}">
              <option value="${opt[pageScope.listKey]}">
              <c:choose>
                    <c:when test="${not empty pageScope.escape}"><c:out value="${opt[pageScope.listValue]}"/></c:when>
                    <c:otherwise>${opt[pageScope.listValue]}</c:otherwise>
                </c:choose>
              </option>
              </c:if>
          </c:forEach>
        </c:if>
    
        <c:if test="${pageScope.saveSorting == false}">
            <c:forEach items="${pageScope.selList}" var="opt">
                <c:forEach items="${pageScope.list}" var="srcOpt">
                    <c:if test="${srcOpt[pageScope.listKey] == opt[pageScope.selListKey]}">
                        <option value="${opt[pageScope.selListKey]}">
                         <c:choose>
                            <c:when test="${not empty pageScope.escape}"><c:out value="${srcOpt[pageScope.listValue]}"/></c:when>
                            <c:otherwise>${srcOpt[pageScope.listValue]}</c:otherwise>
                        </c:choose>
                        </option>
                    </c:if>
                </c:forEach>
            </c:forEach>
        </c:if>
    </ui:select>
</td>
</tr>
</table>

<script type="text/javascript">
    <c:if test="${pageScope.sort}">UI.Optiontransfer.sortSelect(document.getElementById('${pageScope.id}'));</c:if>
    <c:if test="${pageScope.selSort}">UI.Optiontransfer.sortSelect(document.getElementById('${pageScope.selId}'));</c:if>
    
    $(function(){
        $('form:has(#${pageScope.selId})').submit(function() {
            UI.Optiontransfer.selectAllOptions(document.getElementById("${pageScope.selId}"));
        });
        
        $('#${pageScope.id}').on('dblclick', function(){
            UI.Optiontransfer.moveSelectedOptions(document.getElementById('${pageScope.id}'), document.getElementById('${pageScope.selId}'), document.getElementById('${allOptions}'), ${pageScope.saveSorting}, ${pageScope.selSort}, ${onchangeFunction}, ${immovableOptions});
        });
        $('#${pageScope.selId}').on('dblclick', function(){
            UI.Optiontransfer.moveSelectedOptions(document.getElementById('${pageScope.selId}'), document.getElementById('${pageScope.id}'), document.getElementById('${allOptions}'), ${pageScope.saveSorting}, ${pageScope.sort}, ${onchangeFunction}, ${immovableOptions});
        });
    });

</script>