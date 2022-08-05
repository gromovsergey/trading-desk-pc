<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head profile="http://www.w3.org/2005/10/profile">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>AdServerUI Login Error</title>
        <link rel="icon" href="/images/logo.png" />
        <link rel="shortcut icon" href="/images/logo.png" />
        <ui:externalLibrary libName="jquery-css"/>
        <ui:externalLibrary libName="jquery"/>
        <ui:externalLibrary libName="jquery-ui"/>
        <ui:javascript fileName="common.js" />
    </head>
    <body>
        <!--
           HTTP 500 Internal Server Error instead of a meaningful Error Message on IE
           This is an IE feature. When an HTTP error page retrieved from the server is smaller than 512 bytes, then IE will by default show a "Friendly" error page like the one you're facing, which is configureable by Tools > Internet Options > Advanced > Uncheck "Show Friendly Error Message" in the browser. Other (real) browsers does not have this feature.
           Making your HTTP error page a little larger than 512 bytes should workaround this IE feature. You could add some extra meta headers, add some whitespace to indent code, add some more semantic markup following your site's standard layout, add a large HTML comment, etc.
        -->
        <form method="POST" action="/index.action" id="errorForm">
            <h1>AdServerUI Login</h1>
            <div align="center">
                <table>
                    <tr>
                        <td>Authentication Failed!</td>
                        <td><a href="#" onclick="document.getElementById('errorForm').submit();">Re-enter your user name and password.</a> </td>
                    </tr>
                </table>
            </div>
        </form>
    </body>
</html>
