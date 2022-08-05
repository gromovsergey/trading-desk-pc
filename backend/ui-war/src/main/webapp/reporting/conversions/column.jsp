<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>


<tiles:useAttribute name="column"/>
<s:set var="fullColumn" value="'report.output.field.' + #attr.column"/>
<td class="withField" width="150" >
    <label class="withInput">
            <s:checkbox name="columns"
                        id="report_output_field_%{#attr.column}"
                        value="selected(#fullColumn)"
                        fieldValue="%{#fullColumn}"
                        disabled="!available(#fullColumn)"
                        onclick=""
            />
           <fmt:message key="${fullColumn}"/>
    </label>
</td>