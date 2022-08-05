<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<tiles:importAttribute ignore="true" scope="request" name="titleProperty"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-GB" xml:lang="en-GB">
  <head profile="http://www.w3.org/2005/10/profile">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <tiles:importAttribute name="title" scope="request" ignore="true"/>
    <tiles:insertAttribute name="title">
        <tiles:putAttribute name="titleProperty" type="string" value="${titleProperty}" />
    </tiles:insertAttribute>
	<link rel="icon" href="/images/logo.png" />
	<link rel="shortcut icon" href="/images/logo.png" />
    <ui:stylesheet fileName="common.css"/>
    
    <ui:externalLibrary libName="jquery-css"/>
    <ui:externalLibrary libName="jquery"/>
    <ui:externalLibrary libName="jquery-ui"/>
    
    <ui:javascript fileName="jquery-custom.js"/>
    
    <ui:javascript fileName="common.js"/>
  </head>
  <body>
    <table id="root">
      <tr id="header">
        <td class="rootCell">
            <div id="headContainer">
                <a href="/login/login"><div id="applicationLogo">Target RTB</div></a>
            </div>
        </td>
      </tr>
      <tr id="content">
        <td class="rootCell">
          <div class="contentBody">
            <tiles:insertAttribute name="body"/>
          </div>
        </td>
      </tr>
      <tr id="footer">
        <td>
            <div id="appVersion">
            </div>
            <div id="copyright">
                <fmt:message key="form.copyright"/>
            </div>
        </td>
      </tr>
    </table>
  </body>
</html>