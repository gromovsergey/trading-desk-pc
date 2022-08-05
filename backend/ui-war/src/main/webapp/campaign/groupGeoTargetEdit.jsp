<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">

var channelsCnt;

$().ready(function() {
    channelsCnt = ${fn:length(geoChannels)};

    toggleRadioSections();

    var searchGeoFunc = function() {
        $.ajax({
            url: 'searchGeoCode.action',
            data: {geoCode: $('#geoCodeInput').val(),
                id: ${id}
            } ,
            success: function(data) {
                $('#geoCodeResult').html(data);
            },
            type: "GET"
        })
    }

    $('#searchGeoCode').click(searchGeoFunc);
    $('#geoCodeInput').keyup(function(e){
        if(e.keyCode == 13) {
            e.preventDefault();
            return searchGeoFunc()
        }
    });

    $('#radiusUnit').change(function() {
        switch($('#radiusUnit').val()) {
            case 'm':
            case 'yd':
                $('#rangeValues').html("50 - 50000")
                break;
            case 'km':
                $('#rangeValues').html("1 - 50")
                break;
            case 'mi':
                $('#rangeValues').html("1 - 30")
                break;
        }
    });
    $('#radiusUnit').change();
});

function getStateList() {
    //remove all geo channels
    $(".geoChannelsRow").remove();
    UI.Data.Options.get('stateList', 'state', {countryCode:$('#countryCode').val()}, ['form.all'], function(){
        getCityList();
    });

    if ($("#countryCode").val() != 'RU') {
        $("input[name=cityOrAddressFlag][value=true]").click();
        toggleRadioSections();
        $("#radioSection").hide();
    } else {
        $("#radioSection").show();
    }
}

function showHideLocations(){
    var locations = $('#locations');
    if($('#state option').length <= 1 && $('#city option').length <= 1 ){
        locations.hide();
    }else{
        locations.show();
    }
}

function getCityList() {
    UI.Data.Options.get('cityList', 'city', {stateId:$('#state').val(), countryCode:$('#countryCode').val()}, ['form.all'], function(){
        showHideLocations();
    });
}
function addStateOrCity() {
    var stateId = $('#state').val();
    var cityId = $('#city').val();
    if  (!(stateId >0 ) && !(cityId > 0)) {
        return;
    }

    $("#emptyLocations").hide();
    $("#existingLocations").show();

    var id = stateId;
    if (cityId > 0) id = cityId;

    UI.Data.get('coordinates', {channelId: id}, function(data) {
        var lat = $("latitude", data).text();
        var lon = $("longitude", data).text();

        addLocationRow(cityId, stateId, lat, lon);
    });
}

function addLocationRow(cityId, stateId, lat, lon) {
    var isInternal = ${ad:isInternal()};
    var id = stateId;
    if (cityId > 0) id = cityId;
    var stateName = $('#state :selected').text();
    if (!(stateId >0)) {
        stateName = "";
    }
    var cityName = $('#city :selected').text();

    var channelRow = "<tr class='geoChannelsRow'><td><input type='hidden' name='geoChannels[" + channelsCnt + "].id' value='"+ id +"'/>";
    channelRow += "<input type='hidden' name='geoChannels[" + channelsCnt + "].name' value='"+ cityName +"'/>";

    if (cityId > 0) {
        channelRow += "<input type='hidden' name='geoChannels[" + channelsCnt + "].geoType' value='CITY'/>";
        if (isInternal) {
            channelRow += "<a href='/admin/GeoChannel/view.action?id=" + cityId + "'>";
        }
        channelRow += cityName;
        if (isInternal) {
            channelRow += "</a>";
        }
        channelRow += ", "
    } else {
        channelRow += "<input type='hidden' name='geoChannels[" + channelsCnt + "].geoType' value='STATE'/>";
    }

    if (isInternal) {
        channelRow += "<a href='/admin/GeoChannel/view.action?id=" + stateId + "'>";
    }
    channelRow += stateName;
    if (isInternal) {
        channelRow += "</a>";
    }
    channelRow += "</td><td>";

    if (cityId > 0) {
        channelRow += '<fmt:message key="channel.city"/></td>';
    } else {
        channelRow += '<fmt:message key="channel.state"/></td>';
    }

    if (lon && lat && lon != "null" && lat != "null") {
        var latValue = lat.replace(',', '.');
        var lonValue = lon.replace(',', '.');
        channelRow += "<td>" + lat + "&nbsp; " + lon + '&nbsp; <a href="#" onclick="openMap(' + latValue + ',' + lonValue + ')"><fmt:message key="channel.address.map"/></a>' + "</td>";
    } else {
        channelRow += "<td></td>"
    }
    channelRow += "<td><a href='#' onclick='deleteChannel(this);' class='button'><fmt:message key='form.delete'/></a></td>";
    channelRow += "</tr>";
    channelsCnt++;
    $("#channels").append(channelRow);
}

function deleteChannel(that) {
    $(that).closest("tr").remove();
    var num = $("#channels tbody>tr").size();
    if (num == 0) {
        channelsCnt = 0;
        $("#existingLocations").hide();
        $("#emptyLocations").show();
    }
}

function validateRadius() {
    var radius = $("#radius").val();

    if (!/^\d+$/.test(radius) || !$.isNumeric(radius) || radius % 1 > 0) {
        alert('<fmt:message key='channel.address.error.radius.fraction'/>');
        return false;
    }

    var result = true;
    switch($('#radiusUnit').val()) {
        case 'm':
        case 'yd':
            if (radius < 50 || radius > 50000) {
                result = false;
            }
            break;
        case 'km':
            if (radius < 1 || radius > 50) {
                result = false;
            }
            break;
        case 'mi':
            if (radius < 1 || radius > 30) {
                result = false;
            }
            break;
    }

    if (!result) {
        alert('<fmt:message key='channel.address.error.radius.range'/>');
    }
    return result;
}

function addAddress(rowIndex) {
    var addr = $("#address__" + rowIndex).text();
    var latlonA = $("#latlon__" + rowIndex).text().trim().split(/\b\s+/);
    var lat = latlonA[0];
    var lon = latlonA[1];
    var radius = $("#radius").val();
    if (!validateRadius()) {
        return false;
    }
    var unit = $("#radiusUnit").val();

    $("#emptyLocations").hide();
    $("#existingLocations").show();
    var channelRow = "<tr class='geoChannelsRow'><td><fmt:message key='channel.address.within'/> " +  radius + unit + " <fmt:message key='channel.address.of'/> " + addr;
    channelRow += "<input input type='hidden' name='geoChannels[" + channelsCnt + "].address' value='" + addr + "'/>";
    channelRow += "<input input type='hidden' name='geoChannels[" + channelsCnt + "].name' value='ADDRESS'/>";
    channelRow += "<input input type='hidden' name='geoChannels[" + channelsCnt + "].coordinates.longitude' value='" + lon + "'/>";
    channelRow += "<input input type='hidden' name='geoChannels[" + channelsCnt + "].coordinates.latitude' value='" + lat + "'/>";
    channelRow += "<input input type='hidden' name='geoChannels[" + channelsCnt + "].radius.distance' value='" + radius + "'/>";
    channelRow += "<input input type='hidden' name='geoChannels[" + channelsCnt + "].radius.radiusUnit' value='" + unit + "'/>";
    channelRow += "<input input type='hidden' name='geoChannels[" + channelsCnt + "].geoType' value='ADDRESS'/>";
    channelRow += "</td>";

    channelRow += "<td><fmt:message key='channel.address'/></td>";
    channelRow += "<td>" + $("#latlon__" + rowIndex).html() + "</td>";
    channelRow += "<td><a href='#' onclick='deleteChannel(this);' class='button'><fmt:message key='form.delete'/></a></td></tr>";
    channelsCnt++;
    $("#addressRow__" + rowIndex).remove();
    $("#channels").append(channelRow);
}


function submitForm() {
    $('#saveGeoTragetForm').submit();

}

function toggleRadioSections() {
    if ($("#countryCode").val() != 'RU') {
        $("#radioSection").hide();
    }

    if ($('input:radio[name=cityOrAddressFlag]:checked').val() == 'true') {
        $('#geoCodeSection').hide();
        $('#radius, #radiusUnit').prop('disabled', true);
        $('#citySection').show();
    } else {
        $('#citySection').hide();
        $('#geoCodeSection').show();
        $('#radius, #radiusUnit').prop('disabled', false);
    }
}

function openMap(lat, lon) {
    if ($("#countryCode").val() == 'RU') {
        window.open("/campaign/yandexMap.jsp?lat=" + lat + "&lon=" + lon, "Map", "height=620, width=820, scrollbars=1");
    } else {
        window.open("http://maps.google.com/maps?q=" + lat + "," + lon + '&z=12', "Map", "height=620, width=820, scrollbars=1");
    }
    return false
}


</script>
<c:set var="modulePath" scope="page" value="${_context}/channel"/>
<ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
</ui:errorsBlock>
<s:form action="%{#attr.moduleName}/geoTarget/update" id="saveGeoTragetForm">
    <s:hidden name="id"/>
    <s:hidden name="name"/>
    <s:hidden name="version"/>
    <s:hidden name="existingAccount.id" value="%{group.account.id}"/>
<ui:section>
    <ui:fieldGroup>
        <s:if test="international && allowCountryChange">
            <ui:field id="countryElem" labelKey="channel.country" labelForId="countryCode" errors="country.countryCode">
                 <s:select name="country.countryCode" id="countryCode" cssClass="middleLengthText"
                     list="countries" value="country.countryCode"
                     listKey="id" listValue="getText('global.country.' + id + '.name')"
                     onchange="getStateList();"/>
            </ui:field>
        </s:if>
        <s:else>
            <ui:field labelKey="channel.country">
                <span class="simpleText"><c:out
                                  value="${ad:resolveGlobal('country', country.countryCode, false)}"/></span>
                <s:hidden id="countryCode" name="country.countryCode" value="%{country.countryCode}"/>
            </ui:field>
        </s:else>
    </ui:fieldGroup>
</ui:section>

<s:set var="statesIsEmpty" value="states.isEmpty"/>
<s:set var="citiesIsEmpty" value="cities.isEmpty"/>
<ui:section titleKey="channel.locations" id="locations" cssStyle="${(statesIsEmpty && citiesIsEmpty) ? 'display:none;' : ''}">
    <ui:fieldGroup>
        <ui:field>

            <span id="emptyLocations" class="simpleText" style="display:${fn:length(geoChannels) == 0 ? 'block':'none;'}"><fmt:message key="ccg.geoTarget.noLocations"/></span>
            <span id="existingLocations" class="simpleText" style="display:${fn:length(geoChannels) > 0 ? 'block':'none;'}"><fmt:message key="ccg.geoTarget.currentLocations"/>:
                <table id="channels"  class="dataView" width="100%">
                    <thead>
                        <tr>
   
                            <td><fmt:message key='channel.address.locations'/></td>
                            <td><fmt:message key='channel.address.category'/></td>
                            <td><fmt:message key='channel.address.latlon'/></td>
                            <td><fmt:message key='channel.address.actions'/></td>
                        </tr>
                    </th>
                    </thead>
                    <tbody>
                    <c:forEach items="${geoChannels}" var="channel" varStatus="status">
                      <tr id="geoChannelsRow__${channel.id}" class="geoChannelsRow">
                          <td>
                              <input type="hidden" name="geoChannels[${status.index}].id" value="${channel.id}"/>
                              <input type="hidden" name="geoChannels[${status.index}].geoType" value="${channel.geoType}"/>
                            <c:if test="${channel.geoType != 'ADDRESS'}">
                                <c:if test="${ad:isInternal()}"><a href="/admin/GeoChannel/view.action?id=${channel.id}"></c:if>
                                <c:out value="${channel.name}"/>
                                <c:if test="${ad:isInternal()}"></a></c:if>
                                <c:if test="${not empty channel.stateChannel}">
                                    , <c:if test="${ad:isInternal()}"><a href="/admin/GeoChannel/view.action?id=${channel.stateChannel.id}"></c:if>
                                    <c:out value="${channel.stateChannel.name}"/>
                                    <c:if test="${ad:isInternal()}"></a></c:if>
                                </c:if>
                            </c:if>
                            <c:if test="${channel.geoType == 'ADDRESS'}">
                                <fmt:message key="channel.addressText">
                                    <fmt:param value="${channel.radius.distance}"/>
                                    <fmt:param value="${channel.radius.radiusUnit}"/>
                                    <fmt:param value="${channel.address}"/>
                                </fmt:message>
                            </c:if>

                            <s:fielderror><s:param value="'geoChannels[' + #attr.status.index + ']'"/></s:fielderror>
                          </td>
                          <td>
                            <c:choose>
                                <c:when test="${channel.geoType == 'CITY'}">
                                    <fmt:message key="channel.city"/>
                                </c:when>
                                <c:when test="${channel.geoType == 'STATE'}">
                                    <fmt:message key="channel.state"/>
                                </c:when>
                                <c:when test="${channel.geoType == 'ADDRESS'}">
                                    <fmt:message key="channel.address"/>
                                </c:when>
                            </c:choose>
                          </td>
                          <td>
                              <c:if test="${not empty channel.coordinates.latitude and not empty channel.coordinates.longitude}">
                                  <fmt:formatNumber value="${channel.coordinates.latitude}" minFractionDigits="4" maxFractionDigits="4"/>&nbsp;
                                  <fmt:formatNumber value="${channel.coordinates.longitude}" minFractionDigits="4" maxFractionDigits="4"/>&nbsp;
                                  <a href="#" onclick="openMap(${channel.coordinates.latitude}, ${channel.coordinates.longitude})"><fmt:message key="channel.address.map"/></a>
                              </c:if>
                          </td>
                          <td>
                              <ui:button message="form.delete" href='#' onclick='deleteChannel(this);' />
                          </td>
                      </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </span>
        </ui:field>


        <ui:field labelKey="channel.addLocation" required="true" cssClass="valignFix">
            <span id="radioSection">
                <label class="withInput">
                    <s:radio name="cityOrAddressFlag" list="true" template="justradio" onchange="toggleRadioSections()"/><fmt:message key="channel.address.stateOrCity"/>
                </label>
                <label class="withInput" style="float: left">
                    <s:radio name="cityOrAddressFlag" list="false" template="justradio" onchange="toggleRadioSections()"/><fmt:message key="channel.address.radiusTargeting"/>&nbsp;
                </label>
                    <s:textfield id="radius" maxlength="5" cssClass="smallLengthText" disabled="true"/><span style="font-size: 14px; color: red; margin-left: 2px">*</span>&nbsp;
                    <s:select id="radiusUnit"
                              list="#{'m':'m',
                                      'km':'km',
                                      'yd':'yd',
                                      'mi':'mi'
                                      }" listValue="getText('channel.address.units.'+key)" value="'km'" disabled="true"/>
                    &nbsp;
                    <span id="rangeValues"/>
                    <s:fielderror name="radius"/>
            </span>
        </ui:field>

        <ui:field>
            <span id="citySection"  class="simpleText" style="display: none"><fmt:message key="ccg.geoTarget.select"/>
                <s:select name="state" id="state" cssClass="middleLengthText"
                          headerKey="" headerValue="%{getText('form.all')}"
                          list="states" value="state.name"
                          listKey="id" listValue="name"
                          onchange="getCityList();"/>
                <s:select name="city" id="city" cssClass="middleLengthText"
                          headerKey="" headerValue="%{getText('form.all')}"
                          list="cities" value="city.name"
                          listKey="id" listValue="name"
                        />
                <ui:button message="form.add" onclick="addStateOrCity()" />
             </span>
        </ui:field>

        <ui:field id="geoCodeSection" errors="geoCode" cssClass="display">
            <s:textfield id="geoCodeInput" name="geoCode" cssStyle="width: 600px"/>&nbsp;
            <a id="searchGeoCode" href="#"><fmt:message key="channel.address.search"/></a>

            <span id="geoCodeResult" style="display: block"/>
        </ui:field>

    </ui:fieldGroup>
</ui:section>

</s:form>

<div class="wrapper">
    <ui:button message="form.save" onclick="submitForm();" type="submit"/>
    <c:if test="${ccgType =='DISPLAY'}">
        <ui:button message="form.cancel" onclick="location='../viewDisplay.action?id=${id}';" type="button"/>
    </c:if>
    <c:if test="${ccgType =='TEXT'}">
        <ui:button message="form.cancel" onclick="location='../viewText.action?id=${id}';" type="button"/>
    </c:if>
</div>

