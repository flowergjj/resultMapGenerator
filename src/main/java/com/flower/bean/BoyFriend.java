package com.flower.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("boyFriend")
public class BoyFriend {
    @TableField("name")
    private String name;

    @TableField(exist = false)
    private Father father;
}
