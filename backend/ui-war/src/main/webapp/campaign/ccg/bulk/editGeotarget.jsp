<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">
    var channelsCnt = {"Set": 0, "Add": 0, "Remove": 0};

    $(function() {
        $('#stButtons').tabs({
            select: function( event, ui ) {
                $('#editModeId').val($(ui.tab).parent('li').data('id'));
            }
        });
    });

    function showHideLocations(tabName) {
        var locations = $('#locations');
        if($('#state_' + tabName + ' option').length <= 1 && $('#city_' + tabName + ' option').length <= 1 ) {
            locations.hide();
        } else {
            locations.show();
        }
    }

    function getCityList(tabName) {
        UI.Data.Options.get('cityList', 'city_' + tabName, {stateId:$('#state_' + tabName).val(), countryCode:$('#countryCode').val()}, ['form.all'], function() {
            showHideLocations(tabName);
        });
    }

    function addLocation(tabName) {
        var stateId = $('#state_' + tabName).val();
        var cityId = $('#city_' + tabName).val(); 
        if (!(stateId > 0) && !(cityId > 0)) {
            return;
        }
        var stateName = $('#state_' + tabName + ' :selected').text();
        if (!(stateId >0)) {
            stateName = "";
        }
        var cityName = $('#city_' + tabName + ' :selected').text();
        var rowId = "geoChannelsRow_" + tabName + "_" + stateId + "_" + cityId;
        var id = stateId;
        if (cityId > 0) id = cityId;
        var channelRow = "<tr id='" + rowId + "' class='geoChannelsRow'><td><input type='hidden' name='geoChannels" + tabName + "' value='"+ id +"'/> <span class='simpleText'><c:out value='${stateLabel}'/></span>: ";
        channelsCnt[tabName]++;

        channelRow += stateName;
        channelRow += "</td><td>";
        if (cityId > 0) {
            channelRow += "<span class='simpleText'><c:out value='${cityLabel}'/></span>: ";
            channelRow += cityName;
        }
        channelRow += "</td><td>";
        channelRow += "<a href='javascript:deleteChannel(\"" + tabName + "\", \"" + rowId + "\");' class='button'><fmt:message key='form.delete'/></a></td></tr>";
        $("#channels_" + tabName).append(channelRow);
    }

    function deleteChannel(tabName, rowId) {
        $("#" + rowId).remove();
        var num = $("#channels_" + tabName + " tr").size();
        if (num == 0) {
            channelsCnt[tabName] = 0;
        }
    }
</script>

<s:form action="save" id="geotarget">
    <s:hidden name="campaignId"/>
    <s:hidden name="editMode" id="editModeId"/>
    <s:hidden name="countryCode" id="countryCode"/>

    <%@ include file="bulkGroupErrors.jsp"%>

    <div id="stButtons">
        <ul>
            <li data-id="Set"><ui:button message="ccg.bulk.geotarget.setTo" href="#setDiv"/></li>
            <li data-id="Add"><ui:button message="ccg.bulk.geotarget.add" href="#addDiv"/></li>
            <li data-id="Remove"><ui:button message="ccg.bulk.geotarget.remove" href="#removeDiv"/></li>
        </ul>

        <div id="setDiv">
            <c:choose>
                <c:when test="${not empty country}">
                    <ui:section titleKey="channel.locations" id="locations" cssStyle="width: 100%; ${(statesIsEmpty && citiesIsEmpty) ? 'display:none;' : ''}">
                        <ui:fieldGroup>
                            <ui:field>
                                <table id="channels_Set"  class="dataViewSection"></table>
                                <table>
                                    <tr>
                                        <td colspan="3">
                                            <span class="simpleText"><fmt:message key="ccg.bulk.geotarget.select"/></span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <s:select name="state" id="state_Set" cssClass="smallLengthText2"
                                                    headerKey="" headerValue="%{getText('form.all')}"
                                                    list="states" value="state.name"
                                                    listKey="id" listValue="name"
                                                    onchange="getCityList('Set');"/>
                                        </td>
                                        <td>
                                            <s:select name="city" id="city_Set" cssClass="smallLengthText2"
                                                    headerKey="" headerValue="%{getText('form.all')}"
                                                    list="cities" value="city.name"
                                                    listKey="id" listValue="name"/>
                                        </td>
                                        <td>
                                            <ui:button message="form.add" onclick="addLocation('Set')"/>
                                        </td>
                                    </tr>
                                </table>
                            </ui:field>
                        </ui:fieldGroup>
                    </ui:section>
                </c:when>
                <c:otherwise>
                    <p class="withWarningBig">
                        <fmt:message key="ccg.bulk.geotarget.warn"/>
                    </p>
                </c:otherwise>
            </c:choose>
        </div>
        <div id="addDiv">
            <c:choose>
                <c:when test="${not empty country}">
                    <ui:section titleKey="channel.locations" id="locations" cssStyle="width: 100%; ${(statesIsEmpty && citiesIsEmpty) ? 'display:none;' : ''}">
                        <ui:fieldGroup>
                            <ui:field>
                                <table id="channels_Add"></table>
                                <table>
                                    <tr>
                                        <td colspan="3">
                                            <span class="simpleText"><fmt:message key="ccg.bulk.geotarget.select"/></span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <s:select name="state" id="state_Add" cssClass="smallLengthText2"
                                                    headerKey="" headerValue="%{getText('form.all')}"
                                                    list="states" value="state.name"
                                                    listKey="id" listValue="name"
                                                    onchange="getCityList('Add');"/>
                                        </td>
                                        <td>
                                            <s:select name="city" id="city_Add" cssClass="smallLengthText2"
                                                    headerKey="" headerValue="%{getText('form.all')}"
                                                    list="cities" value="city.name"
                                                    listKey="id" listValue="name"/>
                                        </td>
                                        <td>
                                            <ui:button message="form.add" onclick="addLocation('Add')" />
                                        </td>
                                    </tr>
                                </table>
                            </ui:field>
                        </ui:fieldGroup>
                    </ui:section>
                </c:when>
                <c:otherwise>
                    <p class="withWarningBig">
                        <fmt:message key="ccg.bulk.geotarget.warn"/>
                    </p>
                </c:otherwise>
            </c:choose>
        </div>
        <div id="removeDiv">
            <c:choose>
                <c:when test="${not empty country}">
                    <ui:section titleKey="channel.locations" id="locations" cssStyle="width: 100%; ${(statesIsEmpty && citiesIsEmpty) ? 'display:none;' : ''}">
                        <ui:fieldGroup>
                            <ui:field>
                                <table id="channels_Remove"></table>
                                <table>
                                    <tr>
                                        <td colspan="3">
                                            <span class="simpleText"><fmt:message key="ccg.bulk.geotarget.select"/></span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <s:select name="state" id="state_Remove" cssClass="smallLengthText2"
                                                    headerKey="" headerValue="%{getText('form.all')}"
                                                    list="states" value="state.name"
                                                    listKey="id" listValue="name"
                                                    onchange="getCityList('Remove');"/>
                                        </td>
                                        <td>
                                            <s:select name="city" id="city_Remove" cssClass="smallLengthText2"
                                                    headerKey="" headerValue="%{getText('form.all')}"
                                                    list="cities" value="city.name"
                                                    listKey="id" listValue="name"/>
                                        </td>
                                        <td>
                                            <ui:button message="form.add" onclick="addLocation('Remove')" />
                                        </td>
                                    </tr>
                                </table>
                            </ui:field>
                        </ui:fieldGroup>
                    </ui:section>
                </c:when>
                <c:otherwise>
                    <p class="withWarningBig">
                        <fmt:message key="ccg.bulk.geotarget.warn"/>
                    </p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</s:form>
