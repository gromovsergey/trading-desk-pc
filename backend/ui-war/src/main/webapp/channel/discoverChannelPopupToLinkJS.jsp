<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<script type="text/javascript">
var channelsHTML = '';

function popupLinkToDiscoverChannelList(channelListId, channels, fromDiscoverChannelList, event) {
    var postData = {};
    if (fromDiscoverChannelList) {
        postData = {fromDiscoverChannelList: true};
    }
    for (var i = 0; i < channels.length; i++) {
        var propertyNameBase = 'discoverChannels[' + i + ']';
        postData[propertyNameBase + '.id'] = channels[i].id;
        postData[propertyNameBase + '.version'] = channels[i].version;
    }
    $.ajax({
        url: '/admin/DiscoverChannel/linkDiscoverChannelPopup.action',
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
            channelsHTML = '<input  type="hidden" value="' + channelListId + '" id="currentChannelListId">';
            for (var i = 0; i < channels.length; i++) {
                channelsHTML += '<input  type="hidden" value="' + channels[i].id + '" name="discoverChannels[' + i + '].id"><input type="hidden" value="' + channels[i].version + '" name="discoverChannels[' + i + '].version">';
            }
            $('#discoverChannelPopupForm').append(channelsHTML);
            $('input:text:visible:first', dialog).focus();
            
        }
    });
}
</script>
