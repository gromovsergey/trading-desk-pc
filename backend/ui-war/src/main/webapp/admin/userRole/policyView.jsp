<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<s:set var="currentPolicy" value="advertiserPolicy"/>
<s:if test="#currentPolicy.hasPolicy()">
    <table>
        <tr>
            <td>
                <ui:header styleClass="level2">
                    <h2><ad:resolveGlobal resource="permissionHeader" id="advertiser"/></h2>
                </ui:header>
            </td>
        </tr>
        <tr>
            <td>
                <%@include file="policyTableView.jsp"%>
            </td>
        </tr>
    </table>
</s:if>
<s:set var="currentPolicy" value="publisherPolicy"/>
<s:if test="#currentPolicy.hasPolicy()">
    <table>
        <tr>
            <td>
                <ui:header styleClass="level2">
                    <h2><ad:resolveGlobal resource="permissionHeader" id="publisher"/></h2>
                </ui:header>
            </td>
        </tr>
        <tr>
            <td>
                <%@include file="policyTableView.jsp"%>
            </td>
        </tr>
    </table>
</s:if>
<s:set var="currentPolicy" value="ispPolicy"/>
<s:if test="#currentPolicy.hasPolicy()">
    <table>
        <tr>
            <td>
                <ui:header styleClass="level2">
                    <h2><ad:resolveGlobal resource="permissionHeader" id="isp"/></h2>
                </ui:header>
            </td>
        </tr>
        <tr>
            <td>
                <%@include file="policyTableView.jsp"%>
            </td>
        </tr>
    </table>
</s:if>
<s:set var="currentPolicy" value="cmpPolicy"/>
<s:if test="#currentPolicy.hasPolicy()">
    <table>
        <tr>
            <td>
                <ui:header styleClass="level2">
                    <h2><ad:resolveGlobal resource="permissionHeader" id="cmp"/></h2>
                </ui:header>
            </td>
        </tr>
        <tr>
            <td>
                <%@include file="policyTableView.jsp"%>
            </td>
        </tr>
    </table>
</s:if>
<s:set var="currentPolicy" value="internalPolicy"/>
<s:if test="#currentPolicy.hasPolicy()">
    <table>
        <tr>
            <td>
                <ui:header styleClass="level2">
                    <h2><ad:resolveGlobal resource="permissionHeader" id="internal"/></h2>
                </ui:header>
            </td>
        </tr>
        <tr>
            <td>
                <%@include file="policyTableView.jsp"%>
            </td>
        </tr>
    </table>
</s:if>
<s:set var="currentPolicy" value="adminPolicy"/>
<s:if test="#currentPolicy.hasPolicy()">
    <table>
        <tr>
            <td>
                <ui:header styleClass="level2">
                    <h2><ad:resolveGlobal resource="permissionHeader" id="admin"/></h2>
                </ui:header>
            </td>
        </tr>
        <tr>
            <td>
                <%@include file="policyTableView.jsp"%>
            </td>
        </tr>
    </table>
</s:if>
<s:set var="currentPolicy" value="apiPolicy"/>
<s:if test="#currentPolicy.hasPolicy()">
    <table>
        <tr>
            <td>
                <ui:header styleClass="level2">
                    <h2><ad:resolveGlobal resource="permissionHeader" id="api"/></h2>
                </ui:header>
            </td>
        </tr>
        <tr>
            <td>
                <%@include file="policyTableView.jsp"%>
            </td>
        </tr>
    </table>
</s:if>
