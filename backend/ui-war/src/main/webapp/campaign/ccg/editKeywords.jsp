<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>

<s:form action="%{#attr.moduleName}/updateKeywords">

<ui:section>
    <span class="infos">
        <fmt:message key="ccg.format"/>:
        <ul>
            <li><fmt:message key="ccg.keyword.per.line"/></li>
            <li><fmt:message key="ccg.keyword.quotes.extract"/></li>
            <li><fmt:message key="ccg.keyword.squareBrackets.extract"/></li>
            <li><fmt:message key="ccg.keyword.double.asterisk"/>
                <ul>
                    <li><fmt:message key="ccg.keyword.double.asterisk.example.1"/></li>
                    <li><fmt:message key="ccg.keyword.double.asterisk.example.2"/></li>
                    <li><fmt:message key="ccg.keyword.double.asterisk.example.3"/></li>
                    <li><fmt:message key="ccg.keyword.double.asterisk.example.4"/></li>
                </ul>
            </li>
            <li><fmt:message key="ccg.keyword.minus"/></li>
        </ul>
    </span>
    <s:hidden name="id"/>
    <s:hidden name="ccgVersion"/>
    <s:fielderror><s:param value="'keywordsText'"/></s:fielderror>
    <s:textarea name="keywordsText" cssClass="middleLengthText1"/>
</ui:section>  

<div class="wrapper">
    <ui:button message="form.save" type="submit"/>
    <ui:button message="form.cancel" onclick="location='${_context}/campaign/group/viewText.action?id=${id}';" type="button"/>
</div>

</s:form>
