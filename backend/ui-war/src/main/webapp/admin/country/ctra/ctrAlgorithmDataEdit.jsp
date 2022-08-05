<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"    prefix="fmt" %>

<script type="text/javascript">
    $(function() {
        $('#advertiserExclusionsIds').change(function(){
            isAgencyBeingSelected() || $('#advIdSelector').val('');
        });
        
        $('#clicksInterval1Days').change(function(){
            $('#calc1').text(+this.value + 1);
        })
        
        $('#impsInterval1Days').change(function(){
            $('#calc3').text(+this.value + 1);
        })
        
        $('#clicksInterval2Days').change(function(){
            $('#calc2').text(this.value);
        })
        
        $('#impsInterval2Days').change(function(){
            $('#calc4').text(this.value);
        })
        
    });

    function isAgencyBeingSelected(){
         return $('#advertiserExclusionsIds').val().indexOf("|") > 0;
    }

</script>
<ui:pageHeadingByTitle/>

<s:form action="admin/Country/CTRAlgorithm/save" id="algoSaver">
    <s:hidden name="id"/>
    <s:hidden name="countryCode"/>
    <s:hidden name="version"/>
    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:actionerror/>
    </div>
    <ui:section titleKey="ctrAlgorithmData.historyAdjustments">
        <ui:fieldGroup>
            <ui:field errors="historyClicks">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <c:set var="textVal">
                                <s:text name="ctrAlgorithmData.historyClicks"/> =(<s:text name="ctrAlgorithmData.clicksLast"/>
                            </c:set>
                            <ui:text text="${pageScope.textVal}"/>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="clicksInterval1Days" id="clicksInterval1Days" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                        <c:set var="clcIntDays">
                            <s:fielderror><s:param value="'clicksInterval1Days'"/></s:fielderror>
                        </c:set>
                        <c:if test="${not empty clcIntDays}">
                            <td class="withError">
                                ${clcIntDays}
                            </td>
                        </c:if>
                        <td class="withField">
                            <c:set var="textVal">
                                <fmt:message key="ctrAlgorithmData.days" />) *
                            </c:set>
                            <ui:text text="${textVal}"/>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="clicksInterval1Weight" id="clicksInterval1Weight" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                        <c:set var="clcIntWeight">
                            <s:fielderror><s:param value="'clicksInterval1Weight'"/></s:fielderror>
                        </c:set>
                        <c:if test="${not empty clcIntWeight}">
                            <td class="withError">
                                ${clcIntWeight}
                            </td>
                        </c:if>
                        <td class="withField">
                            <table class="fieldAndAccessories">
                                <tr>
                                    <td class="withField">
                                        <c:set var="textVal">
                                            + (<s:text name="ctrAlgorithmData.clicks"/>
                                        </c:set>
                                        <ui:text text="${pageScope.textVal}"/>
                                    </td>
                                    <td>
                                        <s:if test="!getFieldErrors().containsKey('clicksInterval1Days')">
                                            <s:set var="valForCalc" value="clicksInterval1Days"/>
                                            <ui:text text="${valForCalc + 1}" id="calc1"/>
                                        </s:if>
                                        <s:else>
                                            <ui:text text="" id="calc1"/>
                                        </s:else>
                                    </td>
                                    <td class="withField">
                                        <ui:text text="-"/>
                                    </td>
                                </tr>
                            </table>
                            
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="clicksInterval2Days" id="clicksInterval2Days" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                        <c:set var="clcInt2Days">
                            <s:fielderror><s:param value="'clicksInterval2Days'"/></s:fielderror>
                        </c:set>
                        <c:if test="${not empty clcInt2Days}">
                            <td class="withError">
                                ${clcInt2Days}
                            </td>
                        </c:if>
                        <td class="withField">
                            <c:set var="textVal">
                                <s:text name="ctrAlgorithmData.daysAgo"/>) *
                            </c:set>
                            <ui:text text="${pageScope.textVal}"/>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="clicksInterval2Weight" id="clicksInterval2Weight" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                        <c:set var="clcInt2Weight">
                            <s:fielderror><s:param value="'clicksInterval2Weight'"/></s:fielderror>
                        </c:set>
                        <c:if test="${not empty clcInt2Weight}">
                            <td class="withError">
                                ${clcInt2Weight}
                            </td>
                        </c:if>
                        <td class="withField">
                            <table class="fieldAndAccessories">
                                <tr>
                                    <td class="withField">
                                        <c:set var="textVal">
                                            + (<s:text name="ctrAlgorithmData.clicks"/> >
                                        </c:set>
                                        <ui:text text="${pageScope.textVal}"/>
                                    </td>
                                    <td class="withField">
                                        <s:set var="valForCalc" value="clicksInterval2Days"/>
                                        <ui:text text="${valForCalc}" id="calc2"/>
                                    </td>
                                    <td class="withField">
                                        <c:set var="textVal">
                                            <s:text name="ctrAlgorithmData.daysAgo"/>) *
                                        </c:set>
                                        <ui:text text="${pageScope.textVal}"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="clicksInterval3Weight" id="clicksInterval3Weight" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                        <c:set var="clcInt3Weight">
                            <s:fielderror><s:param value="'clicksInterval3Weight'"/></s:fielderror>
                        </c:set>
                        <c:if test="${not empty clcInt3Weight}">
                            <td class="withError">
                                ${clcInt3Weight}
                            </td>
                        </c:if>
                    </tr>
                </table>
            </ui:field>
            <ui:field errors="historyImpressions">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <c:set var="textVal">
                                <s:text name="ctrAlgorithmData.historyImpressions"/> = (<s:text name="ctrAlgorithmData.impsLast"/>
                            </c:set>
                            <ui:text text="${pageScope.textVal}"/>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="impsInterval1Days" id="impsInterval1Days" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                        <c:set var="impInt1Days">
                            <s:fielderror><s:param value="'impsInterval1Days'"/></s:fielderror>
                        </c:set>
                        <c:if test="${not empty impInt1Days}">
                            <td class="withError">
                                ${impInt1Days}
                            </td>
                        </c:if>
                        <td class="withField">
                            <c:set var="textVal">
                                <s:text name="ctrAlgorithmData.days"/>) *
                            </c:set>
                            <ui:text text="${pageScope.textVal}"/>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="impsInterval1Weight" id="impsInterval1Weight" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                        <c:set var="impInt1Weight">
                            <s:fielderror><s:param value="'impsInterval1Weight'"/></s:fielderror>
                        </c:set>
                        <c:if test="${not empty impInt1Weight}">
                            <td class="withError">
                                ${impInt1Weight}
                            </td>
                        </c:if>
                        <td class="withField">
                            <table class="fieldAndAccessories">
                                <tr>
                                    <td class="withField">
                                        <c:set var="textVal">
                                            + (<s:text name="ctrAlgorithmData.imps"/>
                                        </c:set>
                                        <ui:text text="${pageScope.textVal}"/>
                                    </td>
                                    <td class="withField">
                                        <s:if test="!getFieldErrors().containsKey('impsInterval1Days')">
                                            <s:set var="valForCalc" value="impsInterval1Days"/>
                                            <ui:text text="${valForCalc + 1}" id="calc3"/>
                                        </s:if>
                                        <s:else>
                                            <ui:text text="" id="calc3"/>
                                        </s:else>
                                    </td>
                                    <td class="withField">
                                        <ui:text text="-"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="impsInterval2Days" id="impsInterval2Days" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                        <c:set var="impInt2Days">
                            <s:fielderror><s:param value="'impsInterval2Days'"/></s:fielderror>
                        </c:set>
                        <c:if test="${not empty impInt2Days}">
                            <td class="withError">
                                ${impInt2Days}
                            </td>
                        </c:if>
                        <td class="withField">
                            <c:set var="textVal">
                                <s:text name="ctrAlgorithmData.daysAgo"/>) *
                            </c:set>
                            <ui:text text="${pageScope.textVal}"/>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="impsInterval2Weight" id="impsInterval2Weight" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                        <c:set var="impInt2Weight">
                            <s:fielderror><s:param value="'impsInterval2Weight'"/></s:fielderror>
                        </c:set>
                        <c:if test="${not empty impInt2Weight}">
                            <td class="withError">
                                ${impInt2Weight}
                            </td>
                        </c:if>
                        <td class="withField">
                            <table class="fieldAndAccessories">
                                <tr>
                                    <td class="withField">
                                        <c:set var="textVal">
                                            + (<s:text name="ctrAlgorithmData.imps"/> >
                                        </c:set>
                                        <ui:text text="${pageScope.textVal}"/>
                                    </td>
                                    <td class="withField">
                                        <s:set var="valForCalc" value="impsInterval2Days"/>
                                        <ui:text text="${valForCalc}" id="calc4"/>
                                    </td>
                                    <td class="withField">
                                        <c:set var="textVal">
                                            <s:text name="ctrAlgorithmData.daysAgo"/>) *
                                        </c:set>
                                        <ui:text text="${pageScope.textVal}"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="impsInterval3Weight" id="impsInterval3Weight" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                        <c:set var="impInt3Weight">
                            <s:fielderror><s:param value="'impsInterval3Weight'"/></s:fielderror>
                        </c:set>
                        <c:if test="${not empty impInt3Weight}">
                            <td class="withError">
                                ${impInt3Weight}
                            </td>
                        </c:if>
                    </tr>
                </table>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
    <ui:section titleKey="ctrAlgorithmData.publisherAdjustments">
        <ui:fieldGroup>
            <ui:field labelKey="ctrAlgorithmData.pubCTRDefault" labelForId="pubCTRDefault" required="true" errors="pubCTRDefaultPercent">
                <s:textfield name="pubCTRDefaultPercent" id="pubCTRDefault" cssClass="middleLengthNumber" maxLength="9"/>%
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.impressionsLevels" cssClass="subsectionRow"/>
            <ui:field labelKey="ctrAlgorithmData.sysCTRLevel" labelForId="sysCTRLevel" errors="sysCTRLevel" required="true">
                <s:textfield name="sysCTRLevel" id="sysCTRLevel" cssClass="middleLengthNumber" maxLength="9"/>
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.pubCTRLevel" labelForId="pubCTRLevel" errors="pubCTRLevel" required="true">
                <s:textfield name="pubCTRLevel" id="pubCTRLevel" cssClass="middleLengthNumber" maxLength="9"/>
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.siteCTRLevel" labelForId="siteCTRLevel" errors="siteCTRLevel" required="true">
                <s:textfield name="siteCTRLevel" id="siteCTRLevel" cssClass="middleLengthNumber" maxLength="9"/>
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.tagCTRLevel" labelForId="tagCTRLevel" errors="tagCTRLevel" required="true">
                <s:textfield name="tagCTRLevel" id="tagCTRLevel" cssClass="middleLengthNumber" maxLength="9"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
    <ui:section titleKey="ctrAlgorithmData.KWTTG">
        <ui:fieldGroup>
            <ui:field labelKey="ctrAlgorithmData.kwtgCTRDefault" labelForId="kwtgCTRDefault" required="true" errors="kwtgCTRDefaultPercent">
                <s:textfield name="kwtgCTRDefaultPercent" id="kwtgCTRDefault" cssClass="middleLengthNumber" maxLength="9"/>%
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.impressionsLevels" cssClass="subsectionRow"/>
            <ui:field labelKey="ctrAlgorithmData.sysKwtgCTRLevel" labelForId="sysKwtgCTRLevel" errors="sysKwtgCTRLevel" required="true">
                <s:textfield name="sysKwtgCTRLevel" id="sysKwtgCTRLevel" cssClass="middleLengthNumber" maxLength="9"/>
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.keywordCTRLevel" labelForId="keywordCTRLevel" errors="keywordCTRLevel" required="true">
                <s:textfield name="keywordCTRLevel" id="keywordCTRLevel" cssClass="middleLengthNumber" maxLength="9"/>
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.KWinTGCTR" errors="ccgkeywordKwCTRLevel,ccgkeywordTgCTRLevel">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:text textKey="ctrAlgorithmData.forKWCTR"/>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="ccgkeywordKwCTRLevel" id="ccgkeywordKwCTRLevel" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                        <td class="withField">
                            <ui:text textKey="ctrAlgorithmData.forTGCTR"/>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="ccgkeywordTgCTRLevel" id="ccgkeywordTgCTRLevel" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                    </tr>
                </table>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
    <ui:section titleKey="ctrAlgorithmData.timeOfWeek">
        <ui:fieldGroup>
            <ui:field labelKey="ctrAlgorithmData.towRaw" labelForId="towRaw" required="true" errors="towRawPercent">
                <s:textfield name="towRawPercent" id="towRaw" cssClass="middleLengthNumber" maxLength="9"/>%
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.impressionsLevels" cssClass="subsectionRow"/>
            <ui:field labelKey="ctrAlgorithmData.sysTOWLevel" labelForId="sysTOWLevel" errors="sysTOWLevel" required="true">
                <s:textfield name="sysTOWLevel" id="sysTOWLevel" cssClass="middleLengthNumber" maxLength="9"/>
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.campaignTOWLevel" labelForId="campaignTOWLevel" errors="campaignTOWLevel" required="true">
                <s:textfield name="campaignTOWLevel" id="campaignTOWLevel" cssClass="middleLengthNumber" maxLength="9"/>
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.tgTOWLevel" labelForId="tgTOWLevel" errors="tgTOWLevel" required="true">
                <s:textfield name="tgTOWLevel" id="tgTOWLevel" cssClass="middleLengthNumber" maxLength="9"/>
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.keywordTOWLevel" labelForId="keywordTOWLevel" errors="keywordTOWLevel" required="true">
                <s:textfield name="keywordTOWLevel" id="keywordTOWLevel" cssClass="middleLengthNumber" maxLength="9"/>
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.KWinTGTOW" errors="ccgkeywordKwTOWLevel,ccgkeywordTgTOWLevel">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:text textKey="ctrAlgorithmData.forKWTOW"/>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="ccgkeywordKwTOWLevel" id="ccgkeywordKwTOWLevel" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                        <td class="withField">
                            <ui:text textKey="ctrAlgorithmData.forTGTOW"/>
                        </td>
                        <td class="withField mandatory">
                            <s:textfield name="ccgkeywordTgTOWLevel" id="ccgkeywordTgTOWLevel" cssClass="middleLengthNumber" maxLength="9"/>
                        </td>
                    </tr>
                </table>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
    <ui:section titleKey="ctrAlgorithmData.randomAdserving">
        <ui:fieldGroup>
            <ui:field labelKey="ctrAlgorithmData.cpcRandomImps" labelForId="cpcRandomImps" required="true" errors="cpcRandomImps">
                <s:textfield name="cpcRandomImps" id="cpcRandomImps" cssClass="middleLengthNumber" maxLength="7"/>
            </ui:field>
        </ui:fieldGroup>
        <ui:fieldGroup>
            <ui:field labelKey="ctrAlgorithmData.cpaRandomImps" labelForId="cpaRandomImps" required="true" errors="cpaRandomImps">
                <s:textfield name="cpaRandomImps" id="cpaRandomImps" cssClass="middleLengthNumber" maxLength="7"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <s:set var="sectionInfo">
        <s:text name="ctrAlgorithmData.exclusions.hint"/>
    </s:set>
    
    <ui:section titleKey="ctrAlgorithmData.exclusions" infoText="${pageScope.sectionInfo}">
        <ui:fieldGroup>
            <ui:field labelKey="ctrAlgorithmData.byAdvertiser" errors="advertiserExclusions">
                <table class="fieldAndAccessories">
                    <tr>
                        <td class="withField">
                            <ui:autocomplete
                                id="advertiserExclusionsIds"
                                source="getTags"
                                selectedItems="${namedAdvertiserExclusions}"
                                selectedNameKey="name"
                                selectedValueKey="id"
                                cssClass="middleLengthText"
                                isMultiSelect="true"
                                minLength="1"
                            >
                                <script type="text/javascript">
                                    function getTags(request, response){
                                        UI.Data.get('advertisersByName', {name:request.term, countryCode:'<s:property value="countryCode"/>'}, function(data) {
                                            var opts = $('option', data).map(function() {
                                                var curr = $(this);
                                                return {label:curr.text(), value:curr.attr('id')};
                                            });
                                            response(opts);
                                        });
                                    };
                                </script>
                            </ui:autocomplete>
                        </td>
                    </tr>
                </table>
            </ui:field>
            <ui:field>
                <span class="infos">
                    <ui:text textKey="ctrAlgorithmData.byAdvertiser.hint"/>
                </span>
            </ui:field>
            <ui:field labelKey="ctrAlgorithmData.byCampaign" errors="campaignExclusions">
                <s:textarea name="campaignExclusionsText" id="notes" cssClass="middleLengthText" />
            </ui:field>
            <ui:field>
                <span class="infos">
                    <ui:text textKey="ctrAlgorithmData.byCampaign.hint"/>
                </span>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
    <s:include value="/templates/formFooter.jsp"/>
</s:form>
