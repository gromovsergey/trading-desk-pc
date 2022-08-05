<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>


<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>


<ui:pageHeadingByTitle/>
<s:fielderror><s:param value="'keywords'"/></s:fielderror>

<s:form action="admin/CurrencyExchange/save" cssClass="exclusionForm">
    <s:hidden name="previousEffectiveDate"/>
    <s:if test="hasActionErrors()">
        <div style="margin-top: 5px; margin-bottom: 5px">
            <s:actionerror/>
        </div>
    </s:if>
    <s:fielderror><s:param value="'version'"/></s:fielderror>
    <table class="dataView">
        <thead>
        <tr>
            <th style="width:50%;"><s:text name="CurrencyExchange.currency"/></th>
            <th style="width:50%;"><s:text name="CurrencyExchange.currencyRate"/></th>
        </tr>
        </thead>
        <tbody>

        <s:iterator value="currencyExchangeRates" var="r">
            <s:set var="index" value="findIndex(id)"/>
            <tr>
                <td>
                    <s:property value="name" escape="true"/> (<s:property value="symbol" escape="true"/>)
                </td>
                <td>
                    <s:if test="#index != null">
                        <table class="grouping">
                            <tr>
                                <td class="field">
                                    <table class="mandatoryContainer">
                                        <tr>
                                            <td>
                                                <s:hidden name="manualRates[%{#index}].id.currency.id"/>
                                                <s:textfield cssClass="smallLengthText1"
                                                             name="manualRates[%{#index}].rate"
                                                             maxlength="15"/>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                                <td>
                                    <s:fielderror>
                                        <s:param value="'manualRates[' + #index + '].rate'"/>
                                    </s:fielderror>
                                </td>
                            </tr>
                        </table>
                    </s:if>
                    <s:else>
                        <s:textfield cssClass="smallLengthText1" value="%{rate}" maxlength="15" disabled="true"/>
                    </s:else>
                </td>
            </tr>
        </s:iterator>
        </tbody>
    </table>
    <div class="wrapper">
        <ui:button message="form.save" type="submit"/>
        <ui:button message="form.cancel" onclick="location='main.action';" type="button"/>
    </div>
</s:form>
