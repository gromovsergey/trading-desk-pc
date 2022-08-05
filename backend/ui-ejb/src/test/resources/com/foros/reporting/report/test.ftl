select
    <#list columns as column>
    ${column.getResultSetName()}<#if column_has_next>,</#if>
    </#list>
from
    account ag
where
    ag.account_id = :accountId
