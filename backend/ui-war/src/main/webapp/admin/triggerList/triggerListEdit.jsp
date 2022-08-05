<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<s:set var="listType" scope="request">${fn:replace(entityName, ".", "")}</s:set>
<ui:pageHeadingByTitle/>
<s:form action="%{#request.moduleName}/%{#request.listType}/save">
  <s:if test="hasActionErrors()">
  <div style="margin-top: 5px; margin-bottom: 5px">
      <s:actionerror/>
  </div>
  </s:if>
  <div style="margin-top: 5px; margin-bottom: 10px;">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
  </div>
  <s:hidden name="id"/>
  <s:hidden name="version"/>
  <div class="wrapper">
      <table class="grouping fieldsets">
        <tr>
            <td class="singleFieldset">
                <ui:section titleKey="TriggerList.keywords" errors="keywords">
                    <s:textarea name="keywordsText" id="keywords" wrap="off" cssClass="middleLengthText1"/>
                </ui:section>
            </td>
            <td class="singleFieldset">
                <ui:section titleKey="TriggerList.urls" errors="urls">
                    <s:textarea name="urlsText" id="urls" wrap="off" cssClass="middleLengthText1"/>
                </ui:section>
            </td>
        </tr>
        <tr><td colspan="2">&nbsp<td/></tr>
        <tr>
            <td class="singleFieldset">
                <ui:section titleKey="TriggerList.urlKeywords" errors="urlKeywords">
                    <s:textarea name="urlKeywordsText" id="urlKeywords" wrap="off" cssClass="middleLengthText1"/>
                </ui:section>
            </td>
            <td/>
        </tr>
    </table>
    <p>
        <ui:button message="form.save" type="submit"/>
        <ui:button message="form.cancel" onclick="location='/${moduleName}/${listType}/view.action';" type="button" />
    </p>
  </div>
</s:form>