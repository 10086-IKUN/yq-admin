package cn.yanque.models.sale.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data

/**
 * SalePaymentOrderEntity 数据库实体对象。
 *
 * <p>字段与对应业务表保持映射关系，供 MyBatis 查询和写入使用。</p>
 */
public class SalePaymentOrderEntity {
    private Long id;
    private String paymentOrderNo;
    private String orderNo;
    private String studentCode;
    private String studentName;
    private String phone;
    private String productName;
    private BigDecimal paymentAmount;
    private String paymentChannel;
    private String paymentStatus;
    private String uniqueOrderNo;
    private String lastErrorMsg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
