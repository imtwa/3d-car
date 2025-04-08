# 3D汽车展示系统
系统分为平台端和后台管理端
基于SpringBoot 3.X、Vue 3.X、Ant Design Vue 4.X 开发

#### 项目环境
Java 17+（推荐 Java 21，21以下建议删除虚拟线程配置，并自行配置线程池）

MySQL 8+

node 20+（或兼容 vue3 版本）

Redis 3+

#### 使用说明
先开启mysql和redis服务，在后端.yml修改数据库连接信息、文件存储路径，执行car_three.sql脚本
前端项目pnpm install，pnpm run dev启动即可
vue-car-front项目需在public下放置天空球 sky1.hdr 文件，否则3D展示效果不佳

#### 数据库脚本默认账号密码
账号：admin

密码：admin123