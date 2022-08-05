<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">

$().ready(function() {
    $('#countryCode').change(function() {
        updateCurrentWalledGardens($(this).val());
    }).change();
});

function updateCurrentWalledGardens(countryCode) {
    $("#currentWalledGardensContainer").empty();
    
    UI.Data.getUrl('listCurrentWalledGardens.action', "html", { countryCode: countryCode }, function(data) {
        $("#currentWalledGardensContainer").html(data);
    });
}

</script>

<ui:header styleClass="level2">
    <h2><fmt:message key="WalledGarden.current.accounts"/></h2>
</ui:header>

<table class="dataViewSection">
    <tr class="controlsZone">
        <td>
            <table class="grouping">
                <tr>
                    <td class="withButtons"></td>
                    <td class="filterZone">
                        <table class="fieldAndAccessories">
                            <tr>
                                <td class="withField">
                                    <label for="countryCode"><fmt:message key="WalledGarden.select.country"/>:</label>
                                </td>
                                <td class="withField">
                                    <s:select name="countryCode" id="countryCode" list="countries" headerKey="" headerValue="%{getText('form.all')}">
                                    </s:select>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr class="bodyZone">
        <td>
            <div id="currentWalledGardensContainer"></div>
        </td>
    </tr>
</table>
