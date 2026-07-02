package cn.yanque.models.studentVisit.pojo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data

/**
 * StudentVisitEntity 数据库实体对象。
 *
 * <p>字段与对应业务表保持映射关系，供 MyBatis 查询和写入使用。</p>
 */
public class StudentVisitEntity {
    private Long id;
    private Long studentId;
    private Long teacherId;
    private LocalDateTime visitTime;
    private String visitContent;
    private LocalDate nextVisitTime;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String studentName;
    private String studentCode;
    private String phone;
    private String tagType;
    private BigDecimal onTimeRate;
}
