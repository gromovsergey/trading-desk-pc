<script type="text/javascript">

    $(function(){
        $('#backButtonId').click(function(){
            $('#CCGForm').attr('action', $('#backActionId').val());
        });
    });

</script>

<div class="wrapper">

    <ui:button message="form.save" type="submit"/>

    <c:if test="${wizardFunctionalityEnabled}">
        <s:hidden id="backActionId" value="new%{#attr.entityName}Settings.action"/>
        <ui:button message="form.back" type="submit" id="backButtonId"/>
    </c:if>

    <s:if test="id == null">
        <ui:button message="form.cancel" onclick="location='${_context}/campaign/view.action?id=${campaign.id}';" type="button"/>
    </s:if>
    <s:else>
        <ui:button message="form.cancel" onclick="location='view${pageExt}.action?id=${id}';" type="button"/>
    </s:else>

</div>
