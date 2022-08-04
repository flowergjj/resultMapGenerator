<resultMap id="${resultId}" type="${resultType}">
<#list idList as idObject>
    <id column="${idObject.id}" property="${idObject.property}"/>
</#list>
<#list columnList as columnObject>
    <result column="${columnObject.column}" property="${columnObject.property}"/>
</#list>
<#list associationList as associationObject>
    <#include "association.ftl">
</#list>
<#list collectionList as collectionObject>
    <#include "collection.ftl">
</#list>
</resultMap>
