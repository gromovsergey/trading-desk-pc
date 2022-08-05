<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<html>
    <head>
        <ui:stylesheet fileName="common.css" />
    </head>
    <body>
        <table style="width:100%;height:100%;text-align:center;vertical-align:middle;">
            <tr>
                <td>
                    <s:fielderror cssClass="validation_error"/>
                </td>
            </tr>
        </table>
    </body>
</html>
