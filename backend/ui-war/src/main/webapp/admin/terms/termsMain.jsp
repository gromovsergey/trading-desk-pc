<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ui:pageHeadingByTitle/>

<s:set var="enableEditLinkFlag" value="true"/>
<c:if test="${not ad:isPermitted('Account.update', account)}">
    <s:set var="enableEditLinkFlag" value="false"/>
</c:if>
<ui:section>
    <ui:fieldGroup>
        <s:iterator value="params">
            <ui:field labelKey="${id.type}" labelForId="${id.type}">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <textarea readonly="readonly" class="middleLengthText" id="${id.type}" name="${id.type}"><s:property value="value" escape="true"/></textarea>
                        </td>
                        <td class="withButton">
                            <s:if test="%{enableEditLinkFlag}">
                                <s:url var="editUrl" action="%{#attr.moduleName}/%{#attr.entityName}/edit">
                                  <s:param name="type" value="id.type"/>
                                  <s:param name="accountId" value="id.accountId"/>
                                </s:url>
                                <ui:button message="form.edit" href="${editUrl}" />
                            </s:if>
                        </td>
                    </tr>
                </table>
            </ui:field>
        </s:iterator>
    </ui:fieldGroup>
</ui:section>

