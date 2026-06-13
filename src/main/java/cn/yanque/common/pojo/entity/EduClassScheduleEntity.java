package cn.yanque.common.pojo.entity;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * 班级课表实体类
 * 对应数据库表：edu_class_schedule
 * 记录班级每天的课程安排
 */
@Data
public class EduClassScheduleEntity {

    /** 课表ID，主键，自增 */
    private Long id;

    /** 班级ID，关联 edu_class 表 */
    private Long classId;

    /** 课程ID，关联 edu_course 表 */
    private Long courseId;

    /** 课程详情ID，正式上课日才有值，关联 edu_course_detail 表 */
    private Long courseDetailId;

    /** 课程第几天，正式上课日才有值 */
    private Integer courseDayNum;

    /** 排课日期 */
    private Date scheduleDate;

    /** 排课类型：CLASS-上课, SELF_STUDY-自习, REST-休息 */
    private String scheduleType;

    /** 课程内容或当天安排说明 */
    private String courseContent;

    /** 课程所属阶段，自习/休息可为空 */
    private String stageName;

    /** 当天授课老师ID，仅上课日可能有值 */
    private Long teacherId;

    /** 创建时间 */
    private Timestamp createdAt;

    /** 更新时间 */
    private Timestamp updatedAt;
}
