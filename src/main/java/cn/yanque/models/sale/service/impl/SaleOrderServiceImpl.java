package cn.yanque.models.sale.service.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.sale.entity.SaleOrderEntity;
import cn.yanque.models.sale.mapper.SaleOrderMapper;
import cn.yanque.models.sale.service.SaleOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service

/**
 * 销售订单服务。
 *
 * <p>这里区分学生自助购买订单和管理端报名订单：前者可超时取消，后者是登录后的必付报名订单。</p>
 */
public class SaleOrderServiceImpl implements SaleOrderService {

    private static final String ORDER_SOURCE_STUDENT_PURCHASE = "STUDENT_PURCHASE";
    private static final String ORDER_SOURCE_ADMIN_CREATE = "ADMIN_CREATE";
    private static final DateTimeFormatter ORDER_NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Autowired
    private SaleOrderMapper saleOrderMapper;

    @Override
    public SaleOrderEntity createOrder(SaleOrderEntity entity) {
        // 订单号允许调用方不传，由服务层统一补齐，避免多个入口重复生成。
        if (entity.getOrderNo() == null || entity.getOrderNo().isBlank()) {
            entity.setOrderNo(buildOrderNo());
        }
        // 默认按学生自助购买处理；管理端报名订单会显式传入 ADMIN_CREATE。
        if (entity.getOrderSource() == null || entity.getOrderSource().isBlank()) {
            entity.setOrderSource(ORDER_SOURCE_STUDENT_PURCHASE);
        }
        saleOrderMapper.insert(entity);
        return entity;
    }

    @Override
    public void updateOrder(SaleOrderEntity entity) {
        saleOrderMapper.updateById(entity);
    }

    @Override
    public SaleOrderEntity getById(Long id) {
        return saleOrderMapper.selectById(id);
    }

    @Override
    public SaleOrderEntity getByOrderNo(String orderNo) {
        return saleOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<SaleOrderEntity> list(String keyword, String orderStatus) {
        return saleOrderMapper.selectPage(keyword, orderStatus);
    }

    @Override
    public List<SaleOrderEntity> listByStudentCode(String studentCode) {
        return saleOrderMapper.selectByStudentCode(studentCode);
    }

    @Override
    public SaleOrderEntity getRequiredPendingOrder(String studentCode) {
        return saleOrderMapper.selectRequiredPendingOrder(studentCode);
    }

    @Override
    public void cancelStudentOrder(String orderNo, String studentCode) {
        SaleOrderEntity order = saleOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!studentCode.equals(order.getStudentCode())) {
            throw new BusinessException(403, "无权操作该订单");
        }
        // 已支付订单涉及真实流水，学生端不能删除；这里只允许取消待支付订单。
        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new BusinessException(400, "只有待支付订单可以删除");
        }
        // 管理端创建的报名订单是进入学生端前必须支付的订单，不允许学生自行取消。
        if (ORDER_SOURCE_ADMIN_CREATE.equals(order.getOrderSource())) {
            throw new BusinessException(400, "鎶ュ悕璁㈠崟闇€瀹屾垚鏀粯锛屼笉鑳藉垹闄?");
        }
        order.setOrderStatus("CANCELLED");
        saleOrderMapper.updateById(order);
    }

    @Override
    public int cancelExpiredPendingOrders() {
        // 自动过期只处理学生端自助购买订单，报名订单保留到支付完成。
        return saleOrderMapper.cancelExpiredPendingOrders();
    }

    @Override
    public void updateOrderStatus(String orderNo, String orderStatus, BigDecimal paidAmount) {
        SaleOrderEntity order = saleOrderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            order.setOrderStatus(orderStatus);
            order.setPaidAmount(paidAmount);
            saleOrderMapper.updateById(order);
        }
    }

    @Override
    public void updateDiscount(Long id, BigDecimal discountAmount) {
        if (discountAmount == null) {
            throw new BusinessException(400, "优惠金额不能为空");
        }
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(400, "优惠金额不能小于0");
        }

        SaleOrderEntity order = saleOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new BusinessException(400, "只有待支付订单可以优惠");
        }
        if (discountAmount.compareTo(order.getProductAmount()) > 0) {
            throw new BusinessException(400, "优惠金额不能超过产品金额");
        }

        BigDecimal receivableAmount = order.getProductAmount().subtract(discountAmount);
        int updated = saleOrderMapper.updateDiscountById(id, discountAmount, receivableAmount);
        if (updated == 0) {
            throw new BusinessException(400, "订单状态已变化，请刷新后重试");
        }
    }

    private String buildOrderNo() {
        return "ORD" + LocalDateTime.now().format(ORDER_NO_TIME_FORMATTER)
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }
}
