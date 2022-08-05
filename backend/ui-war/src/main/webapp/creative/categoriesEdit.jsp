<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<script type="text/javascript">
    $(function() {
        $('#saveForm').on('submit', function(e){
            $('#visualCategories option').prop('selected', true);
            $('#contentCategories option').prop('selected', true);
        });
    });
</script>

<c:if test="${!textCreative}">
    <ui:section id="visualCategoriesLabelDiv" titleKey="creative.categories.visual">
        <div id="visualCategoriesLabel">
            <span class="simpleText">
                <ad:commaWriter items="${template.categories}" var="category" label="name"><c:out value="${ad:localizeName(category.name)}"/></ad:commaWriter>
            </span>
        </div>
    </ui:section>
    <ui:section id="visualCategoriesSelect" titleKey="creative.categories.visual" errors="visualCategories" mandatory="true" cssStyle="min-width:100%;" cssClass="widest">
        <table class="grouping">
            <tr>
                <td>
                    <ui:optiontransfer id="selectVCId" selId="selectVCId2" name="selectedVisualCategories"
                           list="${availableVisualCategories}"
                           selList="${visualCategories}"
                           cssClass="smallLengthText2" size="9"
                           saveSorting="true" escape="true"
                           titleKey="creative.categories.visual.available" selTitleKey="creative.categories.visual.selected"/>
                </td>
            </tr>
        </table>
    </ui:section>
</c:if>

    <ui:section titleKey="creative.categories.content" errors="contentCategories" mandatory="${!textCreative}" cssStyle="min-width:100%;" cssClass="widest">
        <table class="grouping">
            <tr>
                <td>
                    <ui:optiontransfer id="selectCCId" selId="selectCCId2" name="selectedContentCategories"
                           list="${availableContentCategories}"
                           selList="${contentCategories}"
                           cssClass="smallLengthText2" size="9"
                           saveSorting="true" escape="true"
                           titleKey="creative.categories.content.available" selTitleKey="creative.categories.content.selected"/>
                </td>
            </tr>
        </table>
    </ui:section>

<c:if test="${!textCreative}">
    <ui:section titleKey="creative.categories.tags">
        <ui:fieldGroup>
            <ui:field labelKey="creative.categories.tags.current" errors="selectedTags">
                <ui:autocomplete
                    id="selectedTags"
                    source="Autocomplete.selectedTags.getTags"
                    selectedItems="${tags}"
                    selectedNameKey="name"
                    cssClass="middleLengthText"
                    isMultiSelect="true"
                    minLength="1"
                    maxLength="100"
                    editable="true"
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
</c:if>
