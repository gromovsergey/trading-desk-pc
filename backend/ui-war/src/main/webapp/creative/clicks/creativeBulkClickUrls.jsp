<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">
    $(function() {
        $('#stButtons').tabs({
            select: function( event, ui ) {
                $('#editModeId').val($(ui.tab).parent('li').data('id'));
            }
        });
        $("#stButtons").tabs("select", <s:property value="%{editMode.ordinal()}"/>);
    });

</script>

<form method="post" action="%{#attr._context}/creative/saveclickUrls.action" id="clickurls">
    <s:hidden name="editMode" id="editModeId"/>

    <%@ include file="errors.jsp"%>

    <div id="stButtons">
        <ul>
            <li data-id="Set"><ui:button message="creative.clickUrls.set" href="#setDiv"/></li>
            <li data-id="Append"><ui:button message="creative.clickUrls.append" href="#appendDiv"/></li>
            <li data-id="Replace"><ui:button message="creative.clickUrls.findreplace" href="#replaceDiv"/></li>
        </ul>

        <div id="setDiv">
            <ui:field labelKey="creative.clickUrls.url" labelForId="url" errors="url">
                <s:textfield name="url" cssClass="middleLengthText" maxlength="2000"/>
            </ui:field>
        </div>
        <div id="appendDiv">
            <ui:field labelKey="creative.clickUrls.append.value" labelForId="append" errors="append">
                <s:textfield name="append" cssClass="middleLengthText" maxlength="2000"/>
            </ui:field>
        </div>
        <div id="replaceDiv">
            <p><fmt:message key="creative.clickUrls.searchreplace"/></p>
            <ui:field labelKey="creative.clickUrls.search.value" labelForId="search" errors="search">
                <s:textfield name="search" cssClass="middleLengthText" maxlength="2000"/>
            </ui:field>
            <ui:field labelKey="creative.clickUrls.replace.value" labelForId="replace" errors="replace">
                <s:textfield name="replace" cssClass="middleLengthText" maxlength="2000"/>
            </ui:field>
        </div>
    </div>
</form>
