package cn.yanque.models.studentTag.pojo.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
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
