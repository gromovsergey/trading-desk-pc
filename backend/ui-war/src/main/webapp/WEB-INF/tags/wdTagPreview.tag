<%@ tag language="java" body-content="empty" description="WD Tag preview frame" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="width" required="true" %>
<%@ attribute name="height" required="true" %>
<%@ attribute name="src" required="true" %>

<iframe name="${pageScope.id}" id="${pageScope.id}" width="${pageScope.width}" height="${pageScope.height}" src="${pageScope.src}" frameborder="0" marginwidth="0" marginheight="0" scrolling="auto"> </iframe>

<script type="text/javascript">
    $('#${pageScope.id}').load(function(){
        try {
            var iHeight = $(this).contents().find('body').height();
            if (iHeight && iHeight > $(this).height()) {
                $(this).height(iHeight+10);
            }
        } catch(e) {
            // console.log(e);
        }
    });
</script>
