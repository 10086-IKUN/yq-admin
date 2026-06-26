package cn.yanque.models.studentTag.pojo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StudentTagEntity {
    private Long id;
    private Long studentId;
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
