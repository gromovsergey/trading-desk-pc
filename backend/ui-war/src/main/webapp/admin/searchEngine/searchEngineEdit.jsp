<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:pageHeadingByTitle/>
<ui:section cssClass="message warning">
    <span class="infos"><s:text name="SearchEngine.regexp.warning"/></span>
</ui:section>
<s:form action="admin/SearchEngine/%{#attr.isCreatePage?'create':'update'}">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:actionerror/>
    </div>
    <ui:section>
        <ui:fieldGroup>

            <ui:field labelKey="SearchEngine.name" labelForId="name" required="true" errors="name">
                <s:textfield name="name" cssClass="middleLengthText" maxLength="100" id="name"/>
            </ui:field>

            <ui:field labelKey="SearchEngine.host" labelForId="host" required="true" errors="host">
                <s:textfield name="host" id="host" cssClass="middleLengthText" maxLength="4000"/>
            </ui:field>

            <ui:field labelKey="SearchEngine.regexp" labelForId="regexp" required="true" errors="regexp">
                <s:textfield  name="regexp" id="regexp" cssClass="middleLengthText" maxLength="4000"/>
            </ui:field>

            <ui:field labelKey="SearchEngine.encoding" labelForId="encoding" errors="encoding">
                <s:textfield name="encoding" cssClass="middleLengthText" maxLength="200" id="encoding"/>
            </ui:field>

            <ui:field labelKey="SearchEngine.decodingDepth" labelForId="decodingDepth" required="true" errors="decodingDepth">
                <s:textfield name="decodingDepth" cssClass="middleLengthText" maxLength="2" id="decodingDepth"/>
            </ui:field>

            <ui:field labelKey="SearchEngine.postEncoding" labelForId="postEncoding" errors="postEncoding">
                <s:textfield name="postEncoding" cssClass="middleLengthText" maxLength="13" id="postEncoding"/>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>

    <s:include value="/templates/formFooter.jsp"/>
</s:form>
