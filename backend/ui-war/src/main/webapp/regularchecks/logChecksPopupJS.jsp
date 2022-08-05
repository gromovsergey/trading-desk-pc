<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<script type="text/javascript">
function popupLogChecks(entityName, entityId, event) {
    var postData = {entityName: entityName,
                    entityId: entityId};
    var entityPath = entityName == 'group'? 'campaign/group': 'channel/' + entityName;
    $.ajax({
        url:  '${_context}/' + entityPath + '/logChecksPopup.action',
        type: 'POST',
        data: postData,
        dataType: 'html',
        success: function(data) {
            var dialog = UI.Dialog.createDialog(data)
                 .attr('id', 'popup')
                 .focus()
                 .keyup(function(e) {
                         if (e.keyCode == 27) {
                             UI.Dialog.removeAllDialogs();
                         }
                 });
            $('input:text:visible:first', dialog).focus();
            
        }
    });
}
</script>