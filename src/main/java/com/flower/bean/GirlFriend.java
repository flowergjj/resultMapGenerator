package com.flower.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("girlFriend")
public class GirlFriend {
    private String name;
    @TableId("idCard")
    private String idCard;
    @TableField(exist = false)
    private Father father;
}
