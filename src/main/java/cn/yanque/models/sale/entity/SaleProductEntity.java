package cn.yanque.models.sale.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SaleProductEntity {
    private Long id;
    private String productCode;
    private String productName;
    private String studyMode;
    private BigDecimal productPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
