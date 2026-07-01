package cn.yanque.models.sale.controller;

import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.sale.entity.SaleOrderEntity;
import cn.yanque.models.sale.entity.SaleProductEntity;
import cn.yanque.models.sale.service.SaleOrderService;
import cn.yanque.models.sale.service.SalePaymentService;
import cn.yanque.models.sale.service.SaleProductService;
import cn.yanque.models.studentFront.util.StudentAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student/pay")
@SkipPermission
@Tag(name = "StudentPayController", description = "学员端支付")
@Slf4j
public class StudentPayController {

    @Autowired
    private SaleProductService saleProductService;

    @Autowired
    private SaleOrderService saleOrderService;

    @Autowired
    private SalePaymentService salePaymentService;

    @GetMapping("/products")
    @Operation(description = "可购买产品列表")
    public ApiResponse<List<SaleProductEntity>> productList() {
        return ApiResponse.success(saleProductService.listAll());
    }

    @PostMapping("/orders")
    @Operation(description = "创建订单")
    public ApiResponse<SaleOrderEntity> createOrder(@RequestParam Long productId, HttpServletRequest request) {
        EduStudentEntity student = StudentAuthUtil.getStudent(request);
        SaleProductEntity product = saleProductService.getById(productId);
        if (product == null) {
            throw new BusinessException(404, "产品不存在");
        }

        // 生成订单号
        String orderNo = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 6);

        // 创建订单
        SaleOrderEntity order = new SaleOrderEntity();
        order.setOrderNo(orderNo);
        order.setStudentName(student.getStudentName());
        order.setPhone(student.getPhone());
        order.setProductId(productId);
        order.setClassId(student.getClassId());
        order.setStudentCode(student.getStudentCode());
        order.setProductAmount(product.getProductPrice());
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setReceivableAmount(product.getProductPrice());
        order.setPaidAmount(BigDecimal.ZERO);
        order.setOrderStatus("PENDING");
        // 学员自己在前台购买的订单，允许超过 20 分钟后自动取消。
        order.setOrderSource("STUDENT_PURCHASE");

        return ApiResponse.success(saleOrderService.createOrder(order));
    }

    @PostMapping("/create")
    @Operation(description = "发起支付")
    public ApiResponse<Map<String, String>> createPayment(@RequestParam String orderNo,
                                                           @RequestParam(defaultValue = "ALIPAY") String paymentChannel,
                                                           HttpServletRequest request) throws Exception {
        String paymentForm = salePaymentService.createPayment(orderNo, paymentChannel);
        Map<String, String> result = new HashMap<>();
        result.put("form", paymentForm);
        return ApiResponse.success(result);
    }

    @PostMapping("/notify")
    @Operation(description = "支付宝异步回调")
    public String notify(HttpServletRequest request) throws Exception {
        log.info("收到支付宝异步回调");
        return salePaymentService.handleNotify(request);
    }

    @GetMapping("/orders")
    @Operation(description = "我的订单列表")
    public ApiResponse<List<SaleOrderEntity>> myOrders(HttpServletRequest request) {
        String studentCode = StudentAuthUtil.getStudentNo(request);
        return ApiResponse.success(saleOrderService.listByStudentCode(studentCode));
    }

    @GetMapping("/required-order")
    @Operation(description = "查询学员登录后必须先支付的报名订单")
    public ApiResponse<SaleOrderEntity> requiredOrder(HttpServletRequest request) {
        String studentCode = StudentAuthUtil.getStudentNo(request);
        return ApiResponse.success(saleOrderService.getRequiredPendingOrder(studentCode));
    }

    @DeleteMapping("/orders/{orderNo}")
    @Operation(description = "删除我的待支付订单")
    public ApiResponse<Void> deleteOrder(@PathVariable String orderNo, HttpServletRequest request) {
        String studentCode = StudentAuthUtil.getStudentNo(request);
        // 学员端的“删除”实际是取消待支付订单，后台仍保留订单流水，方便后续核对。
        saleOrderService.cancelStudentOrder(orderNo, studentCode);
        return ApiResponse.success();
    }

    @GetMapping("/orders/{id}")
    @Operation(description = "订单详情")
    public ApiResponse<SaleOrderEntity> orderDetail(@PathVariable Long id) {
        return ApiResponse.success(saleOrderService.getById(id));
    }

    @GetMapping("/query/{orderNo}")
    @Operation(description = "查询支付状态")
    public ApiResponse<SaleOrderEntity> queryPaymentStatus(@PathVariable String orderNo) throws Exception {
        return ApiResponse.success(salePaymentService.syncOrderPaymentStatus(orderNo));
    }
}
