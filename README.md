# 平台简介

[![码云Gitee](https://gitee.com/yixiacoco/ruoyi-tdesign/badge/star.svg?theme=blue)](https://gitee.com/yixiacoco/ruoyi-tdesign)
[![GitHub](https://img.shields.io/github/stars/yixiaco/ruoyi-tdesign.svg?style=social&label=Stars)](https://github.com/yixiaco/ruoyi-tdesign)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://gitee.com/yixiacoco/ruoyi-tdesign/blob/master/LICENSE)
[![使用IntelliJ IDEA开发维护](https://img.shields.io/badge/IntelliJ%20IDEA-提供支持-blue.svg)](https://www.jetbrains.com/?from=ruoyi-tdesign)
<br>
[![ruoyi-tdesign](https://img.shields.io/badge/ruoyi%20tdesign-1.3.1-success.svg)](https://gitee.com/yixiacoco/ruoyi-tdesign)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-blue.svg)]()
[![JDK-17](https://img.shields.io/badge/JDK-17-green.svg)]()
[![JDK-21](https://img.shields.io/badge/JDK-21-green.svg)]()

> ruoyi-tdesign 是重写 RuoYi-Vue-Plus UI的场景及其他功能场景的升级(不兼容原框架)

> 项目代码、文档 均开源免费可商用 遵循开源协议在项目中保留开源协议文件即可<br>
活到老写到老 为兴趣而开源 为学习而开源 为让大家真正可以学到技术而开源

> 交流与讨论: [交流群](https://github.com/yixiaco/ruoyi-tdesign/wiki/%E4%BA%A4%E6%B5%81%E7%BE%A4)<br>
> 系统演示(感谢 [疯狂的麦克粥](https://gitee.com/CrazyMikePorridge) 贡献的演示服务器): [传送门](https://ruoyi-tdesign.hexm.online)

> 查看项目[更新日志](./CHANGELOG.md)

## 文档
要查看项目文档，请访问[ruoyi-tdesign-doc](https://yixiaco.github.io/ruoyi-tdesign-doc/)

## 介绍
| 功能介绍     | 使用技术                | 文档地址                                                                                            | 描述                                                                                                                                              |
|----------|---------------------|-------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| 当前框架     | ruoyi-tdesign       | [ruoyi-tdesign文档](https://gitee.com/yixiacoco/ruoyi-tdesign/wikis/pages)                        | 重写RuoYi-Vue-Plus部分逻辑，UI框架                                                                                                                       |
| 原框架      | RuoYi-Vue-Plus      | [RuoYi-Vue-Plus](https://gitee.com/dromara/RuoYi-Vue-Plus)                                      | 定期同步需要的功能                                                                                                                                       |
| 前端开发框架   | Vue3、Tdesign UI     | [Tdesign UI官网](https://tdesign.tencent.com/vue-next/overview)                                   | 腾讯研发前端Vue3框架                                                                                                                                    |
| 后端开发框架   | SpringBoot          | [SpringBoot官网](https://spring.io/projects/spring-boot/#learn)                                   |                                                                                                                                                 |
| 容器框架     | Undertow            | [Undertow官网](https://undertow.io/)                                                              | 采用 Undertow 基于 XNIO 的高性能容器                                                                                                                      |
| 权限认证框架   | Sa-Token、Jwt        | [Sa-Token官网](https://sa-token.dev33.cn/)                                                        | 采用 Sa-Token、Jwt 静态使用功能齐全 低耦合 高扩展<br/>采用 Sa-Token 支持注解 登录校验、角色校验、权限校验、二级认证校验、HttpBasic校验、忽略校验<br/>角色与权限校验支持多种条件 如 `AND` `OR` 或 `权限 OR 角色` 等复杂表达式 |
| 三方鉴权     | JustAuth            | [JustAuth官网](https://www.justauth.cn/)                                                          | 采用 JustAuth 第三方登录组件 支持微信、钉钉等数十种三方认证                                                                                                             |
| 关系数据库    | MySQL               | [MySQL官网](https://dev.mysql.com/)                                                               | 适配 8.X 最低 5.7，可同时使用异构切换                                                                                                                         |
| 关系数据库    | Oracle              | [Oracle官网](https://www.oracle.com/cn/database/)                                                 | 适配 11g 12c，可同时使用异构切换                                                                                                                            |
| 关系数据库    | PostgreSQL          | [PostgreSQL官网](https://www.postgresql.org/)                                                     | 适配 13 14，可同时使用异构切换                                                                                                                              |
| 缓存数据库    | Redis               | [Redis官网](https://redis.io/)                                                                    | 支持 Redis 5-7 支持大部分新功能特性 如 分布式限流、分布式队列                                                                                                           |
| Redis客户端 | Redisson            | [Redisson官网](https://redisson.org/)                                                             | 采用 Redisson Redis官方推荐 基于Netty的客户端工具<br/>支持Redis 90%以上的命令 底层优化规避很多不正确的用法 例如: keys被转换为scan<br/>支持单机、哨兵、单主集群、多主集群等模式                               |
| 缓存注解     | Spring-Cache        | [Spring Cache文档](https://spring.io/guides/gs/caching/)                                          | 采用 Spring-Cache 注解 对其扩展了实现支持了更多功能<br/>例如 过期时间 最大空闲时间 组最大长度等 只需一个注解即可完成数据自动缓存                                                                    |
| 数据库框架    | Mybatis-Plus        | [Mybatis-Plus文档](https://baomidou.com/guide/)                                                   | 采用 Mybatis-Plus 基于对象几乎不用写SQL全java操作 功能强大插件众多<br/>例如多租户插件 分页插件 乐观锁插件等等                                                                           |
| 数据库框架    | p6spy               | [p6spy官网](https://p6spy.readthedocs.io/)                                                        | 采用 p6spy 可输出完整SQL与执行时间监控                                                                                                                        |
| 多数据源框架   | dynamic-datasource  | [dynamic-ds文档](https://www.kancloud.cn/tracy5546/dynamic-datasource/content)                    | 采用 dynamic-datasource 支持市面大部分数据库<br/>通过yml配置即可动态管理异构不同种类的数据库 也可通过前端页面添加数据源<br/>支持spel表达式从请求头参数等条件切换数据源                                          |
| 序列化框架    | Jackson             | [Jackson官网](https://github.com/FasterXML/jackson)                                               | 统一使用 jackson 高效可靠                                                                                                                               |
| 分布式队列    | Redisson            | [Redisson文档](https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95)                      | 普通队列、延迟队列、优先队列 等                                                                                                                                |
| 分布式锁     | Lock4j              | [Lock4j官网](https://gitee.com/baomidou/lock4j)                                                   | 注解锁、工具锁 多种多样                                                                                                                                    |
| 分布式幂等    | Redisson            | [Lock4j文档](https://gitee.com/baomidou/lock4j)                                                   | 拦截重复提交                                                                                                                                          |
| 监控框架     | SpringBoot-Admin    | [GItHub](https://github.com/codecentric/spring-boot-admin)                                      | 采用 SpringBoot-Admin 基于SpringBoot官方 actuator 探针机制<br/>实时监控服务状态 框架还为其扩展了在线日志查看监控                                                                  |
| 分布式链路追踪  | Apache SkyWalking   | [Apache SkyWalking文档](https://skywalking.apache.org/docs/)                                      | 链路追踪、网格分析、度量聚合、可视化                                                                                                                              |
| 分布式任务调度  | SnailJob            | [SnailJob官网](https://snailjob.opensnail.com/)                                                   | 天生支持分布式 统一的管理中心 支持多种数据库 支持分片重试DAG任务流等                                                                                                           |
| 文件存储     | Minio               | [Minio文档](https://docs.min.io/)                                                                 | 采用 Minio 分布式文件存储 天生支持多机、多硬盘、多分片、多副本存储<br/>支持权限管理 安全可靠 文件可加密存储                                                                                   |
| 云存储      | 七牛、阿里、腾讯            | [OSS使用文档](https://gitee.com/yixiacoco/ruoyi-tdesign/wikis/pages?sort_id=4359146&doc_id=1469725) | 采用 AWS S3 协议客户端 支持 七牛、阿里、腾讯 等一切支持S3协议的厂家                                                                                                        |
| 短信       | 支持数十种短信厂家           | [短信使用文档](https://wind.kim/)                                                                     | 采用 sms4j 短信融合包 支持数十种短信厂家 只需在yml配置好厂家密钥即可使用 可多厂家共用                                                                                               |
| 邮件       | mail-api            |                                                                                                 | 采用 mail-api 通用协议支持大部分邮件厂商                                                                                                                       |
| 监控框架     | SpringBoot-Admin    | [SpringBoot-Admin文档](https://codecentric.github.io/spring-boot-admin/current/)                  | 全方位服务监控                                                                                                                                         |
| 校验框架     | Validation          | [Validation文档](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/)  | 增强接口安全性、严谨性 支持国际化                                                                                                                               |
| Excel框架  | Alibaba EasyExcel   | [EasyExcel文档](https://www.yuque.com/easyexcel/doc/easyexcel)                                    | 性能优异 扩展性强                                                                                                                                       |
| 文档框架     | SpringDoc、javadoc   | [接口文档](https://gitee.com/yixiacoco/ruoyi-tdesign/wikis/pages?sort_id=5805266&doc_id=1469725)    | 无注解零入侵基于java注释                                                                                                                                  |
| 工具类框架    | Hutool、Lombok       | [Hutool文档](https://www.hutool.cn/docs/)                                                         | 减少代码冗余 增加安全性                                                                                                                                    |
| 代码生成器    | 适配MP、SpringDoc规范化代码 | [代码生成文档](https://gitee.com/yixiacoco/ruoyi-tdesign/wikis/pages?sort_id=5522329&doc_id=1469725)  | 只需设计好表结构 一键生成所有crud代码与页面<br/>降低80%的开发量 把精力都投入到业务设计上<br/>框架为其适配MP、SpringDoc规范化代码 同时支持动态多数据源代码生成                                                  |
| 部署方式     | Docker              | [Docker文档](https://docs.docker.com/)                                                            | 基于请求头动态返回不同语种的文本内容 开发难度低 有对应的工具类 支持大部分注解内容国际化                                                                                                   |
| 国际化      | SpringMessage       | [SpringMVC文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc) | Spring标准国际化方案                                                                                                                                   |

## 软件架构图

![Plus部署架构图](https://images.gitee.com/uploads/images/2021/1112/202137_673ac5d2_1766278.png "Plus部署架构图.png")
## 贡献代码

欢迎各路英雄豪杰 `PR` 代码 请提交到 `dev` 开发分支 统一测试发版

框架定位为 `多租户管理系统(分布式集群强化)` 原则上不接受业务 `PR`

### 其他

* 同步升级 RuoYi-Vue-Plus
* 同步升级 tdesign-vue-next-starter

## 业务功能

| 功能      | 介绍                                                                    |
|---------|-----------------------------------------------------------------------|
| 租户管理    | 配置系统租户，支持 SaaS 场景下的多租户功能。                                             |
| 租户套餐管理  | 系统内租户所能使用的套餐管理 如:套餐内所包含的菜单等                                           |
| 应用管理    | 配置租户识别key，将域名、appid等动态地址转换为租户ID                                       |
| 客户端管理   | 系统内对接的所有客户端管理 如: pc端、小程序端等<br/>支持动态授权登录方式 如: 短信登录、密码登录等 支持动态控制token时效 |
| 用户管理    | 用户是系统操作者，该功能主要完成系统用户配置。                                               |
| 部门管理    | 配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。                                       |
| 岗位管理    | 配置系统用户所属担任职务。                                                         |
| 菜单管理    | 配置系统菜单，操作权限，按钮权限标识等。                                                  |
| 角色管理    | 角色菜单权限分配、设置角色按机构进行数据范围权限划分。                                           |
| 字典管理    | 对系统中经常使用的一些较为固定的数据进行维护。                                               |
| 参数管理    | 对系统动态配置常用参数。                                                          |
| 通知公告    | 系统通知公告信息发布维护。                                                         |
| 操作日志    | 系统正常操作日志记录和查询；系统异常信息日志记录和查询。                                          |
| 登录日志    | 系统登录日志记录查询包含登录异常。                                                     |
| OSS配置   | 系统文件上传、下载服务配置                                                         |
| 文件管理    | 系统文件上传、下载等管理。                                                         |
| OSS处理规则 | 处理例如图片压缩配置                                                            |
| 消息配置    | 配置短信、邮箱、自定义发送源。                                                       |
| 消息常量    | 发送消息时，定义的常量字符，不需要关心使用的是模板ID还是内容。                                      |
| 消息模板    | 配置关联的消息配置、消息常量，在发送消息时渲染入参到变量中。                                        |
| 消息发送记录  | 记录消息发送时间及内容。                                                          |
| 定时任务    | 在线（添加、修改、删除)任务调度包含执行结果日志。                                             |
| 代码生成    | 前后端代码的生成（java、html、xml、sql）支持CRUD下载 。                                 |
| 系统接口    | 根据业务代码自动生成相关的api接口文档。                                                 |
| 服务监控    | 监视集群系统CPU、内存、磁盘、堆栈、在线日志、Spring相关配置等。                                  |
| 缓存监控    | 对系统的缓存信息查询，命令统计等。                                                     |
| 在线构建器   | 拖动表单元素生成相应的HTML代码。                                                    |
| 连接池监视   | 监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。                                      |
| 使用案例    | 系统的一些功能案例                                                             |

## 演示图例

|                                  |                              |
|----------------------------------|------------------------------|
| ![登录页.png](./doc/images/登录页.png) | ![](./doc/images/首页.png)     |
| ![](./doc/images/用户管理.png)       | ![](./doc/images/角色管理.png)   |
| ![](./doc/images/菜单管理.png)       | ![](./doc/images/部门管理.png)   |
| ![](./doc/images/岗位管理.png)       | ![](./doc/images/字典管理.png)   |
| ![](./doc/images/字典数据.png)       | ![](./doc/images/参数管理.png)   |
| ![](./doc/images/通知公告.png)       | ![](./doc/images/操作日志.png)   |
| ![](./doc/images/OSS配置管理.png)    | ![](./doc/images/文件管理.png)   |
| ![](./doc/images/OSS处理规则.png)    | ![](./doc/images/消息配置.png)   |
| ![](./doc/images/消息常量.png)       | ![](./doc/images/消息模板.png)   |
| ![](./doc/images/消息发送记录.png)     | ![](./doc/images/客户端管理.png)  |
| ![](./doc/images/租户管理.png)       | ![](./doc/images/租户套餐管理.png) |
| ![](./doc/images/应用管理.png)       | ![](./doc/images/登录日志.png)   |
| ![](./doc/images/在线用户.png)       | ![](./doc/images/缓存监控.png)   |
| ![](./doc/images/代码生成.png)       | ![](./doc/images/个人中心.png)   |
