<c:if test="${ccgRate.rateType == 'CPA'}">
    <ui:section titleKey="ccg.action.tracking.pixel" >
        <ui:fieldGroup>
            <ui:field labelKey="ccg.pixel.code">
                <input type="text" value="${pixelCode}" class="middleLengthText" readonly="true">
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</c:if>
