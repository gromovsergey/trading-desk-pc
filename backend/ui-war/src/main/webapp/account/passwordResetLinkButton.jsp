<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<script type="text/javascript">
    $(function() {
        $('#passwordResetLinkButton').on('click', function() {
            var params = $(this).closest('form').find(':input').serializeArray();
            params.push({name: 'id', value: ${id}})
            $.post('${_context}/account/user/resetPasswordUrl.action', params, function(data){
                $('#passwordDialog').html(data).dialog({
                    width: 600,
                    resizable: false,
                    title: '${ad:formatMessage("user.passwordResetLink.title")}',
                    modal: true,
                    buttons: [
                        {
                            text: '${ad:formatMessage("form.close")}',
                            click: function(){ $(this).dialog('close'); }
                        }
                    ]
                });
            })
        })
    })
</script>
<div id="passwordDialog" class="hide"></div>
