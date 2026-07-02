package cn.yanque.models.sale.mapper;

import cn.yanque.models.sale.entity.SaleOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper

/**
 * 销售订单表访问接口。
 *
 * <p>包含管理端订单列表、学生端订单列表、报名必付订单和超时订单取消等查询更新。</p>
 */
public interface SaleOrderMapper {

    void insert(SaleOrderEntity entity);

    void updateById(SaleOrderEntity entity);

    SaleOrderEntity selectById(@Param("id") Long id);

    SaleOrderEntity selectByOrderNo(@Param("orderNo") String orderNo);

    List<SaleOrderEntity> selectPage(@Param("keyword") String keyword,
                                      @Param("orderStatus") String orderStatus);

    List<SaleOrderEntity> selectByStudentCode(@Param("studentCode") String studentCode);

    SaleOrderEntity selectRequiredPendingOrder(@Param("studentCode") String studentCode);

    /**
     * 取消超过支付窗口的学生自助购买待支付订单。
     */
    int cancelExpiredPendingOrders();

    int updateDiscountById(@Param("id") Long id,
                           @Param("discountAmount") java.math.BigDecimal discountAmount,
                           @Param("receivableAmount") java.math.BigDecimal receivableAmount);
}
