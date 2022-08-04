# 工程简介
根据实体类生成mapper.xml中的resultMap，支持复杂的生成(感觉说的是废话，不复杂的手写就行了)
使用:
ResultMapGenerator类中main方法替换实体类的.class就行了，但是要注意生成的resultMap中是包含类的包名的，你可以在本工程中创建和实际项目
一样结构工程目录,再调用程序生成即可。也可以将ResultMapGenerator类和resources目录下的三个ftl文件复制到实际项目中(注意pox.xml中的依赖
也需要复制),调用ResultMapGenerator的main方法即可。
# 延伸阅读
生成resultMap标签规则:
    1.基于mybatisPlus和mybatisPlus-Plus注解来生成，也就是说直接使用mybstisPlus生成的实体类通过自己随意组装这些实体类而不需要添加
    任何别的注解即可生成resultMap
    2.主键生成规则分两种情况:如果使用mybatisPlus框架的主键注解,程序将解析@TableId作为resultMap中的主键,@TableId的value属性即为数据库列名
      实体类属性名即为数据库列名对应的属性名。如果使用mybatisPlus-Plus框架的注解,程序将解析@MppMultiId和@TableField作为主键注解,
      @TableField的value属性即为数据库列名,实体类属性名即为数据库列名对应的属性名。
    3.普通列生成规则:@TableField的value为列名,实体类属性名为对应的属性名。
    4.@TableField(exist = false) 规则: 如果被此注解修饰的实体类属性对应的类不是List.class及其子类或者实体类属性对应的类上没有@TableName注解，
    将不会进行resultMap映射。如果实体类属性对应的类是List.class及其子类并且List集合中的泛型类上有@TableName注解则会按照一对多的映射进行转换。如果实体类属性对应的类上有@TableName注解将会
    按照一对一的映射进行转换。
    5.@TableName注解中可以不用写value属性值
PS:以上看不懂就直接看Person类例子就行了
        
