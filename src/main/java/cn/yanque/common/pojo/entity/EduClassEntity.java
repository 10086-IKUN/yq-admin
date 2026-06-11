package cn.yanque.common.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class EduClassEntity {

    /** 班级ID */
    private Long id;
    /** 班级期数 */
    private Integer classTerm;
    /** 校区ID */
    private Long campusId;
    /** 班主任用户ID */
    private Long headTeacherId;
    /** 班级状态 WAITING/TEACHING/FINISHED */
    private String classStatus;
    /** 开班时间 */
    private Date startTime;
    /** 课程ID */
    private Long courseId;
    /** 班级人数 */
    private Integer studentCount;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;
}
