<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>

<ui:pageHeadingByTitle/>
<form id="searchForm">
    <div id="tqa_options">
        <s:hidden name="searchParams.type"/>
        <s:hidden name="searchParams.filterBy"/>
        <s:hidden name="searchParams.countryCode"/>
        <s:hidden name="searchParams.channelAccountId"/>
        <s:hidden name="searchParams.channelId"/>
        <s:hidden name="searchParams.channelName"/>
        <s:hidden name="searchParams.ccgAccountId"/>
        <s:hidden name="searchParams.advertiserId"/>
        <s:hidden name="searchParams.campaignId"/>
        <s:hidden name="searchParams.ccgId"/>
        <s:hidden name="searchParams.roleName"/>
        <s:hidden name="searchParams.visibility"/>
        <s:hidden name="searchParams.displayStatusId"/>
        <s:hidden name="searchParams.discoverAccountId"/>
        <s:hidden name="searchParams.discoverChannelId"/>
        <s:hidden name="searchParams.discoverChannelName"/>
        <s:hidden name="searchParams.discoverChannelListId"/>
        <s:hidden name="searchParams.criteria"/>
        <s:hidden name="searchParams.triggerType"/>    
    </div>


    <ui:section titleKey="form.search">
        <ui:fieldGroup>

            <ui:field 
                labelKey="TriggersApproval.type" cssClass="valignFix">
                <s:radio 
                    id="channelTypeOption"
                    cssClass="withInput"
                    name="searchParams.type"
                    list="types"
                    listKey="key"
                    listValue="value"
                    value="searchParams.type.letter"/>
            </ui:field>

            <ui:field
                labelKey="searchParams.country"
                labelForId="country"
                errors="searchParams.countryCode"
                required="true">
                <s:select
                    name="searchParams.countryCode"
                    id="countryCode"
                    list="countries"
                    cssClass="middleLengthText"
                    listKey="id"
                    listValue="getText('global.country.' + id + '.name')"
                    value="searchParams.countryCode"/>
            </ui:field>

            <ui:field 
                labelKey="TriggersApproval.triggerType" cssClass="valignFix">
                <s:radio 
                    cssClass="withInput" 
                    name="searchParams.triggerType" 
                    list="triggerTypes" 
                    listKey="key"
                    listValue="value" 
                    value="searchParams.triggerType.letter"/>
            </ui:field>

            <ui:field 
                id="searchTextRowId" 
                labelKey="TriggersApproval.text" 
                labelForId="searchText"
                errors="searchParams.criteria">
                <s:textfield 
                    cssClass="middleLengthText" 
                    name="searchParams.criteria" 
                    id="searchText" 
                    maxlength="2048"/>
            </ui:field>

            <ui:field id="triggerTypeAllHint">
                <span class="infos"><s:text name="TriggersApproval.triggerTypeAll.hint"/></span>
            </ui:field>

            <ui:field 
                labelKey="TriggersApproval.triggerStatus" 
                labelForId="searchTriggerStatus"
                errors="searchParams.approval">
                <s:select 
                    id="searchTriggerStatus" 
                    name="searchParams.approval" 
                    cssClass="middleLengthText"
                    list="approvalTypes" 
                    listKey="key" 
                    listValue="value"/>
            </ui:field>

            <%-- FILTER --%>
            <ui:field 
                labelKey="form.filter" 
                cssClass="subsectionRow"
                id="_filter" />

            <%-- FIELDS TYPE A --%>
            <ui:field 
                labelKey="form.filterBy" 
                cssClass="tqa_filter valignFix"
                id="_filterBy">
                <s:radio 
                    cssClass="withInput" 
                    name="searchParams.filterBy" 
                    list="filterBy" 
                    listKey="key"
                    listValue="value" 
                    value="searchParams.filterBy"/>
            </ui:field>

            <%-- CCG FILTER --%>
            <ui:field
                labelKey="searchParams.agency" 
                labelForId="accountForCcg"
                errors="searchParams.ccgAccountId"
                cssClass="hide tqa_filter tqa_filter_ccg"
                id="_accountForCcgFilter">
                <select id="accountForCcg" name="searchParams.ccgAccountId" class="middleLengthText">
                    <option value=""><s:text name="form.all"/></option>
                </select>
            </ui:field>
            
            <ui:field 
                labelKey="searchParams.advertiser" 
                labelForId="advertiser"
                errors="searchParams.advertiserId" 
                cssClass="hide tqa_filter tqa_filter_ccg"
                id="_advertiser">
                <select id="advertiser" name="searchParams.advertiserId" class="middleLengthText">
                    <option value=""><s:text name="form.all"/></option>
                </select>
            </ui:field>
            
            <ui:field
                labelKey="searchParams.campaign" 
                labelForId="campaign"
                errors="searchParams.campaignId"
                cssClass="hide tqa_filter tqa_filter_ccg"
                id="_campaign">
                <select id="campaign" name="searchParams.campaignId" class="middleLengthText">
                    <option value=""><s:text name="form.all"/></option>
                </select>
            </ui:field>
            
            <ui:field
                labelKey="searchParams.ccg" 
                labelForId="ccg"
                cssClass="hide tqa_filter tqa_filter_ccg"
                errors="searchParams.ccgId" 
                id="_ccg">
                <select id="ccg" name="searchParams.ccgId" class="middleLengthText">
                    <option value=""><s:text name="form.all"/></option>
                </select>
            </ui:field>

            <%-- CHANNEL FILTER --%>
            <ui:field
                labelKey="searchParams.role" 
                errors="searchParams.roleName" 
                cssClass="hide tqa_filter tqa_filter_channel"
                id="_role">
                <s:radio
                    cssClass="withInput" 
                    name="searchParams.roleName" 
                    id="accountRole"
                    list="accountRoles"
                    listKey="key"
                    listValue="value"/>
            </ui:field>
            
            <ui:field
                labelKey="searchParams.account"
                labelForId="account"
                errors="searchParams.channelAccountId"
                cssClass="hide tqa_filter tqa_filter_channel"
                id="_account">
                <select id="account" name="searchParams.channelAccountId" class="middleLengthText">
                    <option value=""><s:text name="form.all"/></option>
                </select>
            </ui:field>
            
            <ui:field
                labelKey="searchParams.visibility"
                labelForId="visibilitySelect"
                errors="searchParams.visibility"
                cssClass="hide tqa_filter tqa_filter_channel"
                id="_visibility">
                <s:select
                    name="searchParams.visibility"
                    id="visibilitySelect"
                    list="visibilityTypes"
                    listKey="key.name"
                    listValue="value"
                    cssClass="middleLengthText"/>
            </ui:field>
            
            <ui:field
                labelKey="searchParams.status"
                labelForId="status"
                errors="searchParams.displayStatusId"
                cssClass="hide tqa_filter tqa_filter_channel"
                id="_status">
                <s:select
                    name="searchParams.displayStatusId"
                    id="status"
                    list="channelStatuses"
                    listKey="key"
                    listValue="value"
                    cssClass="middleLengthText"/>
            </ui:field>
            
            <ui:field
                    labelKey="searchParams.channel"
                    labelForId="searchParams.channelId"
                    errors="searchParams.channelId"
                    cssClass="hide tqa_filter tqa_filter_channel"
                    id="_channel">
                <ui:autocomplete
                        id="searchParams.channelId"
                        source="Autocomplete.getChannels"
                        cssClass="middleLengthText" />
            </ui:field>

            <%-- FIELDS TYPE D --%>
            <ui:field 
                labelKey="searchParams.account" 
                labelForId="discoverAccount" 
                errors="searchParams.discoverAccountId"
                cssClass="hide tqa_filter tqa_filter_d"
                id="_discoverAccount">
                <s:select 
                    list="internalAccounts" 
                    name="searchParams.discoverAccountId" 
                    id="discoverAccount"
                    headerKey="" headerValue="%{getText('form.all')}"
                    listKey="id" 
                    listValue="name" 
                    cssClass="middleLengthText"
                    value="searchParams.discoverAccountId" />
            </ui:field>
            
            <ui:field 
                labelKey="searchParams.status" 
                labelForId="discoverStatus"
                errors="searchParams.discoverDisplayStatusId"
                cssClass="hide tqa_filter tqa_filter_d"
                id="_discoverStatus">
                <s:select 
                    name="searchParams.discoverDisplayStatusId" 
                    id="discoverStatus" 
                    list="discoverChannelStatuses"
                    listKey="key"
                    listValue="value" 
                    cssClass="middleLengthText"/>
            </ui:field>
            
            <ui:field 
                labelKey="searchParams.channelList" 
                labelForId="discoverChannelList" 
                errors="searchParams.discoverChannelListId"
                cssClass="hide tqa_filter tqa_filter_d"
                id="_discoverChannelList">
                <select name="searchParams.discoverChannelListId" id="discoverChannelList" class="middleLengthText">
                    <option value=""><s:text name="form.all"/></option>
                </select>
            </ui:field>
            
            <ui:field 
                labelKey="searchParams.channel" 
                labelForId="searchParams.discoverChannelId" 
                errors="searchParams.discoverChannelId"
                cssClass="hide tqa_filter tqa_filter_d"
                id="_discoverChannel">
                <ui:autocomplete
                    id="searchParams.discoverChannelId"
                    source="Autocomplete.searchParamsPointdiscoverChannelId.getOpts"
                    cssClass="middleLengthText" />
            </ui:field>

            <%-- ORDER --%>
            <ui:field 
                labelKey="form.order" 
                cssClass="subsectionRow" 
                id="_order" />

            <ui:field 
                labelKey="form.orderBy" 
                labelForId="orderBy" 
                id="_orderBy">
                <s:select 
                    name="searchParams.orderBy" 
                    id="orderBy" 
                    list="orderBy" 
                    listKey="key" 
                    listValue="value" 
                    cssClass="middleLengthText"/>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button message="form.search"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</form>

<div id="result" class="logicalBlock"></div>

<script type="text/javascript">
    (function(){
    
        // callbacks
        var fDChannelsListCallback  = function(){},
        fDChannelIdCallback         = function(){},
        fCCGAdvertistersCallback    = function(){},
        fCCGCamaignsCallback        = function(){},
        fCCGIdCallback              = function(){};
        
    
        /* filter helpers */
        function setTriggerType(type, isHandler) {
            switch (type) {
                case 'URL':
                    if (!isHandler) $('#searchParams_triggerTypeURL').prop('checked', true);
                    $('#triggerTypeAllHint').hide();
                break;
                case 'KEYWORD':
                    if (!isHandler) $('#searchParams_triggerTypeKEYWORD').prop('checked', true);
                    $('#triggerTypeAllHint').hide();
                break;
                default:
                    if (!isHandler) $('#searchParams_triggerType').prop('checked', true);
                    $('#triggerTypeAllHint').show();
            }
        }
        
        function setSearchType(type, options, callback, isHandler) {
            $('.tqa_filter', '#form_search').hide();
            switch (type) {
                case 'A':
                    if (!isHandler) $('#channelTypeOptionA').prop('checked', true);
                    $('#_filterBy').show();
                    
                    setFilterBy(options["searchParams.filterBy"], options, callback, false);
                break;
                case 'D':
                    if (!isHandler) $('#channelTypeOptionD').prop('checked', true);
                    $('.tqa_filter_d', '#form_search').show();
                    
                    $('#discoverStatus').val(options["searchParams.discoverDisplayStatusId"] || '');
                    $('#discoverAccount').val(options["searchParams.discoverAccountId"] || '');
                    
                    loadDChannelsList(
                        $('#discoverAccount').val(),
                        $('#countryCode').val(),
                        options,
                        callback
                    );
                break;
                default:
                    callback();
            }
        }
        /* filter helpers ends */
        
        /* A helpers */
        function setFilterBy(filter, options, callback, isHandler) {

            $('.tqa_filter_ccg, .tqa_filter_channel', '#form_search').hide();
            
            switch (filter) {
                case "CHANNEL":
                    if (!isHandler) $('#searchParams_filterByCHANNEL').prop('checked', true);
                    $('.tqa_filter_channel', '#form_search').show();
                    setChannelRole(options["searchParams.roleName"], options, callback, false);
                break;
                case "CCG":
                    if (!isHandler) $('#searchParams_filterByCCG').prop('checked', true);
                    $('.tqa_filter_ccg', '#form_search').show();
                    setCCGAccountId(options, callback);
                break;
                case "ALL":
                default:
                    if (!isHandler) $('#searchParams_filterByALL').prop('checked', true);
                    $('.tqa_filter_d, .tqa_filter_channel').find(':input').val('');
                    callback();
            }
        }
        
        function setChannelRole(role, options, callback, isHandler) {
            switch (role) {
                case "INTERNAL":
                    if (!isHandler) $('#accountRoleINTERNAL').prop('checked', true);
                    UI.Data.Options.replaceWith('visibilitySelect', [['form.all', 'PRI,PUB,CMP'], ['channel.visibility.PUB', 'PUB'], ['channel.visibility.PRI', 'PRI']]);
                break;
                case "EXTERNAL":
                    if (!isHandler) $('#accountRoleEXTERNAL').prop('checked', true);
                    UI.Data.Options.replaceWith('visibilitySelect', [['form.all', 'PRI,PUB,CMP'], ['channel.visibility.PUB', 'PUB'], ['channel.visibility.PRI', 'PRI'], ['channel.visibility.CMP', 'CMP']]);
                break;
                case "ALL":
                default:
                    if (!isHandler) $('#accountRole').prop('checked', true);
                    UI.Data.Options.replaceWith('visibilitySelect', [['form.all', 'PRI,PUB,CMP'], ['channel.visibility.PUB', 'PUB'], ['channel.visibility.PRI', 'PRI'], ['channel.visibility.CMP', 'CMP']]);
            }
            setChannelAccount(options, callback);
        }
        
        function setChannelAccount(options, callback) {
            UI.Data.Options.get('accounts', 'account', {role: getRoles()}, ['form.all'], function(){
                $('#account').val(options["searchParams.channelAccountId"] || '');
                $('#visibilitySelect').val(options['searchParams.visibility'] || '');
                setChannelStatus(options, callback);
            });
        }
        
        function setChannelStatus(options, callback) {
            UI.Data.Options.get('displayStatusesForTriggerQA', 'status', {role: getRoles()}, ['form.all'], function(){
                $('#status').val(options["searchParams.displayStatusId"] || '');
                setChannelId(options);
                callback();
            });
        }

        Autocomplete.getChannels = function(request, response) {
            if (!$('#account').val()) {
                response([]);
                return;
            }

            UI.Data.get(
                    'channels', {
                        "name": request.term,
                        "accountPair": UI.Util.Pair.createPairById($('#account').val()),
                        "displayStatusId": $('#status').val() || '',
                        "visibilityCriteria": $('#visibilitySelect').val() || '',
                        "countryCode": $('#countryCode').val()
                    },
                    function(data) {
                        var options = $('options', data),
                                opts = $.map($('option', options), function(el){
                                    var jElem = $(el);
                                    return {"label":jElem.text(), "value":jElem.attr('id')};
                                });
                        response(opts);
                    }
            );
        };

        function setChannelId(options){
            if (options["searchParams.channelId"] && options["searchParams.channelName"]) {
                $('#searchParams\\.channelId').val(options["searchParams.channelName"]);
                $('#searchParams\\.channelId').siblings('input[name="searchParams.channelId"]').val(options["searchParams.channelId"]);
            } else {
                $('#searchParams\\.channelId').val('');
                $('#searchParams\\.channelId').siblings('input[name="searchParams.channelId"]').val('');
            }
        }

        function setCCGAccountId(options, callback) {
            UI.Data.Options.get('accounts', 'accountForCcg', {role: getRoles()}, [['form.all'],['form.select.none',-1]], function(){
                $('#accountForCcg').val(options["searchParams.ccgAccountId"] || '');
                setCCGAdvertiserId($('#accountForCcg').val(), options, callback);
            });
        }
        
        function setCCGAdvertiserId(account, options, callback) {
            if (account != '') {
                UI.Data.Options.get('advertisers', 'advertiser', {'accountPair': (account !== '-1') ? UI.Util.Pair.createPairById(account) : '', concatResultForValue: false}, ['form.all'], function(){
                    $('#advertiser').val(options["searchParams.advertiserId"] || '');
                    setCCGCampaignId($('#advertiser').val(), options, callback);
                });
            } else {
                UI.Data.Options.replaceWith('advertiser', ['form.all']);
                setCCGCampaignId($('#advertiser').val(), options, callback);
            }
        }
        
        function setCCGCampaignId(advertiser, options, callback) {
            if (advertiser != '') {
                UI.Data.Options.get('campaigns', 'campaign', {accountPair: UI.Util.Pair.createPairById(advertiser), concatResultForValue: false}, ['form.all'], function(){
                    $('#campaign').val(options["searchParams.campaignId"] || '');
                    setCCGId($('#campaign').val(), options, callback);
                });
            } else {
                UI.Data.Options.replaceWith('campaign', ['form.all']);
                setCCGId($('#campaign').val(), options, callback);
            }
        }

        function setCCGId(campaign, options, callback) {
            if (campaign != '') {
                UI.Data.Options.get('groups', 'ccg', {campaignPair: UI.Util.Pair.createPairById(campaign), concatResultForValue: false}, ['form.all'], function(){
                    $('#ccg').val(options["searchParams.ccgId"] || '');
                    if (callback != undefined && typeof callback === 'function') callback();
                });
            } else {
                UI.Data.Options.replaceWith('ccg', ['form.all']);
                if (callback != undefined && typeof callback === 'function') callback();
            }
        }
        
        
        
        function getRoles() {
            if ( $('input[name="searchParams\\.filterBy"]:checked', '#form_search').val() === "CCG" ) {
                return ['AGENCY'];
            } else {
                switch ( $('input[name="searchParams\\.roleName"]:checked', '#form_search').val() ) {
                    case 'INTERNAL':
                        return ['INTERNAL'];
                    case 'EXTERNAL':
                        return ['ADVERTISER', 'AGENCY', 'CMP'];
                    default:
                        return ['INTERNAL', 'ADVERTISER', 'AGENCY', 'CMP'];
                }
            }
        }
        /* A helpers ends */
        
        /* D helpers */
        function loadDChannelsList(account, country, options, callback){
            if (!account) {
                UI.Data.Options.replaceWith('discoverChannelList', ['form.all']);
                setDChannelId(options);
                callback();
                return;
            }

            UI.Data.Options.get('discoverChannelListsByAccount', 'discoverChannelList', {
                accountPair: UI.Util.Pair.createPairById(account),
                countryCode: country
            }, ['form.all'], function(){
                $('#discoverChannelList').val(options["searchParams.discoverChannelListId"] || '');
                setDChannelId(options);
                callback();
            });
        }
        
        Autocomplete.searchParamsPointdiscoverChannelId.getOpts = function(request, response){
            if (!$('#discoverAccount').val()) {
                response([]);
                fDChannelIdCallback();
                return;
            }

            UI.Data.get(
                'discoverChannels', {
                    "name": request.term,
                    "accountPair": UI.Util.Pair.createPairById($('#discoverAccount').val()),
                    "discoverChannelListId": $('#discoverChannelList').val() || '',
                    "displayStatusId": $('#discoverStatus').val() || '',
                    "countryCode": $('#countryCode').val()
                },
                function(data) {
                    var options = $('options', data),
                    opts = $.map($('option', options), function(el){
                        var jElem = $(el);
                        return {"label":jElem.text(), "value":jElem.attr('id')};
                    });
                    response(opts);
                    fDChannelIdCallback();
                }
            );
        };
        
        function setDChannelId(options){
            if (options["searchParams.discoverChannelId"] && options["searchParams.discoverChannelName"]) {
                $('#searchParams\\.discoverChannelId').val(options["searchParams.discoverChannelName"]);
                $('#searchParams\\.discoverChannelId').siblings('input[name="searchParams.discoverChannelId"]').val(options["searchParams.discoverChannelId"]);
            } else {
                $('#searchParams\\.discoverChannelId').val('');
                $('#searchParams\\.discoverChannelId').siblings('input[name="searchParams.discoverChannelId"]').val('');
            }
        }
        /* D helpers ends */
        
        function getOptions(){
        
            function isDefault(aParams){
                return (
                    oParams.length  === 3 &&
                    aParams["searchParams.type"] != undefined && aParams["searchParams.type"] == 'A' &&
                    aParams["searchParams.filterBy"] != undefined && aParams["searchParams.filterBy"] == 'ALL' &&
                    aParams["searchParams.countryCode"] != undefined);
            }
        
            var aTmp    = $('#tqa_options').children('input').serializeArray(),
            oParams     = {"length":0};
            $('#tqa_options').remove();
            for (var i in aTmp) {
                if (aTmp[i].value   != '') {
                    oParams[ aTmp[i]['name'] ] = aTmp[i]['value'];
                    oParams.length++;
                }
            }
            return !isDefault(oParams) ? oParams : {"length":0};
        }
        
        /* main form state loader */
        function setFormState(options, callback) {

            $('#countryCode').val(options["searchParams.countryCode"] || '');
            $('#searchText').val(options["searchParams.criteria"] || '');
            setTriggerType(options["searchParams.triggerType"] || '');

            setSearchType(options["searchParams.type"], options, callback);
        }
        
        function validateCriteriaAndSubmitSearch(searchCb) {
            if ($('#searchParams\\.discoverChannelId').val() == '') {
                $('#searchParams\\.discoverChannelId').siblings('[name="searchParams\\.discoverChannelId"]').val('');
            }
            UI.Data.post('TriggerQACriterCheck', {
                    searchCriteria: $('#searchText').val(),
                    triggerType: $('input[name="searchParams\\.triggerType"]:checked').val()
                }, function(data) {
                    var validStatus = $('valid', data).text();
                    toggleCriteriaError(validStatus, $('message', data).text());
                    if(validStatus == 'true'){
                        searchCb();
                    } 
            }).error(function(xhr){
                switch (xhr.status) {
                    case 401:
                        window.location.reload();
                    break;
                    case 403:
                        UI.Util.redirectToErrorPage(403);
                    break;
                    case 404:
                        UI.Util.redirectToErrorPage(404);
                    break;
                }
            });
        };

        function toggleCriteriaError(valid, message) {
            var errorTD         = $('#searchTextRowId').find('td[class="withError"]'),
            errorTDExists       = $('#searchTextRowId').find('td[class="withError"]').is('td[class="withError"]');
            if (!errorTDExists) return;
            
            var targetSpanExists = errorTD.find('span[class="errors"]').is('span[class="errors"]');

            if (valid == 'true') {
                targetSpanExists && errorTD.find('span[class="errors"]').remove();
            } else if (valid = 'false') {
                targetSpanExists || errorTD.append('<span class="errors"></span>');
                errorTD.find('span[class="errors"]').text(message);
            }
        };
        
        /* document ready */
        $(function(){
            /* adding handlers */
            $('input[name="searchParams\\.type"]', '#form_search').on('change', function(){
                setSearchType($(this).val(), {}, function(){}, true);
            });

            $('input[name="searchParams\\.triggerType"]', '#form_search').on('change', function(){
                setTriggerType($(this).val(), true);
            });

            $('input[name="searchParams\\.filterBy"]', '#form_search').on('change', function(){
                setFilterBy($(this).val(), {}, function(){}, true);
            });
            
            $('input[name="searchParams\\.roleName"]', '#form_search').on('change', function(){
                setChannelRole($(this).val(), {}, function(){}, true);
            });
            
            $('#account').on('change', function(){
                setChannelStatus({}, function(){});
            });
            
            $('#status, #visibilitySelect').on('change', function(){
                setChannelId($('#account').val(), {}, function(){});
            });
            
            
            /* type Advertising */
            $('#accountForCcg').on('change', function(){
                setCCGAdvertiserId($(this).val(), {}, fCCGAdvertistersCallback);
            });
            
            $('#advertiser').on('change', function(){
                setCCGCampaignId($(this).val(), {}, fCCGCamaignsCallback);
            });
            
            $('#campaign').on('change', function(){
                setCCGId($(this).val(), {}, fCCGIdCallback);
            });
            
            
            /* type Discover */
            $('#discoverChannelList').on('change', function(){
                setDChannelId({});
            });
            
            $('#discoverAccount, #countryCode').on('change', function(){
                loadDChannelsList(
                    $('#discoverAccount').val(),
                    $('#countryCode').val(),
                    {},
                    fDChannelsListCallback
                );
            });
            
            /* init starts here */
            var options = getOptions();

            $('#searchForm').pagingAssist({
                action:         '/admin/Triggers/search.action',
                message:        '${ad:formatMessage("triggersApproval.loading")}',
                onBeforeSubmit: function(callback){
                    var jqChannelName  = $('#searchParams\\.channelId').val();
                    if (jqChannelName) {
                        $('#searchForm').append($('<input />').attr({'type':'hidden', 'name':'searchParams.channelName'}).val(jqChannelName));
                    }

                    var jqDChannelName  = $('#searchParams\\.discoverChannelId').val();
                    if (jqDChannelName) {
                        $('#searchForm').append($('<input />').attr({'type':'hidden', 'name':'searchParams.discoverChannelName'}).val(jqDChannelName));
                    }

                    validateCriteriaAndSubmitSearch(callback);
                },
                onBeforeRestore: function(data, callback){
                    var opts    = {};
                    for (var i in data) {
                        opts[data[i].name]  = data[i].value;
                    }
                    setFormState(opts, callback);
                },
                result: $('#result')
            });
            
            if (options.length) {
                setFormState(options, function(){
                    $('#searchForm').submit();
                });
            }            
        });
    })();
</script>