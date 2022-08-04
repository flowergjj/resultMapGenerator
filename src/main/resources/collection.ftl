    <collection property="${collectionObject.property}" ofType="${collectionObject.ofType}">
        <#list collectionObject.idList as idObject>
            <id column="${idObject.id}" property="${idObject.property}"/>
        </#list>
        <#list collectionObject.columnList as columnObject>
            <result column="${columnObject.column}" property="${columnObject.property}"/>
        </#list>
        <#list collectionObject.associationList as associationObject>
            <#include "association.ftl">
        </#list>
        <#list collectionObject.collectionList as collectionObject>
            <#include "collection.ftl">
        </#list>
    </collection>
