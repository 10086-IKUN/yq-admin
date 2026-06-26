package cn.yanque.models.studentFront.pojo.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 学员端个人信息响应对象。
 *
 * <p>这个对象只给学员端页面展示使用。
 * 它在学员基础信息上额外补充班级期数、班级名称和课程名称，
 * 避免前端直接显示 classId、productId 这种数据库字段。</p>
 */
@Data
@Schema(description = "学员端个人信息响应")
public class StudentProfileRes {

    /** 学员ID。 */
    @Schema(description = "学员ID")
    private Long id;

    /** 学号。 */
    @Schema(description = "学号")
    private String studentCode;

    /** 学员姓名。 */
    @Schema(description = "学员姓名")
    private String studentName;

    /** 手机号。 */
    @Schema(description = "手机号")
    private String phone;

    /** 学习方式：ONLINE 线上，OFFLINE 线下。 */
    @Schema(description = "学习方式")
    private String studyMode;

    /** 班级ID。 */
    @Schema(description = "班级ID")
    private Long classId;

    /** 班级期数。 */
    @Schema(description = "班级期数")
    private Integer classTerm;

    /** 给前端直接展示的班级名称，例如“第5期”。 */
    @Schema(description = "班级名称")
    private String className;

    /** 课程ID。 */
    @Schema(description = "课程ID")
    private Long courseId;

    /** 课程名称。 */
    @Schema(description = "课程名称")
    private String courseName;

    /** 加入时间。 */
    @Schema(description = "加入时间")
    private Date joinTime;
}
