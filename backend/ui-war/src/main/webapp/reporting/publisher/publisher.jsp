<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page import="java.util.Collections"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">
    $(function() {
        UI.Daterange.setTimeZoneShift($('#fastChangeId').val(), {fromDateName: 'dateRange.begin', toDateName: 'dateRange.end'}, $('#accountId').val());
        updateWalledGardenFlag();

        $('#accountId').change(function() {
            if (this.value) {
                UI.Data.Options.get('sites', 'siteId', {publisherPair:$(this).val(), concatResultForValue: false}, ['form.all']);
            } else {
                UI.Data.Options.replaceWith('siteId', ['form.all']);
            }

            $('#tagId').prop({disabled : true});
            UI.Data.Options.replaceWith('tagId', ['form.all']);

            UI.Daterange.setTimeZoneShift($('#fastChangeId').val(), {fromDateName: 'dateRange.begin', toDateName: 'dateRange.end'}, $(this).val());
            updateWalledGardenFlag();
        });

        $('#siteId').change(function() {
            if (this.selectedIndex != 0) {
                $('#tagId').prop({disabled : false});
                UI.Data.Options.get('tags', 'tagId', {sitePair:$(this).val(), concatResultForValue: false}, ['form.all']);
            } else {
                $('#tagId').prop({disabled : true});
                UI.Data.Options.replaceWith('tagId', ['form.all']);
            }
        });

        $('[name=detailLevel]').change(function() {
            onDetailLevelChange($(this).val())
        });

        $('#publisherForm').submit(function(){
            return reportSubmit(this);
        });

        $('#dateRange_begin, dateRange_end').change(function(){
            var level = $('[name=detailLevel]:checked').val();
            onDetailLevelChange(level)
        });
    });

    function onDetailLevelChange(level){
        if (level == 'custom') return;
        presetPublisherLocalColumns(level);
    }

    function updateWalledGardenFlag() {
        if ($('#accountId').val() != undefined) {
            UI.Data.get('walledGardenFlag', {publisherPair:$('#accountId').val()}, setWalledGardenFlag);
        }
    }

    function setWalledGardenFlag(respXML) {
        var newValue = $("walledGardenFlagValue", respXML).text();
        var oldValue = $("#walledGardenFlag").val();
        if (newValue != oldValue) {
            $("#walledGardenFlag").val(newValue);
        }
        presetPublisherLocalColumns($("input:radio[name=detailLevel]:checked").val());
    }

    function isWalledGarden() {
        return $("#walledGardenFlag").val() == 'true';
    }

    function presetPublisherLocalColumns(level) {
        var callback = function(respXML){
            parsePublisherResp(respXML);
        };
        UI.Data.get('publisher/reportColumns', {"dateRange.begin":$('#dateRange_begin').val(), "dateRange.end":$('#dateRange_end').val(),
            accountId:$('#accountId').val(), detailLevel:level, isWalledGarden:isWalledGarden()}, callback);
    }

    function parsePublisherResp(respXML) {
        fillSelect('AvailableColumns', 'availableColumns', respXML);
        fillSelect('SelectedColumns', 'selectedColumns', respXML);

        window.sortListsByPattern && sortListsByPattern();
    }

    function jumpToCustomDetailLevel() {
        $('[name=detailLevel][value=custom]').prop({checked : true});
    }

    function sortListsByPattern() {
        var sortPattern = ${columnsSortPattern};
        sortSelectByPattern('AvailableColumns', sortPattern);
        sortSelectByPattern('SelectedColumns', sortPattern);
    }

    function reportSubmit(form) {
        $('option', form.columns).each(function() {
            this.selected = true;
        });

        // Safari post request cache problem solution
        form.action += $.browser.safari ? '?rnd=' + Math.random() : '';
    }

    function onDateRangeChange() {
        var level = $('[name=detailLevel]:checked').val();
        onDetailLevelChange(level)
    }

</script>

<ui:pageHeadingByTitle/>

<s:form id="publisherForm" action="run" method="post" target="_blank">
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
                        validateRange="true"
                        onChange="onDateRangeChange();"/>
            </ui:field>

            <s:if test="%{canSelectAccount()}">
                <ui:field labelKey="report.input.field.publisherAccount" labelForId="accountId">
                    <c:choose>
                        <c:when test="${not empty accounts}">
                            <select id="accountId" name="accountId" class="middleLengthText">
                                <c:forEach var="account" items="${accounts}">
                                    <option value="${account.id}"><c:out value="${account.name}"/></option>
                                </c:forEach>
                            </select>
                        </c:when>
                        <c:otherwise>
                            <span class="errors"><fmt:message key="report.account.error"/></span>
                        </c:otherwise>
                    </c:choose>
                </ui:field>
            </s:if>
            <s:else>
                <s:if test="%{isInternal()}">
                    <ui:simpleField labelKey="report.input.field.publisherAccount" value="${account.name}"/>
                </s:if>
                <input type="hidden" id="accountId" name="accountId" value="${account.id}"/>
            </s:else>

            <ui:field cssClass="valignFix" labelKey="report.input.field.detailLevel">
                <s:radio list="@com.foros.session.reporting.publisher.DetailLevel@values()"
                         name="detailLevel"
                         listValue="%{getText(getNameKey())}"/>
            </ui:field>

            <ui:field>
                <ui:optiontransfer
                        name="columns"
                        size="10"
                        listKey="id"
                        listValue="name"
                        list="<%= Collections.emptyList() %>"
                        selList="<%= Collections.emptyList() %>"
                        selListKey="id"
                        selListValue="name"
                        id="AvailableColumns"
                        selId="SelectedColumns"
                        titleKey="report.availableColumns"
                        selTitleKey="report.selectedColumns"
                        onchange="jumpToCustomDetailLevel();sortListsByPattern();"
                        immovableOptions="${mandatoryColumns}"
                        />
            </ui:field>

            <input type="hidden" id="walledGardenFlag" name="walledGardenFlag"/>

            <ui:field labelKey="report.input.optionalFilters">
                <table class="formFields">
                    <tr>
                        <td class="fieldName"><label for="siteId"><fmt:message key="report.input.field.site"/>:</label></td>
                        <td class="field">
                            <select id="siteId" name="siteId" class="middleLengthText">
                                <option value=""><fmt:message key="form.all"/></option>
                                <c:forEach var="site" items="${sites}">
                                    <option value="${site.id}"><c:out value="${site.name}"/></option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td class="fieldName"><label for="tagId"><fmt:message key="report.input.field.tag"/>:</label></td>
                        <td class="field">
                            <select id="tagId" name="tagId" class="middleLengthText" disabled="disabled">
                                <option value=""><fmt:message key="form.all"/></option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td class="fieldName"><label for="countryCode"><fmt:message key="report.input.field.country"/>:</label></td>
                        <td class="field">
                            <select name="countryCode" id="countryCode" class="middleLengthText">
                                <option value=""><fmt:message key="form.all"/></option>
                                <c:forEach items="${countries}" var="country">
                                    <option value="${country.id}"><ad:resolveGlobal resource="country" id="${country.id}"/></option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                </table>
            </ui:field>

            <ui:field cssClass="withButton">
                <ui:button message="report.button.runReport" />
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</s:form>
