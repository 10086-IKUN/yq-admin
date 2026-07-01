package cn.yanque.models.sale.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.sale.entity.SaleOrderEntity;
import cn.yanque.models.sale.entity.SaleProductEntity;
import cn.yanque.models.sale.service.SaleOrderService;
import cn.yanque.models.sale.service.SaleProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/sale")
@Tag(name = "SaleAdminController", description = "销售管理")
public class SaleAdminController {

    @Autowired
    private SaleProductService saleProductService;

    @Autowired
    private SaleOrderService saleOrderService;

    // ==================== 产品管理 ====================

    @GetMapping("/products")
    @RequirePermission("sale:product:list")
    @Operation(description = "产品列表")
    public ApiResponse<List<SaleProductEntity>> productList(
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(saleProductService.list(keyword));
    }

    @PostMapping("/products")
    @RequirePermission("sale:product:create")
    @Operation(description = "创建产品")
    public ApiResponse<Void> productCreate(@RequestBody SaleProductEntity entity) {
        saleProductService.create(entity);
        return ApiResponse.success(null);
    }

    @PutMapping("/products/{id}")
    @RequirePermission("sale:product:update")
    @Operation(description = "更新产品")
    public ApiResponse<Void> productUpdate(@PathVariable Long id, @RequestBody SaleProductEntity entity) {
        entity.setId(id);
        saleProductService.update(entity);
        return ApiResponse.success(null);
    }

    @GetMapping("/products/{id}")
    @RequirePermission("sale:product:list")
    @Operation(description = "产品详情")
    public ApiResponse<SaleProductEntity> productDetail(@PathVariable Long id) {
        return ApiResponse.success(saleProductService.getById(id));
    }

    // ==================== 订单管理 ====================

    @GetMapping("/orders")
    @RequirePermission("sale:order:list")
    @Operation(description = "订单列表")
    public ApiResponse<List<SaleOrderEntity>> orderList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String orderStatus) {
        return ApiResponse.success(saleOrderService.list(keyword, orderStatus));
    }

    @GetMapping("/orders/{id}")
    @RequirePermission("sale:order:list")
    @Operation(description = "订单详情")
    public ApiResponse<SaleOrderEntity> orderDetail(@PathVariable Long id) {
        return ApiResponse.success(saleOrderService.getById(id));
    }

    @PutMapping("/orders/{id}/discount")
    @RequirePermission("sale:order:list")
    @Operation(description = "璁㈠崟浼樻儬")
    public ApiResponse<Void> orderDiscount(@PathVariable Long id, @RequestBody OrderDiscountReq req) {
        saleOrderService.updateDiscount(id, req.getDiscountAmount());
        return ApiResponse.success(null);
    }

    public static class OrderDiscountReq {
        private BigDecimal discountAmount;

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
        }
    }
}
