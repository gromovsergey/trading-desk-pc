<#if nodes?exists>
<table class="fieldAndAccessories"<#rt>
<#if parameters.id?exists>
    id="${parameters.id?html}"<#rt/>
</#if><tr>
    <#list nodes as node>
        <td class="withField">
            <#if node.type.name() == 'TEXT'>
                <span class="simpleText">${node.content}</span>
            <#else>
                ${node.content}
            </#if>
        </td>
    </#list>
</tr></table>
</#if>