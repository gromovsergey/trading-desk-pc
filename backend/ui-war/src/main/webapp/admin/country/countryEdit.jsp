<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script type="text/javascript">

    $(function() {
        $("#vatEnabled").change(function() {
            if(!$(this).is(":checked")){
                $('#defaultVATRateView').prop({disabled : true});
                $('#defaultVATRateField').addClass('hide');
                $('#vatNumberInputEnabled').prop({disabled : true});
                $('#vatNumberInputEnabledField').addClass('hide');

            }else{
                $('#defaultVATRateView').prop({disabled : false});
                $('#defaultVATRateField').removeClass('hide');
                $('#vatNumberInputEnabled').prop({disabled : false});
                $('#vatNumberInputEnabledField').removeClass('hide');
            }
        });
        UI.Util.assignSubmitForFields();
    });

    function moveRowDown(jqRow){
        moveRow(jqRow, true)
    }

    function moveRowUp(jqRow){
        moveRow(jqRow)
    }

    function moveRow(jqRow, down){
        var parentContainer = jqRow.parent();
        var jqRowsSet = parentContainer.children('tr');
        var indexOfThis = jqRowsSet.index(jqRow);
        var totalNumberOfRows = jqRowsSet.length;

        if(down){
            if(indexOfThis == totalNumberOfRows - 1) return false;
            var neighborRow = jqRowsSet.eq(indexOfThis + 1);
            jqRow.insertAfter(neighborRow);
        }else{
            if(indexOfThis == 0) return false;
            var neighborRow = jqRowsSet.eq(indexOfThis - 1);
            jqRow.insertBefore(neighborRow);
        }
        changeIdxReferences(jqRow, neighborRow)

        if(totalNumberOfRows > 2){
            $('tr:not(:first-child) .move.up')
                .add('tr:not(:last-child) .move.down')
                .show();
        }

        $('tr:first-child .move.up, tr:last-child .move.down').hide();
        numberRow();
    }

    function changeIdxReferences(jqCurrRow, jqNeighborRow){
        var addressFieldsListElemInCurrRow = $('input[name^=addressFieldsList]:eq(0)', jqCurrRow);
        var addressFieldsListElemInNeighborRow = $('input[name^=addressFieldsList]:eq(0)', jqNeighborRow);
        if(!addressFieldsListElemInCurrRow.length || !addressFieldsListElemInNeighborRow.length) return false;
        var idxReferenceInCurrRow = addressFieldsListElemInCurrRow.attr('name').match(/\[(\d+)\]/)[1];
        var idxReferenceInNeighborRow = addressFieldsListElemInNeighborRow.attr('name').match(/\[(\d+)\]/)[1];
        renameAttrsOfFields(jqCurrRow, idxReferenceInCurrRow, idxReferenceInNeighborRow);
        renameAttrsOfFields(jqNeighborRow, idxReferenceInNeighborRow, idxReferenceInCurrRow);
    }

    function renameAttrsOfFields(jqRow, currIdx, newIdx){
        $('input[name^=addressFieldsList]', jqRow).each(function(){
            this.name = this.name.replace(/\[\d+\]/, '[' + newIdx + ']');
        });
        $('#afFlag_' + currIdx, jqRow).attr({id : 'afFlag_' + newIdx});
    }

    function setAFEnabled(chBox){
        changeFlagValue(chBox, 1);
    }

    function setAFMandatory(chBox){
        changeFlagValue(chBox, 2);
    }

    function changeFlagValue(chBox, flag){
        var chBoxParentRow = $(chBox).parents('tr:eq(0)');
        var flagsElement = $(':hidden[id^=afFlag_]', chBoxParentRow)[0];
        flagsElement.value = chBox.checked ? (flagsElement.value | flag) : (flagsElement.value & ~flag);
    }

    function numberRow() {
        var j = 0;
        $('#addressFieldsTable input').each(function(){
            if(this.name.match('orderNumber$'))
                this.value = j++;
        });
    }

    $().ready(function(){
        $('.chBoxMandatory').click(function(){
            var curr = $(this);
            var parentRow = curr.parents('tr:eq(0)');
            var correspChBoxEnable = $('.chBoxEnable', parentRow);
            if(curr.prop('checked')){
                correspChBoxEnable.prop({checked : true});
                $('.afFlagField', parentRow).attr({value:3});
            }
        });

        $('.chBoxEnable').click(function(){
            var curr = $(this);
            var parentRow = curr.parents('tr:eq(0)');
            var correspChBoxMandatory = $('.chBoxMandatory', parentRow);

            if(!curr.prop('checked')){
                correspChBoxMandatory.prop({checked : false});
                $('.afFlagField', parentRow).attr({value:0});
            }
        });

        $('.move.down').click(function(){
            moveRowDown($(this).parents('tr:eq(0)'));
            return false;
        });

        $('.move.up').click(function(){
            moveRowUp($(this).parents('tr:eq(0)'));
            return false;
        });
    });

    function doSave() {
        $('#saveCountry')[0].action = $('#saveCountry')[0].action + '?id=' + $('[name=id]').val() + '&PWSToken=' + $('[name=PWSToken]').val();
        $('#saveCountry').submit();
    }

</script>

<s:form id="saveCountry" action="admin/Country/save" method="post" enctype="multipart/form-data">
    <s:hidden name="version"/>
    <s:hidden name="id"/>

    <ui:errorsBlock>
        <s:actionerror/>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </ui:errorsBlock>

<ui:section titleKey="form.main" errors="uniqueNameError">
    <ui:fieldGroup>

        <ui:field labelKey="Country.countryCode">
            <c:set var="textVal">
                <s:property value="countryCode"/>
            </c:set>
            <ui:text text="${pageScope.textVal}"/>
            <s:hidden name="countryCode"/>
        </ui:field>

        <ui:field labelKey="Country.name">
            <c:set var="textVal">
                <s:text name="global.country.%{countryCode}.name"/>
            </c:set>
            <ui:text text="${pageScope.textVal}" id="name"/>
        </ui:field>
        <ui:field labelKey="Country.currency" labelForId="currency.id" required="true" errors="currency">
          <s:select name="currency.id" id="currency.id" cssClass="middleLengthText" headerValue="%{getText('form.select.pleaseSelect')}"
               headerKey="" list="availableCurrencies" listKey="id" listValue="%{getText('global.currency.' + name + '.name')}" value="currency.id" >
          </s:select>
        </ui:field>

        <ui:field labelKey="Country.timezone" labelForId="timezone.id" required="true" errors="timezone">
          <s:select name="timezone.id" id="timezone.id" cssClass="middleLengthText" headerValue="%{getText('form.select.pleaseSelect')}"
              headerKey="" list="availableTimeZones" listKey="id" listValue="name   " value="timezone.id" >
          </s:select>
        </ui:field>

        <ui:field labelKey="Country.language" labelForId="language">
            <s:select name="language" id="language" cssClass="middleLengthText" list="availableLanguages" listKey="isoCode"
                listValue="getText('enums.Language.'+name())" value="language" >
            </s:select>
        </ui:field>

        <ui:field labelKey="Country.defaultPaymentTerms" labelForId="defaultPaymentTerms" required="true" errors="defaultPaymentTerms">
            <s:textfield name="defaultPaymentTerms" id="defaultPaymentTerms" cssClass="middleLengthText" maxLength="2"/>
        </ui:field>

        <ui:field labelKey="Country.adservingDomain" labelForId="adservingDomain" errors="adservingDomain">
            <s:textfield name="adservingDomain" id="adservingDomain" cssClass="middleLengthText" maxlength="2000"/>
        </ui:field>

        <ui:field labelKey="Country.discoverDomain" labelForId="discoverDomain" errors="discoverDomain">
            <s:textfield name="discoverDomain" id="discoverDomain" cssClass="middleLengthText" maxlength="2000"/>
        </ui:field>

        <ui:field labelKey="Country.staticDomain" labelForId="staticDomain" errors="staticDomain">
            <s:textfield name="staticDomain" id="staticDomain" cssClass="middleLengthText" maxlength="2000"/>
        </ui:field>

        <ui:field labelKey="Country.adTagDomain" labelForId="adTagDomain" errors="adTagDomain">
            <s:textfield name="adTagDomain" id="adTagDomain" cssClass="middleLengthText" maxlength="2000"/>
        </ui:field>

        <ui:field labelKey="Country.conversionTagDomain" labelForId="conversionTagDomain" required="true" errors="conversionTagDomain">
            <s:textfield name="conversionTagDomain" id="conversionTagDomain" cssClass="middleLengthText" maxlength="2000"/>
        </ui:field>

        <ui:field labelKey="Country.adFooterURL" labelForId="adFooterURL" required="true" errors="adFooterURL" tipKey="Country.adFooterURL.tip">
            <s:textfield name="adFooterURL" id="adFooterURL" cssClass="middleLengthText" maxlength="2000"/>
        </ui:field>

        <ui:field labelKey="Country.highChannelThreshold" labelForId="highChannelThreshold" errors="highChannelThreshold" required="true" tipKey="Country.highChannelThreshold.tip">
            <s:textfield name="highChannelThreshold" id="highChannelThreshold" cssClass="middleLengthText" maxLength="13"/>
        </ui:field>
        <ui:field labelKey="Country.lowChannelThreshold" labelForId="lowChannelThreshold" errors="lowChannelThreshold" required="true" tipKey="Country.lowChannelThreshold.tip">
            <s:textfield name="lowChannelThreshold" id="lowChannelThreshold" cssClass="middleLengthText" maxLength="13"/>
        </ui:field>

        <ui:field labelKey="Country.minUrlTriggerThreshold" labelForId="minUrlTriggerThreshold" errors="minUrlTriggerThreshold" required="true" tipKey="Country.minUrlTriggerThreshold.tip">
            <s:textfield name="minUrlTriggerThreshold" id="minUrlTriggerThreshold" cssClass="middleLengthText" maxLength="13"/>
        </ui:field>

        <ui:field labelKey="Country.maxUrlTriggerShare.label" labelForId="maxUrlTriggerShare" errors="maxUrlTriggerShareView" required="true" tipKey="Country.maxUrlTriggerShare.tip">
            <s:textfield name="maxUrlTriggerShareView" id="maxUrlTriggerShare" cssClass="middleLengthText" maxLength="13"/>
        </ui:field>

        <ui:field labelKey="Country.minRequiredTagVisibility.label" labelForId="minRequiredTagVisibility" required="true" errors="minRequiredTagVisibility" tipKey="Country.minRequiredTagVisibility.tip">
            <s:textfield name="minRequiredTagVisibility" id="minRequiredTagVisibility" cssClass="middleLengthText" maxLength="3"/>
        </ui:field>

        <ui:field labelKey="Country.sortOrder" labelForId="sortOrder" errors="sortOrder">
            <s:textfield name="sortOrder" id="sortOrder" cssClass="middleLengthText" maxLength="10"/>
        </ui:field>
        <ui:field labelKey="Country.VAT" labelForId="vatEnabled" errors="vatEnabled">
            <s:checkbox name="vatEnabled" id="vatEnabled"/>
        </ui:field>
        <ui:field labelKey="Country.defaultVATRate" labelForId="defaultVATRateView" errors="defaultVATRateView" id="defaultVATRateField" required="true" cssClass="${entity.vatEnabled ? '' : 'hide'}">
            <s:textfield name="defaultVATRateView" id="defaultVATRateView" cssClass="middleLengthText" maxLength="10"/>
        </ui:field>
        <ui:field labelKey="Country.VATNumberInput" labelForId="vatNumberInputEnabled" errors="vatNumberInputEnabled" id="vatNumberInputEnabledField" cssClass="${entity.vatEnabled ? '' : 'hide'}">
            <s:checkbox name="vatNumberInputEnabled" id="vatNumberInputEnabled"/>
        </ui:field>
        <ui:field labelKey="Country.defaultAgencyCommission" labelForId="defaultAgencyCommissionView" required="true" errors="defaultAgencyCommissionView">
            <s:textfield name="defaultAgencyCommissionView" id="defaultAgencyCommissionView" cssClass="middleLengthText" maxLength="10"/>
        </ui:field>
        <ui:field labelKey="Country.invoiceRptFile" labelForId="invoiceRptFile" errors="invoiceRptFile">
            <s:if test="%{invoiceRptFile != null}">
                <ui:button message="birtReports.download" href="downloadInvoiceRpt.action?id=${id}"/>
            </s:if>
            <s:file name="invoiceRptFile" id="invoiceRptFile" cssClass="middleLengthText"/>
        </ui:field>

    </ui:fieldGroup>
</ui:section>

<h2><fmt:message key="country.address.fields"/></h2>

<table class="dataView" id="addressFieldsTable">
    <thead>
        <th>
            <s:text name="country.address.fields.table.enable"/>
        </th>
        <th>
            <s:text name="country.address.fields.table.OFFieldName"/>
        </th>
        <th>
            <s:text name="country.address.fields.table.order"/>
        </th>
        <th>
            <s:text name="country.address.fields.table.mandatory"/>
        </th>
        <th>
            <s:text name="country.address.fields.table.englishName"/>
        </th>
        <th>
            <s:text name="country.address.fields.table.resourceName"/>
        </th>
    </thead>
    <tbody>
        <c:set var="addressFieldsCount" value="${fn:length(addressFieldsList)}" />
        <s:iterator value="addressFieldsList" var="field" status="iStatus">
                <tr>
                    <td class="radio">
                        <input type="hidden" name="addressFieldsList[${iStatus.index}].OFFieldName" value="${field.OFFieldName}" />
                        <input type="hidden" name="addressFieldsList[${iStatus.index}].flags" value="${field.flags}" id="afFlag_${iStatus.index}" class="afFlagField" />
                        <input type="hidden" name="addressFieldsList[${iStatus.index}].orderNumber" value="${field.orderNumber}"/>
                        <input type="hidden" name="addressFieldsList[${iStatus.index}].id" value="${field.id}"/>
                        <c:choose>
                            <c:when test="${field.OFFieldName == 'Country' || field.OFFieldName == 'Line1'}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <input type="checkbox" class="chBoxEnable" ${field.enabled ? 'checked="checked"' : ''}" onchange="setAFEnabled(this)"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        ${field.OFFieldName}
                    </td>
                    <td>
                        <div class="moving twoButtons">
                            <a href="#" class="move down" style="display:${ (iStatus.index < addressFieldsCount-1) ? 'block' : 'none'};"></a>
                            <a href="#" class="move up" style="display:${ (iStatus.index > 0) ? 'block' : 'none'};"></a>
                        </div>
                    </td>
                <c:choose>
                <c:when test="${field.OFFieldName == 'Country'}">
                    <td class="radio">N/A</td>
                    <td class="radio">N/A</td>
                    <td class="radio">N/A</td>
                </c:when>
                <c:otherwise>
                    <td class="radio">
                        <c:choose>
                        <c:when test="${field.OFFieldName == 'Line1'}">
                            <input type="checkbox" class="chBoxMandatory" checked="checked" disabled="disabled"/>
                        </c:when>
                        <c:otherwise>
                            <input type="checkbox" class="chBoxMandatory" ${field.mandatory ? 'checked="checked"' : ''} onchange="setAFMandatory(this)"/>
                        </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <table class="fieldAndAccessories">
                            <tr>
                                <td class="withField mandatory">
                                    <input type="text" class="middleLengthText" name="addressFieldsList[${iStatus.index}].name.defaultName" value="${field.name.defaultName}" maxlength="100"/>
                                </td>
                                <td class="withError">
                                    <s:fielderror><s:param value="'addressFieldsList[' + #iStatus.index + '].name.defaultName'"/></s:fielderror>
                                </td>
                            </tr>
                        </table>
                    </td>
                    <td>
                        <table class="fieldAndAccessories">
                            <tr>
                                <td class="withField">
                                    <input type="text" class="middleLengthText" name="addressFieldsList[${iStatus.index}].name.resourceKey" value="${field.name.resourceKey}" maxlength="100"/>
                                </td>
                                <td class="withError">
                                    <s:fielderror><s:param value="'addressFieldsList[' + #iStatus.index + '].name.resourceKey'"/></s:fielderror>
                                </td>
                            </tr>
                        </table>
                    </td>
                </c:otherwise>
                </c:choose>
                </tr>
            </s:iterator>
    </tbody>
</table>

<ui:header>
    <h2><fmt:message key="Country.sitecontent.categories"/></h2>
</ui:header>

<div class="wrapper">
    <table class="grouping fieldsets">
        <tr>
            <td class="singleFieldset">
                <ui:section titleKey="Country.site.category">
                    <ui:fieldGroup id="siteCategoryTable">
                        <%-- To display dynamic site categories values--%>
                        <ui:field cssClass="hide dynamicRow">
                            <table class="fieldAndAccessories">
                                <tr>
                                    <td class="withField mandatory">
                                        <input type="text" class="middleLengthText" name="siteCategoryTOList[?].name" value="" maxlength="200"/>
                                    </td>
                                    <td class="withButton">
                                        <ui:button message="form.delete" onclick="UI.Util.Table.delRow($(this).parents('.dynamicRow')[0]);" type="button" />
                                    </td>
                                </tr>
                            </table>
                        </ui:field>
                        <%-- End of displaying dynamic site categories values--%>
                        
                        <%-- Displaying stored site categories values--%>
                        <s:iterator value="siteCategoryTOList" var="siteCatField" status="iStatus">
                            <ui:field cssClass="dynamicRow">
                                <table class="fieldAndAccessories">
                                    <tr>
                                        <td class="withField mandatory">
                                            <input type="text" class="middleLengthText" name="siteCategoryTOList[${iStatus.index}].name" value="${siteCatField.name}" maxlength="200"/>
                                            <s:hidden name="siteCategoryTOList[%{#iStatus.index}].id" value="%{#siteCatField.id}"/>
                                            <s:hidden name="siteCategoryTOList[%{#iStatus.index}].version" value="%{#siteCatField.version}"/>
                                            <s:hidden name="siteCategoryTOList[%{#iStatus.index}].dependencyExists" value="%{#siteCatField.dependencyExists}"/>
                                        </td>
                                        <td class="withButton">
                                            <s:if test="%{!#siteCatField.dependencyExists}"><ui:button message="form.delete" onclick="UI.Util.Table.delRow($(this).parents('.dynamicRow')[0]);" type="button"/></s:if>
                                        </td>
                                        <td class="withError">
                                            <s:fielderror><s:param value="'siteCategory[' + #iStatus.index + '].name'"/></s:fielderror>
                                        </td>
                                    </tr>
                                </table>
                            </ui:field>
                        </s:iterator>
                        <%-- End of displaying stored site categories values--%>
                        
                    </ui:fieldGroup>
                    
                    <ui:fieldGroup>
                        <ui:field>
                            <ui:button message="Country.addnew.site.category" onclick="UI.Util.Table.addRow('siteCategoryTable');" id="siteAddRowBtn" type="button"/>
                        </ui:field>
                    </ui:fieldGroup>
                </ui:section>
            </td>
            <td class="singleFieldset">
                <ui:section titleKey="Country.content.category">
                    <ui:fieldGroup id="contentCategoryTable">
                        <%-- To display dynamic content categories values--%>
                        <ui:field cssClass="hide dynamicRow">
                            <table class="fieldAndAccessories">
                                <tr>
                                    <td class="withField mandatory">
                                        <input type="text" class="middleLengthText" name="contentCategoryTOList[?].name" maxlength="200"/>
                                    </td>
                                    <td class="withButton">
                                        <ui:button message="form.delete" onclick="UI.Util.Table.delRow($(this).parents('.dynamicRow')[0]);" type="button" />
                                    </td>
                                </tr>
                            </table>
                        </ui:field>
                        <%-- End of displaying dynamic content categories values--%>
                        
                        <%-- Displaying stored content categories values--%>
                        <s:iterator value="contentCategoryTOList" var="contentCatField" status="jStatus">
                            <ui:field cssClass="dynamicRow">
                                <table class="fieldAndAccessories">
                                    <tr>
                                        <td class="withField mandatory">
                                            <input type="text" class="middleLengthText" name="contentCategoryTOList[${jStatus.index}].name" value="${contentCatField.name}" maxlength="200"/>
                                            <s:hidden name="contentCategoryTOList[%{#jStatus.index}].id" value="%{#contentCatField.id}"/>
                                            <s:hidden name="contentCategoryTOList[%{#jStatus.index}].version" value="%{#contentCatField.version}"/>
                                            <s:hidden name="contentCategoryTOList[%{#jStatus.index}].dependencyExists" value="%{#contentCatField.dependencyExists}"/>
                                        </td>
                                        <td class="withButton">
                                            <s:if test="%{!#contentCatField.dependencyExists}"><ui:button message="form.delete" onclick="UI.Util.Table.delRow($(this).parents('.dynamicRow')[0]);" type="button"/></s:if>
                                        </td>
                                        <td class="withError">
                                            <s:fielderror><s:param value="'contentCategory[' + #jStatus.index + '].name'"/></s:fielderror>
                                        </td>
                                    </tr>
                                </table>
                            </ui:field>
                        </s:iterator>
                        <%-- End of displaying stored site categories values--%>
                        
                    </ui:fieldGroup>
                    
                    <ui:fieldGroup>
                        <ui:field>
                            <ui:button message="Country.addnew.content.category" onclick="UI.Util.Table.addRow('contentCategoryTable');" id="contentAddRowBtn" type="button"/>
                        </ui:field>
                    </ui:fieldGroup>
                </ui:section>
            </td>
        </tr>
    </table>
</div>

<div class="wrapper">
    <ui:button message="form.save" onclick="doSave();" type="button" />
    <ui:button message="form.cancel" onclick="location='view.action?id=${id}';" type="button" />
</div>

</s:form>
