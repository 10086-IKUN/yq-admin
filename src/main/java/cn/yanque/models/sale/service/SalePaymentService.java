package cn.yanque.models.sale.service;

import cn.yanque.models.sale.entity.SalePaymentOrderEntity;
import cn.yanque.models.sale.entity.SaleOrderEntity;
import jakarta.servlet.http.HttpServletRequest;

public interface SalePaymentService {

    /**
     * 创建支付订单并返回支付宝支付链接
     */
    String createPayment(String orderNo, String paymentChannel) throws Exception;

    /**
     * 处理支付宝异步回调
     */
    String handleNotify(HttpServletRequest request) throws Exception;

    /**
     * 查询支付订单
     */
    SalePaymentOrderEntity getByPaymentOrderNo(String paymentOrderNo);

    /**
     * 查询订单的支付记录
     */
    SalePaymentOrderEntity getByOrderNo(String orderNo);

    /**
     * 主动查询支付宝交易结果，并把本地业务订单同步成最新支付状态。
     */
    SaleOrderEntity syncOrderPaymentStatus(String orderNo) throws Exception;
}
