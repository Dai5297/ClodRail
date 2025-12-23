-- ===================================================================
-- 积分商城订单表 (mall_order)
-- 用于存储用户使用积分兑换商品的订单信息
-- ===================================================================

CREATE TABLE IF NOT EXISTS `mall_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_number` VARCHAR(50) NOT NULL COMMENT '订单号(唯一标识)',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `item_id` BIGINT NOT NULL COMMENT '商品ID',
  `item_name` VARCHAR(200) NOT NULL COMMENT '商品名称(冗余)',
  `item_image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片(冗余)',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '兑换数量',
  `unit_price` INT NOT NULL COMMENT '单价(积分)',
  `total_points` INT NOT NULL COMMENT '总积分',
  `recipient_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `recipient_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `recipient_address` VARCHAR(500) NOT NULL COMMENT '收货地址',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态: 0-待发货, 1-已发货, 2-已完成, 3-已取消',
  `ship_time` DATETIME DEFAULT NULL COMMENT '发货时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_by` BIGINT DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分商城订单表';

-- ===================================================================
-- 索引说明
-- ===================================================================
-- PRIMARY KEY (id): 主键索引
-- UNIQUE KEY uk_order_number: 订单号唯一索引,保证订单号不重复
-- KEY idx_user_id: 用户ID索引,用于查询用户的订单列表
-- KEY idx_item_id: 商品ID索引,用于统计商品兑换情况
-- KEY idx_status: 状态索引,用于按状态筛选订单
-- KEY idx_create_time: 创建时间索引,用于按时间排序

-- ===================================================================
-- 字段说明
-- ===================================================================
-- id: 订单主键,自增
-- order_number: 订单号,格式为 EX + yyyyMMddHHmmss + 6位序列号,如 EX202412091234560000001
-- user_id: 用户ID,关联用户表
-- item_id: 商品ID,关联商品表
-- item_name: 商品名称,冗余字段,便于查询订单时不用join商品表
-- item_image: 商品图片,冗余字段
-- quantity: 兑换数量,默认1
-- unit_price: 单价(积分),冗余字段,防止商品价格变动影响历史订单
-- total_points: 总积分 = unit_price * quantity
-- recipient_name: 收货人姓名
-- recipient_phone: 收货人联系电话
-- recipient_address: 收货地址
-- status: 订单状态
--   0: 待发货 (订单创建后的初始状态)
--   1: 已发货 (商品已发货)
--   2: 已完成 (用户确认收货或系统自动完成)
--   3: 已取消 (用户取消或系统取消)
-- ship_time: 发货时间,状态变更为1时记录
-- complete_time: 完成时间,状态变更为2时记录
-- cancel_time: 取消时间,状态变更为3时记录
-- remark: 备注信息
-- create_time: 创建时间,自动记录
-- update_time: 更新时间,自动更新
-- create_by: 创建人ID
-- update_by: 最后修改人ID

-- ===================================================================
-- 示例数据
-- ===================================================================
INSERT INTO `mall_order` (
  `order_number`,
  `user_id`,
  `item_id`,
  `item_name`,
  `item_image`,
  `quantity`,
  `unit_price`,
  `total_points`,
  `recipient_name`,
  `recipient_phone`,
  `recipient_address`,
  `status`,
  `create_by`,
  `update_by`
) VALUES (
  'EX202412091234560000001',
  1,
  100002672305,
  '10元车票优惠券',
  'http://example.com/coupon-10.jpg',
  2,
  500,
  1000,
  '张三',
  '13800138000',
  '北京市朝阳区xxx路xxx号',
  0,
  1,
  1
);

-- ===================================================================
-- 业务规则说明
-- ===================================================================
-- 1. 订单创建流程:
--    a) 校验商品状态和库存
--    b) 校验用户积分余额
--    c) 扣减商品库存(乐观锁: stock >= quantity)
--    d) 扣减用户积分(乐观锁: current_points >= total_points)
--    e) 创建订单记录
--    f) 添加积分消费明细
--
-- 2. 订单状态流转:
--    待发货(0) -> 已发货(1) -> 已完成(2)
--    待发货(0) -> 已取消(3)
--
-- 3. 并发控制:
--    - 库存扣减使用乐观锁: WHERE stock >= quantity
--    - 积分扣减使用乐观锁: WHERE current_points >= total_points
--    - 使用事务保证原子性
--
-- 4. 数据一致性:
--    - 商品信息冗余存储,避免历史订单受商品变更影响
--    - 积分扣减和订单创建在同一事务中
--    - 失败时回滚所有操作
