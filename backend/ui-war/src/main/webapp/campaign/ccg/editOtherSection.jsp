<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<ui:section titleKey="campaign.other">
    <ui:fieldGroup>
        <ui:field labelKey="campaign.creative.sequentialAdserving" cssClass="valignFix">
            <label class="withInput">
                <s:radio name="sequentialAdservingFlag" list="true" template="justradio"
                /><fmt:message key="campaign.creative.sequentialAdserving.enabled"/>
            </label>
            <label class="withInput">
                <s:radio name="sequentialAdservingFlag" list="false" template="justradio"
                /><fmt:message key="campaign.creative.sequentialAdserving.disabled"/>
            </label>
        </ui:field>
        <c:set var="creativeOptimisationIsVisible" value="${ad:isInternal() || ccgRate.rateType.name == 'CPM'}"/>
        <ui:field id="rotationCriteriaFieldId" required="true" cssClass="hide" labelKey="campaign.creative.rotationCriteria" labelForId="rotationCriteria"  errors="rotationCriteria">
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField">
                        <label class="withInput">
                            <s:textfield name="rotationCriteria" id="rotationCriteria" cssClass="smallLengthText" maxlength="11" />&nbsp;<fmt:message
                                key="campaign.creative.rotationCriteria.impressions"/>
                        </label>
                    </td>
                    <td class="withTip">
                        <c:choose>
                            <c:when test="${ad:isInternal()}">
                                <ui:hint>
                                    <fmt:message key="campaign.creative.rotationCriteria.optimisation.tooltip"/>
                                </ui:hint>
                            </c:when>
                            <c:otherwise>
                                <div id="optimisationTooltip">
                                    <ui:hint>
                                        <fmt:message key="campaign.creative.rotationCriteria.optimisation.tooltip"/>
                                    </ui:hint>
                                </div>
                                <div id="tooltip">
                                    <ui:hint>
                                        <fmt:message key="campaign.creative.rotationCriteria.tooltip"/>
                                    </ui:hint>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </ui:field>
        <ui:field id="CCGOptimization" labelKey="campaign.creative.optimization" cssClass="valignFix">
            <label class="withInput">
                <s:radio name="optimizeCreativeWeightFlag" list="true" template="justradio"
                /><fmt:message key="campaign.creative.optimization.enabled"/>
            </label>
            <label class="withInput">
                <s:radio name="optimizeCreativeWeightFlag" list="false" template="justradio"
                /><fmt:message key="campaign.creative.optimization.disabled"/>
            </label>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<script type="text/javascript">

function checkRotationCriteria() {
    var showRotationCriteria = $('input[type=radio][name=sequentialAdservingFlag]:checked').val();
    if (showRotationCriteria == 'true') {
        $('#rotationCriteria').removeAttr("disabled")
        $('#rotationCriteriaFieldId').show();
    } else {
        $('#rotationCriteriaFieldId').hide();
        $('#rotationCriteria').attr("disabled", "disabled")
    }
}
</script>