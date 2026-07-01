package cn.yanque.models.sale.mapper;

import cn.yanque.models.sale.entity.SalePaymentOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SalePaymentOrderMapper {

    void insert(SalePaymentOrderEntity entity);

    void updateById(SalePaymentOrderEntity entity);

    SalePaymentOrderEntity selectByPaymentOrderNo(@Param("paymentOrderNo") String paymentOrderNo);

    SalePaymentOrderEntity selectByOrderNo(@Param("orderNo") String orderNo);

    List<SalePaymentOrderEntity> selectListByOrderNo(@Param("orderNo") String orderNo);
}
