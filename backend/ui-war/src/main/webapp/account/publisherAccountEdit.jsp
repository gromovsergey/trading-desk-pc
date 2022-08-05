<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set value="#attr.isCreatePage?'create':'update'" var="saveActionName"/>

<ui:externalLibrary libName="codemirror" />
<script type="text/javascript">
    function showHidePubPixelsParams() {
        if ($('#usePubPixel').is(":checked")) {
            $("#pubPixelsParams")
                    .show()
                    .find(":input").removeAttr('disabled');
        } else {
            $("#pubPixelsParams")
                    .hide()
                    .find(":input")
                    .attr('disabled', 'disabled')
                    .prop({checked:false});
        }
    }

    $(showHidePubPixelsParams);
</script>

<ui:pageHeadingByTitle/>
<s:form action="%{#request.moduleName}/%{#saveActionName}" id="actionSave">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <s:if test="hasActionErrors()">
        <div style="margin-top: 5px; margin-bottom: 5px">
            <s:actionerror/>
        </div>
    </s:if>

    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
    </ui:errorsBlock>

    <ui:section titleKey="form.main">
        <ui:fieldGroup>
            
            <%@ include file="accountDetailsEdit.jsp" %>
            
            <s:if test="isInternal()">
                <s:set var="testFlagDisabled" value="true"/>
                <c:if test="${ad:isPermitted('Account.setTestFlag', existingAccount)}">
                    <s:set var="testFlagDisabled" value="false" />
                </c:if>

                <ui:field id="testFlagTr" labelKey="account.testFlag" labelForId="testFlag" errors="testFlag">
                    <s:if test="testFlagDisabled">
                        <s:checkbox name="testFl" id="testFlag" disabled="true" />
                    </s:if>
                    <s:else>
                        <s:checkbox name="testFl" id="testFlag" />
                    </s:else>
                </ui:field>


                <ui:field labelKey="publisher.passbackBelowFold" errors="passbackBelowFold">
                    <label class="withInput">
                        <s:radio name="passbackBelowFold" list="true" template="justradio"/><fmt:message key="publisher.passbackBelowFold.on"/>
                    </label>
                    <label class="withInput">
                        <s:radio name="passbackBelowFold" list="false" template="justradio"/><fmt:message key="publisher.passbackBelowFold.off"/>
                    </label>
                </ui:field>

                <ui:field id="pubAdvertisingReportElem" labelKey="publisher.pubAdvertisingReport" errors="pubAdvertisingReportFlag">
                    <s:radio cssClass="withInput"  id="pubAdvertisingReportFlag" name="pubAdvertisingReportFlag" value="pubAdvertisingReportFlag" list="#{true, false}" listValue="getText('publisher.pubAdvertisingReport.'+key)" />
                </ui:field>

                <ui:field id="referrerReportElem" labelKey="publisher.referrerReport" errors="referrerReportFlag">
                    <s:radio cssClass="withInput"  id="referrerReportFlag" name="referrerReportFlag" value="referrerReportFlag" list="#{true, false}" listValue="getText('publisher.referrerReport.'+key)" />
                </ui:field>

                <ui:field labelKey="publisher.creativesReapproval" errors="creativesReapproval">
                    <label class="withInput">
                        <s:radio name="creativesReapproval" list="true" template="justradio"/><fmt:message key="publisher.creativesReapproval.true"/>
                    </label>
                    <label class="withInput">
                        <s:radio name="creativesReapproval" list="false" template="justradio"/><fmt:message key="publisher.creativesReapproval.false"/>
                    </label>
                </ui:field>
            </s:if>
            
            <%@ include file="internalAccountLinkEdit.jspf" %>
            
            <ui:field id="accountManTr" labelKey="account.accountManager" labelForId="accountManagerId">
                <s:select name="accountManager.id" id="accountManagerId" cssClass="middleLengthText"
                          headerValue="%{getText('form.select.none')}" headerKey=""
                          list="internalUsers"
                          listKey="id" listValue="name" value="accountManager.id" >
                </s:select>
            </ui:field>

            <ui:field labelKey="account.notes" labelForId="notes" errors="notes">
                <s:textarea name="notes" id="notes" cssClass="middleLengthText" />
            </ui:field>

        </ui:fieldGroup>
    </ui:section>

    <s:if test="isInternal()">
        <ui:section>
            <ui:fieldGroup>
                <ui:field>
                    <ul class="chBoxesTree">
                        <li>
                            <label class="withInput">
                                <s:checkbox id="usePubPixel" name="usePubPixel"
                                            onchange="showHidePubPixelsParams();"/>
                                <s:text name="publisher.usesRetargetingPixels"/>
                            </label>
                        </li>
                        <li>
                            <div class="wrapper" id="pubPixelsParams">
                                <table class="grouping fieldsets">
                                    <tr>
                                        <td class="singleFieldset">
                                            <ui:header styleClass="level2 withTip"><h4><fmt:message key="publisher.usesRetargetingOptInPixel"/></h4></ui:header>
                                            <s:fielderror fieldName="pubPixelOptIn"/>
                                            <s:textarea name="pubPixelOptIn" id="pubPixelOptIn" wrap="off" cssClass="middleLengthText1 html_highlight"/>
                                        </td>
                                        <td class="singleFieldset">
                                            <ui:header styleClass="level2 withTip"><h4><fmt:message key="publisher.usesRetargetingOptOutPixel"/></h4></ui:header>
                                            <s:fielderror fieldName="pubPixelOptOut"/>
                                            <s:textarea name="pubPixelOptOut" id="pubPixelOptOut" wrap="off" cssClass="middleLengthText1 html_highlight"/>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </li>
                    </ul>
                </ui:field>
            </ui:fieldGroup>
        </ui:section>
    </s:if>

    <div class="wrapper">
        <ui:button message="form.save" type="submit" />
        <s:if test="id == null">
            <ui:button message="form.cancel" type="button" onclick="location='main.action';" />
        </s:if>
        <s:else>
            <ui:button message="form.cancel" type="button" onclick="location='view.action?id=${id}';" />
        </s:else>
    </div>

</s:form>
