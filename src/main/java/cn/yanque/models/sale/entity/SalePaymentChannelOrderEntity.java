package cn.yanque.models.sale.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data

/**
 * SalePaymentChannelOrderEntity 数据库实体对象。
 *
 * <p>字段与对应业务表保持映射关系，供 MyBatis 查询和写入使用。</p>
 */
public class SalePaymentChannelOrderEntity {
    private Long id;
    private String paymentOrderNo;
    private String uniqueOrderNo;
    private String merchantNo;
    private String orderId;
    private String channelOrderId;
    private BigDecimal orderAmount;
    private BigDecimal payAmount;
    private String status;
    private String paySuccessDate;
    private String channel;
    private String payerInfo;
    private String failCode;
    private String failReason;
    private String channelTrxId;
    private String bankOrderId;
    private String callbackType;
    private String callbackBody;
    private LocalDateTime callbackTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
