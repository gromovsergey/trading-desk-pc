<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="ad" uri="/ad/serverUI"  %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el"  %>

<script type="text/javascript">

    new UI.AjaxLoader().show();

    function roleSelected() {

        <s:if test="accountRole.name == 'Publisher' && allowPublisherInventoryEstimationDisabled">
            $('#publisherInventoryEstimationFlagtrue, #publisherInventoryEstimationFlagfalse').prop({disabled : true});
        </s:if>

        <s:if test="(accountRole.name == 'Advertiser' || accountRole.name == 'Agency') && siteTargetingDisabled">
            $('#siteTargetingFlagtrue, #siteTargetingFlagfalse').prop({disabled : true});
        </s:if>

        <s:if test="accountRole.name == 'Publisher' && advExclusionSiteFlagDisabled">
            $('#advExclusionsDISABLED').prop({disabled : true});
        </s:if>

        <s:if test="accountRole.name == 'Publisher' && advExclusionTagFlagDisabled">
             $('#advExclusionsSITE_LEVEL, #advExclusionsSITE_AND_TAG_LEVELS, #advExclusionsDISABLED').prop({disabled : true});
        </s:if>

        <s:if test="accountRole.name == 'Publisher' && frequencyCapsFlagDisabled">
            $('#freqCapsFlagtrue, #freqCapsFlagfalse').prop({disabled : true});
        </s:if>

        <s:if test="accountRole.name == 'Publisher' && wdTagsFlagDisabled">
            $('#wdTagsFlagtrue, #wdTagsFlagfalse').prop({disabled : true});
        </s:if>

        <s:if test="accountRole.name == 'Agency' && financialFieldsDisabled">
            $('#financialFieldsFlagtrue, #financialFieldsFlagfalse').prop({disabled : true});
        </s:if>

        <s:if test="(accountRole.name == 'Advertiser' || accountRole.name == 'Agency') && invoicingDisabled">
            $('#invoicingFlagtrue, #invoicingFlagfalse').prop({disabled : true});
        </s:if>

        <s:if test="accountRole.name == 'Agency' && inputRatesAndAmountsDisabled">
            $('#inputRatesAndAmountsFlagtrue, #inputRatesAndAmountsFlagfalse').prop({disabled : true});
        </s:if>
        
        <s:if test="accountRole.name == 'Agency' && invoiceCommissionDisabled">
            $('#invoiceCommissionFlagtrue, #invoiceCommissionFlagfalse').prop({disabled : true});
        </s:if>

        <s:if test="(accountRole.name == 'Advertiser' || accountRole.name == 'Agency') && billingModelDisabled">
            $('#billingModelFlagtrue, #billingModelFlagfalse').prop({disabled: true});
        </s:if>
    }

    function enableDisabledRadioButtons() {

        <s:if test="accountRole.name == 'Publisher' && allowPublisherInventoryEstimationDisabled">
            $('#publisherInventoryEstimationFlagtrue, #publisherInventoryEstimationFlagfalse').prop({disabled : false});
        </s:if>

        <s:if test="(accountRole.name == 'Advertiser' || accountRole.name == 'Agency') && siteTargetingDisabled">
            $('#siteTargetingFlagtrue, #siteTargetingFlagfalse').prop({disabled : false});
        </s:if>

        <s:if test="accountRole.name == 'Publisher' && advExclusionSiteFlagDisabled">
            $('#advExclusionsDISABLED').prop({disabled : false});
        </s:if>

        <s:if test="accountRole.name == 'Publisher' && advExclusionTagFlagDisabled">
             $('#advExclusionsSITE_LEVEL, #advExclusionsSITE_AND_TAG_LEVELS, #advExclusionsDISABLED').prop({disabled : false});
        </s:if>

        <s:if test="accountRole.name == 'Publisher' && frequencyCapsFlagDisabled">
            $('#freqCapsFlagtrue, #freqCapsFlagfalse').prop({disabled : false});
        </s:if>

        <s:if test="accountRole.name == 'Publisher' && wdTagsFlagDisabled">
            $('#wdTagsFlagtrue, #wdTagsFlagfalse').prop({disabled : false});
        </s:if>

        <s:if test="accountRole.name == 'Agency' && financialFieldsDisabled">
            $('#financialFieldsFlagtrue, #financialFieldsFlagfalse').prop({disabled : false});
        </s:if>

        <s:if test="(accountRole.name == 'Advertiser' || accountRole.name == 'Agency') && invoicingDisabled">
            $('#invoicingFlagtrue, #invoicingFlagfalse').prop({disabled : false});
        </s:if>

        <s:if test="accountRole.name == 'Agency' && inputRatesAndAmountsDisabled">
            $('#inputRatesAndAmountsFlagtrue, #inputRatesAndAmountsFlagfalse').prop({disabled : false});
        </s:if>

        <s:if test="accountRole.name == 'Agency' && invoiceCommissionDisabled">
            $('#invoiceCommissionFlagtrue, #invoiceCommissionFlagfalse').prop({disabled : false});
        </s:if>

        <s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' ">
            $('#deviceTargetingNON_MOBILE, #deviceTargetingMOBILE, #deviceTargetingALL').prop({disabled : false});
            $('input[name=selectedMobileChannels]').each(function(){
                $(this).prop({disabled : false});
            });
            $('input[name=selectedNonMobileChannels]').each(function(){
                $(this).prop({disabled : false});
            });

            $('#ioManagementtrue, #ioManagementfalse').prop({disabled : false});
        </s:if>

        <s:if test="(accountRole.name == 'Advertiser' || accountRole.name == 'Agency') && billingModelDisabled">
            $('#billingModelFlagtrue, #billingModelFlagfalse').prop({disabled: true});
        </s:if>
    }

    function fnShowAccounts(jqButton, jqLinkedElem, textShow, textHide) {
        var toShow = jqLinkedElem.css('display') == 'none'; // do not replace with .is(':visible'), because bug in IE (OUI-8269)
        
        jqButton.attr({value : toShow ? textHide : textShow});
        jqLinkedElem[toShow ? 'show' : 'hide']();
    }
    
    function ChBox(jChBox){
        this._jChbox = jChBox;
        this._childs = [];
        this._parent = null;
        var chBox = this;
        
        this._jChbox.change(function(){
            chBox._onChange()
        })
    }
    ChBox.prototype = {
        _onChange : function(){
            this._changeParnt();
            this._changeChlds(this._jChbox.prop('checked'));
        },
        onChldChange : function(){
            this._jChbox.prop({checked : this._isChldsHasProp('checked')});
            this._changeParnt();
        },
        onParntChange : function(newVal){
            this._jChbox.prop({checked : newVal});
            this._changeChlds(newVal);
        },
        _changeParnt : function(){
            this._parent && this._parent.onChldChange();
        },
        _changeChlds : function(newVal){
            $.each(this._childs, function(){
                this.onParntChange(newVal);
            })
        },
        _isChldsHasProp : function(prop){
            var result = false;
            $.each(this._childs, function(){
                if(this._jChbox.prop(prop)){
                    result = true;
                    return false;
                }
            })
            return result;
        },
        updDisabled : function(){
            this._jChbox.prop({disabled : this._isChldsHasProp('disabled')});
            this._parent && this._parent.updDisabled();
        },
        attachParnt : function(parnt){
            this._parent = parnt;
        },
        attachChlds : function(arChilds){
            var curChBox = this;
            $.each(arChilds, function(){
                var child = this;
                if ($.inArray(child, curChBox._childs) > -1) return;
                curChBox._childs.push(child);
                child.attachParnt(curChBox);
            })
            this.updDisabled();
        }
    }

    function hideMobileDeviceChannels(hide) {
        if (hide) {
            $('table[id="mobileDeviceChannelsTable"]').hide();
        } else {
            $('table[id="mobileDeviceChannelsTable"]').show();
        }
    }

    function hideNonMobileDeviceChannels(hide) {
        if (hide) {
            $('table[id="nonMobileDeviceChannelsTable"]').hide();
        } else {
            $('table[id="nonMobileDeviceChannelsTable"]').show();
        }
    }

    $(function(){
        roleSelected();
        
        var displayCampaignsFlag = new ChBox($('#displayCampaignsFlag'));
        var displayCampaigns = new ChBox($('#displayCampaigns'));
        var displayCPMFlag = new ChBox($('#displayCPMFlag'));
        var displayCPCFlag = new ChBox($('#displayCPCFlag'));
        var displayCPAFlag = new ChBox($('#displayCPAFlag'));

        var textCampaingsFlag = new ChBox($('#textCampaingsFlag'));
        var textCampaigns = new ChBox($('#textCampaigns'));
        var keywordTargetedFlag = new ChBox($('#keywordTargetedFlag'));
        var channelTargetedFlag = new ChBox($('#channelTargetedFlag'));
        
        var textCPMFlag = new ChBox($('#textCPMFlag'));
        var textCPCFlag = new ChBox($('#textCPCFlag'));
        var textCPAFlag = new ChBox($('#textCPAFlag'));
        
        displayCampaignsFlag.attachChlds([displayCampaigns, displayCPMFlag, displayCPCFlag, displayCPAFlag]);
        textCampaingsFlag.attachChlds([textCampaigns, keywordTargetedFlag, channelTargetedFlag]);
        channelTargetedFlag.attachChlds([textCPMFlag, textCPCFlag, textCPAFlag]);

        $('#accountRoleId').change(function() {
            $('#accountTypeForm').attr({action : 'new.action'}).submit();
        });

        $('#wdTagsFlagtrue,#wdTagsFlagfalse').change(function() {
            checkWDTagsFlag();
        });

        $('#advExclusionsDISABLED, #advExclusionsSITE_LEVEL, #advExclusionsSITE_AND_TAG_LEVELS').change(function() {
            showHideAdvExclusions();
        });

        $('#deviceTargetingNON_MOBILE, #deviceTargetingMOBILE, #deviceTargetingALL').change(function() {
            if (this.checked) {
                hideMobileDeviceChannels(this.value == 'NON_MOBILE');
                hideNonMobileDeviceChannels(this.value == 'MOBILE');
            }
        });
        $('#deviceTargetingNON_MOBILE, #deviceTargetingMOBILE, #deviceTargetingALL').change();

        <s:if test="accountRole.name == 'Publisher'">
            checkWDTagsFlag();
            showHideAdvExclusions();
        </s:if>

        $('#accountTypeForm').submit(function(){
            <s:if test="accountRole.name == 'Advertiser' || accountRole.name == 'Agency'">
                var checkedCheckboxes = $('#displayCPMFlag, #displayCPCFlag, #displayCPAFlag, #keywordTargetedFlag, #textCPMFlag, #textCPCFlag, #textCPAFlag').filter(':checked');

                if (checkedCheckboxes.length > 0) {
                    checkedCheckboxes.prop({disabled : false});
                    for (i=0; i < checkedCheckboxes.length; i++) {
                        $('#__checkbox_' + checkedCheckboxes[i].id).prop({disabled : false});
                    }
                }
            </s:if>

            <s:if test="accountRole.name == 'Publisher'">
                $('#advExclusionApprovalFlag').prop({disabled:false});
                $('#__checkbox_advExclusionApprovalFlag').prop({disabled : false});
            </s:if>

            enableDisabledRadioButtons();
            return true;
        });

        new UI.AjaxLoader().hide();
    });

    function showHideAdvExclusions() {
        if ($('#advExclusionsDISABLED').is(":checked")) {
            $('#advExclusionApprovalFlag').prop({checked : true});
            $('#advExclusionApprovalFlag').prop({disabled : true});
            $('#approvalFlagId').hide();
        }else{
            if($('#advExclusionApprovalFlag').is(":hidden")){
                $('#advExclusionApprovalFlag').prop({disabled : false});
                $('#approvalFlagId').show();
            }
        }
    };

    function checkWDTagsFlag() {
        if (!$('#wdTagsFlagtrue').is(":checked")) {
            $('#discoverTemplatesElem').hide();
            $('#selectedDiscoverTemplateIds').prop({disabled : true});
        } else {
            $('#discoverTemplatesElem').show();
            $('#selectedDiscoverTemplateIds').prop({disabled : false});
        }
    };
    
    function showHideChannelIntervalDiv() {
        if($('#channelCheckFlag').is(":checked")) {
            <s:iterator value="channelChecks" var="channelCheckEntry">
            $("#channelCheckField${channelCheckEntry.key}").show();
            </s:iterator>
        } else {
            <s:iterator value="channelChecks" var="channelCheckEntry">
            $("#channelCheckField${channelCheckEntry.key}").hide();
            </s:iterator>
        }
    }

    function showHideCampaignIntervalDiv() {
        if($('#campaignCheckFlag').is(":checked")) {
            <s:iterator value="campaignChecks" var="campaignCheckEntry">
            $("#campaignCheckField${campaignCheckEntry.key}").show();
            </s:iterator>
        } else {
            <s:iterator value="campaignChecks" var="campaignCheckEntry">
            $("#campaignCheckField${campaignCheckEntry.key}").hide();
            </s:iterator>
        }
    };

    function showHideCheckIntervalsDivs() {
        showHideChannelIntervalDiv();
        showHideCampaignIntervalDiv();
    };
</script>

<s:form action="admin/AccountType/%{#attr.isCreatePage?'create':'update'}" id="accountTypeForm">
<s:hidden name="id"/>
<s:hidden name="version"/>

<div class="wrapper">
    <s:fielderror><s:param value="'version'"/></s:fielderror>
    <s:actionerror/>
</div>

<ui:section titleKey="form.main">
    <ui:fieldGroup>

        <ui:field labelKey="AccountType.name" labelForId="name" required="true" errors="name">
            <s:textfield name="name" id="name" cssClass="middleLengthText" maxLength="100"/>
        </ui:field>

        <c:if test="${empty id}">
            <ui:field labelKey="AccountType.accountRole" labelForId="accountRoleId" errors="accountRole">
            <s:select name="accountRole" id="accountRoleId" value="accountRole"
                list="availableAccountRoles" cssClass="middleLengthText" listKey="name()"
                listValue="getText('enum.accountRole.'+ top)"/>
            </ui:field>
        </c:if>
        <c:if test="${not empty id}">
            <s:set var="accountRoleString" value="getText('enum.accountRole.'+accountRole)"/>
            <ui:simpleField labelKey="AccountType.accountRole" value="${accountRoleString}"/>
            <s:hidden name="accountRole" id="accountRoleId"/>
        </c:if>

        <s:if test="accountRole.name == 'ISP' ">
            <ui:field id="advancedReportsElem" labelKey="AccountType.advancedReports" errors="advancedReportsFlag">
                <s:radio cssClass="withInput"  id="advancedReportsFlag" name="advancedReportsFlag" value="model.advancedReportsFlag" list="#{true, false}" listValue="getText('AccountType.advancedReports.'+key)" />
            </ui:field>
        </s:if>

        <s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' || accountRole.name == 'Publisher' ">
        <ui:field id="ownershipElem" labelKey="AccountType.ownershipFlag" errors="ownershipFlag">
            <s:radio cssClass="withInput"  id="ownershipFlag" name="ownershipFlag" value="ownership" list="ownershipValues" listValue="getText(key)" listKey="value" />
        </ui:field>
        </s:if>

        <s:if test="accountRole.name == 'Publisher' ">
        <ui:field id="freqCapElem" labelKey="AccountType.freqCaps" errors="freqCapsFlag">
            <s:radio cssClass="withInput"  id="freqCapsFlag" name="freqCapsFlag" value="freqCapsFlag" list="radioButtonFlagValues" listValue="getText(key)" listKey="value"/>
            <s:if test="frequencyCapsFlagDisabled">
                <ui:fieldGroup>
                    <ui:field labelKey="AccountType.siteFlagUsage">
                        <c:set var="buttOnClick">
                            fnShowAccounts($(this), $('#freqCapsSitesDiv'), '<s:text name="AccountType.showSites"/>', '<s:text name="AccountType.hideSites" />');
                        </c:set>

                        <ui:button message="AccountType.showSites" type="button" onclick="${buttOnClick};" />
                    </ui:field>
                </ui:fieldGroup>
                <div class="content hide" id="freqCapsSitesDiv" name="freqCapsSitesDiv">
                    <display:table name="frequencyCapsLinkedSites" class="dataView" id="site">
                        <display:column titleKey="enums.ObjectType.Site">
                            <a href="/admin/site/view.action?id=${site.id}"><c:out value="${site.name}"/></a>
                        </display:column>
                    </display:table>
                </div>
            </s:if>
        </ui:field>

        <ui:field id="publisherInventoryEstimationFlagElem" labelKey="AccountType.inventoryEstimationFlag" errors="publisherInventoryEstimationFlag">
            <s:radio cssClass="withInput"  id="publisherInventoryEstimationFlag" name="publisherInventoryEstimationFlag" value="publisherInventoryEstimationFlag" list="radioButtonFlagValues" listValue="getText(key)" listKey="value"/>
            <s:if test="allowPublisherInventoryEstimationDisabled">
                <s:if test="publisherInventoryEstimationFlag"><s:hidden name="publisherInventoryEstimationFlag" value="true"/></s:if>
                <ui:fieldGroup>
                    <ui:field labelKey="AccountType.tagFlagUsage">
                        <c:set var="buttOnClick">
                            fnShowAccounts($(this), $('#pubInventoryTagsDiv'), '<s:text name="AccountType.showTags"/>', '<s:text name="AccountType.hideTags" />');
                        </c:set>
                        <ui:button type="button" message="AccountType.showTags" onclick="${buttOnClick};" />
                    </ui:field>
                </ui:fieldGroup>
                <div class="content hide" id="pubInventoryTagsDiv" name="pubInventoryTagsDiv">
                    <display:table name="publisherInventoryLinkedTags" class="dataView" id="tag">
                        <display:column titleKey="enums.ObjectType.Tag">
                            <a href="/admin/tag/view.action?id=${tag.id}"><c:out value="${tag.name}"/></a>
                        </display:column>
                    </display:table>
                </div>
            </s:if>
        </ui:field>

        <ui:field id="advExclusionFlagElem" labelKey="AccountType.advertiserExclusionFlag" errors="advExclusionFlag">
            <s:radio cssClass="withInput" name="advExclusions" id="advExclusions" value="advExclusions"
                     list="@com.foros.model.security.AdvExclusionsType@values()"
                     listKey="name()" listValue="getText('enums.AccountType.advExclusionsType.' + name())"/>

            <table class="fieldAndAccessories" id="approvalFlagId">
                <tr>
                    <td class="withField nomargin">
                        <label class="withInput">
                            <s:checkbox id="advExclusionApprovalFlag" name="advExclusionApprovalFlag" disabled="advExclusionApprovalDisabled"/><s:text name="AccountType.advExclusionApproval"/>
                        </label>
                    </td>
                    <td class="withTip">
                        <ui:hint>
                            <s:text name="AccountType.advExclusionApproval.tip"/>
                        </ui:hint>
                    </td>
                </tr>
            </table>

            <s:if test="advExclusionSiteFlagDisabled">
                <p><ui:fieldGroup>
                    <ui:field labelKey="AccountType.siteFlagUsage">
                        <c:set var="buttOnClick">
                            fnShowAccounts($(this), $('#advExclusionSitesDiv'), '<s:text name="AccountType.showSites"/>', '<s:text name="AccountType.hideSites" />');
                        </c:set>
                        <ui:button type="button" message="AccountType.showSites" onclick="${buttOnClick};" />
                    </ui:field>
                </ui:fieldGroup>
                <div class="content hide" style="float:none;" id="advExclusionSitesDiv" name="advExclusionSitesDiv">
                    <display:table name="advExclusionLinkedSites" class="dataView" id="site">
                        <display:column titleKey="enums.ObjectType.Site">
                            <a href="/admin/site/view.action?id=${site.id}"><c:out value="${site.name}"/></a>
                        </display:column>
                    </display:table>
                </div>
                </p>
            </s:if>

            <s:if test="advExclusionTagFlagDisabled">
                <ui:fieldGroup>
                    <ui:field labelKey="AccountType.tagFlagUsage">
                        <c:set var="buttOnClick">
                            fnShowAccounts($(this), $('#advExclusionTagsDiv'), '<s:text name="AccountType.showTags"/>', '<s:text name="AccountType.hideTags" />');
                        </c:set>
                        <ui:button type="button" message="AccountType.showTags" onclick="${buttOnClick};" />
                    </ui:field>
                </ui:fieldGroup>
                <div class="content hide" style="float:none;" id="advExclusionTagsDiv" name="advExclusionTagsDiv">
                    <display:table name="advExclusionLinkedTags" class="dataView" id="tag">
                        <display:column titleKey="AccountType.tag">
                            <a href="/admin/tag/view.action?id=${tag.id}"><c:out value="${tag.name}"/></a>
                        </display:column>
                    </display:table>
                </div>
            </s:if>
        </ui:field>

        <ui:field id="wdTagsFlagElem" labelKey="AccountType.wdTagsFlag" errors="wdTagsFlag">
            <s:radio cssClass="withInput"  id="wdTagsFlag" name="wdTagsFlag" value="wdTagsFlag" list="radioButtonFlagValues" listValue="getText(key)" listKey="value"/>
            <s:if test="wdTagsFlagDisabled">
                <s:if test="wdTagsFlag"><s:hidden name="wdTagsFlag" value="true"/></s:if>
                <table class="grouping">
                    <tr>
                        <td style="vertical-align: middle; padding-right: 5px;">
                            <s:text name="AccountType.wdTagsFlagDisabled"/>
                        </td>
                        <td>
                            <c:set var="buttOnClick">
                                fnShowAccounts($(this), $('#wdTagsSitesDiv'), '<s:text name="AccountType.showSites"/>', '<s:text name="AccountType.hideSites" />');
                            </c:set>
                            <ui:button type="button" message="AccountType.showSites" onclick="${buttOnClick};" />
                        </td>
                    </tr>
                </table>

                <div class="content hide" id="wdTagsSitesDiv" name="wdTagsSitesDiv">
                    <display:table name="wdTagsLinkedSites" class="dataView" id="site">
                        <display:column titleKey="enums.ObjectType.Site">
                            <a href="/admin/site/view.action?id=${site.id}"><c:out value="${site.name}"/></a>
                        </display:column>
                    </display:table>
                </div>
            </s:if>
        </ui:field>

        <ui:field labelKey="AccountType.iframeTag" errors="showIframeTag">
            <s:radio cssClass="withInput"  id="showIframeTag" name="showIframeTag" value="showIframeTag" list="showTagsValues" listValue="getText(key)" listKey="value"/>
        </ui:field>

        <ui:field labelKey="AccountType.browserPassbackTag" errors="showBrowserPassbackTag">
            <s:radio cssClass="withInput"  id="showBrowserPassbackTag" name="showBrowserPassbackTag" value="showBrowserPassbackTag" list="showTagsValues" listValue="getText(key)" listKey="value"/>
        </ui:field>

        <ui:field labelKey="AccountType.clicksDataForExternalUsers" errors="clicksDataVisibleToExternal">
                    <s:radio cssClass="withInput"  id="clicksDataVisibleToExternal" name="clicksDataVisibleToExternal" value="clicksDataVisibleToExternal" list="showTagsValues" listValue="getText(key)" listKey="value"/>
        </ui:field>
        </s:if>

        <s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' ">
        <ui:field id="siteTargetingFlagElem" labelKey="AccountType.siteTargetingFlag" errors="siteTargetingFlag">
            <s:radio cssClass="withInput"  id="siteTargetingFlag" name="siteTargetingFlag" value="siteTargetingFlag" list="radioButtonFlagValues" listValue="getText(key)" listKey="value"/>
            <s:if test="siteTargetingDisabled">
                <s:if test="siteTargetingFlag"><s:hidden name="siteTargetingFlag" value="true"/></s:if>
                <ui:fieldGroup>
                    <ui:field labelKey="AccountType.ccgFlagUsage">
                        <c:set var="buttOnClick">
                            fnShowAccounts($(this), $('#siteCcgsDiv'), '<s:text name="AccountType.showCreativeGroups"/>', '<s:text name="AccountType.hideCreativeGroups" />');
                        </c:set>
                        <ui:button type="button" message="AccountType.showCreativeGroups" onclick="${buttOnClick};" />
                    </ui:field>
                </ui:fieldGroup>
                <div class="hide" id="siteCcgsDiv" name="siteCcgsDiv" style="margin-top:10px;">
                    <display:table name="SiteTargetingLinkedCcgs" class="dataView" id="ccg">
                        <display:column titleKey="enums.ObjectType.CampaignCreativeGroup">
                            <c:choose>
                                <c:when test="${ccg.ccgType.letter=='D'}">
                                    <a href="/admin/campaign/group/viewDisplay.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                                </c:when>
                                <c:otherwise>
                                    <a href="/admin/campaign/group/viewText.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                                </c:otherwise>
                            </c:choose>
                        </display:column>
                    </display:table>
                </div>
            </s:if>
        </ui:field>

        </s:if>

        <s:if test="accountRole.name == 'Agency'">
            <ui:field id="financialFieldsElem" labelKey="AccountType.financialFieldsFlag" errors="financialFieldsFlag">
                <s:radio cssClass="withInput"  id="financialFieldsFlag" name="financialFieldsFlag" value="financialFieldsFlag" list="financialFieldsValues" listValue="getText(key)" listKey="value" />
                <s:if test="financialFieldsDisabled">
                    <div class="rowWithButton nomargin">
                        <label><fmt:message key="AccountType.accountUsage"/></label>
                        <c:set var="buttOnClick">
                            fnShowAccounts($(this), $('#financialFieldsDiv'), '<s:text name="AccountType.showAccounts"/>', '<s:text name="AccountType.hideAccounts" />');
                        </c:set>
                        <ui:button type="button" message="AccountType.showAccounts" onclick="${buttOnClick};" />
                    </div>
                    <div id="financialFieldsDiv" class="hide">
                        <display:table name="financialFieldsLinkedAccunts" class="dataView" id="financialFieldAccount">
                            <display:column titleKey="AccountType.account">
                                <a href="/admin/account/view.action?id=${financialFieldAccount.id}"><c:out value="${financialFieldAccount.name}"/></a>
                            </display:column>
                        </display:table>
                    </div>
                </s:if>
            </ui:field>
        </s:if>

        <s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' ">
        <ui:field id="invoicingElem" labelKey="AccountType.invoicingFlag" errors="invoicingFlag">
            <s:radio cssClass="withInput"  id="invoicingFlag" name="invoicingFlag" value="invoicingFlag" list="invoicingValues" listValue="getText(key)" listKey="value" />
            <s:if test="invoicingDisabled">
                <div class="rowWithButton nomargin">
                    <label><fmt:message key="AccountType.accountUsage"/></label>
                    <c:set var="buttOnClick">
                        fnShowAccounts($(this), $('#invoicingDiv'), '<s:text name="AccountType.showAccounts"/>', '<s:text name="AccountType.hideAccounts" />');
                    </c:set>
                    <ui:button type="button" message="AccountType.showAccounts" onclick="${buttOnClick};" />
                </div>
                <div id="invoicingDiv" class="hide">
                    <display:table name="invoicingLinkedAccunts" class="dataView" id="invoicingAccount">
                        <display:column titleKey="AccountType.account">
                            <a href="/admin/account/view.action?id=${invoicingAccount.id}"><c:out value="${invoicingAccount.name}"/></a>
                        </display:column>
                    </display:table>
                </div>
            </s:if>
        </ui:field>
        </s:if>

        <s:if test="accountRole.name == 'Agency' ">
        <ui:field id="inputRatesAndAmountsElem" labelKey="AccountType.inputRatesAndAmountsFlag" errors="inputRatesAndAmountsFlag">
            <s:radio cssClass="withInput"  id="inputRatesAndAmountsFlag" name="inputRatesAndAmountsFlag" value="inputRatesAndAmountsFlag" list="inputRatesAndAmountsValues" listValue="getText(key)" listKey="value" />
            <s:if test="inputRatesAndAmountsDisabled">
                <div class="rowWithButton nomargin">
                    <label><fmt:message key="AccountType.accountUsage"/></label>
                    <c:set var="buttOnClick">
                        fnShowAccounts($(this), $('#inputRatesAndAmountsDiv'), '<s:text name="AccountType.showAccounts"/>', '<s:text name="AccountType.hideAccounts" />');
                    </c:set>
                    <ui:button type="button" message="AccountType.showAccounts" onclick="${buttOnClick};" />
                </div>
                <div id="inputRatesAndAmountsDiv" class="hide">
                    <display:table name="inputRatesAndAmountsLinkedAccunts" class="dataView" id="inputRatesAndAmountsAccount">
                        <display:column titleKey="AccountType.account">
                            <a href="/admin/account/view.action?id=${inputRatesAndAmountsAccount.id}"><c:out value="${inputRatesAndAmountsAccount.name}"/></a>
                        </display:column>
                    </display:table>
                </div>
            </s:if>
        </ui:field>
        <ui:field labelKey="AccountType.auctionRate" errors="auctionRate">
            <s:radio cssClass="withInput" name="auctionRate" value="auctionRate" list="auctionRateValues" listValue="getText(key)" listKey="value" />
        </ui:field>
        </s:if>

        <s:if test="accountRole.name == 'Agency'">
        <ui:field id="invoiceCommissionElem" labelKey="AccountType.commissionFlag" errors="invoiceCommissionFlag">
            <s:radio cssClass="withInput"  id="invoiceCommissionFlag" name="invoiceCommissionFlag" value="invoiceCommissionFlag" list="invoiceCommissionValues" listValue="getText(key)" listKey="value" />
            
            <s:if test="invoiceCommissionDisabled">
                <div class="rowWithButton nomargin">
                    <label><fmt:message key="AccountType.accountUsage"/></label>
                    <c:set var="buttOnClick">
                        fnShowAccounts($(this), $('#invoiceCommissionDiv'), '<s:text name="AccountType.showAccounts"/>', '<s:text name="AccountType.hideAccounts" />');
                    </c:set>
                    <ui:button type="button" message="AccountType.showAccounts" onclick="${buttOnClick};" />
                </div>
                <div id="invoiceCommissionDiv" class="hide">
                    <display:table name="invoiceCommissionLinkedAccunts" class="dataView" id="invoiceCommissionAccount">
                        <display:column titleKey="AccountType.account">
                            <a href="/admin/account/view.action?id=${invoiceCommissionAccount.id}"><c:out value="${invoiceCommissionAccount.name}"/></a>
                        </display:column>
                    </display:table>
                </div>
            </s:if>
        </ui:field>
        </s:if>

        <s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' ">
            <ui:field id="ioManagementElem" labelKey="AccountType.ioManagement" errors="ioManagement">
                <s:radio cssClass="withInput"
                         id="ioManagement"
                         name="ioManagement"
                         value="ioManagement"
                         list="radioButtonFlagValues"
                         listValue="getText(key)"
                         listKey="value"
                         disabled="%{IOManagementDisabled}"
                        />
            </ui:field>
        </s:if>

        <s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser'">
            <ui:field id="billingModelElem" labelKey="AccountType.billingModelFlag" errors="billingModelFlag">
                <s:radio cssClass="withInput"
                         id="billingModelFlag"
                         name="billingModelFlag"
                         value="billingModelFlag"
                         list="billingModelValues"
                         listValue="getText(key)"
                         listKey="value"
                         disabled="%{billingModelDisabled}" />
            </ui:field>
        </s:if>

    </ui:fieldGroup>
</ui:section>

<s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' || accountRole.name == 'CMP' || accountRole.name == 'Internal'">
    <ui:section titleKey="AccountType.checkSettings.channelCheckPeriods">
        <ui:fieldGroup>
            <ui:field labelKey="form.enabled"> 
                <s:checkbox name="channelCheck" id="channelCheckFlag" onchange="showHideChannelIntervalDiv();"></s:checkbox>
            </ui:field> 
            </ui:fieldGroup>
            
            <ui:fieldGroup>
            
                <s:iterator value="channelChecks" var="channelCheckEntry">
                    <c:set var="checkNumberLabelKey">AccountType.checkSettings.${channelCheckEntry.key}Check</c:set>
                    <c:set var="checkNumberName" value="channelChecks.${channelCheckEntry.key}.value"/>
                    <ui:field id="channelCheckField${channelCheckEntry.key}" labelKey="${checkNumberLabelKey}"  required="true" errors="channelChecks.${channelCheckEntry.key}.value" >
                    <s:textfield name="%{#attr.checkNumberName}" cssClass="smallLengthNumber" maxLength="3"/>

                    <select name="channelChecks.${channelCheckEntry.key}.uom">
                        <s:iterator value="@com.foros.action.admin.accountType.AccountTypeSupportAction$CheckUOM@values()" var="channelUOM">
                            <option value="${channelUOM}" 
                                <c:if test="${channelCheckEntry.value.uom == channelUOM}">selected="selected"</c:if>>
                                <fmt:message key="${channelUOM.nameKey}"/>
                                </option>
                        </s:iterator>
                    </select>
                    </ui:field> 
                </s:iterator>
        </ui:fieldGroup>
    </ui:section>
</s:if>
    
<s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser'">
    <ui:section titleKey="AccountType.checkSettings.campaignCheckPeriods">
        <ui:fieldGroup>
            <ui:field labelKey="form.enabled"> 
                <s:checkbox name="campaignCheck" id="campaignCheckFlag" onchange="showHideCampaignIntervalDiv();"></s:checkbox>
            </ui:field> 
          </ui:fieldGroup>  
           
           <ui:fieldGroup >
                <s:iterator value="campaignChecks" var="campaignCheckEntry" >
                    <c:set var="checkNumberLabelKey">AccountType.checkSettings.${campaignCheckEntry.key}Check</c:set>
                    <c:set var="checkNumberName" value="campaignChecks.${campaignCheckEntry.key}.value"/>
                    <ui:field id="campaignCheckField${campaignCheckEntry.key}" labelKey="${checkNumberLabelKey}" required="true" errors="campaignChecks.${campaignCheckEntry.key}.value" >
                    <s:textfield name="%{#attr.checkNumberName}" cssClass="smallLengthNumber" maxLength="3"/>

                    <select name="campaignChecks.${campaignCheckEntry.key}.uom">
                        <s:iterator value="@com.foros.action.admin.accountType.AccountTypeSupportAction$CheckUOM@values()" var="campaignUOM">
                            <option value="${campaignUOM}" 
                                <c:if test="${campaignCheckEntry.value.uom == campaignUOM}">selected="selected"</c:if>>
                                <fmt:message key="${campaignUOM.nameKey}"/>
                                </option>
                        </s:iterator>
                    </select>
                    </ui:field> 
                </s:iterator>
        </ui:fieldGroup>
    </ui:section>
</s:if>

<script type="text/javascript">
   showHideCheckIntervalsDivs();   
</script>

<s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' ">
<ui:section titleKey="AccountType.rateTypes" id="rateTypesElem" mandatory="true" errors="walledGardenAgencyType">
    <ui:fieldGroup>
        <ui:field>
            <s:set var="isCampaignOrCreativeExist" value="displayCreativesExist || displayCampaignsExist"/>
            <ul class="chBoxesTree">
                <li class="nomargin">
                    <label class="withInput">
                        <s:checkbox id="displayCampaignsFlag" name="displayCampaignsFlag"
                                    disabled="displayCPMDisabled || displayCPCDisabled || displayCPADisabled || #isCampaignOrCreativeExist"
                        /><s:text name="AccountType.displayCampaigns"/>
                    </label>
                </li>
                <s:checkbox id="displayCampaigns" name="displayCampaigns" value="#isCampaignOrCreativeExist" disabled="#isCampaignOrCreativeExist" cssClass="hide"/>
                <li>
                    <ul>
                        <li>
                            <div style="padding-top: 3px;">
                                <s:text name="AccountType.displayCreativeGroups"/>
                            </div>
                        </li>
                        <li>
                            <ul>
                                <li class="nomargin">
                                    <label class="withInput">
                                        <s:checkbox id="displayCPMFlag" name="displayCPMFlag" disabled="displayCPMDisabled"
                                        /><s:text name="AccountType.CPM"/>
                                    </label>
                                </li>
                                <li class="nomargin">
                                    <label class="withInput">
                                        <s:checkbox id="displayCPCFlag" name="displayCPCFlag" disabled="displayCPCDisabled"
                                        /><s:text name="AccountType.CPC"/>
                                    </label>
                                </li>
                                <li class="nomargin">
                                    <label class="withInput">
                                        <s:checkbox id="displayCPAFlag" name="displayCPAFlag" disabled="displayCPADisabled"
                                        /><s:text name="AccountType.CPA"/>
                                    </label>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </li>
            </ul>
        </ui:field>

        <s:if test="displayCreativesExist">
            <ui:field>
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:text textKey="AccountType.displayCreativesExist"/>
                        </td>
                        <td class="withButton">
                            <c:set var="buttOnClick">
                                fnShowAccounts($(this), $('#displayCreativesDiv'), '<s:text name="AccountType.showCreatives"/>', '<s:text name="AccountType.hideCreatives" />');
                            </c:set>
                            <ui:button type="button" message="AccountType.showCreatives" onclick="${buttOnClick};" />
                        </td>
                    </tr>
                </table>
            </ui:field>
            <ui:field cssClass="hide" id="displayCreativesDiv">
                <display:table name="displayCreatives" class="dataView" id="creative">
                  <display:column titleKey="creatives">
                        <a href="/admin/creative/view.action?id=${creative.id}"><c:out value="${creative.name}"/></a>
                  </display:column>
                </display:table>
            </ui:field>
        </s:if>
        <s:if test="displayCampaignsExist">
            <ui:field>
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:text textKey="AccountType.displayCampaignsExist"/>
                        </td>
                        <td class="withButton">
                            <c:set var="buttOnClick">
                                fnShowAccounts($(this), $('#displayCampaignsDiv'), '<s:text name="AccountType.showCampaigns"/>', '<s:text name="AccountType.hideCampaigns" />');
                            </c:set>
                            <ui:button type="button" message="AccountType.showCampaigns" onclick="${buttOnClick};" />
                        </td>
                    </tr>
                </table>
            </ui:field>
            <ui:field cssClass="hide" id="displayCampaignsDiv">
                <display:table name="displayCampaigns" class="dataView" id="campaign">
                  <display:column titleKey="campaigns">
                        <a href="/admin/campaign/view.action?id=${campaign.id}"><c:out value="${campaign.name}"/></a>
                  </display:column>
                </display:table>
            </ui:field>
        </s:if>
        <s:if test="displayCPMDisabled">
            <ui:field labelKey="AccountType.CPM">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:text textKey="AccountType.ccgFlagUsage"/>
                        </td>
                        <td class="withButton">
                            <c:set var="buttOnClick">
                                fnShowAccounts($(this), $('#displayCPMCCGDiv'), '<s:text name="AccountType.showCreativeGroups"/>', '<s:text name="AccountType.hideCreativeGroups" />');
                            </c:set>
                            <ui:button type="button" message="AccountType.showCreativeGroups" onclick="${buttOnClick};" />
                        </td>
                    </tr>
                </table>
            </ui:field>
            <ui:field cssClass="hide" id="displayCPMCCGDiv">
                <display:table name="displayCpmLinkedCampaignCreativeGroups" class="dataView" id="ccg">
                  <display:column titleKey="campaign.creative.groups">
                    <c:choose>
                      <c:when test="${ccg.ccgType.letter=='D'}">
                        <a href="/admin/campaign/group/viewDisplay.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                      </c:when>
                      <c:otherwise>
                        <a href="/admin/campaign/group/viewText.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                      </c:otherwise>
                    </c:choose>
                  </display:column>
                </display:table>
            </ui:field>
        </s:if>
        <s:if test="displayCPCDisabled">
            <ui:field labelKey="AccountType.CPC">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:text textKey="AccountType.ccgFlagUsage"/>
                        </td>
                        <td class="withButton">
                            <c:set var="buttOnClick">
                                fnShowAccounts($(this), $('#displayCPCCCGDiv'), '<s:text name="AccountType.showCreativeGroups"/>', '<s:text name="AccountType.hideCreativeGroups" />');
                            </c:set>
                            <ui:button type="button" message="AccountType.showCreativeGroups" onclick="${buttOnClick};" />
                        </td>
                    </tr>
                </table>
            </ui:field>
            <ui:field id="displayCPCCCGDiv" cssClass="hide">
                <display:table name="displayCpcLinkedCampaignCreativeGroups" class="dataView" id="ccg">
                  <display:column titleKey="campaign.creative.groups">
                      <c:choose>
                        <c:when test="${ccg.ccgType.letter=='D'}">
                          <a href="/admin/campaign/group/viewDisplay.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                        </c:when>
                        <c:otherwise>
                          <a href="/admin/campaign/group/viewText.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                        </c:otherwise>
                      </c:choose>
                    </display:column>
                </display:table>
            </ui:field>
        </s:if>
        <s:if test="displayCPADisabled">
            <ui:field labelKey="AccountType.CPA">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:text textKey="AccountType.ccgFlagUsage"/>
                        </td>
                        <td class="withButton">
                            <c:set var="buttOnClick">
                                fnShowAccounts($(this), $('#displayCPACCGDiv'), '<s:text name="AccountType.showCreativeGroups"/>', '<s:text name="AccountType.hideCreativeGroups" />');
                            </c:set>
                            <ui:button type="button" message="AccountType.showCreativeGroups" onclick="${buttOnClick};" />
                        </td>
                    </tr>
                </table>
            </ui:field>
            <ui:field id="displayCPACCGDiv" cssClass="hide">
                <display:table name="displayCpaLinkedCampaignCreativeGroups" class="dataView" id="ccg">
                  <display:column titleKey="campaign.creative.groups">
                      <c:choose>
                        <c:when test="${ccg.ccgType.letter=='D'}">
                          <a href="/admin/campaign/group/viewDisplay.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                        </c:when>
                        <c:otherwise>
                          <a href="/admin/campaign/group/viewText.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                        </c:otherwise>
                      </c:choose>
                    </display:column>
                </display:table>
            </ui:field>
        </s:if>

        <ui:field>
            <ul class="chBoxesTree">
                <s:set var="istextCampaignExists" value="textCampaignsExist"/>
                <li class="nomargin">
                    <label class="withInput">
                        <s:checkbox id="textCampaingsFlag" name="textCampaingsFlag" disabled="keywordTargetedDisabled || textCPMDisabled || textCPCDisabled || textCPADisabled || #istextCampaignExists"
                        /><s:text name="AccountType.textCampaigns"/>
                    </label>
                </li>
                <s:checkbox id="textCampaigns" name="textCampaigns" disabled="#istextCampaignExists" value="#istextCampaignExists" cssClass="hide"/>
                <li>
                    <ul>
                        <li class="nomargin">
                            <label class="withInput">
                                <s:checkbox id="keywordTargetedFlag" name="keywordTargetedFlag" disabled="keywordTargetedDisabled"
                                /><s:text name="AccountType.keywordTargeted"/>
                            </label>
                        </li>
                        <li class="nomargin">
                            <label class="withInput">
                                <s:checkbox id="channelTargetedFlag" name="channelTargetedFlag" disabled="textCPMDisabled || textCPCDisabled || textCPADisabled"
                                /><s:text name="AccountType.channelTargeted"/>
                            </label>
                        </li>
                        <li>
                            <ul>
                                <li class="nomargin">
                                    <label class="withInput">
                                        <s:checkbox id="textCPMFlag" name="textCPMFlag" disabled="textCPMDisabled"
                                        /><s:text name="AccountType.CPM"/>
                                    </label>
                                </li>
                                <li class="nomargin">
                                    <label class="withInput">
                                        <s:checkbox id="textCPCFlag" name="textCPCFlag" disabled="textCPCDisabled"
                                        /><s:text name="AccountType.CPC"/>
                                    </label>
                                </li>
                                <li class="nomargin">
                                    <label class="withInput">
                                        <s:checkbox id="textCPAFlag" name="textCPAFlag" disabled="textCPADisabled"
                                        /><s:text name="AccountType.CPA"/>
                                    </label>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </li>
            </ul>
        </ui:field>

        <s:if test="textCampaignsExist">
            <ui:field>
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:text textKey="AccountType.textCampaignsExist"/>
                        </td>
                        <td class="withButton">
                            <c:set var="buttOnClick">
                                fnShowAccounts($(this), $('#textCampaignsDiv'), '<s:text name="AccountType.showCampaigns"/>', '<s:text name="AccountType.hideCampaigns" />');
                            </c:set>
                            <ui:button type="button" message="AccountType.showCampaigns" onclick="${buttOnClick};" />
                        </td>
                    </tr>
                </table>
            </ui:field>
            <ui:field cssClass="hide" id="textCampaignsDiv">
                <display:table name="textCampaigns" class="dataView" id="campaign">
                  <display:column titleKey="campaigns">
                        <a href="/admin/campaign/view.action?id=${campaign.id}"><c:out value="${campaign.name}"/></a>
                  </display:column>
                </display:table>
            </ui:field>
        </s:if>

        <s:if test="keywordTargetedDisabled">
            <ui:field>
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:text textKey="AccountType.referringTextCCGs"/>
                        </td>
                        <td class="withButton">
                            <c:set var="buttOnClick">
                                fnShowAccounts($(this), $('#keywordTargetedCcgsDiv'), '<s:text name="AccountType.showCreativeGroups"/>', '<s:text name="AccountType.hideCreativeGroups" />');
                            </c:set>
                            <ui:button type="button" message="AccountType.showCreativeGroups" onclick="${buttOnClick};" />
                        </td>
                    </tr>
                </table>
            </ui:field>
            <ui:field id="keywordTargetedCcgsDiv" cssClass="hide">
                <display:table name="keywordTargetedLinkedCcgs" class="dataView" id="ccg">
                  <display:column titleKey="enums.ObjectType.CampaignCreativeGroup">
                    <c:choose>
                      <c:when test="${ccg.ccgType.letter=='D'}">
                        <a href="/admin/campaign/group/viewDisplay.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                      </c:when>
                      <c:otherwise>
                        <a href="/admin/campaign/group/viewText.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                      </c:otherwise>
                    </c:choose>
                  </display:column>
                </display:table>
            </ui:field>
        </s:if>

        <s:if test="textCPMDisabled">
            <ui:field labelKey="AccountType.CPM">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:text textKey="AccountType.ccgFlagUsage"/>
                        </td>
                        <td class="withButton">
                            <c:set var="buttOnClick">
                                fnShowAccounts($(this), $('#textCPMCCGDiv'), '<s:text name="AccountType.showCreativeGroups"/>', '<s:text name="AccountType.hideCreativeGroups" />');
                            </c:set>
                            <ui:button type="button" message="AccountType.showCreativeGroups" onclick="${buttOnClick};" />
                        </td>
                    </tr>
                </table>
            </ui:field>
            <ui:field id="textCPMCCGDiv" cssClass="hide">
                <display:table name="textCpmLinkedCampaignCreativeGroups" class="dataView" id="ccg">
                  <display:column titleKey="campaign.creative.groups">
                      <c:choose>
                        <c:when test="${ccg.ccgType.letter=='D'}">
                          <a href="/admin/campaign/group/viewDisplay.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                        </c:when>
                        <c:otherwise>
                          <a href="/admin/campaign/group/viewText.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                        </c:otherwise>
                      </c:choose>
                    </display:column>
                </display:table>
            </ui:field>
        </s:if>

        <s:if test="textCPCDisabled">
            <ui:field labelKey="AccountType.CPC">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:text textKey="AccountType.ccgFlagUsage"/>
                        </td>
                        <td class="withButton">
                            <c:set var="buttOnClick">
                                fnShowAccounts($(this), $('#textCPCCCGDiv'), '<s:text name="AccountType.showCreativeGroups"/>', '<s:text name="AccountType.hideCreativeGroups" />');
                            </c:set>
                            <ui:button type="button" message="AccountType.showCreativeGroups" onclick="${buttOnClick};" />
                        </td>
                    </tr>
                </table>
            </ui:field>
            <ui:field id="textCPCCCGDiv" cssClass="hide">
                <display:table name="textCpcLinkedCampaignCreativeGroups" class="dataView" id="ccg">
                  <display:column titleKey="campaign.creative.groups">
                      <c:choose>
                        <c:when test="${ccg.ccgType.letter=='D'}">
                          <a href="/admin/campaign/group/viewDisplay.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                        </c:when>
                        <c:otherwise>
                          <a href="/admin/campaign/group/viewText.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                        </c:otherwise>
                      </c:choose>
                    </display:column>
                </display:table>
            </ui:field>
        </s:if>

        <s:if test="textCPADisabled">
            <ui:field labelKey="AccountType.CPA">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:text textKey="AccountType.ccgFlagUsage"/>
                        </td>
                        <td class="withButton">
                            <c:set var="buttOnClick">
                                fnShowAccounts($(this), $('#textCPACCGDiv'), '<s:text name="AccountType.showCreativeGroups"/>', '<s:text name="AccountType.hideCreativeGroups" />');
                            </c:set>
                            <ui:button type="button" message="AccountType.showCreativeGroups" onclick="${buttOnClick};" />
                        </td>
                    </tr>
                </table>
            </ui:field>
            <ui:field id="textCPACCGDiv" cssClass="hide">
                <display:table name="textCpaLinkedCampaignCreativeGroups" class="dataView" id="ccg">
                  <display:column titleKey="campaign.creative.groups">
                      <c:choose>
                        <c:when test="${ccg.ccgType.letter=='D'}">
                          <a href="/admin/campaign/group/viewDisplay.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                        </c:when>
                        <c:otherwise>
                          <a href="/admin/campaign/group/viewText.action?id=${ccg.id}"><c:out value="${ccg.name}"/></a>
                        </c:otherwise>
                      </c:choose>
                    </display:column>
                </display:table>
            </ui:field>
        </s:if>
    </ui:fieldGroup>

    <s:fielderror><s:param value="'AccountType.rateType.notAvailable'"/></s:fielderror>
</ui:section>
</s:if>

<s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' ">
    <ui:section titleKey="AccountType.deviceTargeting" id="mobileOptions" mandatory="true" errors="mobileOptions">
        <jsp:include
            page="/admin/accountType/deviceTargetingEdit.jsp">
            <jsp:param name="isDeviceTargetingEditPage" value="false" />
         </jsp:include>

    </ui:section>
</s:if>

<s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' || accountRole.name == 'CMP' ">
<ui:section titleKey="AccountType.keywordAndUrlLimits" id="keywordAndUrlLimitsElem" mandatory="true">
    <ui:fieldGroup>
        <ui:field labelKey="AccountType.maxKeywordLength" errors="maxKeywordLength" required="true">
            <s:textfield name="maxKeywordLength" id="maxKeywordLengthInput" cssClass="middleLengthNumber" maxlength="10"/>
        </ui:field>
        <ui:field labelKey="AccountType.maxUrlLength" errors="maxUrlLength" required="true">
            <s:textfield name="maxUrlLength" id="maxUrlLengthInput" cssClass="middleLengthNumber" maxlength="10"/>
        </ui:field>
        <s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' ">
            <ui:field labelKey="AccountType.maxKeywordsPerGroup" errors="maxKeywordsPerGroup" required="true">
                <s:textfield name="maxKeywordsPerGroup" id="maxKeywordsPerGroupInput" cssClass="middleLengthNumber" maxlength="10"/>
            </ui:field>
        </s:if>
        <ui:field labelKey="AccountType.maxKeywordsPerChannel" errors="maxKeywordsPerChannel" required="true">
            <s:textfield name="maxKeywordsPerChannel" id="maxKeywordsPerChannelInput" cssClass="middleLengthNumber" maxlength="10"/>
        </ui:field>
        <ui:field labelKey="AccountType.maxUrlsPerChannel" errors="maxUrlsPerChannel" required="true">
            <s:textfield name="maxUrlsPerChannel" id="maxUrlsPerChannelInput" cssClass="middleLengthNumber" maxlength="10"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>
</s:if>

<s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' || accountRole.name == 'Publisher'">
<ui:section id="sizesElem" titleKey="CreativeSize.plural" >
    <ui:fieldGroup cssClass="formFields">
        <ui:field>
            <s:fielderror><s:param value="'AccountType.sizes.notAvailable'"/></s:fielderror>
            <ui:optiontransfer id="selectedSizeIds" selId="creativeSizes" name="creativeSizeList.id"
                   list="${availableSizes}"
                   selList="${creativeSizeList}"
                   listValue="localizedName"
                   mandatory="true"
                   cssClass="smallLengthText2" size="9"
                   titleKey="AccountType.sizes.available" selTitleKey="AccountType.sizes.selected"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>
</s:if>

<s:if test="accountRole.name == 'Agency' || accountRole.name == 'Advertiser' ">
<ui:section id="creativeTemplatesElem" titleKey="CreativeTemplate.plural" >
    <ui:fieldGroup>

        <ui:field>
            <s:fielderror><s:param value="'AccountType.templates.notAvailable'"/></s:fielderror>
            <ui:optiontransfer id="selectedCreativeTemplateIds" selId="creativeTemplates" name="creativeTemplateList.id"
                       list="${availableCreativeTemplates}" selList="${creativeTemplateList}" mandatory="true"  listValue="localizedName"
                       cssClass="smallLengthText2" size="9"
                       titleKey="AccountType.templates.creative.available" selTitleKey="AccountType.templates.creative.selected"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>
</s:if>

<s:if test="accountRole.name == 'Publisher'">
<ui:section id="discoverTemplatesElem" titleKey="DiscoverTemplate.plural">
    <ui:fieldGroup>
        <ui:field>
            <s:fielderror><s:param value="'AccountType.discoverTemplates.notAvailable'"/></s:fielderror>
            <ui:optiontransfer id="selectedDiscoverTemplateIds" selId="discoverTemplates" name="discoverTemplateList.id"
                       list="${availableDiscoverTemplates}" selList="${discoverTemplateList}" mandatory="true"  listValue="localizedName"
                       cssClass="smallLengthText2" size="9"
                       titleKey="AccountType.templates.discover.available" selTitleKey="AccountType.templates.discover.selected"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>
</s:if>

  <s:include value="/templates/formFooter.jsp"/>
 </s:form>
