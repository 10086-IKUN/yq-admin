-- 支付订单来源字段。
-- STUDENT_PURCHASE：学员端自助购买，超过 20 分钟未支付可以自动取消。
-- ADMIN_CREATE：管理端新增学员时自动创建，作为报名必付订单，不参与自动取消。
ALTER TABLE `sale_order`
    ADD COLUMN `order_source` varchar(32) NOT NULL DEFAULT 'STUDENT_PURCHASE'
        COMMENT '订单来源：STUDENT_PURCHASE学生自助购买，ADMIN_CREATE管理端创建'
        AFTER `order_status`;

-- 兼容历史订单。
-- 老订单没有来源字段时，统一按学员端自助购买处理，避免影响已有的超时取消逻辑。
UPDATE `sale_order`
SET `order_source` = 'STUDENT_PURCHASE'
WHERE `order_source` IS NULL OR `order_source` = '';
