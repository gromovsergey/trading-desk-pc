<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
    <s:if test="%{id == null}">
    $().ready(function() {
        $("#currencyCode").change(function() {
            UI.Data.get('currencyInfo', {currencyCode: $(this).val()}, function(data) {
                $("#name").html($("name", data).text());
                $("#symbol").html($("symbol", data).text());
                $("#fractionDigits").html($("fractionDigits", data).text());
                $("#hiddenFractionDigits").val($("fractionDigits", data).text());
            });
        });

        $("#currencyCode").change();
    });
    </s:if>

    $().ready(function() {
        <s:if test="%{source.letter == 'F'}">
        $("#rateId").prop('disabled', true);
        </s:if>
        $("input[name='source']").click(function(){
            if (this.checked && $(this).val() == 'MANUAL') {
                $("#rateId").prop('disabled', false);
            } else if (this.checked && $(this).val() == 'FEED') {
                $("#rateId").prop('disabled', true);
            }
        });
    });
</script>

<s:form action="admin/Currency/%{id == null?'create':'update'}">
    <s:hidden name="id" id="id"/>
    <s:hidden name="version"/>
    <s:hidden name="effectiveDate"/>
    <div class="wrapper">
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </div>

<c:if test="${valueDoesntExistInFeed}">
    <ui:section cssClass="message warning">
        <fmt:message key="Currency.warning.noCurrencyInFeed">
            <fmt:param value="${currencyCode}"/>
        </fmt:message>
    </ui:section>
</c:if>
    <ui:section titleKey="form.main" >
        <ui:fieldGroup id="" cssClass="">

            <ui:field labelKey="Currency.currencyCode" labelForId="currencyCode" errors="currencyCode,code">
                <s:if test="%{id == null}">
                    <s:select name="currencyCode" id="currencyCode" value="currencyCode" list="availableCurrencyCodesWithoutDefault" cssClass="smallLengthText"/>
                </s:if>
                <s:else>
                    <c:set var="textVal">
                        <s:property value="currencyCode"/>
                    </c:set>
                    <ui:text text="${pageScope.textVal}"/>
                    <s:hidden name="currencyCode"/>
                </s:else>
            </ui:field>

            <ui:field labelKey="Currency.name">
                <c:set var="textVal">
                    <s:if test="%{id != null}">
                        <s:text name="global.currency.%{currencyCode}.name"/>
                    </s:if>
                </c:set>
                <ui:text id="name" text="${pageScope.textVal}"/>
            </ui:field>

            <ui:field labelKey="Currency.symbol">
                <s:label name="symbol" id="symbol" value="%{id != null ? getCurrencySymbol(currencyCode) : ''}"/>
            </ui:field>
            
            <ui:field labelKey="Currency.fractionDigits" errors="fractionDigits">
                <s:label name="fractionDigits" id="fractionDigits"/>
                <s:hidden id="hiddenFractionDigits" name="fractionDigits"/>
            </ui:field>

            <ui:field labelKey="Currency.source">
                <c:if test="${sourceEditable}">
                <s:radio cssClass="withInput" name="source" 
                    list="sourceValues" listValue="getText(resourceKey)" />
                </c:if>
                <c:if test="${!sourceEditable}">
                <s:radio cssClass="withInput" name="source" 
                    list="sourceValues" listValue="getText(resourceKey)" disabled="true"/>
                </c:if>
            </ui:field>

            <ui:field labelKey="Currency.rate" labelForId="rateId" required="true" errors="rate">
                <s:textfield name="rate" cssClass="smallLengthText" maxlength="15" id="rateId" />
            </ui:field>

        </ui:fieldGroup>
    </ui:section>

  <div class="wrapper">
    <ui:button message="form.save" type="submit"/>
    <ui:button message="form.cancel" onclick="location='main.action';" type="button" />
  </div>
</s:form>
