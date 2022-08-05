<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">

    function checkTags (tagsName){
        var b = false;
        var value = tagsName.toLowerCase();
        $('#tags option').each(function(){
            if(value == this.value.toLowerCase()){
                b = true;
                return false;
            }
        });
        return b;
    }

    $(function(){
        $('#removeTagButton').on('click', function(e){
            e.preventDefault();
            $('#tags option:selected').remove();
            $('#tags').click();
        });

        $('#siteSave').on('submit', function(e){
            $('#tags option').prop('selected', true);
        })
    });

    function checkAllRadios(jqRadio, val) {
        jqRadio.each(function(){
            if ($(this).val()   == val) {
                $(this).prop({"checked":true});
            }
        });
    }
</script>

<s:form action="%{#attr.moduleName}/%{#attr.isCreatePage?'create':'update'}" id="siteSave">

    <ui:pageHeadingByTitle/>

    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <s:hidden name="account.id"/>

    <ui:errorsBlock>
        <s:fielderror><s:param value="'version'"/></s:fielderror>
        <s:fielderror><s:param value="'error'"/></s:fielderror>
        <s:fielderror><s:param value="'frequencyCapDisabled'"/></s:fielderror>
        <s:fielderror><s:param value="'noAdsTimeoutDisabled'"/></s:fielderror>
        <s:fielderror><s:param value="'siteLevelCreativeExclusionError'"/></s:fielderror>
    </ui:errorsBlock>

    <ui:section errors="errors.duplicate">
        <ui:fieldGroup>
            
            <ui:field labelKey="site.name" labelForId="name" required="true" errors="name">
                <s:textfield name="name" cssClass="middleLengthText" maxlength="100"/>
            </ui:field>
            
            <ui:field labelKey="site.url" labelForId="siteUrl" required="true" errors="siteUrl">
                <s:textfield name="siteUrl" cssClass="middleLengthText" maxlength="255"/>
            </ui:field>

            <c:choose>
                <c:when test="${id == null || siteCategory.id == null || ad:isPermitted0('PublisherEntity.advanced') || fieldErrors.containsKey('siteCategory')}">
                    <ui:field labelKey="site.category" labelForId="siteCategoryId" required="true" errors="siteCategory">
                        <s:select name="siteCategory.id" id="siteCategoryId" cssClass="middleLengthText"
                             headerValue="%{getText('form.select.pleaseSelect')}" headerKey=""
                             list="siteCategories" value="siteCategory.Id"
                             listKey="id" listValue="name">
                        </s:select>
                    </ui:field>
                    <ui:field>
                        <fmt:message key="category.note"/>
                    </ui:field>
                </c:when>
                <c:otherwise>
                    <ui:simpleField labelKey="site.category" value="${siteCategory.name}"/>
                    <s:hidden name="siteCategory.id"/>
                    <s:hidden name="siteCategory.name"/>
                </c:otherwise>
            </c:choose>

            <s:if test="allowFreqCaps">
                <ui:field labelKey="site.noAdsTimeout" labelForId="noAdsTimeout" id="_noAdsTimeout" required="true" errors="noAdsTimeout">
                    <s:textfield name="noAdsTimeout" id="noAdsTimeout" cssClass="middleLengthText" maxlength="10"/>
                </ui:field>
            </s:if>
            
        </ui:fieldGroup>
    </ui:section>

    <s:if test="allowFreqCaps">
        <ui:frequencyCapEdit fcPropertyName="frequencyCap" id="_frequencyCap"/>
    </s:if>


    <s:if test="allowAdvExclusions">

        <div id="_categoryExclusions" class="logicalBlock">
            <h2><fmt:message key="site.advertiserExclusions"/></h2>

            <s:fielderror><s:param value="'packedCategories'"/></s:fielderror>
            <table class="formFields">
                <tr>
                    <td class="field">
                        <table class="dataView" id="_visualCategories">
                            <thead>
                            <tr>
                                <th>
                                    <s:text name="site.visualCategory"/>
                                </th>
                                <th>
                                    <s:text name="site.accept"/>
                                    <ui:button message="form.all" type="button"
                                               onclick="checkAllRadios($('#_visualCategories').find('input[type=radio]'), 'A')"/>
                                </th>
                                <th>
                                    <s:text name="site.reject"/>
                                    <ui:button message="form.all" type="button"
                                               onclick="checkAllRadios($('#_visualCategories').find('input[type=radio]'), 'R')"/>
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            <s:iterator value="visualCategories" var="visualCategory" status="row">
                                <tr>
                                    <td>
                                        <s:hidden name="visualCategories[%{#row.index}].creativeCategory.id"/>
                                        <label><c:out
                                                value="${ad:localizeName(visualCategory.creativeCategory.name)}"/></label>
                                    </td>
                                    <td>
                                        <input type="radio" name="visualCategories[${row.index}].approval"
                                               value="A" ${visualCategory.approval =='ACCEPT' ? 'checked="checked"' : 'false'}/>
                                    </td>
                                    <td>
                                        <input type="radio" name="visualCategories[${row.index}].approval"
                                               value="R" ${visualCategory.approval =='REJECT' ? 'checked="checked"' : 'false'}/>
                                    </td>
                                </tr>
                            </s:iterator>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td class="field">
                        <s:set var="isAllowAdvExclusionApproval" value="allowAdvExclusionApproval"/>
                        <table class="dataView" id="_contentCategories">
                            <thead>
                            <tr>
                                <th>
                                    <s:text name="site.contentCategory"/>
                                </th>
                                <th>
                                    <s:text name="site.accept"/>
                                    <ui:button message="form.all" type="button"
                                               onclick="checkAllRadios($('#_contentCategories').find('input[type=radio]'), 'A')"/>
                                </th>
                                <s:if test="isAllowAdvExclusionApproval">
                                    <th>
                                        <s:text name="site.approval"/>
                                        <ui:button message="form.all" type="button"
                                                   onclick="checkAllRadios($('#_contentCategories').find('input[type=radio]'), 'P')"/>
                                    </th>
                                </s:if>
                                <th>
                                    <s:text name="site.reject"/>
                                    <ui:button message="form.all" type="button"
                                               onclick="checkAllRadios($('#_contentCategories').find('input[type=radio]'), 'R')"/>
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            <s:iterator value="contentCategories" var="contentCategory" status="row">
                                <tr>
                                    <td>
                                        <s:hidden name="contentCategories[%{#row.index}].creativeCategory.id"/>
                                        <label><c:out
                                                value="${ad:localizeName(contentCategory.creativeCategory.name)}"/></label>
                                    </td>
                                    <td>
                                        <input type="radio" name="contentCategories[${row.index}].approval"
                                               value="A" ${contentCategory.approval =='ACCEPT' ? 'checked="checked"' : 'false'}/>
                                    </td>
                                    <s:if test="isAllowAdvExclusionApproval">
                                        <td>
                                            <input type="radio" name="contentCategories[${row.index}].approval"
                                                   value="P" ${contentCategory.approval =='APPROVAL' ? 'checked="checked"' : 'false'}/>
                                        </td>
                                    </s:if>
                                    <td>
                                        <input type="radio" name="contentCategories[${row.index}].approval"
                                               value="R" ${contentCategory.approval =='REJECT' ? 'checked="checked"' : 'false'}/>
                                    </td>
                                </tr>
                            </s:iterator>
                            </tbody>
                        </table>

                    </td>
                </tr>
            </table>
            <ui:section titleKey="site.excludedTags" tipKey="site.excludedTags.hint">
                <ui:fieldGroup>

                    <ui:field labelKey="site.currentlyExcludedTags" errors="selectedTags">
                        <ui:autocomplete
                            id="selectedTags"
                            source="Autocomplete.selectedTags.getTags"
                            selectedItems="${tagCategories}"
                            selectedNameKey="creativeCategory.defaultName"
                            cssClass="middleLengthText"
                            isMultiSelect="true"
                            minLength="1"
                            editable="true"
                            maxLength="100"
                            addInLowercase="false"
                        >
                            <script type="text/javascript">
                                Autocomplete.selectedTags.getTags = function(request, response){
                                    UI.Data.get('excludeTags', {query:request.term}, function(data) {
                                        var opts = $.map($('tag', data), function(el){
                                            var tagName = $('name', el).text();
                                            return new $.custom.extAutocomplete.Option(tagName, tagName);
                                        });
                                        response(opts);
                                    });
                                };
                                
                            </script>
                        </ui:autocomplete>
                    </ui:field>

                </ui:fieldGroup>
            </ui:section>

        </div>

    </s:if>
    <ui:section titleKey="site.notes" errors="notes">
        <s:textarea name="notes" id="notes" cssClass="middleLengthText"/>
    </ui:section>

    <div class="wrapper">
        <ui:button message="form.save" id="saveButton" type="submit"/>
        <s:if test="id == null || id == ''">
            <ui:button message="form.cancel" onclick="location='main.action${ad:accountParam('?accountId',account.id)}';" type="button" />
        </s:if>
        <s:else>
            <ui:button message="form.cancel" onclick="location='view.action?id=${id}';" type="button"/>
        </s:else>
    </div>
  </s:form>
