<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:form action="admin/CategoryChannel/update" method="post">
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
        <c:if test="${ad:isPermitted0('CategoryChannel.update')}">
          <c:choose>
            <c:when test="${empty id}">
              <ui:field labelKey="channel.account" labelForId="account" required="true" >
                <s:select list="channelOwners" listKey="id" listValue="name" value="account.id"
                             name="accountId" id="account" cssClass="middleLengthText"/>
              </ui:field>
            </c:when>
            <c:otherwise>
              <input type="hidden" name="accountId" value="${accountId}">
              <input type="hidden" name="accountName" value="${accountName}">
              <ui:simpleField labelKey="channel.account" value="${accountName}"/>
            </c:otherwise>
          </c:choose>

          <ui:field labelKey="channel.defaultName" labelForId="name" required="true" errors="name,errors.duplicate">
              <s:textfield name="name" id="name" cssClass="middleLengthText" maxlength="100"/>
          </ui:field>

          <ui:field labelKey="channel.hidden" cssClass="valignFix">
              <label class="withInput"><input type="radio" name="isHiddenChannel" value="false" <c:if test="${!isHiddenChannel}">checked="checked"</c:if> /><fmt:message key="no"/></label>
              <label class="withInput"><input type="radio" name="isHiddenChannel" value="true" <c:if test="${isHiddenChannel}">checked="checked"</c:if> /><fmt:message key="yes"/></label>
          </ui:field>

          <ui:field labelKey="channel.newsgateCategoryName" labelForId="newsgateCategoryName" errors="newsgateCategoryName">
              <s:textfield name="newsgateCategoryName" id="newsgateCategoryName" cssClass="middleLengthText" maxlength="1000"/>
          </ui:field>
        </c:if>
      </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
      <ui:button message="form.save" type="submit"/>
      <c:choose>
        <c:when test="${empty id and empty parentChannelId}">
          <ui:button message="form.cancel" onclick="location='/admin/CategoryChannel/main.action';" type="button"/>
        </c:when>
        <c:when test="${empty id and not empty parentChannelId}">
          <ui:button message="form.cancel" onclick="location='/admin/CategoryChannel/view.action?id=${parentChannelId}';" type="button"/>
        </c:when>
        <c:otherwise>
          <ui:button message="form.cancel" onclick="location='/admin/CategoryChannel/view.action?id=${id}';" type="button"/>
        </c:otherwise>
      </c:choose>
    </div>
</s:form>
