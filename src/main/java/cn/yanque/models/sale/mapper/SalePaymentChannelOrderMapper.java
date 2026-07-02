package cn.yanque.models.sale.mapper;

import cn.yanque.models.sale.entity.SalePaymentChannelOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper

/**
 * 第三方渠道支付流水表访问接口。
 *
 * <p>用于记录支付宝等渠道返回的唯一交易号，避免重复处理同一笔渠道通知或查询结果。</p>
 */
public interface SalePaymentChannelOrderMapper {

    void insert(SalePaymentChannelOrderEntity entity);

    void updateById(SalePaymentChannelOrderEntity entity);

    SalePaymentChannelOrderEntity selectByUniqueOrderNo(@Param("uniqueOrderNo") String uniqueOrderNo);
}
