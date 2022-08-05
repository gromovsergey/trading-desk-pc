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

<s:form action="save" id="clickurls">
    <s:hidden name="campaignId"/>
    <s:hidden name="editMode" id="editModeId"/>

    <ui:errorsBlock>
        <s:actionerror/>
    </ui:errorsBlock>

    <div id="stButtons">
        <ul>
            <li data-id="Set"><ui:button message="ccg.bulk.adclickurls.set" href="#setDiv"/></li>
            <li data-id="Append"><ui:button message="ccg.bulk.adclickurls.append" href="#appendDiv"/></li>
            <li data-id="Replace"><ui:button message="ccg.bulk.adclickurls.findreplace" href="#replaceDiv"/></li>
        </ul>

        <div id="setDiv">
            <ui:field labelKey="ccg.bulk.adclickurls.url" labelForId="url" errors="url">
                <s:textfield name="url" cssClass="middleLengthText" maxlength="2000"/>
            </ui:field>
        </div>
        <div id="appendDiv">
            <ui:field labelKey="ccg.bulk.adclickurls.append.value" labelForId="append" errors="append">
                <s:textfield name="append" cssClass="middleLengthText" maxlength="2000"/>
            </ui:field>
        </div>
        <div id="replaceDiv">
            <p><fmt:message key="ccg.bulk.adclickurls.searchreplace"/></p>
            <ui:field labelKey="ccg.bulk.adclickurls.search.value" labelForId="search" errors="search">
                <s:textfield name="search" cssClass="middleLengthText" maxlength="2000"/>
            </ui:field>
            <ui:field labelKey="ccg.bulk.adclickurls.replace.value" labelForId="replace" errors="replace">
                <s:textfield name="replace" cssClass="middleLengthText" maxlength="2000"/>
            </ui:field>
        </div>
    </div>
</s:form>
