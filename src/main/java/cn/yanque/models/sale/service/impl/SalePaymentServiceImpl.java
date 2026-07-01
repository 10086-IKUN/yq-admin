package cn.yanque.models.sale.service.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.config.AlipayConfig;
import cn.yanque.models.sale.entity.SaleOrderEntity;
import cn.yanque.models.sale.entity.SalePaymentChannelOrderEntity;
import cn.yanque.models.sale.entity.SalePaymentOrderEntity;
import cn.yanque.models.sale.mapper.SalePaymentChannelOrderMapper;
import cn.yanque.models.sale.mapper.SalePaymentOrderMapper;
import cn.yanque.models.sale.service.SaleOrderService;
import cn.yanque.models.sale.service.SalePaymentService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class SalePaymentServiceImpl implements SalePaymentService {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private SalePaymentOrderMapper salePaymentOrderMapper;

    @Autowired
    private SalePaymentChannelOrderMapper salePaymentChannelOrderMapper;

    @Autowired
    private SaleOrderService saleOrderService;

    @Override
    @Transactional
    public String createPayment(String orderNo, String paymentChannel) throws Exception {
        // 查询订单
        SaleOrderEntity order = saleOrderService.getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new BusinessException(400, "订单状态异常，无法支付");
        }

        // 生成支付订单号
        String paymentOrderNo = "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6);

        // 创建支付订单
        SalePaymentOrderEntity paymentOrder = new SalePaymentOrderEntity();
        paymentOrder.setPaymentOrderNo(paymentOrderNo);
        paymentOrder.setOrderNo(orderNo);
        paymentOrder.setStudentCode(order.getStudentCode());
        paymentOrder.setStudentName(order.getStudentName());
        paymentOrder.setPhone(order.getPhone());
        paymentOrder.setProductName("课程购买");
        paymentOrder.setPaymentAmount(order.getReceivableAmount());
        paymentOrder.setPaymentChannel(paymentChannel);
        paymentOrder.setPaymentStatus("PENDING");
        salePaymentOrderMapper.insert(paymentOrder);

        // 调用支付宝创建支付
        if ("ALIPAY".equals(paymentChannel)) {
            return createAlipayPayment(order, paymentOrder);
        }

        throw new BusinessException(400, "不支持的支付渠道");
    }

    private String createAlipayPayment(SaleOrderEntity order, SalePaymentOrderEntity paymentOrder) throws Exception {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        request.setReturnUrl(alipayConfig.getReturnUrl());

        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(paymentOrder.getPaymentOrderNo());
        model.setTotalAmount(paymentOrder.getPaymentAmount().toPlainString());
        model.setSubject("燕雀教育 - 课程购买");
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        request.setBizModel(model);

        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if (response.isSuccess()) {
            log.info("支付宝支付创建成功: paymentOrderNo={}", paymentOrder.getPaymentOrderNo());
            return response.getBody();
        } else {
            log.error("支付宝支付创建失败: {}", response.getSubMsg());
            throw new BusinessException(500, "支付宝支付创建失败: " + response.getSubMsg());
        }
    }

    @Override
    @Transactional
    public String handleNotify(HttpServletRequest request) throws Exception {
        // 获取支付宝回调参数
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        log.info("支付宝异步回调参数: {}", params);

        // 验签
        boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                alipayConfig.getAlipayPublicKey(),
                "UTF-8",
                "RSA2"
        );

        if (!signVerified) {
            log.error("支付宝回调验签失败");
            return "failure";
        }

        // 获取关键参数
        String outTradeNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        String totalAmount = params.get("total_amount");
        String gmtPayment = params.get("gmt_payment");

        log.info("支付宝回调: outTradeNo={}, tradeNo={}, tradeStatus={}, totalAmount={}",
                outTradeNo, tradeNo, tradeStatus, totalAmount);

        // 查询支付订单
        SalePaymentOrderEntity paymentOrder = salePaymentOrderMapper.selectByPaymentOrderNo(outTradeNo);
        if (paymentOrder == null) {
            log.error("支付订单不存在: {}", outTradeNo);
            return "failure";
        }

        // 创建渠道支付记录
        SalePaymentChannelOrderEntity channelOrder = new SalePaymentChannelOrderEntity();
        channelOrder.setPaymentOrderNo(outTradeNo);
        channelOrder.setChannelOrderId(tradeNo);
        channelOrder.setOrderAmount(paymentOrder.getPaymentAmount());
        channelOrder.setPayAmount(new BigDecimal(totalAmount));
        channelOrder.setChannel("ALIPAY");
        channelOrder.setCallbackBody(params.toString());
        channelOrder.setCallbackTime(LocalDateTime.now());

        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            // 支付成功
            channelOrder.setStatus("SUCCESS");
            channelOrder.setPaySuccessDate(gmtPayment);

            // 更新支付订单状态
            paymentOrder.setPaymentStatus("SUCCESS");
            paymentOrder.setUniqueOrderNo(tradeNo);
            salePaymentOrderMapper.updateById(paymentOrder);

            // 更新业务订单状态
            saleOrderService.updateOrderStatus(
                    paymentOrder.getOrderNo(),
                    "PAID",
                    paymentOrder.getPaymentAmount()
            );

            log.info("支付成功: orderNo={}, paymentOrderNo={}", paymentOrder.getOrderNo(), outTradeNo);
        } else if ("TRADE_CLOSED".equals(tradeStatus)) {
            channelOrder.setStatus("CLOSED");
            paymentOrder.setPaymentStatus("CLOSED");
            salePaymentOrderMapper.updateById(paymentOrder);
        } else {
            channelOrder.setStatus("PENDING");
        }

        salePaymentChannelOrderMapper.insert(channelOrder);

        return "success";
    }

    @Override
    @Transactional
    public SaleOrderEntity syncOrderPaymentStatus(String orderNo) throws Exception {
        // 先读取本地业务订单；如果本地已经不是待支付，就不再请求支付宝。
        SaleOrderEntity order = saleOrderService.getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!"PENDING".equals(order.getOrderStatus())) {
            return order;
        }

        // 同一个业务订单可能多次打开支付页，所以这里要逐条支付流水向支付宝确认。
        List<SalePaymentOrderEntity> paymentOrders = salePaymentOrderMapper.selectListByOrderNo(orderNo);
        if (paymentOrders == null || paymentOrders.isEmpty()) {
            return order;
        }

        for (SalePaymentOrderEntity paymentOrder : paymentOrders) {
            try {
                SaleOrderEntity latestOrder = syncSingleAlipayPayment(orderNo, paymentOrder);
                if (latestOrder != null && "PAID".equals(latestOrder.getOrderStatus())) {
                    return latestOrder;
                }
            } catch (Exception ex) {
                log.warn("同步支付流水失败: orderNo={}, paymentOrderNo={}",
                        orderNo,
                        paymentOrder.getPaymentOrderNo(),
                        ex);
                paymentOrder.setLastErrorMsg(ex.getMessage());
                salePaymentOrderMapper.updateById(paymentOrder);
            }
        }

        return saleOrderService.getByOrderNo(orderNo);
    }

    /**
     * 查询单条支付宝支付流水。
     * 支付宝沙箱有时不会把异步回调打到本地环境，所以学生端查询时需要做一次主动同步。
     */
    private SaleOrderEntity syncSingleAlipayPayment(String orderNo, SalePaymentOrderEntity paymentOrder) throws Exception {
        if ("SUCCESS".equals(paymentOrder.getPaymentStatus())) {
            saleOrderService.updateOrderStatus(orderNo, "PAID", paymentOrder.getPaymentAmount());
            return saleOrderService.getByOrderNo(orderNo);
        }
        if (!"PENDING".equals(paymentOrder.getPaymentStatus())) {
            return saleOrderService.getByOrderNo(orderNo);
        }

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(paymentOrder.getPaymentOrderNo());
        request.setBizModel(model);

        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if (!response.isSuccess()) {
            paymentOrder.setLastErrorMsg(response.getSubMsg());
            salePaymentOrderMapper.updateById(paymentOrder);
            return saleOrderService.getByOrderNo(orderNo);
        }

        String tradeStatus = response.getTradeStatus();
        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            // 支付宝确认已支付后，同步支付流水、渠道流水和业务订单。
            paymentOrder.setPaymentStatus("SUCCESS");
            paymentOrder.setUniqueOrderNo(response.getTradeNo());
            salePaymentOrderMapper.updateById(paymentOrder);

            SalePaymentChannelOrderEntity channelOrder = new SalePaymentChannelOrderEntity();
            channelOrder.setPaymentOrderNo(paymentOrder.getPaymentOrderNo());
            channelOrder.setChannelOrderId(response.getTradeNo());
            channelOrder.setOrderAmount(paymentOrder.getPaymentAmount());
            channelOrder.setPayAmount(new BigDecimal(response.getTotalAmount()));
            channelOrder.setStatus("SUCCESS");
            channelOrder.setChannel("ALIPAY");
            channelOrder.setCallbackType("ACTIVE_QUERY");
            channelOrder.setCallbackBody(response.getBody());
            channelOrder.setCallbackTime(LocalDateTime.now());
            salePaymentChannelOrderMapper.insert(channelOrder);

            saleOrderService.updateOrderStatus(orderNo, "PAID", paymentOrder.getPaymentAmount());
        } else if ("TRADE_CLOSED".equals(tradeStatus)) {
            paymentOrder.setPaymentStatus("CLOSED");
            salePaymentOrderMapper.updateById(paymentOrder);
        }

        return saleOrderService.getByOrderNo(orderNo);
    }

    @Override
    public SalePaymentOrderEntity getByPaymentOrderNo(String paymentOrderNo) {
        return salePaymentOrderMapper.selectByPaymentOrderNo(paymentOrderNo);
    }

    @Override
    public SalePaymentOrderEntity getByOrderNo(String orderNo) {
        return salePaymentOrderMapper.selectByOrderNo(orderNo);
    }
}
