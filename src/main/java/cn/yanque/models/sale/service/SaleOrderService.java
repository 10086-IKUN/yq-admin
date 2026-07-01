package cn.yanque.models.sale.service;

import cn.yanque.models.sale.entity.SaleOrderEntity;
import java.util.List;

public interface SaleOrderService {

    SaleOrderEntity createOrder(SaleOrderEntity entity);

    void updateOrder(SaleOrderEntity entity);

    SaleOrderEntity getById(Long id);

    SaleOrderEntity getByOrderNo(String orderNo);

    List<SaleOrderEntity> list(String keyword, String orderStatus);

    List<SaleOrderEntity> listByStudentCode(String studentCode);

    SaleOrderEntity getRequiredPendingOrder(String studentCode);

    /**
     * 学员端删除订单。
     * 这里不做物理删除，而是把待支付订单改成已取消，避免支付、对账、后台查询记录丢失。
     *
     * @param orderNo 订单号
     * @param studentCode 当前登录学员编号
     */
    void cancelStudentOrder(String orderNo, String studentCode);

    /**
     * 自动取消超过 20 分钟仍未支付的订单。
     *
     * @return 本次取消的订单数量
     */
    int cancelExpiredPendingOrders();

    void updateOrderStatus(String orderNo, String orderStatus, java.math.BigDecimal paidAmount);

    void updateDiscount(Long id, java.math.BigDecimal discountAmount);
}
