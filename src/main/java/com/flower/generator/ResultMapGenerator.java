package com.flower.generator;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.flower.bean.Person;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Data;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author flower
 */
@Data
public class ResultMapGenerator {
    private static Map<String, Object> resultMap = new HashMap(3);
    private static final String ID_LIST = "idList";
    private static final String COLUMN_LIST = "columnList";
    private static final String ASSOCIATION_LIST = "associationList";
    private static final String COLLECTION_LIST = "collectionList";
    private static final String RESULT_ID = "resultId";
    private static final String RESULT_ID_SUFFIX = "ResultMap";
    private static final String RESULT_TYPE = "resultType";
    /**
     * xml标签属性
     **/
    private static final String ID = "id";
    private static final String PROPERTY = "property";
    private static final String COLUMN = "column";
    private static final String OF_TYPE = "ofType";
    private static final String JAVA_TYPE = "javaType";
    private static final String RESULT_MAP_TEMPLATE_NAME = "resultMap.ftl";

    /**
     * 解析class生成对应map数据
     *
     * @param clazz 需要解析的class对象
     * @return
     * @throws Exception
     */
    public static Map<String, Object> analyze(Class clazz) throws Exception {
        Map<String, Object> contentMap = new HashMap();
        List<Map<String, Object>> idList = new ArrayList(16);
        List<Map<String, Object>> columnList = new ArrayList(16);
        List<Map<String, Object>> associationList = new ArrayList(16);
        List<Map<String, Object>> collectionList = new ArrayList(16);
        contentMap.put(ID_LIST, idList);
        contentMap.put(COLUMN_LIST, columnList);
        contentMap.put(ASSOCIATION_LIST, associationList);
        contentMap.put(COLLECTION_LIST, collectionList);
        String resultJdbcType = clazz.getName();
        String resultId = clazz.getSimpleName();
        if(resultMap.get(RESULT_ID) == null){
            resultMap.put(RESULT_ID, resultId + RESULT_ID_SUFFIX);
            resultMap.put(RESULT_TYPE, resultJdbcType);
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        //循环类属性
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            boolean isMppMultiId = declaredField.isAnnotationPresent(MppMultiId.class);
            boolean isTableId = declaredField.isAnnotationPresent(TableId.class);
            boolean isTableField = declaredField.isAnnotationPresent(TableField.class);
            //MPP 注解判断是否是主键
            if ((isMppMultiId  && isTableField) || isTableId ) {
                String id = null;
                if(isTableId){
                    TableId tableId = declaredField.getAnnotation(TableId.class);
                    id =  tableId.value();
                }else{
                    TableField tableField = declaredField.getAnnotation(TableField.class);
                    id = tableField.value();
                }
                String idProperty = declaredField.getName();
                HashMap<String, Object> idMap = new HashMap(2);
                idMap.put(ID, id);
                idMap.put(PROPERTY, idProperty);
                idList.add(idMap);
                //非主键
            } else if (isTableField) {
                TableField tableField = declaredField.getAnnotation(TableField.class);
                boolean exist = tableField.exist();
                String column = tableField.value();
                Class<?> propertyClass = declaredField.getType();
                //exist属性为true 并且value的值不为null,基本类型exist为false的不做映射
                if (exist) {
                    //类上面没有tableName注解则是普通的非对象类型，进行映射
                    boolean annotationPresent = propertyClass.isAnnotationPresent(TableName.class);
                    if (!annotationPresent && !List.class.isAssignableFrom(propertyClass)) {
                        String columnProperty = declaredField.getName();
                        HashMap<String, Object> columnMap = new HashMap();
                        columnMap.put(COLUMN, column);
                        columnMap.put(PROPERTY, columnProperty);
                        columnList.add(columnMap);
                    }else{
                        throw new Exception("复杂类型不支持数据库字段存在映射ERROR:@TableField(exist=true)");
                    }
                    //数据库中不存在的字段
                } else {
                    boolean annotationPresent = propertyClass.isAnnotationPresent(TableName.class);
                    //非基本类型时候进行一对一或者一对多映射
                    if (annotationPresent || List.class.isAssignableFrom(propertyClass)) {
                        try {
                            if (List.class.isAssignableFrom(propertyClass)) {
                                String collectionProperty = declaredField.getName();
                                Type genericType = declaredField.getGenericType();
                                if (genericType instanceof ParameterizedType) {
                                    Class<?> actualTypeArgument = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                                    boolean annotationPresent1 = actualTypeArgument.isAnnotationPresent(TableName.class);
                                    //List集合中泛型类上必须有@TableName注解才进行映射
                                    if(annotationPresent1){
                                        String classFullName = actualTypeArgument.getName();
                                        HashMap<String, Object> collectionMap = new HashMap();
                                        collectionMap.put(PROPERTY, collectionProperty);
                                        collectionMap.put(OF_TYPE, classFullName);
                                        Map<String, Object> generator = analyze(actualTypeArgument);
                                        collectionMap.putAll(generator);
                                        collectionList.add(collectionMap);
                                    }
                                }
                            } else {
                                String associationProperty = declaredField.getName();
                                String classFullName = propertyClass.getName();
                                HashMap<String, Object> associationMap = new HashMap();
                                associationMap.put(PROPERTY, associationProperty);
                                associationMap.put(JAVA_TYPE, classFullName);
                                Map<String, Object> generator = analyze(propertyClass);
                                associationMap.putAll(generator);
                                associationList.add(associationMap);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new Exception("解析错误", e);
                        }
                    }

                }
            }
        }
        return contentMap;
    }

    /**
     * 根据map调用freemarker模板生成resultMap字符串
     *
     * @param xmlMap
     * @return
     */
    public static String generator(Map<String, Object> xmlMap) throws Exception {
        resultMap.putAll(xmlMap);
        //1.创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //2.设置模板所在的目录
        try {
            String templatePath = ResultMapGenerator.class.getResource("/").getFile();
            configuration.setDirectoryForTemplateLoading(new File(templatePath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("模板文件路径未找到", e);
        }
        //3.设置字符集
        configuration.setDefaultEncoding("utf-8");
        //4.加载模板
        Template template = null;
        try {
            template = configuration.getTemplate(RESULT_MAP_TEMPLATE_NAME);
        } catch (IOException e) {
            throw new Exception("模板文件未找到", e);
        }
        //5.创建Writer对象

        //6.输出
        StringWriter out = new StringWriter();
        template.process(resultMap,out);
        //8.关闭Writer对象
        out.flush();
        out.close();
        String result = out.getBuffer().toString();
        return format(result).replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>","");
    }

    /**
     * 格式化xml字符串,百度一堆，没什么必要了解
     * @param str
     * @return
     * @throws Exception
     */
    public static String format(String str) throws Exception {
        SAXReader reader = new SAXReader();
        // System.out.println(reader);
        // 注释：创建一个串的字符输入流
        StringReader in = new StringReader(str);
        Document doc = reader.read(in);
        // System.out.println(doc.getRootElement());
        // 注释：创建输出格式
        OutputFormat formater = OutputFormat.createPrettyPrint();
        // 注释：设置xml的输出编码
        formater.setEncoding("utf-8");
        // 注释：创建输出(目标)
        StringWriter out = new StringWriter();
        // 注释：创建输出流
        XMLWriter writer = new XMLWriter(out, formater);
        // 注释：输出格式化的串到目标中，执行后。格式化后的串保存在out中。
        writer.write(doc);
        writer.close();
        // 注释：返回我们格式化后的结果
        return out.toString();

    }
    public static void main(String[] args) throws Exception {
        /*注意Person类所在的包目录结构,避免生成的ResultMap到实际项目中不可用*/
        Map<String, Object> xmlMap = analyze(Person.class);
        String xmlStr = generator(xmlMap);
        System.out.println(xmlStr);
    }
}
