    <association property="${associationObject.property}" javaType="${associationObject.javaType}">
        <#list associationObject.idList as idObject>
            <id column="${idObject.id}" property="${idObject.property}"/>
        </#list>
        <#list associationObject.columnList as columnObject>
            <result column="${columnObject.column}" property="${columnObject.property}"/>
        </#list>
        <#list associationObject.associationList as associationObject>
            <#include "association.ftl">
        </#list>
        <#list associationObject.collectionList as collectionObject>
            <#include "collection.ftl">
        </#list>
    </association>
