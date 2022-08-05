<%@ tag description="js/css library link" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="libName" required="true" %>

<c:set var="prefix" value="/thirdparty"/>
<c:choose>
    <c:when test="${pageScope.libName == 'jquery'}">
        <script type="text/javascript" src="${prefix}/jQuery/1.8.2/jquery.min.js"></script>
    </c:when>
    <c:when test="${pageScope.libName == 'jquery-ui'}">
        <script type="text/javascript" src="${prefix}/jQuery-UI/1.9.2/jquery-ui.min.js"></script>
    </c:when>
    <c:when test="${pageScope.libName == 'jquery-css'}">
        <link rel="stylesheet" href="${prefix}/jQuery-UI/1.9.2/forosui/jquery-ui.css">
    </c:when>
    <c:when test="${pageScope.libName == 'codemirror'}">
        <link type="text/css" rel="stylesheet" href="${prefix}/codemirror/4.7/lib/codemirror.css" />
        <script type="text/javascript" src="${prefix}/codemirror/4.7/lib/codemirror.js"></script>
        <script type="text/javascript" src="${prefix}/codemirror/4.7/mode/xml/xml.js"></script>
        <script type="text/javascript" src="${prefix}/codemirror/4.7/mode/javascript/javascript.js"></script>
        <script type="text/javascript" src="${prefix}/codemirror/4.7/mode/css/css.js"></script>
        <script type="text/javascript" src="${prefix}/codemirror/4.7/mode/htmlmixed/htmlmixed.js"></script>
        <script type="text/javascript">
            $(function(){
                $('.html_highlight').each(function(){
                    CodeMirror.fromTextArea(this, {
                        lineNumbers:    true,
                        lineWrapping:   true,
                        mode:           "text/html",
                        readOnly:       $(this).data('readonly')
                    });
                });
            });
        </script>
    </c:when>
</c:choose>