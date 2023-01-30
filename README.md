# eleme
一个基于Springboot+vue的前后端分离项目 实现了饿了么外卖平台客户端商家端和后台管理端,是一个技术栈较为全面的项目

前端采用vue+vite+js,用到了组件库vant3,elementui

后端技术栈包含了mysql,mybatisplus,redis,elasticsearch,rockermq,并接入了阿里的沙箱支付和高德地图的api

实现了用户手动定位城市购物车下单选取收获地址,商家接单配送,包含了完整的订单流程

利用redis实现了缓存热点商家,每个城市热搜词排行榜功能

拉取<5km范围的商家采用了elasticsearch的geohash_point

利用了elasticsearch的父子查询以及分词器实现了输入商品/商家关键词搜索附近商家,后台可以手动添加词库中词汇.

rockermq实现了订单超时功能,订单状态接口实现了接口的幂等性,为支付失败提供退款等等功能.





