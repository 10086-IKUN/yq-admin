package cn.yanque.models.sale.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SaleOrderEntity {
    private Long id;
    private String orderNo;
    private String studentName;
    private String phone;
    private Long productId;
    private Long classId;
    private String studentCode;
    private BigDecimal productAmount;
    private BigDecimal discountAmount;
    private BigDecimal receivableAmount;
    private BigDecimal paidAmount;
    private String orderStatus;
    /**
     * 订单来源。
     * STUDENT_PURCHASE：学员端自己购买，超过 20 分钟未支付可以自动取消。
     * ADMIN_CREATE：管理端新增学员时创建，作为报名必付订单，不走自动取消。
     */
    private String orderSource;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
