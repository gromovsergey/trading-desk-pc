<%-----------------------------------------------------------------------------
	Copyright (c) 2004 Actuate Corporation and others.
	All rights reserved. This program and the accompanying materials 
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html
	
	Contributors:
		Actuate Corporation - Initial implementation.
-----------------------------------------------------------------------------%>
<%@ page contentType="text/html; charset=utf-8" %>
<%@ page session="false" buffer="none" %>
<%@ page import="com.foros.birt.web.util.ExceptionUtils,
                 org.eclipse.birt.report.utility.ParameterAccessor,
                 org.eclipse.birt.report.resource.BirtResources,
                 java.io.PrintWriter" %>

<%-----------------------------------------------------------------------------
	Expected java beans
-----------------------------------------------------------------------------%>
<jsp:useBean id="error" type="java.lang.Exception" scope="request" />

<%-----------------------------------------------------------------------------
    Error content
-----------------------------------------------------------------------------%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
    <HEAD>
        <TITLE>
            <%= BirtResources.getMessage( "birt.viewer.title.error" )%>
        </TITLE>
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8">
        <LINK REL="stylesheet" HREF="<%= request.getContextPath( ) + "/webcontent/birt/styles/style.css" %>" TYPE="text/css">
    </HEAD>
    <BODY>
        <!--
          HTTP 500 Internal Server Error instead of a meaningful Error Message on IE
          This is an IE feature. When an HTTP error page retrieved from the server is smaller than 512 bytes, then IE will by default show a "Friendly" error page like the one you're facing, which is configureable by Tools > Internet Options > Advanced > Uncheck "Show Friendly Error Message" in the browser. Other (real) browsers does not have this feature.
          Making your HTTP error page a little larger than 512 bytes should workaround this IE feature. You could add some extra meta headers, add some whitespace to indent code, add some more semantic markup following your site's standard layout, add a large HTML comment, etc.
        -->
        <TABLE CLASS="BirtViewer_Highlight_Label">
            <TR><TD NOWRAP>
                <%=ParameterAccessor.htmlEncode(ExceptionUtils.getMessageByException(error))%>
            </TD></TR>
        </TABLE>
    </BODY>
</HTML>