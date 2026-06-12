package cn.yanque.common.pojo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 系统用户实体类
 * 对应数据库中的用户表
 */
@Data
public class SysUserEntity {

    /** 用户ID */
    private Long id;
    /** 登录用户名 */
    private String username;
    /** 登录密码 */
    private String password;
    /** 用户昵称 */
    private String nickname;
    /** 真实姓名 */
    private String realName;
    /** 手机号 */
    private String phone;
    /** 邮箱 */
    private String email;
    /** 飞书UnionID */
    private String unionId;
    /** 状态，ACTIVE启用，INACTIVE禁用 */
    private String status;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;
}
