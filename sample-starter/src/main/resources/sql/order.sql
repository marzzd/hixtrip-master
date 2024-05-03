# 背景: 存量订单10亿, 日订单增长百万量级。
# 主查询场景如下:
# 1. 买家频繁查询我的订单, 高峰期并发100左右。实时性要求高。
# 2. 卖家频繁查询我的订单, 高峰期并发30左右。允许秒级延迟。
# 3. 平台客服频繁搜索客诉订单(半年之内订单, 订单尾号，买家姓名搜索)，高峰期并发10左右。允许分钟级延迟。
# 4. 平台运营进行订单数据分析，如买家订单排行榜, 卖家订单排行榜。
# resources/sql中给出整体设计方案。包含存储基础设施选型, DDL（基础字段即可）, 满足上述场景设计思路。
#
# 我的个人方案如下：
# 基础存储设施：mysql + elasticSearch
#
# 假设按照500W日增，则10亿存量有200日订单，半年左右，针对第三点的需求，任然存在搜索情况，所以订单数据暂不适合归档。
# 我个人意见是前台与后台分离的架构设计，区分买家库，卖家库。

# 基于买家id与订单号的基因法实现的分库分表，保证同一个买家的订单落到同一个买家库的同一张表中，
# 而且一般买家查询自己的订单都会限制查询时间段，例如不能查询超过3个月，每次只能查看10条之类的限制，可以从业务功能上进行限制，
# 做好mysql优化，表数据减少，访问速度就不会慢，实时性也有保证。即解决第一点

# 针对第二点，以买家库为主库，通过binlog+canal的方式，将买家库的订单数据异步同步至elasticSearch中，
# 创建卖家id索引，优化索引配置，提高检索效率。
# 因为是基于binlog+canal的异步数据同步方式，所以会存在一定的延迟，可通过调整binlog文件格式为Mixed，减少binlog文件大小，来减少传输压力。
# 也可以另起一套mysql，用卖家id进行分库分表，在服务器执行卖家库操作时，而外同步卖家库，并进行分库分表。不过这里就可能存在数据不一致的情况，就需要通过其他机制来保证。

# 针对第三点，平台客服查询客诉订单是针对全量订单，所以使用买家库或卖家库多表join去查询效率是非常低的。
# 这里依旧可以使用elasticSearch搜索引擎，针对es订单数据的订单类型，时间, 订单尾号，
# 买家姓名等字段创建合适的索引后，进行搜索也能最大化利用elasticSearch查询。

# 第四点，依旧可以使用elasticSearch来解决。通过买家id或卖家id统计订单后再进行排序。
# 需要强调的1点是elasticSearch本身是非常耗内存的，所以需要将elasticSearch部署到内存较大的节点上。
CREATE TABLE `t_order` (
     `id` bigint(20) NOT NULL COMMENT '订单号',
     `user_id` bigint(20) NOT NULL COMMENT '买家id',
     `user_name` varchar(32) NOT NULL COMMENT '买家名称',
     `seller_id` bigint(20) NOT NULL COMMENT '卖家id',
     `goods_title` varchar(255) NOT NULL COMMENT '商品标题',
     `goods_sku` varchar(255) NOT NULL COMMENT '商品规格内容',
     `goods_sku_pic` varchar(255) NOT NULL COMMENT '商品规格图片地址',
     `sku_id` bigint(20) NOT NULL COMMENT '商品规格id',
     `num` bigint(20) NOT NULL COMMENT '订单数量',
     `sku_price` decimal(10,2) NOT NULL COMMENT 'sku单价',
     `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
     `pay_status` char(2) NOT NULL DEFAULT '0' COMMENT '支付状态 0未支付 1支付成功 2支付失败',
     `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
     `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
     `pay_method` char(2) DEFAULT NULL COMMENT '支付方式 1微信,2支付宝',
     `serial_no` varchar(64) DEFAULT NULL COMMENT '第三方流水号',
     `pay_url` varchar(1000) DEFAULT NULL COMMENT '第三方支付回调',
     `status` char(2) NOT NULL DEFAULT '0' COMMENT '订单状态 0待付款 1待发货 2待收货 3待评价 4已取消 5退款 6已完成',
     `remark` varchar(100) DEFAULT NULL COMMENT '备注',
     `consignee` varchar(32) DEFAULT NULL COMMENT '收货人',
     `phone` char(11) DEFAULT NULL COMMENT '手机号',
     `address` varchar(255) DEFAULT NULL COMMENT '地址',
     `cancel_time` datetime DEFAULT NULL COMMENT '订单取消时间',
     `cancel_reason` varchar(255) DEFAULT NULL COMMENT '订单取消原因',
     `rejection_reason` varchar(255) DEFAULT NULL COMMENT '订单拒绝原因',
     `complaint_status` char(2) NOT NULL DEFAULT '0' COMMENT '投诉状态 0无投诉 1已投诉',
     `complaint_reason` varchar(255) DEFAULT NULL COMMENT '投诉原因',
     `delivery_time` datetime DEFAULT NULL COMMENT '送达时间',
     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_by` bigint(20) NOT NULL COMMENT '最后更新人id',
     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
     PRIMARY KEY (`id`),
     KEY `idx_order_user_id` (`create_time`,`user_id`),
     KEY `idx_order_seller_id` (`create_time`,`seller_id`),
     KEY `idx_order_status` (`status`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';


