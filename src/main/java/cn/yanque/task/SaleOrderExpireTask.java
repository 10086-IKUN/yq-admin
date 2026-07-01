package cn.yanque.task;

import cn.yanque.models.sale.service.SaleOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SaleOrderExpireTask {

    @Autowired
    private SaleOrderService saleOrderService;

    /**
     * 每分钟检查一次待支付订单。
     * 如果订单创建后超过 20 分钟仍未支付，就自动改成已取消。
     */
    @Scheduled(fixedDelay = 60 * 1000)
    public void cancelExpiredPendingOrders() {
        int cancelCount = saleOrderService.cancelExpiredPendingOrders();
        if (cancelCount > 0) {
            log.info("自动取消超时未支付订单，数量：{}", cancelCount);
        }
    }
}
