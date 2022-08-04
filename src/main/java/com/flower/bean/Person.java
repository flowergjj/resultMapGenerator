package com.flower.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import java.util.Date;
import java.util.List;

@TableName("person")
public class Person {
    //@MppMultiId
    //@TableField("idCard")//mybatisPlu多主键策略,不懂的也不需要了解
    @TableId("idCard") //mybatisPlus主键策略
    private String idCard;
    @TableField("name")
    private String name;
    @TableField("sex")
    private String sex;
    @TableField(exist = false)
    //不要问为什么女朋友是一个集合
    private List<GirlFriend> girlFriendList;
    @TableField(exist = false)
    //虽然exist是false,但是BoyFriend类上有@TableName注解，所以会进行一对一映射
    private BoyFriend boyFriend;
    @TableField(exist = false)
    //虽然exist是false,但是属性类不是List.class类、属性类String.class类上也没有@TableName注解，所以不会进行转换。不要问为什么没有钱，因为穷
    private String money;
    @TableField("date")
    private Date date;
    @TableField(exist = false)
    //虽然exist是false,并且是list集合，但list集合的泛型是Integer.class没有@TableName修饰，所以不会进行映射转换
    private List<Integer> list;

}
