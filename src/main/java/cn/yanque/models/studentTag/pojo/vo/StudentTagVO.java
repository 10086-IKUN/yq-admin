package cn.yanque.models.studentTag.pojo.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data

/**
 * 学生标签列表展示对象。
 *
 * <p>在系统计算出的标签基础上，补充学生、班级和确认人相关信息，供管理端列表页展示。</p>
 */
public class StudentTagVO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentCode;
    private Long classId;
    private Integer classTerm;
    private String className;
    private String tagType;
    private BigDecimal onTimeRate;
    private Integer totalAssignments;
    private Integer onTimeCount;
    private Integer confirmed;
    private Long confirmedBy;
    private LocalDateTime confirmedTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
