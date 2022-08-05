<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<tiles:useAttribute name="container"/>

<div id="logDetails" style="display: none"></div>

<script type="text/javascript">
    $(function() {
        $("#logDetails").dialog({
            autoOpen: false,
            width: 570,
            modal: true,
            resizable: false,
            open: function(event, ui) {
                $('.ui-widget-overlay').bind('click', function(){ $("#logDetails").dialog('close'); });
            }
        });

        $("${container}").on("click", ".logDetails", function () {
            var title = $(this).attr("targetTitle") || "";
            $( "#logDetails" ).load(this.href, function () {
                $("#logDetails").dialog( "option", "title", title).dialog("open");
            });
            return false;
        })
    });
</script>

