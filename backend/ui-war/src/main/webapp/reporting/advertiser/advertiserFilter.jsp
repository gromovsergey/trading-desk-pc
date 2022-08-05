<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page import="com.foros.session.reporting.advertiser.olap.OlapDetailLevel" %>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<tiles:importAttribute ignore="true" scope="request" name="isDisplay"/>
<c:set var="advertiserContext" value="${requestContexts.advertiserContext}"/>

<script type="text/javascript">
    var noneText = '<fmt:message key="form.select.none"/> ';
    
    $().ready(function() {
    
        $(document).ajaxStop(function(){
            if ($('select#agencyId:visible').length) return;
            $('#treeFilterField').hasClass('hide') ? $('#filters').hide() : $('#filters').show();
        });
   
        prepareAccounts(true);
        toggleKeywords();

        $('[name=reportType]').change(function() {
            toggleExcludeDeletedInactiveEntities();
            toggleKeywords();
            prepareAccounts(false);
        });

        $('#agencyId').change(function() {
            toggleAdvertisers();
            reloadFilterAndColumns();
            changeReportType();
        });

        $('#advertiserId').change(function() {
            reloadFilterAndColumns()
        });
    });

    function reloadFilterAndColumns(){
        updateAccountId();
        var isColumnsUpdated = reloadTreeFilter();
        <c:if test="${isDisplay}">
        if (!isColumnsUpdated){
            updateColumns(true);
        }
        </c:if>
    }

    function toggleExcludeDeletedInactiveEntities() {
        ( $('[name=reportType]:checked').val() == '<%=OlapDetailLevel.Account%>') ? $('#treeFilterField').addClass('hide') : $('#treeFilterField').removeClass('hide');
    }

    function updateAccountId() {
        $('#accountId').val(getAccountId());
    }

    function prepareAccounts(initial) {
        var isByAdvertiser = $('[name=reportType]:checked').val() == '<%=OlapDetailLevel.Advertiser%>';
        var agencyIdVal = $('#agencyId').val();
        var advertiserIdVal = $('#advertiserId').val();
        var accountId = getAccountId();

        var agencyMessages;
        if (isByAdvertiser) {
            agencyMessages = ['form.select.pleaseSelect'];
        } else {
            agencyMessages = ['form.select.pleaseSelect', 'form.select.none'];
        }

        var agencyDeferred = loadAccounts('agencyId', 'AGENCY', agencyMessages, function() {
            $('#agencyId option:first-child').val("pleaseSelect");
            if (!initial) {
                $('#agencyId').val(agencyIdVal);
            }
            toggleAdvertisers();
        });

        if (initial) {
            var advertiserDeferred = loadAccounts('advertiserId', 'ADVERTISER', [], null);
        }

        $.when(agencyDeferred, advertiserDeferred).done(function () {
            reloadTreeFilter();
        });
    }

    function loadAccounts(selectId, accountRole, messages, callback) {
        var select = $('#' + selectId);
        if (select.length == 0 || select[0].tagName != "SELECT") {
            return null;
        }

        return UI.Data.Options.get(
                'advertiserReport/accounts',
                selectId,
                {accountRole : accountRole},
                messages,
                callback,
                {waitHolder: $('#filters')}
        );
    }

    function reloadTreeFilter() {
        showTreeFilter();
        var id = getAccountId();
        if (id) {
            UI.Daterange.setTimeZoneShift($('#fastChangeId').val(),  {fromDateName: 'dateRange.begin', toDateName: 'dateRange.end'}, id);
        }
        if ($("[name='reportType']:checked").val() == 'Account') {
            updateColumns(true);
            return true;
        }

        return false;
    }

    function getAccountId() {
        var agencyId = $('#agencyId').val();
        if (agencyId == "pleaseSelect") {
            return null;
        }

        return agencyId || $('#advertiserId').val();
    }

    function getAgencyId() {
        var agencyId = $('#agencyId').val();
        if (agencyId == "pleaseSelect") {
            return null;
        }
        return agencyId;
    }

    function toggleKeywords() {
        if ($('[name=reportType]:checked').val() == '<%=OlapDetailLevel.Keyword%>') {
            $('#keyword').removeAttr('disabled');
            $('#keywordId').show();
        } else {
            $('#keywordId').hide();
            $('#keyword').attr('disabled', 'disabled');
        }
    }

    function toggleAdvertisers() {
    <c:if test="${not advertiserContext.set}">
        var noneOption = $('#agencyId > option[value=""]');
        var isByAdvertiser = $('[name=reportType]:checked').val() == '<%=OlapDetailLevel.Advertiser%>';

        if ($('#agencyId').val() || isByAdvertiser) {
            if ($('#advertiserIdField').is(':visible')) {
                $('#accountId').val("");
            }
            $('#advertiserIdField').hide();
        } else {
            $('#advertiserIdField').show();
        }
    </c:if>
    }

</script>

<ui:section id="filters" titleKey="form.filter">
    <ui:fieldGroup>
        <s:hidden id="accountId" name="accountId"/>
        <c:choose>
            <c:when test="${not advertiserContext.set}">
                <ui:field labelKey="report.input.field.agency" labelForId="agencyId">
                    <select id="agencyId" class="middleLengthText"></select>
                </ui:field>
                <ui:field id="advertiserIdField" labelKey="report.input.field.advertiser" labelForId="advertiserSelect" cssClass="hide">
                    <select id="advertiserId" class="middleLengthText"></select>
                </ui:field>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${advertiserContext.agencyContext}">
                        <s:hidden id="agencyId" value="%{accountId}"/>
                    </c:when>
                    <c:otherwise>
                        <s:hidden id="advertiserId" value="%{accountId}"/>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>

        <ui:field id="treeFilterField">
            <%@ include file="treeFilter.jsp" %>
        </ui:field>

        <c:if test="${!isDisplay}">
            <ui:field cssClass="hide" labelKey="report.input.field.keyword" id="keywordId">
                <input class="middleLengthText" type="text" name="keyword" id="keyword" maxlength="512"/>
            </ui:field>
        </c:if>
    </ui:fieldGroup>
</ui:section>
