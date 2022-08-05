<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="java.util.Collections" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
    function insertOptionAfterIndex(select, option, index) {
        try {
            var o = select.options[index];
            select.add(option, o); // standards compliant; doesn't work in IE
        } catch(ex) {
            select.add(option, index); // IE only
        }
    }

    function handleNoneAgency() {
        var available = $('#AvailableOutput');
        var selected = $('#SelectedOutput');
        var availableAgency = available.find('> option[value="report\\.output\\.field\\.agency"]');
        var selectedAgency = selected.find('> option[value="report\\.output\\.field\\.agency"]');

        if (isAgency()) {
            if (availableAgency.length == 0 && selectedAgency.length == 0) {
                var agency = new Option($.localize('report.output.field.agency'), 'report.output.field.agency');
                insertOptionAfterIndex(available[0], agency, 0);
            }
        } else {
            availableAgency.remove();
            selectedAgency.remove();
        }
    }

    function prepareAdvertisers() {
        var agencyId = $('#agencyId').val();

        if (agencyId != '') {
            UI.Data.Options.get('accountsFilter', 'advertiserId', {
                agencyId: agencyId,
                countryCode: $('#countryCode').val(),
                accountRole: 'ADVERTISER'
            }, ['form.all']
            );

            if(agencyId != '-1') {
                prepareCampaigns();
            } else {
                UI.Data.Options.replaceWith('campaignId', ['form.all']);
            }
        } else {
            UI.Data.Options.replaceWith('advertiserId', ['form.all']);
        }
    }

    function prepareAgencies() {
        UI.Data.Options.get('accountsFilter', 'agencyId', {
                    countryCode: $('#countryCode').val(),
                    accountRole: 'AGENCY'
                }, ['form.all',['form.select.none',-1]]
        );
        prepareAdvertisers();
    }

    function prepareIsps() {
        UI.Data.Options.get('accountsFilter', 'ispId', {
                    countryCode: $('#countryCode').val(),
                    accountRole: 'ISP'
                }, ['form.all']
        );
        $('#ispId').change();
    }

    function preparePublishers() {
        UI.Data.Options.get('accountsFilter', 'publisherId', {
                    countryCode: $('#countryCode').val(),
                    accountRole: 'PUBLISHER'
                }, ['form.all']
        );
        $('#publisherId').change();
    }

    function prepareCampaigns() {
        if($('#advertiserId').val() ) {
            UI.Data.Options.get('campaigns', 'campaignId', {
                accountPair: $('#advertiserId').val(), concatResultForValue: false
            }, ['form.all']);
        } else {
            UI.Data.Options.replaceWith('campaignId', ['form.all']);
        }
    }

    function reportSubmit(form) {
        if(!form.outputColumns.length) {
            alert($.localize("report.invalid.output.columns"));
            return false;
        }

        if(!form.metricsColumns.length) {
            alert($.localize("report.invalid.metrics.columns"));
            return false;
        }
    }

    function isAgency() {
        return $('#agencyId').val() != '-1';
    }

    $().ready(function() {

        $('#countryCode').change(function() {
            prepareAgencies();
            prepareIsps();
            preparePublishers();
            $('#advertiserId').change();
        });

        $('#agencyId').change(function() {
            handleNoneAgency();
            if(this.value) {
                prepareAdvertisers();
            } else {
                UI.Data.Options.replaceWith('advertiserId, campaignId', ['form.all']);
            }
        });

        $('#advertiserId').change(prepareCampaigns);

        $('#ispId').change(function() {
            if(this.value) {
                UI.Data.Options.get('colocations', 'colocationId', {ispPair:$(this).val(), concatResultForValue: false}, ['form.all']);
            } else {
                UI.Data.Options.replaceWith('colocationId', ['form.all']);
            }
        });

        $('#publisherId').change(function() {
            if(this.value) {
                UI.Data.Options.get('sites', 'siteId', {publisherPair:$(this).val(), concatResultForValue: false}, ['form.all']);
            } else {
                UI.Data.Options.replaceWith('siteId', ['form.all']);
            }
        });

        $('#reportForm').submit(function(){
            return reportSubmit(this);
        });

        $('form :input[type=text]').unbind('keypress') // ":input[type=text]" it is fixed jquery bug (http://bugs.jquery.com/ticket/10570)
            .keypress(function(e){
                if (e.which == 13) {
                    $('#reportForm')[0].submit();
                }
            });

        handleNoneAgency();
    });
</script>

<c:if test="${not empty taskTitle}">
    <ui:pageHeadingByTitle/>
</c:if>

<c:set var="deletedStatus" value="D"/>

<form id="reportForm" action="run.action" method="post" target="_blank">
    <ui:section titleKey="form.filter">
        <ui:fieldGroup>
            <ui:field labelKey="report.input.field.dateRange">
                <ui:daterange
                        fromDateFieldName="dateRange.begin"
                        toDateFieldName="dateRange.end"
                        options="Y T WTD MTD QTD YTD LW LM LQ LY R"
                        fastChangeId="Y"
                        currentPos="1"
                        maxDate="+1d"
                        validateRange="true"/>
            </ui:field>

            <ui:field labelForId="countryCode" labelKey="report.input.field.publisherCountry">
                <c:choose>
                    <c:when test="${countries.size()==1}">
                        <c:forEach items="${countries}" var="country">
                            <ad:resolveGlobal resource="country" id="${country.id}"/>
                            <input type="hidden" name="countryCode" id="countryCode" value="${country.id}"
                                   data-timezone="${country.timezone.name}"/>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <select name="countryCode" id="countryCode" class="middleLengthText">
                            <c:if test="${empty defaultCountryCode}">
                                <option value=""><fmt:message key="form.all"/></option>
                            </c:if>
                            <c:forEach items="${countries}" var="country">
                                <option value="${country.id}" ${country.id == defaultCountryCode ? "selected" : ""}
                                        data-timezone="${country.timezone.name}">
                                    <ad:resolveGlobal resource="country" id="${country.id}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </c:otherwise>
                </c:choose>
            </ui:field>

            <ui:field labelForId="agencyId" labelKey="report.input.field.agency">
                <select id="agencyId" name="agencyId" class="middleLengthText">
                    <option value=""><fmt:message key="form.all"/></option>
                    <option value="-1"><fmt:message key="form.select.none"/></option>
                    <c:forEach items="${accounts}" var="account">
                         <option value="${account.id}"><c:out value="${account.name}"/></option>
                    </c:forEach>
                </select>
            </ui:field>

            <ui:field id="AdvertiserFilter" labelForId="advertiserId" labelKey="report.input.field.advertiser">
                <select id="advertiserId" name="advertiserId" class="middleLengthText">
                    <option value=""><fmt:message   key="form.all"/></option>
                </select>
            </ui:field>

            <ui:field labelForId="campaignId" labelKey="report.input.field.campaign">
                <select id="campaignId" name="campaignId" class="middleLengthText">
                    <option value=""><fmt:message   key="form.all"/></option>
                </select>
            </ui:field>

            <ui:field labelForId="campaignCreativeId" labelKey="report.input.field.CCID">
                <input class="middleLengthText" type="text" name="campaignCreativeId" id="campaignCreativeId" maxlength="10"><c:if test="${campaignCreativeId != null}">${campaignCreativeId}</c:if></input>
            </ui:field>

            <ui:field labelForId="ispId" labelKey="report.input.field.ISP">
                <select id="ispId" name="ispId" class="middleLengthText">
                    <option value=""><fmt:message  key="form.all"/></option>
                    <c:forEach items="${isps}" var="anIsp">
                        <option value="${anIsp.id}"><c:out value="${anIsp.name}"/></option>
                    </c:forEach>
                </select>
            </ui:field>

            <ui:field labelForId="colocationId" labelKey="report.input.field.colocation">
                <select id="colocationId" name="colocationId" class="middleLengthText">
                    <option value=""><fmt:message  key="form.all"/></option>
                </select>
            </ui:field>

            <ui:field labelForId="publisherId" labelKey="report.input.field.publisher">
                <select id="publisherId" name="publisherId" class="middleLengthText">
                    <option value=""><fmt:message  key="form.all"/></option>
                    <c:forEach items="${publishers}" var="aPublisher">
                        <option value="${aPublisher.id}"><c:out value="${aPublisher.name}"/></option>
                    </c:forEach>
                </select>
            </ui:field>

            <ui:field labelForId="siteId" labelKey="report.input.field.site">
                <select id="siteId" name="siteId" class="middleLengthText">
                    <option value=""><fmt:message  key="form.all"/></option>
                </select>
            </ui:field>

            <ui:field labelForId="sizeId" labelKey="report.input.field.creativeSize">
                <select name="sizeId" id="sizeId" class="middleLengthText">
                    <option value=""><fmt:message   key="form.all"/></option>
                    <c:forEach items="${sizes}" var="aSize">
                        <option value="${aSize.id}" ><c:out value="${aSize.localizedName}"/></option>
                    </c:forEach>
                </select>
            </ui:field>

            <ui:field labelForId="outputCurrencyCode" labelKey="report.input.field.currency" tipKey="report.custom.currency.tooltip">
                <s:radio id="outputCurrencyCode" name="outputCurrencyCode"
                         list="#{'' : getText('report.custom.currency.account'), 'USD' : getText('report.custom.currency.USD')}"/>
            </ui:field>

            <tr>
              <td colspan="2">
              <table class="optionTransferGroup">
                <thead>
                <tr class="header"><th><label><fmt:message key="report.outputColumns"/></label></th><th><label><fmt:message key="report.metricsColumns"/></label></th></tr>
                </thead>
                <tr>
                  <td>
                    <ui:optiontransfer
                        name="outputColumns"
                        size="9"
                        cssClass="middleLengthText"
                        list="${outputColumns}"
                        selList="<%= Collections.emptyList() %>"
                        id="AvailableOutput"
                        selId="SelectedOutput"
                        titleKey="report.availableColumns"
                        selTitleKey="report.selectedColumns"
                        mandatory="true"
                        sort="true"
                    />
                  </td>
                  <td>
                    <ui:optiontransfer
                        name="metricsColumns"
                        size="9"
                        cssClass="middleLengthText"
                        list="${metricsColumns}"
                        selList="${selectedMetricsColumns}"
                        id="AvailableMetric"
                        selId="SelectedMetric"
                        titleKey="report.availableColumns"
                        selTitleKey="report.selectedColumns"
                        mandatory="true"
                        sort="true"
                    />
                  </td>
                </tr>
                </table>
              </td>
            </tr>

            <ui:field labelKey="report.outputFormat">
                <div class="nomargin">
                    <label class="narrowSet">
                        <input type="radio" name="format" value="" checked="checked"/>
                        <fmt:message key="report.outputFormat.HTML"/>
                    </label>
                    <label class="narrowSet">
                        <input type="radio" name="format" value="EXCEL">
                        <fmt:message key="report.outputFormat.Excel"/>
                    </label>
                    <label class="narrowSet">
                        <input type="radio" name="format" value="EXCEL_NOLINKS">
                        <fmt:message key="report.outputFormat.Excel.nolinks"/>
                    </label>
                    <label class="narrowSet">
                        <input type="radio" name="format" value="CSV">
                        <fmt:message key="report.outputFormat.CSV"/>
                    </label>
                </div>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button message="report.button.runReport" />
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</form>
