package cn.yanque.common.pojo.entity;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;

/**
 * 值班安排实体类
 * 对应数据库表：edu_duty_assignment
 */
@Data
public class EduDutyAssignmentEntity {

    /** 值班安排ID */
    private Long id;

    /** 值班日期 */
    private Date dutyDate;

    /** 值班类型 CLASS_NIGHT_DUTY/GLOBAL_NIGHT_DUTY */
    private String dutyType;

    /** 班级ID，全局值班填0 */
    private Long classId;

    /** 开始时间 */
    private Time startTime;

    /** 结束时间 */
    private Time endTime;

    /** 值班老师ID */
    private Long teacherId;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private Date createdAt;

    /** 更新时间 */
    private Date updatedAt;
}
