<%@ tag language="java" body-content="empty" description="Displays Activate/Inactivate buttons" %>
<%@ attribute name="total" required="true"%>
<%@ attribute name="totalHasMore" type="java.lang.Boolean"%>
<%@ attribute name="selectedNumber" required="true"%>
<%@ attribute name="handler" required="true"%>
<%@ attribute name="pageSize" required="true"%>
<%@ attribute name="visiblePagesCount"%>
<%@ attribute name="displayHeader" required="true"%>
<%@ attribute name="withoutNumbers" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="model" class="com.foros.web.taglib.model.PagesModel">
    <jsp:setProperty name="model" property="total" value="${total}"/>
    <jsp:setProperty name="model" property="selectedNumber" value="${selectedNumber}"/>
    <jsp:setProperty name="model" property="pageSize" value="${pageSize}"/>
    <jsp:setProperty name="model" property="visiblePageCount" value="${visiblePagesCount}"/>
</jsp:useBean>

<c:choose>
    <c:when test="${pageScope.withoutNumbers}">
        <c:if test="${displayHeader && (not model.first || (model.first && (model.total > model.pageSize)))}">
            <div class="paginationHeader">
                <fmt:message key="paging.records.summary">
                    <fmt:param value="${model.startCount}"/>
                    <fmt:param value="${model.total > model.pageSize ? model.pageSize*model.selectedNumber : (model.selectedNumber-1)*model.pageSize+model.total}"/>
                </fmt:message>
            </div>
        </c:if>
        
        <div class="paginationButtons paginationHeader">
            <c:if test="${not model.first}">
                <fmt:message var="buttText" key="paging.prev"/>
                <a href="#${model.selectedNumber-1}" data-page="${model.selectedNumber-1}" class="button pagingButton">${buttText}</a>
            </c:if>
            <c:if test="${not model.first && model.total-model.pageSize > 0}">
                <span class="delimiter">|</span>
            </c:if>
            <c:if test="${model.total-model.pageSize > 0}">
                <fmt:message var="buttText" key="paging.next"/>
                <a href="#${model.selectedNumber+1}" data-page="${model.selectedNumber+1}" class="button pagingButton">${buttText}</a>
            </c:if>
        </div>
    </c:when>
    <c:otherwise>
        <c:if test="${model.pagingNeeded}">
            <c:if test="${displayHeader}">
                <div class="paginationHeader">
                    <fmt:message key="${pageScope.totalHasMore ? 'paging.recordsMore' : 'paging.records'}">
                        <fmt:param value="${model.startCount}"/>
                        <fmt:param value="${model.endCount}"/>
                        <fmt:param value="${model.total}"/>
                    </fmt:message>
                </div>
            </c:if>
            <div class="paginationButtons paginationHeader">
                <c:if test="${not model.first}">
                    <fmt:message var="buttText" key="paging.prev"/>
                    <a href="#${model.selectedNumber-1}" data-page="${model.selectedNumber-1}" class="button pagingButton">${buttText}</a>
                    <span class="delimiter">|</span>
                </c:if>
                <c:if test="${not pageScope.withoutNumbers}">
                    <c:if test="${model.less}">...</c:if>
                    <c:forEach items="${model.pages}" var="page">
                        <c:choose>
                            <c:when test="${not page.selected}">
                                <a href="#${page.number}" data-page="${page.number}" class="button pagingButton">${page.number}</a>
                            </c:when>
                            <c:otherwise>
                                <strong>${page.number}</strong>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${page.number != model.count}">
                            <span class="delimiter">|</span>
                        </c:if>
                    </c:forEach>
                    <c:if test="${model.more}">...</c:if>
                </c:if>
                <c:if test="${not model.last}">
                    <span class="delimiter">|</span>
                    <fmt:message var="buttText" key="paging.next"/>
                    <a href="#${model.selectedNumber+1}" data-page="${model.selectedNumber+1}" class="button pagingButton">${buttText}</a>
                </c:if>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>
