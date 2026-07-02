package cn.yanque.models.sale.mapper;

import cn.yanque.models.sale.entity.SalePaymentOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper

/**
 * 本地支付流水表访问接口。
 *
 * <p>一笔业务订单可能多次拉起支付，所以既支持按支付流水号查询，也支持按业务订单号查询全部流水。</p>
 */
public interface SalePaymentOrderMapper {

    void insert(SalePaymentOrderEntity entity);

    void updateById(SalePaymentOrderEntity entity);

    SalePaymentOrderEntity selectByPaymentOrderNo(@Param("paymentOrderNo") String paymentOrderNo);

    SalePaymentOrderEntity selectByOrderNo(@Param("orderNo") String orderNo);

    List<SalePaymentOrderEntity> selectListByOrderNo(@Param("orderNo") String orderNo);
}
