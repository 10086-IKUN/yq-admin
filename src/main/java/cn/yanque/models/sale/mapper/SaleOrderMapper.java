package cn.yanque.models.sale.mapper;

import cn.yanque.models.sale.entity.SaleOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SaleOrderMapper {

    void insert(SaleOrderEntity entity);

    void updateById(SaleOrderEntity entity);

    SaleOrderEntity selectById(@Param("id") Long id);

    SaleOrderEntity selectByOrderNo(@Param("orderNo") String orderNo);

    List<SaleOrderEntity> selectPage(@Param("keyword") String keyword,
                                      @Param("orderStatus") String orderStatus);

    List<SaleOrderEntity> selectByStudentCode(@Param("studentCode") String studentCode);

    SaleOrderEntity selectRequiredPendingOrder(@Param("studentCode") String studentCode);

    int cancelExpiredPendingOrders();

    int updateDiscountById(@Param("id") Long id,
                           @Param("discountAmount") java.math.BigDecimal discountAmount,
                           @Param("receivableAmount") java.math.BigDecimal receivableAmount);
}
