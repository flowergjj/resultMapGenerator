package com.flower.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("father")
public class Father {
    @TableField("name")
    private String name;

}
