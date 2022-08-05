<%@ tag description="UI Tab" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"    prefix="fmt" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="url" %>
<%@ attribute name="form" type="java.lang.String" %>
<%@ attribute name="width" %>
<%@ attribute name="height" %>
<%@ attribute name="loadOnShow" type="java.lang.Boolean" %>
<%@ attribute name="onload" %>

<div class="logicalBlock" id="${pageScope.id}" style="width:${pageScope.width};height:${pageScope.height}">
    <jsp:doBody />
</div>


<script type="text/javascript">
    $().ready(function(){
        var ajaxPanel = $('#${pageScope.id}').ajaxPanel({
            url: '${pageScope.url}'
            <c:if test="${not empty pageScope.form}">
                ,form: $('#${pageScope.form}')
            </c:if>
            <c:if test="${not empty pageScope.onload}">
                ,onload:function(){${pageScope.onload}}
            </c:if>
        });
        <c:if test="${pageScope.loadOnShow}">
            ajaxPanel.loadOnShow();
        </c:if>
    });
</script>
