package cn.yanque.models.studentTag.pojo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data

/**
 * StudentTagEntity 数据库实体对象。
 *
 * <p>字段与对应业务表保持映射关系，供 MyBatis 查询和写入使用。</p>
 */
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
