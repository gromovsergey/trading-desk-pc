<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    function showHideConversionsTracking(check) {
        if ($('#conversionsTrackingFlag').is(":checked")) {
            $("#conversionsTrackingOptions")
                    .show()
                    .find(":input").removeAttr('disabled');
            
            if (check) {
            	$("#conversionsTrackingOptions")
				.find(":input")
            	.prop({checked:true});
        	}
        } else {
            $("#conversionsTrackingOptions")
                    .hide()
                    .find(":input")
                    .attr('disabled', 'disabled')
                    .prop({checked:false});
        }
    }

    function checkAllConversionsUnchecked() {
        if ($('#conversionsTrackingIds').find('input:checked').length == 0) {
            $("#conversionsTrackingFlag")
                    .prop({checked:false});

            $("#conversionsTrackingOptions")
                    .hide()
                    .find(":input")
                    .attr('disabled', 'disabled')
                    .prop({checked:false});
        }
    }

    $(function(){
        showHideConversionsTracking(false);
    });
    
</script>


<ui:section titleKey="ccg.conversions.tracking" id="ccgConversionsTracking">
    <ui:fieldGroup>
        <ui:field errors="conversionTrackingIds" >
            <ul class="chBoxesTree">
                <li>
                    <label class="withInput">
                        <s:checkbox id="conversionsTrackingFlag" name="conversionsTrackingFlag"
                                    onchange="showHideConversionsTracking(true);"/>
                        <s:text name="ccg.conversions.tracking.expanded"/>
                        <s:fielderror><s:param value="'conversionsTracking'"/></s:fielderror>
                    </label>
                </li>
                <li>
                    <div id="conversionsTrackingOptions">
                        <c:choose>
                            <c:when test="${empty availableConversions}">
                                <fmt:message key="ccg.conversions.absent"/>
                            </c:when>
                            <c:otherwise>
                                <ul id="conversionsTrackingIds">
                                    <s:iterator var="conversion" value="availableConversions">
                                        <li>
                                            <label class="withInput">
                                                <c:choose>
                                                    <c:when test="${checkedConversionTrackingIds.contains(conversion.id)}">
                                                        <s:checkbox name="conversionTrackingIds" fieldValue="%{#conversion.id}" value="true" onchange="checkAllConversionsUnchecked();"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <s:checkbox name="conversionTrackingIds" fieldValue="%{#conversion.id}" value="false" onchange="checkAllConversionsUnchecked();"/>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:set var="labelText">
                                                    ${conversion.name}
                                                    <s:if test="#conversion.conversionCategory != @com.foros.model.action.ConversionCategory@OTHER"> (<fmt:message key="${conversion.conversionCategory.nameKey}"/>)</s:if>
                                                </c:set>
                                                <ui:displayStatus displayStatus="${conversion.displayStatus}" cssClass="inline">
                                                    <a href="${_context}/Action/view.action?id=${conversion.id}"><c:out value="${labelText}"/></a>
                                                </ui:displayStatus>
                                            </label>
                                        </li>
                                    </s:iterator>
                                </ul>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </li>
            </ul>
        </ui:field>
    </ui:fieldGroup>
</ui:section>
