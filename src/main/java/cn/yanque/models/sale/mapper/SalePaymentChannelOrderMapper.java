package cn.yanque.models.sale.mapper;

import cn.yanque.models.sale.entity.SalePaymentChannelOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SalePaymentChannelOrderMapper {

    void insert(SalePaymentChannelOrderEntity entity);

    void updateById(SalePaymentChannelOrderEntity entity);

    SalePaymentChannelOrderEntity selectByUniqueOrderNo(@Param("uniqueOrderNo") String uniqueOrderNo);
}
