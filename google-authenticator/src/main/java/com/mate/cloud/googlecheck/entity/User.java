package com.mate.cloud.googlecheck.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 简单的用户类
 *
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends Model<User> {
    /**
     * 用户ID ，唯一标识
     */
    private String userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 登录密码
     */
    private String password;

    /**
     * google 验证的 密钥
     */
    private String googleSecret;

    private Boolean googleEnable;


    /**
     * 状态：0 正常 1 锁定
     */
    private Integer status;

}
