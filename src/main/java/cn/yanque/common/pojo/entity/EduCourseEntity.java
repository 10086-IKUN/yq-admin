package cn.yanque.common.pojo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class EduCourseEntity {

    /** 课程ID */
    private Long id;
    /** 课程名称 */
    private String courseName;
    /** 课程天数 */
    private Integer courseDays;
    /** 飞书资料路径 */
    private String feishuMaterialPath;
    /** 状态，1启用，0禁用 */
    private Integer status;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;
}
