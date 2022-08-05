<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
    <table>
        <s:if test="!isInternal()">
            <c:set var="usersCount" value="${users.size()}"/>
            <c:if test="${usersCount > 0}">
                <tr>
                    <td colspan="2">
                        <div style="margin-top: 20px; margin-bottom: 20px"><span class="infos"><fmt:message key="account.context.switch.error"/></span></div>
                    </td>
                </tr>
                <form id="errorSwitchUserForm" method="get" action="/login/j_spring_switch_user">
                    <input type="hidden" name="j_return_url" value="${_url}">
                <c:choose>
                    <c:when test="${usersCount == 1}">
                        <tr>
                            <td>
                                <fmt:message key="account.switchto"/>
                            </td>
                            <td>
                                <span>
                                    <input type="hidden" name="j_switch_to_user_id" value="${users[0].id}"/>
                                    <a href="#" onclick="$('#errorSwitchUserForm').submit(); return false;"><c:out value="${users[0].account.name}"/></a>
                                </span>
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="2">
                                <select id="switchableUserId" name="j_switch_to_user_id" class="middleLengthText">
                                    <c:forEach items="${users}" var="switchableUser">
                                        <option value="${switchableUser.id}"><c:out value="${switchableUser.account.name}"/></option>
                                    </c:forEach>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <div class="wrapper" style="margin-top: 30px">
                                    <ui:button message="account.switch" type="button" onclick="$('#errorSwitchUserForm').submit();"/>
                                </div>
                            </td>
                        </tr>

                    </c:otherwise>
                </c:choose>
                </form>
            </c:if>
        </s:if>
    </table>
