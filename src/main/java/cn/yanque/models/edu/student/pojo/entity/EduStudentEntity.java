package cn.yanque.models.edu.student.pojo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 学员实体类
 * 对应数据库中的学员表
 */
@Data
public class EduStudentEntity {

    /** 学员ID */
    private Long id;
    /** 学号 */
    private String studentCode;
    /** 姓名 */
    private String studentName;
    /** 手机号 */
    private String phone;
    /** 登录密码 */
    private String password;
    /** 毕业届数 */
    private Integer graduationSession;
    /** 学校 */
    private String school;
    /** 学历 */
    private String education;
    /** 学习方式 ONLINE/OFFLINE */
    private String studyMode;
    /** 班级ID */
    private Long classId;
    /** 产品ID */
    private Long productId;
    /** 加入时间 */
    private Date joinTime;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;
}
