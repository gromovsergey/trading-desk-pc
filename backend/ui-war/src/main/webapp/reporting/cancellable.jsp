<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:pageHeadingByTitle/>

<ui:reportForm>
    <s:hidden id="cancellationTokenHidden" name="cancellationToken"/>
    <script type="text/javascript">
        $(function() {
            var form  = $( "#reportForm" );
            var closeOnDownloadComplete = false;
            var ajaxLoader = new UI.AjaxLoader();
            // can't be interrupted
            ajaxLoader.interrupt = function(){};

            var cancelAsync = function() {
                $.ajax({
                    type: "POST",
                    async: false,
                    url: "cancelAsync.action",
                    data: form.find("[name='cancellationToken'],[name='PWSToken']").serializeArray()
                });
            };

            form.submit(function( event ) {
                ajaxLoader.show();
                $(window).bind("beforeunload", cancelAsync);

                var fileDownloadComplete = function () {
                    ajaxLoader.hide();
                    $(window).unbind("beforeunload", cancelAsync);
                    if (closeOnDownloadComplete) {
                        window.close();
                    }
                };

                $.fileDownload($(this).prop('action'), {
                    httpMethod: "POST",
                    cookieName: "download-" + form.find("[name='cancellationToken']").val(),
                    successCallback: fileDownloadComplete,
                    failCallback: fileDownloadComplete,
                    data: $(this).serialize()
                });
                event.preventDefault();
            });

            var format = form.find("[name='format']").val();
            if (format == null || format.length == 0 || format == "HTML") {
                $(document).ajaxStart(function() {
                    $(window).bind("beforeunload", cancelAsync);
                });
                $(document).ajaxStop(function() {
                    $(window).unbind("beforeunload", cancelAsync);
                });
                $.ajax({
                    type: "POST",
                    url: "cancellableRun.action",
                    data: form.serialize(),
                    success: function(data) {
                        $('#result').append(data)
                    }
                });
            } else {
                closeOnDownloadComplete = true;
                form.submit();
            }
        });
    </script>

    <div id="result"></div>
</ui:reportForm>