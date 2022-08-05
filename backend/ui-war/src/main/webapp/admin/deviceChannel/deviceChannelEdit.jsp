<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">


function insertText(text){
    var cdml = $("#cdml");
    UI.Text.insertAtCaret(cdml[0], text);
    cdml.focus();
    UI.Text.setCaretToEnd(cdml[0]);
}

function insertParenthesis(){
    var container = $("#cdml")[0];
    container.value += '() ';
    UI.Text.setCaretToEnd(container);
}
</script>

<s:form action="admin/DeviceChannel/%{id == null?'create':'update'}" method="post">
    <s:hidden name="id"/>
    <s:hidden name="parentChannelId"/>
    <s:if test="id != null">
        <s:hidden name="version"/>
    </s:if>

    <ui:pageHeadingByTitle/>

    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </ui:errorsBlock>

    <ui:section>
      <ui:fieldGroup>

          <ui:field labelKey="channel.name" labelForId="name" required="true" errors="name,errors.duplicate">
              <s:textfield name="name" id="name" cssClass="middleLengthText" maxlength="100"/>
          </ui:field>

          <ui:field labelKey="channel.deviceChannel.location">
              <div class="path">
                  <c:forEach items="${parentLocations}" var="parentLocation" varStatus="indexId">
                      <c:if test="${indexId.count > 0}">
                          <span class='delimiter'>/</span>
                      </c:if>
                      <a href="/admin/DeviceChannel/view.action?id=${parentLocation.id}"><c:out value="${parentLocation.name}"/></a>
                  </c:forEach>
              </div>
          </ui:field>

      </ui:fieldGroup>
    </ui:section>

<ui:header styleClass="level3">
    <h2><fmt:message key="channel.deviceChannel.Platforms"/></h2>
</ui:header>

<ui:section >
    <ui:fieldGroup>
        <ui:field>
         <c:forEach var="platform" items="${platforms}" varStatus="status">
            <c:if test="${status.index % 3 == 0}"><tr></c:if>
            <td>
            <ui:button messageText="${platform.name}" onclick="insertText(' [${platform.name}] ');" />
            </td>
          
            <c:if test="${status.index % 3 == 2}"></tr></c:if>
          
          </c:forEach>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<ui:section titleKey="channel.expressionEditor" mandatory="true">
    <ui:fieldGroup>
        <ui:field labelKey="channel.expressionEditor.operators">
            <ui:button messageText="OR" onclick="insertText(' OR ');" />
            <ui:button messageText="AND" onclick="insertText(' AND ');" />
            <ui:button messageText="AND&nbsp;NOT" onclick="insertText(' AND_NOT ');" />
            <ui:button messageText="( ... )" onclick="insertParenthesis();" />
        </ui:field>
    </ui:fieldGroup>
    <ui:fieldGroup>

        <ui:field>
            <s:fielderror><s:param value="'expression'"/></s:fielderror>
            <s:textarea id="cdml" styleId="cdml" rows="3" cssClass="bigLengthText"
                name="humanExpression" style="height: 50px"/>
        </ui:field>

    </ui:fieldGroup>
</ui:section>

    <div class="wrapper">
      <ui:button message="form.save" type="submit"/>
      <c:choose>
        <c:when test="${empty id}">
          <ui:button message="form.cancel" onclick="location='/admin/DeviceChannel/view.action?id=${parentChannelId}';" type="button"/>
        </c:when>
        <c:otherwise>
          <ui:button message="form.cancel" onclick="location='/admin/DeviceChannel/view.action?id=${id}';" type="button"/>
        </c:otherwise>
      </c:choose>
    </div>
</s:form>
