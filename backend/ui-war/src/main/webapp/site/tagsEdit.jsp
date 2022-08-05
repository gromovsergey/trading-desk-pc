<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">
    function replace(s,p,r){
        while(s.indexOf(p) >= 0) s = s.replace(p,r);
        return s;
    }

    function creativeExclusionControl(isHide){
        $('#_categoryExclusions')[isHide ? 'show' : 'hide']();
    }

    function confirmChoice(){
        if (${tagLevelExclusionFlag} && $('#exclusionFlag').length && !$('#exclusionFlag')[0].checked) {
            if (!confirm("${ad:formatMessage('site.edittag.tagExclusions.propmt')}")) {
                $('#exclusionFlag')[0].checked = true;
                creativeExclusionControl(true);
                return false;
            }
        }

        return true;
    }

    function showFields() {
        $('#_tagPricing, #_addButton, #_imprTackPixel, #_passback').show();
        $('#passbackTypeHTML_CODE, #passbackTypeHTML_URL, #passbackTypeJS_CODE').prop({disabled : false});


        if ($('input[name="passbackType"]:checked').val() == 'HTML_URL') {
            $('#passback').prop({disabled : false}).show();
        } else {
            $('#passbackHtml').prop({disabled : false}).show();
        }
    }

    function hideFields() {
        $('#passbackHtml, #passback, #passbackTypeHTML_CODE, #passbackTypeHTML_URL, #passbackTypeJS_CODE').prop({disabled : true});
        $('#_tagPricing, #_addButton, #_imprTackPixel, #_passback').hide();
    }


    function changeSizeType() {
        if ($('#sizeTypeId').val()== "") {
            $("#size").hide();
            $("#sizes").hide();
            processAllowExpansion('N');
            return;
        }
        
        $.ajax(
                {
                    type: 'POST',
                    url: 'changeSizeType.action',
                    dataType: 'html',
                    data: {'tag.sizeType.id' : $('#sizeTypeId').val(), 'site.id' : $('input[name="site.id"]').val(), 'site.account.id' : $('input[name="site.account.id"]').val()},
                    success : function(data) {
                        $("#size").hide();
                        $('#divSize').html("");
                        $("#sizes").hide();
                        $('#divSizes').html("");
                        if ($('#sizeTypeId option:selected').data('multisize')) {
                            $("#sizes").show();
                            $('#divSizes').html(data);
                        } else {
                            $("#size").show();
                            $('#divSize').html(data);
                        }
                        processAllowExpansion('N');
                    }
                }
        );
    }
    
    function processAllowExpansion(status) {
        if (status == 'N') {
            $("#allowExpandableCreativeField").hide();
            $("#allowExpandable").attr('checked', false);
        } else {
            $("#allowExpandableCreativeField").show();
            $("#allowExpandable").attr('checked', true);
        }
    }

    $().ready(function() {
        if (${allowInventoryEstimation} && ${inventoryEstimationFlag}) {
            hideFields();
        }

        if (!$("[name='pricings[0].siteRate.rate']").attr('value')) {
            $("[name='pricings[0].siteRate.rate']").attr('value', '0')
        }

        $('input[name=passbackType]').change(function() {
            var type = this.value;
            if(type == 'HTML_URL') {
                $('#passbackHtml').hide().children('textarea:eq(0)').prop({disabled : true});
                $('#passback').show().children('input:eq(0)').prop({disabled : false}).focus();
            } else {
                $('#passback').hide().children('input:eq(0)').prop({disabled : true});
                $('#passbackHtml').show().children('textarea:eq(0)').prop({disabled : false}).focus();
            }
        }).trigger('change');

        $('input[name=passbackType]:checked').change();

        $('input[type=radio][name=inventoryEstimationFlag]').change(function() {
            var isInventoryFlag = $('input[type=radio][name=inventoryEstimationFlag]:checked').val();
            if (isInventoryFlag == 'true') {
                hideFields();
            } else {
                showFields();
            }
        });

        creativeExclusionControl(${tagLevelExclusionFlag});
        
        <c:if test="${tag.sizeType != null && tag.sizeType.multiSize}">
        $("#sizes").show();
        </c:if>
        
        <c:if test="${tag.sizeType != null && tag.sizeType.singleSize}">
        $("#size").show();
        </c:if>
        
    });


    function updateLinkedRateType(elementName, selectedIndex) {
        var selectElement = $("select[name^='" + elementName.replace('ccgType', 'ccgRateType') + "']");
        var element = selectElement[0];
        if (selectedIndex == 2) {
            // display 'All', 'CPM', 'CPC', 'CPA'
            if (element.length == 1) {
                <s:iterator value="@com.foros.model.campaign.RateType@values()" var="ccgRateTypeEnum">
                element.options[element.length] = new Option('<fmt:message key="enum.RateType.${ccgRateTypeEnum.name}"/>', '${ccgRateTypeEnum}');
                </s:iterator>
            }
            selectElement.prop({disabled : false});
        } else {
            // display only 'All'
            if (element.length > 1) {
                <s:iterator value="@com.foros.model.campaign.RateType@values()" var="ccgRateTypeEnum">
                element.remove(1);
                </s:iterator>
            }
            selectElement.prop({disabled : true});
        }
    }

    function findPrevRevenueShare(namePrefix) {
        var key = $("select[name='" + namePrefix + ".country.countryCode']").val() +
                $("select[name='" + namePrefix + ".ccgType']").val() +
                $("select[name='" + namePrefix + ".ccgRateType']").val();
        return $('input[name="prevRevenueShare[\'' + key + '\']"]');
    }

    function checkAllRadios(jqRadio, val) {
        jqRadio.each(function(){
            if ($(this).val()   == val) {
                $(this).prop({"checked":true});
            }
        });
    }

    $().ready(function() {
        $("form#saveForm").submit(function() {
            $("form#saveForm [name^='pricings']")
                    .prop('disabled', $("input[type=radio][name=inventoryEstimationFlag]:checked").val()=="true");
        });
        <c:if test="${not ad:isInternal()}">
        $("input[name$='.siteRate.rate']").each(function() {
            if (this.name.indexOf("?") < 0) {
                var prefix = this.name.replace(/.siteRate.rate/, "");
                var select = $("select[name='" + prefix + ".siteRate.rateType']");
                if (findPrevRevenueShare(prefix).size() < 1) {
                    select.prop('disabled', true);
                }
            }
        });
        $("input[name$='.siteRate.ratePercent']").each(function() {
            if (this.name.indexOf("?") < 0) {
                this.disabled = true;
            }
        });
        </c:if>
        $("select[name$='.siteRate.rateType']").change(function() {
            var prefix = this.name.replace(/.siteRate.rateType/, "");
            var rate = $("input[name='" + prefix + ".siteRate.rate'],input[name='" + prefix + ".siteRate.ratePercent']");
            <c:choose>
            <c:when test="${ad:isInternal()}">
            rate.val('');
            </c:when>
            <c:otherwise>
            rate.val('').prop('disabled', false);
            if ($(this).val() == "RS") {
                var prevRevenueShare = findPrevRevenueShare(prefix);
                if (prevRevenueShare.size() > 0) {
                    rate.val(prevRevenueShare.val()).prop('disabled', true);
                }
            }
            </c:otherwise>
            </c:choose>
            if ($(this).val() == "RS") {
                rate.attr("name", prefix + ".siteRate.ratePercent");
            } else {
                rate.attr("name", prefix + ".siteRate.rate");
            }
        });
    });

    $().ready(function() {
        $('#sizeTypeId').change(changeSizeType);
        <c:if test= "${tag.id == null && empty selectedSizes}">
        changeSizeType();
        </c:if>
    });
    
</script>
<ui:externalLibrary libName="codemirror" />

<s:form action="%{#request.moduleName}/%{model.id != null?'update':'create'}" id="saveForm">
    <ui:pageHeadingByTitle/>

    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:fielderror><s:param value="'country'"/></s:fielderror>
        <s:fielderror><s:param value="'error'"/></s:fielderror>
        <s:fielderror><s:param value="'adservingMode'"/></s:fielderror>
        <s:fielderror><s:param value="'tagLevelCreativeExclusionError'"/></s:fielderror>
        <s:fielderror><s:param value="'options'"/></s:fielderror>
    </ui:errorsBlock>

    <s:hidden name="id"/>
    <s:hidden name="site.id"/>
    <s:hidden name="site.account.id"/>
    <s:hidden name="site.name"/>
    <s:hidden name="version"/>
    <s:hidden name="status"/>
    <s:hidden name="allowInventoryEstimation"/>
    <s:hidden name="currencyCode" />

    <c:set var="currencyCode" value="${ad:currencySymbol(currencyCode)}"/>
    <ui:section>
        <ui:fieldGroup>

            <ui:field labelKey="site.edittag.name" labelForId="name" required="true" errors="name">
                <s:textfield id="name" name="name" cssClass="middleLengthText" maxLength="100"/>
            </ui:field>

            <ui:field labelKey="site.edittag.sizeType" labelForId="sizeTypeId" required="true" errors="type">
                <s:set var="sizeTypeId" value="%{getTag().getSizeType().getId()}"/>
                <select name="sizeType.id" id="sizeTypeId" class="middleLengthText">
                    <s:iterator var="type" value="%{types}">
                        <s:if test="%{#type.id == #sizeTypeId}">
                            <option value="${type.id}" data-multisize="${type.allowMultiSize}" selected="selected"><c:out value="${ad:localizeName(type.localizableName)}"/></option>
                        </s:if>
                        <s:else>
                            <option value="${type.id}" data-multisize="${type.allowMultiSize}"><c:out value="${ad:localizeName(type.localizableName)}"/></option>
                        </s:else>
                    </s:iterator>
                </select>
            </ui:field>

            <ui:field labelKey="site.edittag.creativeSize" labelForId="sizeId" required="true" errors="sizes" id="size" cssClass="hide">
                <div id="divSize">
                    <c:if test="${tag.sizeType != null && tag.sizeType.singleSize}">
                        <jsp:include page="tag/size.jsp"></jsp:include>
                    </c:if>
                </div>
            </ui:field>
            
            <ui:field labelKey="site.edittag.creativeSizes" labelForId="sizeId" required="true" errors="sizes" id="sizes" cssClass="hide">
                <div id="divSizes">
                    <c:if test="${tag.sizeType != null && tag.sizeType.multiSize}">
                        <jsp:include page="tag/sizes.jsp"></jsp:include>
                    </c:if>
                </div>
            </ui:field>

            <ui:field labelKey="tags.allowExpandableCreative" labelForId="allowExpandable" id="allowExpandableCreativeField">
                <s:checkbox name="allowExpandable" id="allowExpandable"/>
            </ui:field>

            <c:if test="${allowInventoryEstimation}">
                <ui:field labelKey="site.edittag.adserving.mode" id="_adservingMode" cssClass="valignFix">
                    <s:radio cssClass="withInput" id="adservingOn" name="inventoryEstimationFlag" list="'false'" listValue="%{getText('site.edittag.adserving')}"/>
                    <s:radio cssClass="withInput" id="inventoryEstimationOn" name="inventoryEstimationFlag" list="'true'" listValue="%{getText('site.edittag.inventoryEstimation')}"/>
                </ui:field>
            </c:if>

            <c:if test="${walledGardenEnabled}">
                <ui:field labelKey="site.tag.wgSettings" errors="wgSettings" required="true">
                    <label class="withInput">
                        <s:checkbox name="marketplaceTypeTO.inWG"/><fmt:message key="WalledGarden.publisher.marketplace.WG"/>
                    </label>
                    <table class="fieldAndAccessories">
                        <tr>
                            <td class="withField">
                                <label class="withInput">
                                    <s:checkbox name="marketplaceTypeTO.inFOROS"/><fmt:message key="WalledGarden.publisher.marketplace.FOROS"/>
                                </label>
                            </td>
                            <td class="withTip">
                                <ui:hint>
                                    <fmt:message key="WalledGarden.publisher.marketplace.FOROS.tip"/>
                                </ui:hint>
                            </td>
                        </tr>
                    </table>
                </ui:field>
            </c:if>

            <ui:field cssClass="subsectionRow" />

            <ui:field labelKey="site.edittag.tagPricings" id="_tagPricing">
                <table id="pricingsTable" class="dataView" style="border:none;">
                    <thead>
                      <tr>
                        <th><fmt:message key="site.edittag.tagPricings.country"/></th>
                        <th><fmt:message key="creative.type"/></th>
                        <th><fmt:message key="ccg.rate.type"/></th>
                        <th width="1"><span class="textWithHint"><fmt:message key="site.edittag.tagPricings.rate"/><ui:hint><fmt:message key="site.edittag.tagPricings.cpm.toolTip"/></ui:hint></span></th>
                        <th class="withButton"></th>
                      </tr>
                    </thead>
                    <tbody>
                    <tr class="hide dynamicRow">
                        <td nowrap="true" class="field">
                            <table class="mandatoryContainer">
                                <tr>
                                    <td>
                                        <select name="pricings[?].country.countryCode" class="middleLengthText">
                                            <option value=""><fmt:message key="form.all"/></option>
                                            <c:forEach items="${countries}" var="country">
                                                <option value="${country.id}">
                                                    <ad:resolveGlobal resource="country" id="${country.id}"/></option>
                                            </c:forEach>
                                        </select>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td class="field">
                            <select name="pricings[?].ccgType" class="smallLengthText" onchange="javascript: updateLinkedRateType(this.name, this.selectedIndex);">
                                <option value=""><fmt:message key="form.all"/></option>
                                <s:iterator value="@com.foros.model.campaign.CCGType@values()" var="ccgTypeEnum">
                                    <option value="${ccgTypeEnum}">
                                        <fmt:message key="ccg.type.${ccgTypeEnum.pageExtension}"/>
                                    </option>
                                </s:iterator>
                            </select>
                        </td>
                        <td class="field">
                            <select name="pricings[?].ccgRateType" class="smallLengthText" disabled="disabled">
                                <option value=""><fmt:message key="form.all"/></option>
                            </select>
                        </td>
                        <td class="field">
                            <table class="grouping">
                                <tr>
                                    <td>
                                        <table class="mandatoryContainer">
                                            <tr>
                                                <td>
                                                    <input type="text" name="pricings[?].siteRate.rate" value="" class="smallLengthText" maxlength="13"/>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                    <td>
                                        <select name="pricings[?].siteRate.rateType"
                                                <c:if test="${not ad:isInternal()}">disabled="" </c:if>>
                                            <option value="CPM" selected>
                                                (${currencyCode}) <fmt:message key="enums.SiteRateType.CPM"/>
                                            </option>
                                            <option value="RS">(%) <fmt:message key="enums.SiteRateType.RS"/></option>
                                        </select>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td class="withButton">
                            <ui:button message="form.remove" onclick="UI.Util.Table.delRow($(this).parents('.dynamicRow')[0]);" type="button"/>
                        </td>
                    </tr>

                    <s:if test="!pricings.empty">
                        <s:bean name="java.util.HashSet" var="priceErrorsSet"></s:bean>
                        <s:iterator value="pricings" var="tagPricing" status="indexID">
                            <tr ${indexID.index==0?'':'class="dynamicRow"'}>
                                <s:if test="%{#indexID.index==0}">
                                    <td colspan="3" class="field">
                                        <ui:text textKey="site.edittag.tagPricings.country.defaultWorldwide"/>
                                    </td>
                                </s:if>
                                <s:else>
                                    <td>
                                        <table class="mandatoryContainer">
                                            <tr>
                                                <td>
                                                    <select name="pricings[${indexID.index}].country.countryCode"
                                                            class="middleLengthText">
                                                        <option value=""><fmt:message key="form.all"/></option>
                                                        <c:forEach items="${countries}" var="country">
                                                            <option value="${country.id}"
                                                                    <c:if test="${pricings[indexID.index].country.countryCode == country.id}">selected="selected"</c:if>>
                                                                <ad:resolveGlobal resource="country" id="${country.id}"/>
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                    <td>
                                        <select name="pricings[${indexID.index}].ccgType" class="smallLengthText" onchange="javascript: updateLinkedRateType(this.name, this.selectedIndex);">
                                            <option value=""
                                                    <c:if test="${pricings[indexID.index].ccgType eq null}">selected="selected"</c:if>>
                                                <fmt:message key="form.all"/>
                                            </option>
                                            <s:iterator value="@com.foros.model.campaign.CCGType@values()" var="ccgTypeEnum">
                                                <option value="${ccgTypeEnum}"
                                                        <c:if test="${pricings[indexID.index].ccgType eq ccgTypeEnum}">selected="selected"</c:if>>
                                                    <fmt:message key="ccg.type.${ccgTypeEnum.pageExtension}"/>
                                                </option>
                                            </s:iterator>
                                        </select>
                                    </td>
                                    <td>
                                        <select name="pricings[${indexID.index}].ccgRateType" class="smallLengthText"
                                                <c:if test="${pricings[indexID.index].ccgType.letter == 'T' || pricings[indexID.index].ccgType.letter == null}">
                                                    disabled="disabled"
                                                </c:if>>
                                            <option value="" <c:if test="${pricings[indexID.index].ccgRateType == null}">selected="selected"</c:if>>
                                                <fmt:message key="form.all"/>
                                            </option>
                                            <c:if test="${pricings[indexID.index].ccgType.letter == 'D'}">
                                                <s:iterator value="@com.foros.model.campaign.RateType@values()" var="ccgRateTypeEnum">
                                                    <option value="${ccgRateTypeEnum}" <c:if test="${pricings[indexID.index].ccgRateType eq ccgRateTypeEnum}">selected="selected"</c:if>>
                                                        <fmt:message key="enum.RateType.${ccgRateTypeEnum.name}"/>
                                                    </option>
                                                </s:iterator>
                                            </c:if>
                                        </select>
                                    </td>
                                </s:else>
                                <td class="field">
                                    <table class="grouping">
                                        <tr>
                                            <td>
                                                <table class="mandatoryContainer">
                                                    <tr>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${pricings[indexID.index].siteRate.rateType=='RS'}">
                                                                    <s:textfield name="pricings[%{#indexID.index}].siteRate.ratePercent"
                                                                                 cssClass="smallLengthText" maxlength="13"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <s:textfield name="pricings[%{#indexID.index}].siteRate.rate"
                                                                                 cssClass="smallLengthText" maxlength="13"/>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                            <td>
                                                <s:select name="pricings[%{#indexID.index}].siteRate.rateType"
                                                          list="@com.foros.model.site.SiteRateType@values()" listKey="name()"
                                                          listValue="''+'('+(name()=='RS'?'%':#attr.currencyCode)+') '+getText('enums.SiteRateType.'+name())"/>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                                <td class="withButton">
                                    <table class="fieldAndAccessories">
                                        <tr>
                                            <td class="withButton">
                                                <c:if test="${indexID.index!=0}">
                                                    <ui:button message="form.remove"
                                                               onclick="UI.Util.Table.delRow($(this).parents('.dynamicRow')[0]);"
                                                               type="button"/>
                                                </c:if>
                                            </td>
                                            <td class="withError">
                                                <s:fielderror><s:param value="'pricings[' + #indexID.index + '].unique'"/></s:fielderror>
                                                <s:fielderror><s:param value="'pricings[' + #indexID.index + '].rate'"/></s:fielderror>
                                                <s:fielderror><s:param value="'pricings[' + #indexID.index + '].rateType'"/></s:fielderror>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </s:iterator>
                    </s:if>
                    </tbody>
                </table>
                <s:iterator value="prevRevenueShare.keySet()" var="prevRevenueShareKey">
                    <s:hidden name="prevRevenueShare['%{prevRevenueShareKey}']"/>
                </s:iterator>
            </ui:field>

            <ui:field id="_addButton">
                <ui:button message="form.add" onclick="UI.Util.Table.addRow('pricingsTable', true);" id="addRowBtn" type="button"/>
            </ui:field>

            <ui:field cssClass="subsectionRow" />

            <ui:field labelKey="site.edittag.passback" id="_passback" cssClass="valignFix">
                <table class="formFields">
                    <tr>
                        <td class="field nomargin" style="vertical-align: top; white-space: nowrap;">
                            <s:radio name="passbackType" id="passbackType"
                                     list="@com.foros.model.site.PassbackType@values()"
                                     listKey="name()" listValue="getText('tag.passbackType.' + name())"/>
                        </td>
                        <td class="field" style="vertical-align: top">
                            <table class="fieldAndAccessories">
                                <tr>
                                    <td class="withField">
                                        <div class="hide" id="passback">
                                            <s:textfield name="passback" cssClass="middleLengthText" maxlength="2000"/>
                                        </div>
                                        <div class="hide" id="passbackHtml">
                                            <s:textarea name="passbackHtml" cssClass="html_highlight"/>
                                        </div>
                                    </td>
                                    <td class="withError">
                                        <s:fielderror><s:param value="'passback'"/></s:fielderror>
                                        <s:fielderror><s:param value="'passbackHtml'"/></s:fielderror>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>

            </ui:field>

            <ui:field cssClass="subsectionRow">
            </ui:field>

            <c:choose>
                <c:when test="${id == null || contentCategories.size() == 0 || ad:isPermitted0('PublisherEntity.advanced')}">
                    <ui:field labelKey="site.tag.category" required="true">
                    <table class="grouping">
                        <tr>
                            <s:fielderror><s:param value="'contentCategories'"/></s:fielderror>
                        </tr>
                        <tr>
                            <td>
                                <ui:optiontransfer
                                        name="selectedContentCategories"
                                        cssClass="smallLengthText2" size="9"
                                        id="category1"
                                        selId="category2"
                                        listKey="id"
                                        listValue="name"
                                        list="${availableContentCategories}"
                                        selList="${tagContentCategories}"
                                        selListKey="id"
                                        selListValue="name"
                                        titleKey="site.tag.categories.available"
                                        selTitleKey="site.tag.categories.selected"
                                        saveSorting="true"/>
                            </td>
                        </tr>
                        <tr>
                            <td><label><fmt:message key="category.note"/></label></td>
                        </tr>
                    </table>
                    </ui:field>
                </c:when>
                <c:otherwise>
                    <c:set var="contentCategoriesString">
                        <ad:commaWriter items="${tagContentCategories}" label="name" escape="false"/>
                    </c:set>
                    <ui:simpleField labelKey="site.tag.category" value="${contentCategoriesString}"/>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${exclusionFlagAccountLevel}">
                    <ui:field labelKey="site.edittag.tagExclusions" cssClass="valignFix">
                        <label class="withInput">
                            <s:checkbox name="tagLevelExclusionFlag" onclick="creativeExclusionControl(this.checked);" id="exclusionFlag"/><fmt:message key="site.edittag.tagExclusions.setVisualCategory"/>
                        </label>
                    </ui:field>
                    <ui:field><label><fmt:message key="site.edittag.tagExclusions.important"/></label></ui:field>

                    <ui:field>
                        <table class="dataView" id="_categoryExclusions">
                            <thead>
                            <tr>
                                <th>
                                    <s:text name="site.visualCategory"/>
                                </th>
                                <th>
                                    <s:text name="site.accept"/>
                                    <ui:button message="form.all" type="button"
                                               onclick="checkAllRadios($('#_categoryExclusions').find('input[type=radio]'), 'A')"/>
                                </th>
                                <th>
                                    <s:text name="site.reject"/>
                                    <ui:button message="form.all" type="button"
                                               onclick="checkAllRadios($('#_categoryExclusions').find('input[type=radio]'), 'R')"/>
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            <s:iterator value="availableCreativeCategoryExclusions" var="visualCategory" status="row">
                                <tr>
                                    <td>
                                        <input type="hidden" name="categoryExclusions[${row.index}].id" value="${visualCategory.creativeCategory.id}"/>
                                        <label><c:out
                                                value="${ad:localizeName(visualCategory.creativeCategory.name)}"/></label>
                                    </td>
                                    <td>
                                        <input type="radio" name="categoryExclusions[${row.index}].approval"
                                               value="A" ${visualCategory.approval =='ACCEPT' ? 'checked="checked"' : ''}/>
                                    </td>
                                    <td>
                                        <input type="radio" name="categoryExclusions[${row.index}].approval"
                                               value="R" ${visualCategory.approval =='REJECT' ? 'checked="checked"' : ''}/>
                                    </td>
                                </tr>
                            </s:iterator>
                            </tbody>
                        </table>
                    </ui:field>
                </c:when>
            </c:choose>
        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">

      <ui:button message="form.save" onclick="if (!confirmChoice()){return false};" type="submit"/>

      <c:choose>
        <c:when test="${id == null || id == ''}">
          <ui:button message="form.cancel" onclick="location='${_context}/site/view.action?id=${site.id}';" type="button"/>
        </c:when>
        <c:otherwise>
          <ui:button message="form.cancel" onclick="location='view.action?id=${id}';" type="button"/>
        </c:otherwise>
      </c:choose>
    </div>
</s:form>
