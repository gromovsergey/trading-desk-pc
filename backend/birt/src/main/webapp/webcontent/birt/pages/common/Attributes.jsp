<%@ page import="org.eclipse.birt.report.IBirtConstants,
				 org.eclipse.birt.report.session.IViewingSession" %>
<%@ page import="org.eclipse.birt.report.session.ViewingSessionUtil" %>
<%@ page import="org.eclipse.birt.report.utility.ParameterAccessor" %>
<%@ page import="com.foros.config.ConfigParameters" %>

<%-- Map Java attributes to Javascript constants --%>
<script type="text/javascript">
// <![CDATA[
            
    Constants.nullValue = '<%= IBirtConstants.NULL_VALUE %>';
    
	// Request attributes
	if ( !Constants.request )
	{
		Constants.request = {};
	}
	Constants.request.format = '<%= ParameterAccessor.getFormat(request) %>';
	Constants.request.rtl = <%= ParameterAccessor.isRtl( request ) %>;
	Constants.request.isDesigner = <%= ParameterAccessor.isDesigner() %>;
	Constants.request.servletPath = "<%= request.getAttribute( "ServletPath" ) %>".substr(1);
	<%  IViewingSession viewingSession = ViewingSessionUtil.getSession(request);
		String subSessionId = null;
		if ( viewingSession != null )
		{
			subSessionId = viewingSession.getId();
		}%>
	Constants.viewingSessionId = <%= subSessionId!=null?"\"" + subSessionId + "\"":"null" %>;
    Constants.EXPORT_XLS_THRESHOLD = Math.ceil(<%= application.getInitParameter(ConfigParameters.BIRT_XLS_MAX_ROWS.getName()) %>*1.5);

// ]]>
</script>
