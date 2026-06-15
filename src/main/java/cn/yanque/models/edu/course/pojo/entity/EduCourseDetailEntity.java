package cn.yanque.models.edu.course.pojo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 课程详情实体类
 * 对应数据库表：edu_course_detail
 * 作用：存储每个课程在不同阶段、不同天数的具体教学内容
 *
 * 设计思路：
 * 1. 一个课程(courseId)可以包含多个阶段(如"JAVA基础"、"框架")
 * 2. 每个阶段包含多天的课程内容(dayNum)
 * 3. 通过courseId关联到edu_course表
 */
@Data
public class EduCourseDetailEntity {

    /** 课程详情ID，主键，自增 */
    private Long id;

    /** 课程ID，关联edu_course表的id字段 */
    private Long courseId;

    /** 所属阶段名称，如"JAVA基础"、"框架"、"数据库"等 */
    private String stageName;

    /** 第几天课，数字越大表示课程越靠后 */
    private Integer dayNum;

    /** 课程内容，描述当天要学习的具体知识点 */
    private String courseContent;

    /** 创建时间，记录插入数据库的时间 */
    private Date createdAt;

    /** 更新时间，记录最后修改的时间 */
    private Date updatedAt;
}
