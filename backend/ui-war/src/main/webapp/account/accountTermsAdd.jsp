<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<style type="text/css">
   .file-name {
   width: 120px;
   }
</style>

<script type="text/javascript">
var MAX_SIZE = 3;
var terms = [
             <c:forEach items="${terms}" var="term" varStatus="status">
             '<c:out value="${term.name}"/>'<c:if test="${not status.last}">,</c:if>
             </c:forEach>
             ];
function extractFileName(fileName) {
    return fileName.replace(/^.*\\/, '');
}

function appendInputFile() {
    var div = $('<tr><td><input type="file" name="fileToUpload" class="middleLengthText" /><span class="file-name" style="display:none"></span></td><td><a style="display:none" href="javascript:void(false);" onclick="removeFile($(this)); return false;" class="button "><fmt:message key="form.delete"/></a></td></tr>').appendTo($('#addSection'));
    div.find('input[name="fileToUpload"]').change(addFile);
}
function removeFile(elem) {
    var size = $('#addSection').find('input:hidden').size();
    elem.parent().parent().remove();
    if (size == MAX_SIZE) {
        appendInputFile();
    }
}

function addFile() {
    var fileName = $(this).val(); 
    if (fileName == '') return;

    fileName = extractFileName(fileName);
    var fileInput = $(this).hide();
    var fileNameSpan = fileInput.parent().find('span.file-name').text(fileName).show();
    fileNameSpan.parent().parent().find('a').show();
    var size = $('#addSection').find('tr').size();
    if (size < MAX_SIZE) {
        appendInputFile();
    }
}

function findDuplicates(terms) {
    var sorted_arr = terms.sort();
    var results = [];
    for (var i = 0; i < sorted_arr.length - 1; i += 1) {
        if (sorted_arr[i + 1] == sorted_arr[i]) {
            if ($.inArray(sorted_arr[i], results) < 0) {
                results.push(sorted_arr[i]);
            }
        }
    }
    return results;
}

function doUpload(){
    var updatedTerms = terms.slice(0);
    var canUpload = false;
    $('input[name="fileToUpload"]').each(function(index) {
        var fileName = $.trim($(this).val());
        if (fileName.length > 0) {
            fileName = extractFileName(fileName);
            updatedTerms.push(fileName);
            canUpload = true;
        }
    });
    if (!canUpload) {
        alert('${ad:formatMessage("fileman.file.notExist")}')
        return;
    }
    var duplicates = findDuplicates(updatedTerms);
    if (duplicates.length > 0) {
        if(!confirm('<fmt:message key="account.terms.confirmFileOverwrite"/>'.replace('{0}', duplicates))) return;
    };
    $('#uploadTerm').submit();

}
$().ready(function(){
    $('input[name="fileToUpload"]').change(addFile);
});

</script>
<s:form id="uploadTerm" action="%{#attr.moduleName}/terms/save" enctype="multipart/form-data" method="post">
  <ui:section titleKey="account.terms">
      <ui:fieldGroup>
        <ui:field>
          <s:actionerror/>
          <s:fielderror fieldName="fileToUpload" />
          <table id="addSection">
              <tr>
                <td><input type="file" name="fileToUpload" class="middleLengthText" /><span class="file-name" style="display:none"></span></td>
                <td><a style="display:none" href="javascript:void(false);" onclick="removeFile($(this)); return false;" class="button "><fmt:message key="form.delete"/></a></td>
              </tr>
          </table>
        </ui:field>
      </ui:fieldGroup>
  </ui:section>
<s:hidden name="id"/>
<ui:button message="fileman.upload" onclick="doUpload();" id="uploadButton" type="button" />
<ui:button message="form.cancel" href="/${moduleName}/view.action?id=${id}" id="cancelButton" type="button" />
</s:form>


